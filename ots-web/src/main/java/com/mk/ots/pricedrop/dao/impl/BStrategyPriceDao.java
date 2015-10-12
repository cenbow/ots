package com.mk.ots.pricedrop.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pricedrop.dao.IBStrategyPriceDao;
import com.mk.ots.pricedrop.model.BStrategyPrice;
import com.mk.ots.promo.model.BPromotion;

public class BStrategyPriceDao extends MyBatisDaoImpl<BStrategyPrice, Long> implements IBStrategyPriceDao{

	@Override
	public void save(BStrategyPrice bStrategyPrice) {
		// TODO Auto-generated method stub
		this.insert(bStrategyPrice);
	}

	

}
