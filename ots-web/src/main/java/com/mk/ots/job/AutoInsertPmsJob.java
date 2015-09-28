package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.order.service.AutoPmsWorkerServiceImpl;


public class AutoInsertPmsJob extends QuartzJobBean {

	private static Logger logger = LoggerFactory.getLogger(AutoInsertPmsJob.class);
	private AutoPmsWorkerServiceImpl testService = AppUtils.getBean(AutoPmsWorkerServiceImpl.class);

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		this.logger.info("AutoInsertPmsJob.run");
		testService.changePmsRoomOrderStatusJob();
		this.logger.info("AutoInsertPmsJob.end");
	}
}