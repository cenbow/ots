package com.mk.ots.order.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.order.bean.OrderBusinessLog;
import com.mk.ots.order.bean.OtaOrder;

@Service
public class OrderBusinessLogService {

	/**
	 * 
	 * @param order
	 * @param businessCode
	 * @param bussinessParams
	 * @param desc
	 * @param exception
	 */
	public void saveLog(OtaOrder order, String orderFlag, String busParms, String desc, String exception) {
		OrderBusinessLog log = new OrderBusinessLog();
		log.set("orderId", order.getId());
		log.set("orderStatus", order.getOrderStatus());
		Long mid = MyTokenUtils.getMidByToken("");
		if (mid != null) {
			log.set("operateUser", String.valueOf(mid));
		}
		log.set("bussinessCode", orderFlag);
		log.set("businessParams", busParms);
		log.set("businessDesc", desc);
		log.set("businessException", exception);
		log.set("createTime", new Date());
		log.save();
	}
	
/**
 * 
 *  
 * @param order
 * @param orderFlag
 * @param busParms
 * @param desc
 * @param exception
 * @param operateName
 */
	public void saveLog1(OtaOrder order, String orderFlag, String busParms, String desc, String exception, String operateName) {
		OrderBusinessLog log = new OrderBusinessLog();
		log.set("orderId", order.getId());
		log.set("orderStatus", order.getOrderStatus());
		Long mid = MyTokenUtils.getMidByToken("");
		if (mid != null) {
			log.set("operateUser", String.valueOf(mid));
		}
		log.set("bussinessCode", orderFlag);
		log.set("businessParams", busParms);
		log.set("businessDesc", desc);
		log.set("businessException", exception);
		log.set("createTime", new Date());
		log.set("operateName", operateName);
		log.save();
	}
}
