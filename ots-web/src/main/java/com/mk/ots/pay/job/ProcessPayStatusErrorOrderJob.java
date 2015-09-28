package com.mk.ots.pay.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.model.PPayStatusErrorOrder;
import com.mk.ots.pay.model.PPayTask;
import com.mk.ots.pay.service.IPayService;

@Service("processPayStatusErrorOrderJob")
public class ProcessPayStatusErrorOrderJob implements PayJob  {
	
	private Logger logger = LoggerFactory.getLogger(RetrySendLezhuJob.class);

	@Autowired
	private IPayService payService;
	@Autowired
	private OrderService orderService;

	@Override
	public void doJob(PayTaskTypeEnum taskType, PayTaskStatusEnum currentStatus, PayTaskStatusEnum afterStatus) {
		
		List<PPayTask> tasks = payService.selectInitTask(taskType, currentStatus);

		logger.info("本轮次需要处理的订单[" + tasks.size() + "]条.");
		
		int insert = 0;
		
		List<PPayTask> finishTasks = new ArrayList<PPayTask>();

		for (PPayTask task : tasks) {

			OtaOrder order = orderService.findOtaOrderById(task.getOrderId());

			logger.info("订单:" + order.getId() + "订单状态为:" + order.getOrderStatus());
			
			if (order.getOrderStatus() >= OtaOrderStatusEnum.CheckIn.getId().intValue()) {

				if(order.getPayStatus() == PayStatusEnum.paying.getId().intValue()) {
					
					logger.info("订单:" + order.getId() + "支付状态为:" + order.getPayStatus() + "将其置为支付失败并存入支付异常订单表...");
					orderService.changeOrderStatusByPay(order.getId(), null, PayStatusEnum.payFail, null);
					if(insertDB(order)) {
						
						insert++;
						finishTasks.add(task);
						continue;
					}
					
				}
				
				if (order.getPayStatus() >= PayStatusEnum.alreadyPay.getId().intValue()) {

					finishTasks.add(task);
					continue;

				}
				
				if (order.getOrderStatus() <= OtaOrderStatusEnum.CheckOut.getId().intValue()) {
					if (order.getPayStatus() == PayStatusEnum.waitPay.getId().intValue()) {

						logger.info("订单:" + order.getId() + "支付状态为:" + order.getPayStatus() + "将其存入支付异常订单表...");

						if (insertDB(order)) {

							insert++;
							finishTasks.add(task);
							continue;
						}

					} 
				} else {
					
					finishTasks.add(task);
					continue;
				}
				
			} 
			
		}
		if (CollectionUtils.isNotEmpty(finishTasks)) {
			
			payService.updatePayTask(afterStatus, finishTasks);
		}
		
		logger.info("本轮存入支付状态异常表[" +insert+ "]条.");
		
	}

	private boolean insertDB(OtaOrder order) {
		PPayStatusErrorOrder errorOrder = new PPayStatusErrorOrder();
		errorOrder.setOrderId(order.getId());
		errorOrder.setOrderCreateTime(order.getCreateTime());
		
		Long id = payService.insertPayStatusError(errorOrder);
		
		logger.info("订单:" + order.getId() + "存入支付异常订单表,id:" + id);
		
		if(id != null && id > 0) {
			return true;
		} else {
			return false;
		}
	}
		
}
