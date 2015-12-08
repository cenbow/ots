package com.mk.ots.job;


import com.mk.framework.AppUtils;
import com.mk.ots.hotel.service.HotelService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 每天00:05执行一次，缓存酒店月销量纪录  显示近30天内的销量数据(不包含current date)
 * @author yubin
 *
 */
public class refreshESJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(refreshESJob.class);
	private HotelService hotelService = AppUtils.getBean(HotelService.class);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("refreshESJob::start");
		try {
			hotelService.batchUpdateEsIndexer();

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("refreshESJob::end");
	}
}
