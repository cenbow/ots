package com.mk.ots.promo.dao.impl;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.dao.IBPromotionPriceDao;
import com.mk.ots.promo.model.BPromotionPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BPromotionPriceDAO extends MyBatisDaoImpl<BPromotionPrice, Long> implements IBPromotionPriceDao {
	final Logger logger = LoggerFactory.getLogger(BPromotionPriceDAO.class);
	
	@Override
	public List<BPromotionPrice> findBPromotionPriceByOrderAndPType(long orderid, PromotionTypeEnum type){
		Map<String,Object> param = Maps.newHashMap();
		param.put("orderid", orderid);
		param.put("type", type);
		return this.find("findBPromotionPriceByOrderAndPType", param);
	}
	
	@Override
	public BPromotionPrice findPromotionPricesByOrderIdAndPromoId(Long orderid, Long promotionid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("promotionid", promotionid);
		param.put("orderid", orderid);
		return this.findOne("findPromotionPricesByOrderIdAndPromoId", param);
	}
	
	@Override
	public void saveOrUpdate(BPromotionPrice bPromotionPrice) {
		if(bPromotionPrice.getId()!=null){
			this.update(bPromotionPrice);
		}else{
			this.insert(bPromotionPrice);
		}
	}
	
	@Override
	public void deleteByOrderidAndPromotionId(Long promotionid, Long otaorderid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("promotionid", promotionid);
		param.put("otaorderid", otaorderid);
		this.delete("deleteByOrderidAndPromotionId", param);
	}
	
	@Override
	public List<BPromotionPrice> findPromotionPricesByOrderId(Long orderid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("orderid", orderid);
		return this.find("findPromotionPricesByOrderId", param);
	}

	@Override
	public List<BPromotionPrice> queryBPromotionPricesByPromId(Long promotionid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("promotionid", promotionid);

		return this.find("queryBPromotionPricesByPromId", param);
	}

	@Override
	public List<BPromotionPrice> findUserPromotionPricesByOrderId(Long orderid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("orderid", orderid);
		return this.find("findUserPromotionPricesByOrderId", param);
	}
}
