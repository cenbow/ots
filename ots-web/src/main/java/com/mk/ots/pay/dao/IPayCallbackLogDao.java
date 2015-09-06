package com.mk.ots.pay.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.pay.model.PayCallbackLog;

public interface IPayCallbackLogDao extends BaseDao<PayCallbackLog, Long> {

	public Long insertPayCallbackLog(PayCallbackLog payCallbackLog);
}
