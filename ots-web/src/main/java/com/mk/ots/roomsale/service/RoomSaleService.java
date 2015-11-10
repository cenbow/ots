package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.*;

import java.util.List;
import java.util.Map;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
public interface RoomSaleService {
	public void saleBegin();

	public TRoomSale getOneRoomSale(TRoomSale bean);

	public List<TRoomSale> queryRoomSale(TRoomSale bean);

	public List<String> queryPromoTime();

	public Map<String, Object> queryRoomPromoByType(String roomTypeId) throws Exception;

	/**
	 * map contains following fields:
	 * <p>
	 * roomid
	 * <p>
	 * roomtypeid
	 * <p>
	 * salename
	 * <p>
	 * starttime
	 * <p>
	 * endtime
	 * <p>
	 * namefontcolor
	 * <p>
	 * typefontcolor
	 * <p>
	 * typedesc
	 * 
	 * @param hotelId
	 * @return
	 * @throws Exception
	 */
	public List<RoomPromoDto> queryRoomPromoByHotel(TRoomSaleConfig bean);
	public List<RoomPromoDto> queryRoomPromoByHotelNew(TRoomSaleConfig bean);
	public List<Map<String, Object>> queryRoomPromoInfoByHotel(String hotelId) throws Exception;
	public List<Map<String, Object>> queryRoomPromoInfoByHotelAndPromoType(String hotelId, Integer promoType) throws Exception;
	public Map<String, Object> queryRoomPromoInfo() throws Exception;
	public Boolean checkRoomSale(TRoomSaleConfig bean);
	public Boolean checkPromoCity(String cityCode);
	public Boolean checkRoomSaleWithOldRoomType(TRoomSaleConfig bean);
	public List<RoomSaleToIndexDto> getUpdateIndexList(TRoomSaleConfig bean);
	public List<Map<String, Object>> queryRoomByHotelAndRoomType(String hotelId, String roomTypeId) throws Exception;
}
