package com.mk.ots.promo.service;

import java.util.List;

import com.mk.ots.promo.model.BPromotionProduct;

public interface IPromotionProductService {
	/**
	 * 根据产品id获取优惠券信息
	 * @param productid
	 * @return
	 */
	public abstract BPromotionProduct queryBPromotionProduct(Long productid);


}
