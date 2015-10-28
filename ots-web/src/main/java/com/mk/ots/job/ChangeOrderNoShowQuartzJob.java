package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.ots.bill.dao.BillOrderDAO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * 修改订单状态为noshow
 * @author Administrator
 *
 */
public class ChangeOrderNoShowQuartzJob  extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(ChangeOrderNoShowQuartzJob.class);
    @Override
    protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
        logger.info("每天更新订单状态为520，job");
        BillOrderDAO billOrderDAO = AppUtils.getBean(BillOrderDAO.class);
        billOrderDAO.changeOrderStatusNoshow(new Date());
    }
}