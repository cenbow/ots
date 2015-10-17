package com.mk.ots.room.sale.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.mapper.RoomSaleMapper;
import com.mk.ots.room.sale.model.TRoomSale;
import com.mk.ots.room.sale.service.RoomSaleService;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
@Service
public class RoomSaleServiceImpl implements RoomSaleService {

	@Autowired
	private RoomSaleMapper roomSaleMapper;
	@Autowired
	private HotelService hotelService;

	public void saleBegin() {
		List<TRoomSale> saleRoomList = roomSaleMapper.getSaleRoomListByHotel();
		for (TRoomSale roomSale : saleRoomList) {
			hotelService.readonlyInitPmsHotel(roomSale.getCityId().toString(), roomSale.getHotelId().toString());
		}
	}

	public TRoomSale getOneRoomSale(TRoomSale bean) {
		return roomSaleMapper.getOneRoomSale(bean);
	}

	public List<TRoomSale> queryRoomSale(TRoomSale bean) {
		return roomSaleMapper.queryRoomSale(bean);
	}
	
	public List<String> queryPromoTime() {
		List<TRoomSale> saleRoomList = roomSaleMapper.getSaleRoomListByHotel();
		List<String> promoTime = new ArrayList<String>();
		if (saleRoomList != null) {
			TRoomSale roomSale = saleRoomList.get(0);
			promoTime.add(roomSale.getStartTime());
			promoTime.add(roomSale.getEndTime());
		}

		return promoTime;
	}
}
