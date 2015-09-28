package com.mk.ots.pay.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pay.dao.IPayStatusErrorDao;
import com.mk.ots.pay.model.PPayStatusErrorOrder;

@Component
public class PayStatusErrorDao extends MyBatisDaoImpl<PPayStatusErrorOrder, Long> implements IPayStatusErrorDao   {

	@Override
	public Long insertErrorOrder(PPayStatusErrorOrder errorOrder) {
		
		return super.insert(errorOrder);
	}

	@Override
	public int deleteErrorOrder(Long orderId) {

		return super.delete("delete", orderId);
	}

}
