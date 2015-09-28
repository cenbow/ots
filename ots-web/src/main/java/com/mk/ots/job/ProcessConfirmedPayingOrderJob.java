package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.pay.job.PayJob;
import com.mk.ots.pay.job.ProcessPayingConfirmedOrderJob;

public class ProcessConfirmedPayingOrderJob extends QuartzJobBean {
	
	private static Logger logger = LoggerFactory.getLogger(ProcessConfirmedPayingOrderJob.class);
	
	private PayJob processPayingConfirmedOrderJob = AppUtils.getBean(ProcessPayingConfirmedOrderJob.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("处理已确认支付中订单Job启动...");
		
		processPayingConfirmedOrderJob.doJob(null, null, null);
		
		logger.info("处理已确认支付中订单Job完毕.");
	}

}
