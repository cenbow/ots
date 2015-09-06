package com.mk.ots.pay.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pay.dao.IPayCallbackLogDao;
import com.mk.ots.pay.model.PayCallbackLog;

@Component
public class PayCallbackLogDao extends MyBatisDaoImpl<PayCallbackLog, Long> implements IPayCallbackLogDao {

	@Override
	public Long insertPayCallbackLog(PayCallbackLog payCallbackLog) {
		
		return super.insert(payCallbackLog);
	}

}
