package com.mk.ots.pricedrop.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pricedrop.dao.IBStrategyPriceDao;
import com.mk.ots.pricedrop.model.BStrategyPrice;
@Component
public class BStrategyPriceDao extends MyBatisDaoImpl<BStrategyPrice, Long> implements IBStrategyPriceDao{

	@Override
	public void save(BStrategyPrice bStrategyPrice) {
		// TODO Auto-generated method stub
		this.insert(bStrategyPrice);
	}

	

}
