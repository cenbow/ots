package com.mk.ots.ticket.service.parse;

import java.math.BigDecimal;

import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.ticket.model.UTicket;

/**
 * 
 * 立减优惠券保存格式
 * 
 * <root> 
 *
 *  </root>
 * 
 * @author shellingford
 * @version 2015年1月29日
 */
public class YiJiaTicket extends ITicketParse{
	
	private BPromotionPrice promoPrice;


	@Override
	public void init(UTicket uTicket, BPromotion promotion) {
		this.promotion = promotion;
	}

	@Override
	public void parse(OtaOrder otaOrder) {
		super.parse(otaOrder);
		OrderService orderService = AppUtils.getBean(OrderService.class);
		promoPrice = orderService.getPromotionPriceByOrderId(otaOrder.getId(),promotion.getId());
	}

	@Override
	public void clear() {
		super.clear();
		promoPrice =null;
	}

	@Override
	public boolean checkUsable() {
		return true;
	}

	@Override
	public BigDecimal allSubSidy() {
		//线下到付
		if(OrderTypeEnum.PT.equals(orderType)){
			return getOfflinePrice();
		}
		return getOnlinePrice();
	}


	@Override
	public BigDecimal getOfflinePrice() {
		if(promoPrice == null){
			return new BigDecimal(0);
		}
		return promoPrice.getOfflineprice();
	}
	
	@Override
	public BigDecimal getOnlinePrice() {
		if(promoPrice == null){
			return new BigDecimal(0);
		}
		return promoPrice.getPrice();
	}
}
