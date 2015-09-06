package com.mk.ots.promo.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.model.BPromotion;

@Component
public class BPromoDAO extends MyBatisDaoImpl<BPromotion, Long> implements IBPromotionDao {
	
	@Override
	public void saveOrUpdate(BPromotion bPromotion) {
		if(bPromotion.getId()!=null){
			this.update(bPromotion);
		}else{
			this.insert(bPromotion);
		}
	}

	@Override
	public List<BPromotion> findByPromotionType(
			PromotionTypeEnum promotionTypeEnum) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("promotiontype", promotionTypeEnum.getId());
		return this.find("findByPromotionType", param);
	}
	
	@Override
	public List<BPromotion> findByActiveId(Long activeid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("activeid", activeid);
		return this.find("findByActiveId", param);
	}
	
	@Override
	public List<BPromotion> queryAllOrderQikePromotions(Long mid, Long otaorderid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("otaorderid", otaorderid);
		return this.find("queryAllOrderQikePromotions", param);
	}
	
	@Override
	public List<BPromotion> queryYiJiaAndQiKePromotionByOrderId(Long otaorderid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("otaorderid", otaorderid);
		return this.find("queryYiJiaAndQiKeAndLiJianPromotionByOrderId", param);
	}
	
}
