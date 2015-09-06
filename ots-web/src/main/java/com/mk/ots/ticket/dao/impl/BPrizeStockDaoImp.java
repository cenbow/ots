package com.mk.ots.ticket.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.BPrizeStockDao;
import com.mk.ots.ticket.model.BPrizeStock;
@Component
public class BPrizeStockDaoImp extends MyBatisDaoImpl<BPrizeStock, Long> implements
		BPrizeStockDao {

	@Override
	public BPrizeStock findBPrizeStockByPrizeid(Long prizeid,Long activeid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("prizeid", prizeid);
		param.put("activeid", activeid);
		return findOne("findBPrizeStockByMerchantid", param);
	}

	@Override
	public void saveOrUpdate(BPrizeStock bPrizeStock) {
		if(bPrizeStock.getId()!=null){
			 this.update(bPrizeStock);
		 }else{
			 this.insert(bPrizeStock);
		 }
	}

	@Override
	public BPrizeStock findBPrizeStockById(long bPrizeStockId) {
		// TODO Auto-generated method stub
		Map<String,Object> param = Maps.newHashMap();
		param.put("id", bPrizeStockId);
		return findOne("findBPrizeStockById", param);
	}

	@Override
	public long findStockCountByPrizeIDAndStatus(long prizeid,long status) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("prizeid", prizeid);
		param.put("status", status);
		return count("findStockCountByPrizeID", param);
	}

}
