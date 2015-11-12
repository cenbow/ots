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
	/**
	 * 根据手机，活动，设备号，检查该手机号是否领取过奖品
	 * @param phone
	 * @param activeid
	 * @param ostype
	 * @param date
	 * @return
	 */
	public boolean checkReceivePrizeByPhone(String phone,Long activeid,String ostype,String date);
	public UPrizeRecord findUPrizeRecordById(Long id);
	public void updatePrizeRecordByRecordId(UPrizeRecord prizeRecord);
	/**
	 * 根据流水号，插入手机，更新奖品状态
	 * @param prizerecordid
	 * @param phone
	 * @param receivestate
	 */
	public void  updatePhoneByRecordId(Long prizerecordid,String phone,Integer receivestate);
	/**
	 * 获取领取奖品记录
	 * @param usermark
	 * @param activeid
	 * @return
	 */
	public List<UPrizeRecord> queryMyHistoryPrizeByUserMark(String usermark,long activeid,Integer state,String date);
	public long checkLuckChanceByUserMark(String usermark,Long activeid);
	/**
	 * 获取可以绑定的奖品
	 * @param usermark
	 * @param phone
	 * @param activeid
	 * @param date
	 * @param unget
	 * @param geted
	 * @return
	 */
	public List<UPrizeRecord>findEffectivePrizeByPhone(String phone,Long activeid,String ostype,String date,Integer geted);
}
