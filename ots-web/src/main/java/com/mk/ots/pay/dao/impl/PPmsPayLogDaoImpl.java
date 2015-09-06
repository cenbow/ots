package com.mk.ots.pay.dao.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pay.dao.IPPmsPayLogDao;
import com.mk.ots.pay.model.PPmsPayLog;

/**
 * @author nolan
 *
 */
@Component
public class PPmsPayLogDaoImpl extends MyBatisDaoImpl<PPmsPayLog, Long> implements IPPmsPayLogDao {

	@Override
	public void logpay(Long orderid, BigDecimal lezhu, String reason, Long operatorid){
		PPmsPayLog paylog = new PPmsPayLog();
		paylog.setCreatetime(new Date());
		if(lezhu!=null){
			paylog.setLezhu(lezhu.longValue());
		}
		paylog.setOperator(operatorid);
		paylog.setOrderid(orderid);
		paylog.setReason(reason);
		this.insert(paylog);
	}
}
