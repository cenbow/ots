package com.mk.ots.order.service;

import org.springframework.stereotype.Service;

import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.enums.OtaFreqTrvEnum;
import com.mk.ots.order.bean.OrderLog;
import com.mk.ots.order.bean.OtaOrder;

@Service
public class OrderLogService {

	public OrderLog findOrderLog(long orderid){
		OrderLog log = OrderLog.dao.findFirst("select * from b_orderlog where orderid=?", orderid);
		if (log == null) {
			log = new OrderLog();
			log.set("orderId", orderid);
		}
		return log; 
	}

	public OrderLog createOrderLog(OtaOrder order){
		Long orderId = order.getId();
		Long spreadUser = order.getSpreadUser();
		OrderLog log = new OrderLog();
		log.set("orderId", orderId);
		if (spreadUser == null) {
			log.set("spreadNote", "非切客订单");
		} else {
			log.set("spreadUser", spreadUser);
		}
		log.save();
		return log;
	}
	
	public OrderLog setQieKe(OtaOrder otaorder, OrderLog log, boolean isSpreadUserNotNull, boolean isCheckOnceToday, boolean isCheckFourTimesMonth) {
		if (!isCheckFourTimesMonth) {
			log.set("spreadNote", "非有效切客订单:一人一个月一个酒店只允许切四次");
			otaorder.set("Invalidreason", OtaFreqTrvEnum.MONTHE_UP4.getId());
			otaorder.saveOrUpdate();
		} else if (!isCheckOnceToday) {
			log.set("spreadNote", "非有效切客订单:一人一天只允许切一次");
			otaorder.set("Invalidreason", OtaFreqTrvEnum.ONEDAY_UP1.getId());
			otaorder.saveOrUpdate();
		} else if (isSpreadUserNotNull) {
			log.set("spreadNote", "切客订单");
		}
		log.saveOrUpdate();
		return log;
	}
}
