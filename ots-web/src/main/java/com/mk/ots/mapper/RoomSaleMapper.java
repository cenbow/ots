package com.mk.ots.mapper;

import com.mk.ots.roomsale.model.TRoomSale;

import java.util.List;
import java.util.Map;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 *
 */
public interface RoomSaleMapper {
	public List<TRoomSale> getSaleRoomListByHotel();

	public TRoomSale getOneRoomSale(TRoomSale bean);

	public TRoomSale getOneRoomSaleByRoomTypeId(TRoomSale bean);

	public List<TRoomSale> queryRoomSale(TRoomSale bean);

	public List<Map<String, Object>> queryRoomPromoByType(String roomTypeId);

	public List<Map<String, Object>> queryRoomPromoByHotel(String hotelId);

	public List<Map<String, Object>> queryRoomPromoInfoByHotel(String hotelId);

	public Map<String, Object> queryRoomPromoInfo();

	public Map<String, Object> checkPromoCity(String citycode);

	public List<Map<String, Object>> queryRoomPromoInfoByHotelAndPromoType(Map<?, ?> map);

	public List<Map<String, Object>> queryRoomByHotelAndRoomType(Map<String, Object> map);

	public TRoomSale queryRoomSaleByOriginal(TRoomSale bean);

	public void saveRoomSale(Map<String, Object> map);
	
	public List<Map<String, Object>> isRoomSaleExisted(Map<String, Object> parameters);	
}
