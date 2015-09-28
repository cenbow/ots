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
import com.mk.ots.pay.job.SendMsgJob;

public class PaySendMsgJob extends QuartzJobBean  {
	
	private static Logger logger = LoggerFactory.getLogger(PaySendMsgJob.class);
	
	private PayJob sendMsgJob = AppUtils.getBean(SendMsgJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		logger.info("给酒店老板发送短信Job启动...");
		
		sendMsgJob.doJob(PayTaskTypeEnum.SENDMSG2LANDLORD, PayTaskStatusEnum.INIT, PayTaskStatusEnum.FINISH);
		
		logger.info("给酒店老板发送短信Job完毕.");
	}

}
