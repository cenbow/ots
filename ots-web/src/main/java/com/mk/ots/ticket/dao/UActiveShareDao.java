package com.mk.ots.ticket.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.UActiveShare;

public interface UActiveShareDao extends BaseDao<UActiveShare, Long> {

	public long countNumByActiveId(Long activeid);
	/**
	 * 根据用户id,活动id，分享时间获取记录数
	 * @param mid
	 * @param activeid
	 * @param time
	 * @return
	 */
	public long countNumByMidAndActiveIdAndTime(long mid,long activeid,String time);
	
}
