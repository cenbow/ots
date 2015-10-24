package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.ots.bill.service.BillOrderService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class weekClearingJob extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(weekClearingJob.class);
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        logger.info("周结算开始");
        BillOrderService billOrderDAO = AppUtils.getBean(BillOrderService.class);
        billOrderDAO.runtWeekClearing(null,null);
        logger.info("周结算完成");

    }

}
