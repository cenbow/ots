package com.mk.ots.pay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PPayInfoTypeEnum;
import com.mk.ots.pay.dao.IPPayInfoDao;
import com.mk.ots.pay.model.PPayInfo;

@Component
public class PPayInfoDaoImpl extends MyBatisDaoImpl<PPayInfo, Long> implements IPPayInfoDao {
	@Override
	public PPayInfo saveOrUpdate(PPayInfo iPPayInfo) {
		if(iPPayInfo.getId()!=null){
			super.update(iPPayInfo);
		}else{
			super.insert(iPPayInfo);
		}
		return iPPayInfo;
	}

	@Override
	public PPayInfo findById(long id) {
		return super.findById(id);
	}

	@Override
	public List<PPayInfo> findByPayId(long payid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("payid", payid);
		return super.find("findByPayId", param);
	}
	
	@Override
	public void deletePayInfoByPayid(long payid){
		super.delete("deleteByPayId",payid);
	}
	
	
	public PPayInfo getPPayInfoByPayid(String payid){
		Map<String, Object> param = Maps.newHashMap();
		param.put("payid", payid);
		List<PPayInfo> list=super.find("findPayInfoByPayId1", param);
		if(list.isEmpty() || list.size()==0){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	public boolean findPPayInfoByPayid(String payid){
		Map<String, Object> param = Maps.newHashMap();
		param.put("payid", payid);
		List<PPayInfo> list=super.find("findPayInfoByPayId1", param);
		if(list.isEmpty() || list.size()==0){
			return false;
		}else{
			return true;
		}
	}
	
	public void aliPayRefundSuccess(String  payids){
	  if(payids!=null && payids.trim().length()!=0 ){
		   Map<String, Object> map = Maps.newHashMap();
		   map.put("payids", payids);
		   this.update("aliPayRefundSuccess", map);
	   }
	}
	
	
	
	
	public PPayInfo getPPayInfoByPayidAndPayOk(long payid){
		logger.info("getPPayInfoByPayidAndPayOk payid:{} ,PPayInfoTypeEnum:{} ", payid,PPayInfoTypeEnum.Z2P.getId().intValue());
		return getPPayInfo(payid, PPayInfoTypeEnum.Z2P);
	}
	
	
	public PPayInfo getPPayInfoByCoupon(long payid){
		logger.info("getPPayInfoByCoupon payid:{} ,PPayInfoTypeEnum:{} ", payid,PPayInfoTypeEnum.Y2P.getId().intValue());
		return getPPayInfo(payid, PPayInfoTypeEnum.Y2P);
	}
	
	
	
	public PPayInfo getPPayInfo(long payid,PPayInfoTypeEnum type){
		Map<String, Object> param = Maps.newHashMap();
		param.put("payid", payid);
		param.put("type", type.getId().intValue());
		List<PPayInfo> list=super.find("findByPayIdAndPayOk", param);
		if(list.isEmpty() || list.size()==0){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	
	public PPayInfo  getPayOk(String  otherno){
		Map<String, Object> param = Maps.newHashMap();
		param.put("otherno", otherno);
		List<PPayInfo> list=super.find("getPayOkPayInfo", param);
		if(list!=null && list.size()!=0 ){
			return list.get(0);
		}else{
			return  null;
		}
	}
	
	
	public PPayInfo getPPayInfoByRefund(long payid){
		return getPPayInfo(payid, PPayInfoTypeEnum.Z2U);
	}
	
	

	public PPayInfo selectByOrderIdAndPayOk(String  orderid){
		Map<String, Object> param = Maps.newHashMap();
		param.put("type", PPayInfoTypeEnum.Z2P.getId().intValue());
		param.put("orderid", orderid);
		List<PPayInfo> list=super.find("selectByOrderIdAndPayOk", param);
		if(list!=null && list.size()!=0 ){
			return list.get(0);
		}else{
			return  null;
		}
	}

	@Override
	public void updatePmsSendIdByPayId(Long payid,Long payinfoid) {
		 if(payid!=null && payinfoid!=null ){
			   Map<String, Object> map = Maps.newHashMap();
//			   map.put("type", PPayInfoTypeEnum.Y2P.getId().intValue());
			   map.put("payid", payid);
			   map.put("pmsSendId", payinfoid);
			   this.update("updatePmsSendIdByPayidAndType", map);
		   }
	}

	@Override
	public void updatePmsSendIdById(Long id, Long pmsSendId) {

		if (id != null && pmsSendId != null) {

			Map<String, Object> map = Maps.newHashMap();
			map.put("id", id);
			map.put("pmsSendId", pmsSendId);
			this.update("updatePmsSendIdByPPayInfoId", map);
		}

	}
	
	public void deleteById(Long id) {
		
		this.delete(id);
	}

}
