package com.mk.ots.activity.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.activity.dao.IBActiveCDKeyDao;
import com.mk.ots.activity.model.BActiveCDKey;

@Component
public class BActiveCDKeyDaoImpl extends MyBatisDaoImpl<BActiveCDKey, Long>  implements IBActiveCDKeyDao {
	
	@Override
	public Optional<BActiveCDKey> getBActiveCDKey(String code) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("code", code);
		List<BActiveCDKey> list = this.find("getBActiveCDKey", param);
		if(list!=null && list.size()>0){
			return Optional.fromNullable(list.get(0));
		}
		return Optional.absent();
	}

	@Override
	public Long getCDKeyNum(Long activeid, Long channelid, Long promotionid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("activeid", activeid);
		param.put("channelid", channelid);
		param.put("promotionid", promotionid);
		return this.count("getCDKeyNum", param);
	}
	
	@Override
	public Long getNotUseCDKeyNum(Long activeid, Long channelid,Long promotionid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("activeid", activeid);
		param.put("channelid", channelid);
		param.put("promotionid", promotionid);
		return this.count("getNotUseCDKeyNum", param);
	}
	
	@Override
	public List<BActiveCDKey> getCDKeys(String batchNo, Long activeid, Long channelid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("batchno", batchNo);
		param.put("activeid", activeid);
		param.put("channelid", channelid);
		return this.find("getCDKeys", param);
	}
	
	@Override
	public void useBActiveCDKey(String code) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("used", true);
		param.put("usetime", new Date());
		param.put("code", code);
		this.update("useBActiveCDKey", param);
	}
	
	@Override
	public void batchGenCode(String batchNo, Long activeid, Long channelid, Long promotionid, Date expiration, List<String> codeList){
		List<BActiveCDKey> batchList = Lists.newArrayList();
		for(String tmpCode : codeList){
			BActiveCDKey bActiveCDKey = new BActiveCDKey();
			bActiveCDKey.setBatchno(batchNo);
			bActiveCDKey.setActiveid(activeid);
			bActiveCDKey.setChannelid(channelid);
			bActiveCDKey.setPromotionid(promotionid);
			bActiveCDKey.setExpiration(expiration);
			bActiveCDKey.setUsed(false);
			bActiveCDKey.setCode(tmpCode);
			bActiveCDKey.setCreatetime(new Date());
			batchList.add(bActiveCDKey);
		}
		Map<String,Object> param = Maps.newHashMap();
		param.put("itemList", batchList);
		this.find("batchGenCode", param);
	}
	
	@Override
	public boolean existBatchNo(Long activeid, String batchno) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("activeid", activeid);
		param.put("batchno", batchno);
		return this.count("existBatchNo", param)>0;
	}
	
}
