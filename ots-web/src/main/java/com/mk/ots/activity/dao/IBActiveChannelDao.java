package com.mk.ots.activity.dao;

import com.google.common.base.Optional;
import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.activity.model.BActiveChannel;

public interface IBActiveChannelDao extends BaseDao<BActiveChannel, Long> {

	/**
	 * 查询某一活动的渠道信息
	 * @param activeid
	 * @param channelid
	 * @return
	 */
	public Optional<BActiveChannel> findActiveChannel(Long activeid, Long channelid);
}
