package com.mk.ots.job;

import java.util.HashSet;
import java.util.Set;

import com.mk.ots.bill.service.BillOrderService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.common.utils.DateUtils;

/**
 * 构建账单的订单数据
 * 每天
 * @author zzy
 *
 */
public class BillOrdersDayJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BillOrdersDayJob.class);
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// 获取上个月
		BillOrderService billOrderService = AppUtils.getBean(BillOrderService.class);
		String beginTime = DateUtils.getDateAdded(-1, DateUtils.getDate());
		String endTime = DateUtils.getDateAdded(0, DateUtils.getDate());
		Cat.logEvent("OtsJob", "BillOrdersDayJob.executeInternal", Event.SUCCESS, "");
		billOrderService.genBillOrdersV2(DateUtils.getDateFromString(beginTime), DateUtils.getDateFromString(endTime));
		logger.info("BillOrdersDayJob::genBillOrders::end");
	}
}
