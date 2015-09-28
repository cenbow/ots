package com.mk.ots.ticket.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.BUser500Dao;
import com.mk.ots.ticket.model.BUser500;

@Component
public class BUser500DaoImpl extends MyBatisDaoImpl<BUser500, Long> implements BUser500Dao{

	@Override
	public void updateStatusTByPhone(String phone){
		Map<String,Object> param = Maps.newHashMap();
		param.put("phone", phone);
		super.update("updateStatusTByPhone", param);
	 } 
	
	@Override
	public List<BUser500> findStatusNullList(){
		Map<String,Object> param = Maps.newHashMap();
		return this.find("findStatusNullList", param);
	}
}
