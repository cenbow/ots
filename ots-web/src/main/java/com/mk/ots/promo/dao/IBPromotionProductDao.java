package com.mk.ots.promo.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionProduct;

public interface IBPromotionProductDao extends BaseDao<BPromotionProduct, Long>{
	public BPromotionProduct queryBPromotionProduct(Long productid);
}
