package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;

import java.util.List;
import java.util.Map;

public interface RoomSaleConfigInfoService {
    /**
     *
     * @param saleTypeId
     * @return
     */
    List<TRoomSaleConfigInfo> queryListBySaleTypeId(int saleTypeId,int start,int limit);

	public Map<String, Object> queryRoomPromoByType(String roomTypeId) throws Exception;

	/**
	 * map contains following fields:
	 * <p>
	 * promotype
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
	public List<Map<String, Object>> queryRoomPromoByHotel(String hotelId) throws Exception;
}
