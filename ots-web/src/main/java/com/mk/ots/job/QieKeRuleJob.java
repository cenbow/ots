package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.ots.order.service.QiekeRuleService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * 构建账单的订单数据
 * 每天
 * @author zzy
 *
 */
public class QieKeRuleJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(QieKeRuleJob.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("QieKeRuleJob begin");
		QiekeRuleService qiekeRuleService = AppUtils.getBean(QiekeRuleService.class);
		qiekeRuleService.updateTopInvalidReason(new Date());
		logger.info("QieKeRuleJob end");
	}
}
