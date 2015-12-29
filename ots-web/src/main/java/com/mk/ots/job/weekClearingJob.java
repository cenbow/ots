package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.ots.bill.service.BillOrderService;
import com.mk.ots.common.utils.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

public class weekClearingJob extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(weekClearingJob.class);
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        logger.info("周结算开始");
        BillOrderService billOrderDAO = AppUtils.getBean(BillOrderService.class);
        Date beginTime = DateUtils.getDateFromString(DateUtils.formatDateTime(new Date(), DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE);
        billOrderDAO.runtWeekClearing(beginTime,null);
        logger.info("周结算完成");

    }

}
