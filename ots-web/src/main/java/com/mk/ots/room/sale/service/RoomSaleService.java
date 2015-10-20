package com.mk.ots.room.sale.service;

import com.mk.ots.room.sale.model.TRoomSale;

import java.util.List;
import java.util.Map;

/**
 * RoomSaleMapper.
 * @author kangxiaolong.
 */
public interface RoomSaleService {
    public void saleBegin();
    public TRoomSale getOneRoomSale(TRoomSale bean);
    public List<TRoomSale> queryRoomSale(TRoomSale bean);
    public List<String> queryPromoTime();
    public Map<String, Object> queryRoomPromoTime(String roomTypeId) throws Exception;
}
