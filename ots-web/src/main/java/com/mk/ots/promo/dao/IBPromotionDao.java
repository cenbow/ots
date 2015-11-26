package com.mk.ots.promo.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.model.BPromotion;

import java.util.List;

public interface IBPromotionDao extends BaseDao<BPromotion, Long>{

	
	public abstract List<BPromotion> findByPromotionType(PromotionTypeEnum promotionTypeEnum);

	public abstract void saveOrUpdate(BPromotion bPromotion);

	public abstract List<BPromotion> findByActiveId(Long activeid);

	public abstract List<BPromotion> queryAllOrderQikePromotions(Long mid, Long otaorderid);

	public abstract List<BPromotion> queryYiJiaAndQiKePromotionByOrderId(Long otaorderid);
	
	public abstract List<BPromotion> queryPromotionByOrderId(Long otaorderid);

    List<BPromotion> findFirstOrderPromotionByHardwarecode(String hardwarecode);
	
    public abstract List<BPromotion> queryBPromotionByOrderId(Long otaorderid);
	public List<BPromotion> findByActiveidAndPrizeId(Long activeid, Long prizeid);
	public abstract List<BPromotion> findByActiveIdAndName(Long activeid,String promotionName);
	public List<BPromotion> findByActiveidAndPrizeRecordId(long activeid,long recordid);
	public List<BPromotion> findByActiveidAndPrizeIdList(Long activeid,List<Long> prizeidList);
}
