package com.mk.ots.job;

import com.mk.ots.bill.service.BillOrderService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 构建账单的订单数据 每周一4.10分跑
 * 
 * @author zzy
 *
 */
public class BillPromoOrdersJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BillPromoOrdersJob.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// 获取上个月
		BillOrderService billOrderService = AppUtils.getBean(BillOrderService.class);
		Calendar c = Calendar.getInstance();
		//周一的凌晨跑 所以减去2天到上周的时间
		c.setTime(DateUtils.addDays(new Date(), -2));
		Date[] date = DateUtils.getWeekStartAndEndDate(c);
		String beginTime = DateUtils.formatDateTime(date[0], DateUtils.FORMAT_DATE);
		String endTime = DateUtils.formatDateTime(date[1], DateUtils.FORMAT_DATE);
		logger.info(String.format("BillPromoOrdersJob::genBillOrders::start params beginTime[%s], endTime[%s]",
				beginTime, endTime));
		billOrderService.createBillReport(DateUtils.getDateFromString(beginTime), DateUtils.getDateFromString(endTime));
		Cat.logEvent("OtsJob", "BillPromoOrdersJob.executeInternal", Event.SUCCESS, "");
		logger.info("BillPromoOrdersJob::genBillOrders::end");
	}
}
