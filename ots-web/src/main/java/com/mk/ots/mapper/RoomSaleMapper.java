package com.mk.ots.mapper;

<<<<<<< HEAD
import java.util.List;
import java.util.Map;

import com.mk.ots.room.sale.model.TRoomSale;
=======
import com.mk.ots.roomsale.model.TRoomSale;
>>>>>>> deeaa0c6aeb48fad29abcabc3f1fa1864756d5e3

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
