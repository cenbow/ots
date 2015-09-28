package com.mk.ots.promo.service;

import com.mk.ots.member.model.UMember;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;

import java.util.List;

public interface IPromotionPriceService {

	public abstract void bindPromotionPrice(List<Long> promotionidList, UMember member, OtaOrder otaOrder);

	public abstract List<BPromotionPrice> queryBPromotionPrices(Long otaorderid);

	public abstract List<BPromotion> queryAllOrderQikePromotions(Long mid, Long otaorderid);

	public abstract void updateTicketStatus(OtaOrder order);

	public abstract List<BPromotionPrice> queryBPromotionPricesByPromId(Long promotionid);

	/**
	 * 判断订单是否绑定优惠券,其中包括用户优惠券和切客券
	 *
	 * @param otaorderid 订单id
	 * @return 是/否
	 */
	boolean isBindPromotion(Long otaorderid);
}
