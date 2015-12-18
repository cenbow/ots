package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.ots.order.service.OrderServiceImpl;
import com.mk.ots.remind.service.RemindService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class RemindMessageJob extends QuartzJobBean{

	private RemindService remindService = AppUtils.getBean(RemindService.class);

	private static Logger logger = LoggerFactory.getLogger(RemindMessageJob.class);
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		logger.info("Remind push 消息job启动。。。");
		remindService.pushMessage();
		logger.info("Remind push 消息job完成");
	}

}
