package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.ots.hotel.service.HotelService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created with IntelliJ IDEA.
 * User: jnduan
 * Date: 15/8/7
 * Time: 下午12:19
 */
public class RefreshEsMikePriceJob extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(RefreshEsMikePriceJob.class);
    private HotelService hotelService = AppUtils.getBean(HotelService.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("RefreshEsMikePriceJob:: 全量刷新ES眯客价:: start");
        hotelService.batchUpdateEsMikePrice();
        logger.info("RefreshEsMikePriceJob:: 全量刷新ES眯客价:: end");
    }
}
