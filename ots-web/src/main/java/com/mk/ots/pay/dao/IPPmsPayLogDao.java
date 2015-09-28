package com.mk.ots.pay.dao;

import java.math.BigDecimal;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.pay.model.PPmsPayLog;

/**
 * 下发乐住币日志
 * @author nolan
 *
 */
public interface IPPmsPayLogDao extends BaseDao<PPmsPayLog, Long>{

	/**
	 * 记录管理员下发乐住币日志
	 * @param orderid	订单编码
	 * @param lezhu		乐住币
	 * @param reason	下发原因
	 * @param operatorid	操作员
	 */
	public abstract void logpay(Long orderid, BigDecimal lezhu, String reason, Long operatorid);

}
