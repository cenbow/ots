package com.mk.ots.ticket.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.UPrizeRecord;

import java.util.List;

public interface IUPrizeRecordDao extends BaseDao<UPrizeRecord, Long>{

	/**
	 * 
	 * @return
	 */
	public long selectCountByMidAndActiveIdAndOstypeAndTime(long mid, long activeid,List<String> ostypes,String time);
	

	/**
	 * 根据用户id、活动id和奖品类型查找该用户是否领取过该活动实物奖品
	 * @param mid
	 * @param activeid
	 * @param type  奖品类型
	 * @return
	 */
	public long findMaterialCountByMidAndAct(long mid,long activeid,long type);
	public void saveOrUpdate(UPrizeRecord uPrizeRecord);
	
	 /**
     * 获取领取奖品记录
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
	 * @return
	 */
	public long findReceivePrizeCountByPhone(String phone,Long activeid,String ostype,String date);
	public UPrizeRecord findUPrizeRecordById(Long id);
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
	public List<UPrizeRecord> queryMyHistoryPrizeByUserMark(String usermark ,long activeid,Integer state,String date);
	public long queryRecodeByUserMark(String usermark,Long activeid);
	/**
	 * 获取可以绑定的奖品
	 * @param usermark
	 * @param phone
	 * @param activeid
	 * @param unget
	 * @param geted
	 * @return
	 */
	public List<UPrizeRecord>findEffectivePrizeByPhone(String phone,Long activeid,String ostype,String date,Integer geted);

}
