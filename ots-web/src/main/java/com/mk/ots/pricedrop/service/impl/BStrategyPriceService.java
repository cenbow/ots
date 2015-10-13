package com.mk.ots.pricedrop.service.impl;

import com.mk.ots.pricedrop.dao.IBStrategyPriceDao;
import com.mk.ots.pricedrop.model.BStrategyPrice;
import com.mk.ots.pricedrop.service.IBStrategyPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BStrategyPriceService implements IBStrategyPriceService {

	@Autowired
	private IBStrategyPriceDao iBStrategyPriceDao;
	@Override
	public void save(BStrategyPrice bStrategyPrice) {
		// TODO Auto-generated method stub
		iBStrategyPriceDao.save(bStrategyPrice);
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

	@Override
	public int deleteByPrimaryKey(Long id) {
		return 0;
	}

	@Override
	public List<BStrategyPrice> findBStrategyPricesByHotelId(List<Long> hotelid) {
		return null;
	}

	@Override
	public BStrategyPrice findByHotelidAndRoomtype(Long hotelid, Long roomtype) {
		return null;
	}
}
