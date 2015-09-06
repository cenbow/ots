package com.mk.ots.promo.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.model.BPromotionPrice;

import java.util.List;

public interface IBPromotionPriceDao extends BaseDao<BPromotionPrice, Long>{

	public abstract List<BPromotionPrice> findBPromotionPriceByOrderAndPType(long orderid,PromotionTypeEnum type);

	public abstract BPromotionPrice findPromotionPricesByOrderIdAndPromoId(Long orderid, Long promotionid);
	
	public abstract List<BPromotionPrice> findPromotionPricesByOrderId(Long orderId);

	public abstract void saveOrUpdate(BPromotionPrice bPromotionPrice);

	public abstract void deleteByOrderidAndPromotionId(Long promotoinid, Long otaorderid);
	
	public abstract List<BPromotionPrice> queryBPromotionPricesByPromId(Long promotionid);

	/**
	 * 查询订单绑定的用户优惠券
	 *
	 * @param orderid 订单id
	 * @return List<BPromotionPrice>
	 */
	List<BPromotionPrice> findUserPromotionPricesByOrderId(Long orderid);
}
