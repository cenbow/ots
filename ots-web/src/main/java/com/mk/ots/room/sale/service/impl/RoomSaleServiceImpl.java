package com.mk.ots.room.sale.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
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
	private Logger logger = org.slf4j.LoggerFactory.getLogger(RoomSaleServiceImpl.class);

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
		if (saleRoomList != null && saleRoomList.size() > 0) {
			TRoomSale roomSale = saleRoomList.get(0);
			promoTime.add(roomSale.getStartTime());
			promoTime.add(roomSale.getEndTime());
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("no saleRoom has been found in queryPromoTime");
			}
		}

		return promoTime;
	}

	public Map<String, Object> queryRoomPromoTime(String roomTypeId) throws Exception {
		try {
			List<Map<String, Object>> saleRoomList = roomSaleMapper.queryRoomPromoByType(roomTypeId);
			return saleRoomList.get(0);
		} catch (Exception ex) {
			logger.error(String.format("failed to queryRoomPromoByType %s", roomTypeId), ex);
			throw new Exception(String.format("failed to queryRoomPromoByType %s", roomTypeId), ex);
		}
	}
}
