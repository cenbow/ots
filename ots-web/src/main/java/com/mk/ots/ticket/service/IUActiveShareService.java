package com.mk.ots.ticket.service;

public interface IUActiveShareService {
	/**
	 * 记录用户转发消息
	 * @param mid
	 * @param activeid
	 */
	public void record(Long mid, Long activeid);
	
	/**
	 * 统计此活动的转发次数
	 * @param activeid
	 */
	public long count(Long activeid);
	
	/**
	 * 根据用户id,活动id，分享时间获取记录数
	 * @author zhangyajun
	 * @param mid
	 * @param activeid
	 * @param time
	 * @return
	 */
	public long countNumByMidAndActiveIdAndTime(long mid,long activeid,String time);
	
	
}
