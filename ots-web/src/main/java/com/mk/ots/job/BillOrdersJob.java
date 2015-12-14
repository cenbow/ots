package com.mk.ots.job;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
public class BillOrdersJob  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BillOrdersJob.class);
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// 获取上个月
		BillOrderDAO billOrderDAO = AppUtils.getBean(BillOrderDAO.class);
		String beginTime = DateUtils.formatDate(DateUtils.addDays(new Date(), -1));
		Set<String> times = new HashSet<>();
		times.add(beginTime);
		for (String time : times) {
			
			Cat.logEvent("OtsJob", "BillOrdersJob.executeInternal", Event.SUCCESS, "");
			
			logger.info("BillOrdersJob::genBillOrders::{}", time);
			billOrderDAO.genBillOrders(DateUtils.getDateFromString(time), null, null);
		}
		logger.info("BillOrdersJob::genBillOrders::end");
	}
}
