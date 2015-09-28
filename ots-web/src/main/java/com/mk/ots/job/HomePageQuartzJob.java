package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.dao.HomeDAO;

/**
 * Hms首页的数据生成
 * @author zzy
 */
public class HomePageQuartzJob extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		HomeDAO homeDAO = AppUtils.getBean(HomeDAO.class);
		
		// 每天1点计算首页数据，调度安排到次日执行即可。
		// 这里读取执行日志表，读取最后一次成功执行时间，然后 计算 当前时间和【最后一次成功执行时间】之间的数据。尚未开发
		homeDAO.genHomeDatas(null, DateUtils.getDate(), -1);
	}
}
