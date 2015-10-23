package com.mk.ots.roomsale.service.impl;

import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.mapper.RoomSaleConfigMapper;
import com.mk.ots.mapper.RoomSaleMapper;
import com.mk.ots.roomsale.model.RoomPromoDto;
import com.mk.ots.roomsale.model.RoomSaleToIndexDto;
import com.mk.ots.roomsale.model.TRoomSale;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.service.RoomSaleService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	@Autowired
	private RoomSaleConfigMapper roomSaleConfigMapper;

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

	public Map<String, Object> queryRoomPromoByType(String roomTypeId) throws Exception {
		try {
			List<Map<String, Object>> saleRoomList = roomSaleMapper.queryRoomPromoByType(roomTypeId);
			return saleRoomList.get(0);
		} catch (Exception ex) {
			logger.error(String.format("failed to queryRoomPromoByType %s", roomTypeId), ex);
			throw new Exception(String.format("failed to queryRoomPromoByType %s", roomTypeId), ex);
		}
	}

	@Override
	public List<RoomPromoDto> queryRoomPromoByHotel(TRoomSaleConfig bean){
		List<TRoomSaleConfig> roomSaleConfig = roomSaleConfigMapper.getRoomSaleByParams(bean);
		List<RoomPromoDto> roomPromoDtoList = new ArrayList<RoomPromoDto>();
		for (TRoomSaleConfig rooms:roomSaleConfig){
			RoomPromoDto roomPromo=new RoomPromoDto();
			roomPromo.setRoomId(rooms.getRoomId());
			roomPromo.setRoomTypeId(rooms.getSaleRoomTypeId());
			roomPromo.setSaleName(rooms.getSaleName());
			roomPromo.setStartTime(rooms.getStartTime());
			roomPromo.setEndTime(rooms.getEndTime());
			roomPromo.setNameFontColor(rooms.getFontColor());
			roomPromo.setTypeDesc(rooms.getDescription());
			roomPromo.setStartDate(rooms.getStartDate());
			roomPromo.setEndDate(rooms.getEndDate());
			roomPromo.setPromoType(rooms.getPromoType());
			roomPromo.setPromoValue(rooms.getSaleTypeValue());
			roomPromo.setPromoLabel(rooms.getSaleLabel());
			roomPromoDtoList.add(roomPromo);
		}
		return roomPromoDtoList;
	}


	@Override
	public List<Map<String, Object>> queryRoomPromoInfoByHotel(String hotelId) throws Exception {
		try {
			List<Map<String, Object>> saleRoomList = roomSaleMapper.queryRoomPromoInfoByHotel(hotelId);
			return saleRoomList;
		} catch (Exception ex) {
			logger.error(String.format("failed to queryRoomPromoInfoByHotel %s", hotelId), ex);
			throw new Exception(String.format("failed to queryRoomPromoInfoByHotel %s", hotelId), ex);
		}
	}

	@Override
	public Map<String, Object> queryRoomPromoInfo() throws Exception {
		try {
			Map<String, Object>saleRoomList = roomSaleMapper.queryRoomPromoInfo();
			return saleRoomList;
		} catch (Exception ex) {
			logger.error(String.format("failed to queryRoomPromoInfo %s"), ex);
			throw new Exception(String.format("failed to queryRoomPromoInfo %s"), ex);
		}
	}
	public Boolean checkRoomSale(TRoomSaleConfig bean){
		TRoomSaleConfig roomSaleConfig = roomSaleConfigMapper.checkRoomSale(bean);
		if (roomSaleConfig==null||roomSaleConfig.getId()==null){
			return  false;
		}else {
			return  true;
		}
	}
	public List<RoomSaleToIndexDto> getUpdateIndexList(TRoomSaleConfig bean){
		List<TRoomSaleConfig> roomSaleConfig = roomSaleConfigMapper.getRoomSaleByParams(bean);
		List<RoomSaleToIndexDto> roomSaleToIndexList = new ArrayList<RoomSaleToIndexDto>();
		for (TRoomSaleConfig rooms:roomSaleConfig){
			RoomSaleToIndexDto saleIndex=new RoomSaleToIndexDto();
			saleIndex.setPromoType(rooms.getSaleType());
			saleIndex.setPromoPrice(rooms.getSaleValue());
		}
		return roomSaleToIndexList;
	}
}
