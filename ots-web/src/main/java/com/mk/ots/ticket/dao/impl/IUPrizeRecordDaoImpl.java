package com.mk.ots.ticket.dao.impl;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.IUPrizeRecordDao;
import com.mk.ots.ticket.model.UPrizeRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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
	@Override
	public long findReceivePrizeCountByPhone(String phone, Long activeid,
											 String ostype, String date) {
		// TODO Auto-generated method stub
		Map<String, Object> params = Maps.newHashMap();
		params.put("phone", phone);
		params.put("activeid", activeid);
		params.put("ostype", ostype);
		params.put("createtime", date);
		return  this.count("findReceivePrizeCountByPhone", params);
	}
	@Override
	public UPrizeRecord findUPrizeRecordById(Long id) {
		// TODO Auto-generated method stub
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", id);
		return this.findOne("findUPrizeRecordById", params);
	}
	@Override
	public void updatePhoneByRecordId(Long prizerecordid, String phone,
									  Integer receivestate) {
		// TODO Auto-generated method stub
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("id", prizerecordid);
		newHashMap.put("phone", phone);
		newHashMap.put("receivestate", receivestate);
		this.update("updatePhoneByRecordId", newHashMap);

	}
	@Override
	public List<UPrizeRecord> queryMyHistoryPrizeByUserMark(String usermark,
															long activeid, Integer state, String date) {
		// TODO Auto-generated method stub
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("usermark", usermark);
		newHashMap.put("activeid", activeid);
		newHashMap.put("receivestate", state);
		newHashMap.put("createtime", date);
		return this.find("queryMyHistoryPrizeByUserMark", newHashMap);
	}

	@Override
	public long queryRecodeByUserMark(String usermark,Long activeid) {
		// TODO Auto-generated method stub
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("usermark", usermark);
		newHashMap.put("activeid", activeid);
		return this.count("queryRecodeByUserMark", newHashMap);
	}
	@Override
	public List<UPrizeRecord> findEffectivePrizeByPhone(String phone,
														Long activeid, String ostype, String date, Integer geted) {
		// TODO Auto-generated method stub
		Map<String, Object> params = Maps.newHashMap();
		params.put("phone", phone);
		params.put("activeid", activeid);
		params.put("ostype", ostype);
		params.put("createtime", date);
		params.put("geted", geted);
		return this.find("findEffectivePrizeByPhone", params);
	}
}
