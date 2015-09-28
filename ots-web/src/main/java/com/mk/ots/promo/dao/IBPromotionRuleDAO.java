package com.mk.ots.promo.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.promo.model.BPromotionRule;

public interface IBPromotionRuleDAO extends BaseDao<BPromotionRule, Long>{

	/**
	 * 根据优惠券id获取规则
	 * @param promotionid
	 */
	public abstract BPromotionRule findRuleByPromotionId(Long promotionid);
}
