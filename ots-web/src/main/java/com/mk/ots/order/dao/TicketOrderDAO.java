package com.mk.ots.order.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.order.model.BTicketOrder;

/**
 * otaorder dao
 * 
 * @author zzy
 *
 */
@Repository
@Component
public class TicketOrderDAO extends MyBatisDaoImpl<BTicketOrder, Long>  {
	public BTicketOrder getWaitPay(String orderid) {
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("id", orderid);
		map.put("paystatus", PayStatusEnum.waitPay.getId());
		return findOne("getWaitPay", map);
	}
}
