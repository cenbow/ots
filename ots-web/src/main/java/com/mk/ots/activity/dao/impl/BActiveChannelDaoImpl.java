package com.mk.ots.activity.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.activity.dao.IBActiveChannelDao;
import com.mk.ots.activity.model.BActiveChannel;

@Component
public class BActiveChannelDaoImpl extends MyBatisDaoImpl<BActiveChannel, Long> implements IBActiveChannelDao {

	@Override
	public Optional<BActiveChannel> findActiveChannel(Long activeid,
			Long channelid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("activeid", activeid);
		param.put("channelid", channelid);
		List<BActiveChannel> result = this.find("findActiveChannel", param);
		if(result!=null && result.size()>0){
			return Optional.fromNullable(result.get(0));
		}
		return Optional.absent();
	}
}
