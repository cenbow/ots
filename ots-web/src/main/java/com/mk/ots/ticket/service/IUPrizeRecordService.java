package com.mk.ots.ticket.service;

import java.util.List;

import com.mk.ots.ticket.model.UPrizeRecord;

public interface IUPrizeRecordService {

	/**
	 * 根据用户id，活动id，设备类型，时间获取对象数量(判断用户是否有抽奖机会)
	 * @param mid
	 * @param activeid
	 * @param  ostypes
	 * @param time
	 * @return
	 */
	public long selectCountByMidAndActiveIdAndOstypeAndTime(long mid, long activeid,List<String> ostypes,String time);

	 /**
     * 获取领取不是眯客奖品记录
     * @param mid
     * @param activeid
     * @return
     */
    public List<UPrizeRecord> queryMyHistoryIsOrNotMiKePrize(long mid ,long activeid,int type,boolean isMiKe);
}
