package com.mk.ots.pay.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.module.QueryPay;
import com.mk.ots.pay.module.query.QueryPayPram;
import com.mk.ots.pay.service.IPayService;

@Service("processPayingConfirmedOrderJob")
public class ProcessPayingConfirmedOrderJob implements PayJob {
	
	private Logger logger = LoggerFactory.getLogger(ProcessPayingConfirmedOrderJob.class);
	
	@Autowired
	private OrderService orderService;
	@Autowired
    private IPayService payService;
	
	@Override
	public void doJob(PayTaskTypeEnum taskType, PayTaskStatusEnum currentStatus, PayTaskStatusEnum afterStatus) {
		
		List<OtaOrder> orders = orderService.getPayingConfirmedOrders(PayTaskTypeEnum.PROCESSPAYINGCONFIRMEDORDER.getInterval());
		
		int num4CancelOrder = 0;
		
		logger.info("支付中已确认订单[" + orders.size() + "]条.");
		for(OtaOrder order : orders) {
			
			Long orderId = order.getId();
			logger.info("验证订单:" +orderId+ "是否已经支付...");
			
			QueryPayPram pram = QueryPay.findPay(String.valueOf(orderId));
			
			if(pram.isSuccess()) {
				logger.info("订单:" +orderId+ "银行确认已经支付.");
				if(payService.payIsWaitPay(String.valueOf(orderId))) {
					logger.info("订单:" +orderId+ "支付流水中没有记录,调用payresponse.");
					payService.payresponse(orderId, pram.getBankno(), pram.getPrice().toString(), pram.getBanktype());
				} else {
					logger.info("订单:" +orderId+ "支付流水中已经存在记录,更改订单支付状态为已支付(120).");
					orderService.changeOrderStatusByPay(orderId, null, PayStatusEnum.alreadyPay, null);
				}
				
			} else {
				logger.info("订单:" +orderId+ "没有支付,调用订单系统取消...");
				try {
					orderService.cancelOrder(order);
					orderService.changeOrderStatusByPay(orderId, null, PayStatusEnum.payFail, null);
					num4CancelOrder++;
				} catch (Throwable e) {
					logger.error("订单:" +orderId+ "调用订单系统取消异常!", e);
				}
				logger.info("订单:" +orderId+ "调用订单系统取消完毕.");
			}
			
		}
		
		logger.info("本轮共处理" +orders.size()+ "条已确认支付中订单,取消" +num4CancelOrder+ "条.");
	}
	
}
