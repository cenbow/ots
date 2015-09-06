package com.mk.ots.log.dao.impl;

import java.util.Date;
import java.util.List;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.log.dao.IBLogOrderDao;
import com.mk.ots.log.model.BLogOrder;

/**
 * @author nolan
 *
 */
public class BLogOrderDao extends MyBatisDaoImpl<BLogOrder, Long>implements IBLogOrderDao{
	
	@Override
	public BLogOrder log(Long orderid, String oldValue, String newValue, String note){
		BLogOrder logOrder = new BLogOrder();
		logOrder.setLogtime(new Date());
		logOrder.setNewstatus(newValue);
		logOrder.setOldstatus(oldValue);
		logOrder.setNote(note);
		this.insert(logOrder);
		return logOrder;
	}
	
	@Override
	public BLogOrder log(Long orderid, String oldValue, String newValue){
		return log(orderid, oldValue, newValue,"");
	}
}
