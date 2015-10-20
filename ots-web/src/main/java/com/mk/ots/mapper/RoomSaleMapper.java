package com.mk.ots.mapper;

import java.util.List;
import java.util.Map;

import com.mk.ots.room.sale.model.TRoomSale;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 *
 */
public interface RoomSaleMapper {
	public List<TRoomSale> getSaleRoomListByHotel();

	public TRoomSale getOneRoomSale(TRoomSale bean);

	public List<TRoomSale> queryRoomSale(TRoomSale bean);

	public List<Map<String, Object>> queryRoomPromoByType(String roomTypeId);
	
	public List<Map<String, Object>> queryRoomPromoByHotel(String hotelId);
}
