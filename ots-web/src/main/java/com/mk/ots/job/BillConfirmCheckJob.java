package com.mk.ots.job;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.common.utils.DateUtils;

/**
 * 构建账单的数据
 * 每月
 * @author zzy
 *
 */
public class BillConfirmCheckJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BillConfirmCheckJob.class);
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		BillOrderDAO billOrderDAO = AppUtils.getBean(BillOrderDAO.class);
		String theMonth = DateUtils.getYearMonth(0);
		
		logger.info("BillConfirmCheckJob::genBillConfirmChecks::{}", theMonth);
		String begintime = DateUtils.getMonthFirstDay(theMonth);
		billOrderDAO.genBillConfirmChecks(DateUtils.getDateFromString(begintime), null, null);
		logger.info("BillConfirmCheckJob::genBillConfirmChecks::end");
	}
	
	public static void main(String[] args) {
//		String theMonth = DateUtils.getYearMonth(0);
//		String monthFirstDay = DateUtils.getMonthFirstDay(theMonth);
//		System.out.println(monthFirstDay);
//		String monthLastDay = DateUtils.getMonthLastDay(theMonth);
//		System.out.println(monthLastDay);
		Date beginTime = new Date();
		String startTime = DateUtils.getStringFromDate(beginTime, DateUtils.FORMAT_DATE);
		System.out.println("beginTime"+startTime);
		String endTime = DateUtils.getDateAdded(1, startTime);
		System.out.println("endTime"+endTime);
		String theMonthFirstDay = DateUtils.getMonthFirstDay(DateUtils.getDatetime(beginTime));
		System.out.println("theMonthFirstDay"+theMonthFirstDay);
		String theLastMonthFirstDay = DateUtils.getMonthFirstDay(DateUtils.getDateAdded(-1, theMonthFirstDay)); //下个月1号的时间
		System.out.println("theLastMonthFirstDay"+theLastMonthFirstDay);
	}

}
