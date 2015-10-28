package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.TRoomSaleConfigForPms;
import com.mk.ots.roomsale.model.TRoomSaleForPms;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
public interface RoomSaleForPmsService {
	public Boolean updateTRoomSaleConfig(TRoomSaleConfigForPms bean);
	public TRoomSaleForPms getHotelRoomSale(TRoomSaleConfigForPms bean);
}
