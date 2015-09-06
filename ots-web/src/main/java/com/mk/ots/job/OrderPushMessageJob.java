package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.order.service.OrderServiceImpl;

public class OrderPushMessageJob  extends QuartzJobBean{
	private OrderServiceImpl orderService = AppUtils.getBean(OrderServiceImpl.class);
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		orderService.pushMessage();
		orderService.pushMessagePms2Order();
	}

}
