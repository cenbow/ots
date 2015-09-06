package com.mk.ots.appstatus.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.appstatus.dao.IAppStatusDao;
import com.mk.ots.appstatus.model.AppStatus;

@Component
public class AppStatusDao extends MyBatisDaoImpl<AppStatus, Long> implements IAppStatusDao {

	@Override
	public List<AppStatus> findByMid(Long mid){
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid", mid);
		return this.find("findByMid", param);
	}
}
