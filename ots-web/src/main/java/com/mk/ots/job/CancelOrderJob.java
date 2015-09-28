package com.mk.ots.job;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import redis.clients.jedis.Jedis;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.OtaOrderFlagEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.SysConfig;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderServiceImpl;

public class CancelOrderJob extends QuartzJobBean {

	private static Logger logger = LoggerFactory.getLogger(CancelOrderJob.class);
	@Autowired
	private OtsCacheManager manager;
	@Autowired
	private OrderBusinessLogService orderBusinessLogService;
	
	private static final long CANCEL_ORDER_MAX_TIME_SPAN = 15 * 60 * 1000L;
	private static final long CANCEL_ORDER_TIME_20MIN = 20 * 60 * 1000L;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// 缓存中取得订单的对象，通过订单id和创建时间判断是否超过15分钟
		CancelOrderJob.logger.info("OTSMessage::CancelOrderJob:1分钟启动调度一次开始。");
		// insert into sy_config (skey, svalue, stype) VALUES ('CANCEL_ORDER_FIND_DB', 'true', 'mikeweb'); 
		String cancelOrderFindDb = SysConfig.getInstance().getSysValueByKey("CANCEL_ORDER_FIND_DB");
		if (this.manager == null) {
			this.manager = AppUtils.getBean(OtsCacheManager.class);
		}
		if (this.orderBusinessLogService == null) {
			this.orderBusinessLogService = AppUtils.getBean(OrderBusinessLogService.class);
		}

		Jedis jedis = this.manager.getNewJedis();
		try {
			Map<String, String> orderMap = jedis.hgetAll("orderJobList");
			Date nowDateTime = new Date();
			CancelOrderJob.logger.info("CancelOrderJob::15分钟取消订单redis中订单数:{}", orderMap.size());
			OrderServiceImpl orderService = AppUtils.getBean(OrderServiceImpl.class);
			// 查数据库里错过取消的订单
			if ("true".equals(cancelOrderFindDb)) {
				orderMap.putAll(orderService.findNeedCancelOrders());
			}
			StringBuilder orderKeys = new StringBuilder();
			Iterator it = orderMap.keySet().iterator();
			while (it.hasNext()) {
				String orderid = (String) it.next();
				String createTime = orderMap.get(orderid);

				if (orderid != null) {
					Date orderCreateTime = DateUtils.getDateFromString(createTime);
					long temp = nowDateTime.getTime() - orderCreateTime.getTime(); // 相差毫秒数
					// 当前和订单创建时间差大于15分钟
					if (temp >= CancelOrderJob.CANCEL_ORDER_MAX_TIME_SPAN) {
						Cat.logEvent("OtsJob", "CancelOrderJob.executeInternal", Event.SUCCESS, "");
						CancelOrderJob.logger.info("OTSMessage::CancelOrderJob:orderid:{}开始",orderid);
						OtaOrder order = orderService.findOtaOrderById(Long.parseLong(orderid));
						if ((order != null) && (order.getOrderStatus() < OtaOrderStatusEnum.Confirm.getId())) {
							CancelOrderJob.logger.info("OTSMessage::CancelOrderJob:orderid:{},orderstatus:{}",orderid, order.getOrderStatus());
							try {
								CancelOrderJob.logger.info("OTSMessage::CancelOrderJob: 15分钟已到取消{}订单---start", order.get("id").toString());
								orderService.cancelOrderError(order,"1");
								CancelOrderJob.logger.info("OTSMessage::CancelOrderJob:15分钟已到取消{}订单---end", order.get("id").toString());
								orderKeys.append(orderid).append(",");
							} catch (Exception e) {
								orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELBYSYSTEM.getId(), "", "系统调度:订单15分钟后取消", "订单id:"+orderid+"异常:"+e.getLocalizedMessage());
								if (temp >= CancelOrderJob.CANCEL_ORDER_TIME_20MIN) {
									// 取消OTS订单
									orderService.cancelOrderError(order,"2");
									orderKeys.append(orderid).append(",");
									orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELBYSYSTEM.getId(), "", "系统调度:订单20分钟后强制取消!", "异常:"+e.getLocalizedMessage());
								}
							}
						} else {
							CancelOrderJob.logger.info("CancelOrderJob:jedis.hdel:orderid:{}",orderid);
                            jedis.hdel("orderJobList", orderid);
						}
					}
				}
			}
			if(orderKeys.length() > 0){
				logger.info("OTSMessage::CancelOrderJob: orderJobList:{}", orderKeys.substring(0, orderKeys.length() - 1));
				jedis.hdel("orderJobList",orderKeys.substring(0, orderKeys.length() - 1).split(","));
				orderKeys = null;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			jedis.close();
		}
		CancelOrderJob.logger.info("OTSMessage::CancelOrderJob:1分钟启动调度一次成功结束。");
	}

}
