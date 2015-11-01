package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.order.service.OrderServiceImpl;

public class OrderPushMessageJob  extends QuartzJobBean{

	private OrderServiceImpl orderService = AppUtils.getBean(OrderServiceImpl.class);

	private static Logger logger = LoggerFactory.getLogger(OrderPushMessageJob.class);
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("订单 push 消息job启动。。。");
		orderService.pushMessage();
		logger.info("订单 push 消息job完成");
		logger.info("订单 push PMS2 消息job启动。。。");
		orderService.pushMessagePms2Order();
		logger.info("订单 push PMS2 消息job完成");
	}

}
