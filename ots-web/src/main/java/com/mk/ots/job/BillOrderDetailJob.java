package com.mk.ots.job;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.bill.service.BillOrderDetailService;
import com.mk.ots.common.utils.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.ParseException;
import java.util.Date;

/**
 * 构建账单的订单数据 每天1:10分跑
 * 
 * @author zzy
 *
 */
public class BillOrderDetailJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(BillOrderDetailJob.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// 获取上个月
		BillOrderDetailService billOrderDetailService = AppUtils.getBean(BillOrderDetailService.class);
		Date billDate = DateUtils.addDays(new Date(), -1);
		try {
			if(billDate.compareTo(DateUtils.parseDate("2016-01-01", DateUtils.FORMAT_DATE)) <1 ){
				logger.info("genOrderDetail billDate is not 2016");
				Cat.logEvent("OtsJob", "BillOrderDetailJob.executeInternal", Event.SUCCESS, "");
    			return;
            }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		billOrderDetailService.genOrderDetail(billDate);

		Cat.logEvent("OtsJob", "BillOrderDetailJob.executeInternal", Event.SUCCESS, "");
		logger.info("BillOrderDetailJob::executeInternal::end");
	}
}
