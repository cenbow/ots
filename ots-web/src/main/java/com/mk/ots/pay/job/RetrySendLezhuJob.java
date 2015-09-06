package com.mk.ots.pay.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.model.PPayTask;
import com.mk.ots.pay.model.ReSendLeZhu;
import com.mk.ots.pay.service.IPayService;

@Service("retrySendLezhuJob")
public class RetrySendLezhuJob implements PayJob {

	private Logger logger = LoggerFactory.getLogger(RetrySendLezhuJob.class);

	@Autowired
	private IPayService payService;
	@Autowired
	private OrderService orderService;

	@Override
	public void doJob(PayTaskTypeEnum taskType, PayTaskStatusEnum currentStatus, PayTaskStatusEnum afterStatus) {

		List<PPayTask> tasks = payService.selectInitTask(taskType, currentStatus);

		logger.info("本轮次需要重试的任务[" + tasks.size() + "]条.");
		
		List<PPayTask> finishTasks = new ArrayList<PPayTask>();

		for (PPayTask task : tasks) {

			OtaOrder order = orderService.findOtaOrderById(task.getOrderId());

			if (order.getOrderStatus() >= OtaOrderStatusEnum.Account.getId().intValue()) {

				logger.info("自动发送乐住币,订单:" + order.getId() + "订单状态为[" + order.getOrderStatus() + "],不能进行乐住币操作,此订单的自动下发乐住币任务终止.");

				finishTasks.add(task);

				continue;
			}

			ReSendLeZhu reSendLeZhu = null;
			String json4ReSendLeZhu = task.getContent();

			if (StringUtils.isNotEmpty(json4ReSendLeZhu)) {
				reSendLeZhu = JSON.parseObject(json4ReSendLeZhu, ReSendLeZhu.class);
			} else {
				logger.error("自动发送乐住币,订单:" + order.getId() + "content为空.");
				
				continue;
			}

			Boolean reSend = false;
			if (reSendLeZhu != null) {
				reSend = this.payService.reSendLeZhu(reSendLeZhu.getOrder(), reSendLeZhu.getPayid(), reSendLeZhu.getPmsSendId(),
						reSendLeZhu.getPrice(), reSendLeZhu.getMemberName());
			} else {
				logger.error("自动发送乐住币,订单:" + order.getId() + "反序列化后的对象为空.");
				
				continue;
			}

			if (reSend != null && reSend) {
				logger.info("自动发送乐住币,订单号:" + reSendLeZhu.getOrder().getId() + ",支付订单号:" + reSendLeZhu.getPayid() + "下发成功");
				finishTasks.add(task);
			} else {
				logger.info("自动发送乐住币,订单号:" + reSendLeZhu.getOrder().getId() + ",支付订单号:" + reSendLeZhu.getPayid() + "下发失败");
			}
			
		}

		if (CollectionUtils.isNotEmpty(finishTasks)) {
			
			logger.info("本轮次结束后不再需要重试的任务[" + finishTasks.size() + "]条.");
			payService.updatePayTask(afterStatus, finishTasks);
		}
		
	}

}
