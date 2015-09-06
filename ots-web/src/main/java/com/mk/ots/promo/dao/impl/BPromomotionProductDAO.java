package com.mk.ots.promo.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.dao.IBPromotionProductDao;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionProduct;

@Component
public class BPromomotionProductDAO extends MyBatisDaoImpl<BPromotionProduct, Long> implements IBPromotionProductDao {

	@Override
	public BPromotionProduct queryBPromotionProduct(Long productid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("productid", productid);
		return this.findOne("queryBPromotionProduct", param);
	}
	
}
