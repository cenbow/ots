package com.mk.ots.activity.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.mk.ots.activity.dao.IBActiveChannelDao;
import com.mk.ots.activity.model.BActiveChannel;
import com.mk.ots.activity.service.IBActiveChannelService;

@Service
public class BActiveChannelServiceImpl implements IBActiveChannelService {
	@Autowired
	private IBActiveChannelDao activeChannelDao;

	public Optional<BActiveChannel> findActiveChannel(Long activeid, Long channelid){
	
		return activeChannelDao.findActiveChannel(activeid, channelid);
	}
}
