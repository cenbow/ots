package com.mk.ots.promo.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.model.BPromotion;

public interface IBPromotionDao extends BaseDao<BPromotion, Long>{

	
	public abstract List<BPromotion> findByPromotionType(PromotionTypeEnum promotionTypeEnum);

	public abstract void saveOrUpdate(BPromotion bPromotion);

	public abstract List<BPromotion> findByActiveId(Long activeid);

	public abstract List<BPromotion> queryAllOrderQikePromotions(Long mid, Long otaorderid);

	public abstract List<BPromotion> queryYiJiaAndQiKePromotionByOrderId(Long otaorderid);
}
