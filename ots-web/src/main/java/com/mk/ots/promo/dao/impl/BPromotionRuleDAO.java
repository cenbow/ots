package com.mk.ots.promo.dao.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.promo.dao.IBPromotionRuleDAO;
import com.mk.ots.promo.model.BPromotionRule;

@Component
public class BPromotionRuleDAO extends MyBatisDaoImpl<BPromotionRule, Long> implements IBPromotionRuleDAO {
	final Logger logger = LoggerFactory.getLogger(BPromotionPriceDAO.class);

	@Override
	public BPromotionRule findRuleByPromotionId(Long promotionid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("promotionid", promotionid);
		return this.findOne("findRuleByPromotionId", param);
		
	}
	
}
