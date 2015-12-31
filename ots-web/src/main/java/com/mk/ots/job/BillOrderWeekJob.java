package com.mk.ots.job;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.bill.service.BillOrderDetailService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * 构建账单的订单数据 每周一4.10分跑
 * 
 * @author zzy
 *
 */
public class BillOrderWeekJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BillOrderWeekJob.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// 获取上个月
		BillOrderDetailService billOrderDetailService = AppUtils.getBean(BillOrderDetailService.class);
		Date now = new Date();
		billOrderDetailService.genOrderDetailWeek(now);
		Cat.logEvent("OtsJob", "BillOrderWeekJob.executeInternal", Event.SUCCESS, "");
		logger.info("BillOrderWeekJob::executeInternal::end");
	}
}
