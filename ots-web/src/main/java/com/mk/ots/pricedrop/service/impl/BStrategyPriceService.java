package com.mk.ots.pricedrop.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mk.ots.pricedrop.service.IBStrategyPriceService;
import com.mybatis.model.BStrategyPrice;

@Service
public class BStrategyPriceService implements IBStrategyPriceService {

	@Override
	public void insert(BStrategyPrice bStrategyPrice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BStrategyPrice> findBStrategyPricesByHotelId(Long hotelid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BStrategyPrice> findAllBStrategyPrices() {
		// TODO Auto-generated method stub
		return null;
	}

}
