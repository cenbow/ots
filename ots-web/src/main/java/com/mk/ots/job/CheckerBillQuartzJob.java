package com.mk.ots.job;

import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import cn.com.winhoo.mikeweb.ssh.Services;

import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.dao.HomeDAO;
import com.mk.ots.home.util.HomeConst;

/**
 * 酒店流水
 * @author zzy
 *
 */
public class CheckerBillQuartzJob extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		HomeDAO homeDAO = AppUtils.getBean(HomeDAO.class);
		// 取前某分钟时间点，这里取某分钟的范围，为了防止遗漏 建议多覆盖1分钟的数据
		String beginDate = DateUtils.getDatetime(DateUtils.addMinutes(DateUtils.createDate(), -10));
		Map his = homeDAO.getJobHistory(HomeConst.CHECKER_BILL_JOB_HIS);
		if (his != null && his.containsKey("job")) {
			// 2015-04-01 12:21:22
			String lastRunTime = (String) his.get("last_run_time");
			if (lastRunTime.compareTo(beginDate) < 0) {
				beginDate = lastRunTime;
			}
		}
		JdbcTemplate jdbc = (JdbcTemplate)AppUtils.getBean("jdbcTemplate");
		String endTime = DateUtils.getDatetime();
		// 计算某前分钟内的数据
		homeDAO.genCheckerBill(beginDate, endTime);
		// 记录执行日志
		homeDAO.saveJobHistory(HomeConst.CHECKER_BILL_JOB_HIS, endTime, 1);
	}
	 
}
