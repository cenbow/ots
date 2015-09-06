package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.dao.HomeDAO;

/**
 * 酒店流水
 * @author zzy
 */
public class BillDetailQuartzJob extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		System.err.println(BillDetailQuartzJob.class.getName());
		HomeDAO homeDAO = AppUtils.getBean(HomeDAO.class);
		// 每天7点开始， 计算昨天的 数据
		homeDAO.genBillDetails(null, DateUtils.getCertainDate(-2), DateUtils.getCertainDate(-1));
	}

}
