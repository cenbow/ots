package com.mk.ots.system.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.system.dao.ISyConfigDao;
import com.mk.ots.system.model.SyConfig;

/**
 * 
 * @author nolan
 *
 */
@Component
public class SyConfigDao extends MyBatisDaoImpl<SyConfig, Long>implements ISyConfigDao {

	@Override
	public void updateByKeyAndType(String key, String type, String value){
		Map<String,Object> param = Maps.newHashMap();
		param.put("key", key);
		param.put("type", type);
		param.put("value", value);
		this.findOne("updateByKeyAndType", param);
	}
	
	@Override
	public String findByKeyAndType(String key, String type){
		Map<String,Object> param = Maps.newHashMap();
		param.put("key", key);
		param.put("type", type);
		SyConfig config = this.findOne("findByKeyAndType", param);
		return config!=null ? config.getSvalue() : "";
	}
}
