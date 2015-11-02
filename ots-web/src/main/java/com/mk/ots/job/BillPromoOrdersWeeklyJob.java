package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.bill.service.BillOrderService;
import com.mk.ots.common.utils.DateUtils;

public class BillPromoOrdersWeeklyJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BillPromoOrdersWeeklyJob.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		BillOrderService billOrderService = AppUtils.getBean(BillOrderService.class);
		String beginTime = DateUtils.getDateAdded(-1, DateUtils.getDate());
		String endTime = DateUtils.getDateAdded(0, DateUtils.getDate());
		logger.info(String.format("BillPromoOrdersJob::genBillOrders::start params beginTime[%s], endTime[%s]", beginTime, endTime));
		Cat.logEvent("OtsJob", "BillPromoOrdersJob.executeInternal", Event.SUCCESS, "");
		billOrderService.genBillOrdersV2(DateUtils.getDateFromString(beginTime), DateUtils.getDateFromString(endTime));
		logger.info("BillPromoOrdersJob::genBillOrders::end");
	}

}
