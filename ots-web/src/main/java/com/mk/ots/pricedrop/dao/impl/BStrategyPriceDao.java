package com.mk.ots.pricedrop.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pricedrop.service.IBStrategyPriceService;
import com.mk.ots.promo.model.BPromotion;
import com.mybatis.model.BStrategyPrice;

public class BStrategyPriceDao extends MyBatisDaoImpl<BPromotion, Long> implements IBStrategyPriceService{

	@Override
	public void insert(BStrategyPrice bStrategyPrice) {
		// TODO Auto-generated method stub
		
	}

}
