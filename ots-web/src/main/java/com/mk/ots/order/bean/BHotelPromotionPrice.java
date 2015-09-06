package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="b_hotel_promotionprice", pkey="id")
public class BHotelPromotionPrice extends BizModel<BHotelPromotionPrice> {

	private static final long serialVersionUID = -434027864719974886L;
	public static final BHotelPromotionPrice dao = new BHotelPromotionPrice();
}
