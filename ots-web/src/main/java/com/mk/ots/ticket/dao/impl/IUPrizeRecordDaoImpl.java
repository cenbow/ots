package com.mk.ots.ticket.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.IUPrizeRecordDao;
import com.mk.ots.ticket.model.BPrizeInfo;
import com.mk.ots.ticket.model.UPrizeRecord;
@Component
public class IUPrizeRecordDaoImpl extends MyBatisDaoImpl<UPrizeRecord, Long> implements IUPrizeRecordDao{

	@Override
	public long selectCountByMidAndActiveIdAndOstypeAndTime(long mid,
			long activeid, List<String> ostypes, String time) {
		// TODO Auto-generated method stub
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("activeid", activeid);
		newHashMap.put("ostype", ostypes);
		newHashMap.put("createtime", time);
		return this.count("selectCountByMidAndActiveIdAndOstypeAndTime", newHashMap);
	}

	@Override
	public void saveOrUpdate(UPrizeRecord uPrizeRecord) {
		if(uPrizeRecord.getId()!=null){
			 this.update(uPrizeRecord);
		 }else{
			 this.insert(uPrizeRecord);
		 }
		
	}

	@Override
	public long findMaterialCountByMidAndAct(long mid, long activeid,long type) {
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("activeid", activeid);
		newHashMap.put("type", type);
		return this.count("findMaterialCountByMidAndAct", newHashMap);
	}
	 public List<UPrizeRecord> queryMyHistoryIsOrNotMiKePrize(long mid ,long activeid,int type,boolean isMiKe){
		 Map<String, Object> newHashMap = Maps.newHashMap();
		 newHashMap.put("mid", mid);
		 newHashMap.put("activeid", activeid);
		 newHashMap.put("type", type);
		 if (isMiKe) {
			 return this.find("queryMyHistoryMiKePrize", newHashMap);
		}else {
			 return this.find("queryMyHistoryNotMiKePrize", newHashMap);
		}
		
	 }
}
