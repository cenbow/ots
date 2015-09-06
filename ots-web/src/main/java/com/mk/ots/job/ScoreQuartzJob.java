package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.dao.HomeDAO;
import com.mk.ots.score.service.ScoreService;

/**
 * 评分系统批处理
 * @author LYN
 *
 */
public class ScoreQuartzJob extends QuartzJobBean {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		//夜间酒店评分系统批处理，计算出酒店的最终评分
		logger.info("计算酒店最终评分定时任务开始");
		ScoreService scoreService = AppUtils.getBean(ScoreService.class);
		scoreService.score();
		logger.info("计算酒店最终评分定时任务开始");
	}
}
