package com.mk.ots.roomsale.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.roomsale.model.TRoomSale;

/**
 * RoomSaleMapper.
 * @author kangxiaolong.
 */
public interface RoomSaleService {
    public void saleBegin();
    public TRoomSale getOneRoomSale(TRoomSale bean);
    public List<TRoomSale> queryRoomSale(TRoomSale bean);
    public List<String> queryPromoTime();
    public Map<String, Object> queryRoomPromoByType(String roomTypeId) throws Exception;
    public List<Map<String, Object>> queryRoomPromoByHotel(String hotelId) throws Exception;
}
