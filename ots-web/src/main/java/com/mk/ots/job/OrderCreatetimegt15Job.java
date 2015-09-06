package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.order.service.OrderServiceImpl;

/**
 * B规则：每分钟执行一次，取上1分钟内，有优惠券的，当前时间大于创建时间15分钟的订单的集合
 * 
 */
public class OrderCreatetimegt15Job  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(OrderCreatetimegt15Job.class);
	private OrderServiceImpl orderServiceImpl = AppUtils.getBean(OrderServiceImpl.class);
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("OrderCreatetimegt15Job:: 查询创建时间大于15分钟订单:: start");
		//规则B 优惠券 createtime>15分钟 --- 订单集合
		orderServiceImpl.selectOrderCreatetimegt15();
		logger.info("OrderCreatetimegt15Job:: 查询创建时间大于15分钟订单:: end");
	}
}
