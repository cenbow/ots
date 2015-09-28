package com.mk.ots.ticket.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.UActiveShareDao;
import com.mk.ots.ticket.model.UActiveShare;

@Component
public class UActiveShareDaoImpl extends MyBatisDaoImpl<UActiveShare, Long> implements
		UActiveShareDao {

	@Override
	public long countNumByActiveId(Long activeid) {
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("activeid", activeid);
		return this.count("countNumByActiveId", newHashMap);
	}
	
	@Override
	public long countNumByMidAndActiveIdAndTime(long mid,long activeid,String time){
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("activeid", activeid);
		newHashMap.put("createtime", time);
		return this.count("countNumByMidAndActiveIdAndTime", newHashMap);
	}
}
