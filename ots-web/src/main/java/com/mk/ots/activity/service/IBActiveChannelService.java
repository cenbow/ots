package com.mk.ots.activity.service;

import com.google.common.base.Optional;
import com.mk.ots.activity.model.BActiveChannel;

public interface IBActiveChannelService {

	public Optional<BActiveChannel> findActiveChannel(Long activeid, Long channelid);
}
