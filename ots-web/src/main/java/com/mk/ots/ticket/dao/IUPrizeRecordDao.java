package com.mk.ots.ticket.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.BPrizeInfo;
import com.mk.ots.ticket.model.UPrizeRecord;

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
}
