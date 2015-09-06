package com.mk.ots.ticket.service;

import java.util.Date;

public interface IUGiftRecordService {

	/**
	 * 添加用户参加某一活动礼品抽奖记录
	 * @param mid
	 * @param activeid
	 */
	public void record(Long mid, Long activeid);
	
	/**
	 * 查询当日用户参加某一活动的抽奖次数
	 * @param mid
	 * @param activeid
	 * @return
	 */
	public long getRecordNumToday(Long mid, Long activeid);
	
	/**
	 * 查询用户参加此活动总抽奖次数
	 * @param mid
	 * @param activeid
	 * @return
	 */
	public long getTotalRecordNum(Long mid, Long activeid, Date begintime, Date endtime);
}
