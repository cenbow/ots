package com.mk.ots.pay.job;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.model.PPayTask;
import com.mk.ots.pay.service.IPayService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("sendMsgJob")
public class SendMsgJob implements PayJob {
	
	private Logger logger = LoggerFactory.getLogger(SendMsgJob.class);

	@Autowired
    private IPayService payService;
	@Autowired
    private OrderService orderService;
	
	@Override
	public void doJob(PayTaskTypeEnum taskType, PayTaskStatusEnum currentStatus, PayTaskStatusEnum afterStatus) {
		
		int doNotNeedSend = 0;
		int sendFail = 0;
		
		List<PPayTask> tasks = payService.selectInitTask(taskType, currentStatus);
		
		List<PPayTask> finishTasks = new ArrayList<PPayTask>();
		
		for(PPayTask task : tasks) {
			
			Cat.logEvent("OtsJob", "SendMsgJob.doJob", Event.SUCCESS, "");
			
			OtaOrder order = orderService.findOtaOrderById(task.getOrderId());
			
			if(order != null && order.getOrderStatus() >= OtaOrderStatusEnum.CheckIn.getId().intValue()) {
				
				logger.info("订单:" + task.getOrderId() + "已经是" + OtaOrderStatusEnum.getByID(order.getOrderStatus()).name() + "状态,不需要再发送短信.");
				
				doNotNeedSend++;
				finishTasks.add(task);
				
				continue;
			}
			
			Boolean result = payService.sendMsg(order);
			
			if(result != null && result) {
				
				finishTasks.add(task);
			} else {
				
				sendFail++;
				logger.info("订单:" + task.getOrderId() + "给酒店老板发送短信失败,1分钟后重试.");
			}
		}
		
		if (CollectionUtils.isNotEmpty(finishTasks)) {
			payService.updatePayTask(afterStatus, finishTasks);
		}
		logger.info("本次需要给酒店老板发送短信的任务共" + tasks.size() + "条,成功发送短信"
				+ (finishTasks.size() - doNotNeedSend) + "条,不需要发送" + doNotNeedSend + "条,发送失败" + sendFail + "条.");
	}
	
}
