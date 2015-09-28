package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.pay.job.PayJob;
import com.mk.ots.pay.job.ProcessPayStatusErrorOrderJob;

public class ProcessPayStatusErrorJob extends QuartzJobBean  {
	
	private static Logger logger = LoggerFactory.getLogger(ProcessPayStatusErrorJob.class);
	
	private PayJob processPayStatusErrorOrderJob = AppUtils.getBean(ProcessPayStatusErrorOrderJob.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("处理异常支付状态订单Job启动...");
		
		processPayStatusErrorOrderJob.doJob(PayTaskTypeEnum.PROCESSPAYSTATUSERROR, PayTaskStatusEnum.INIT, PayTaskStatusEnum.FINISH);
		
		logger.info("处理异常支付状态订单Job完毕.");
	}

}
