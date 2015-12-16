package com.mk.ots.job;


import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.order.service.OrderService;

/**
 * 每天00:05执行一次，缓存酒店月销量纪录  显示近30天内的销量数据(不包含current date)
 * @author yubin
 *
 */
public class LoadMonthlySalesJob  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(LoadMonthlySalesJob.class);
	private OrderService orderService = AppUtils.getBean(OrderService.class);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("LoadMonthlySalesJob::start");
		try {
			orderService.findMonthlySales(null);
			orderService.findPMSMonthlySales(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("LoadMonthlySalesJob::end");
	}
}
