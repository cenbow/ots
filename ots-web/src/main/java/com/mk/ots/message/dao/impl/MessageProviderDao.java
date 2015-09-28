package com.mk.ots.message.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.message.dao.IMessageProviderDao;
import com.mk.ots.message.model.MessageProvider;
@Component
public class MessageProviderDao extends MyBatisDaoImpl<MessageProvider, Long> implements IMessageProviderDao {

	@Override
	public List<MessageProvider> queryAllProviders(String ExceptProvider) {
		// TODO Auto-generated method stub
		Map<String, Object> param = Maps.newHashMap();
		param.put("ExceptProvider", ExceptProvider);
		return this.find("queryAllMessageProviders", param);
	}

}
