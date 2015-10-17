package com.mk.ots.room.sale.service;

import com.mk.ots.room.sale.model.TRoomSale;

import java.util.List;

/**
 * RoomSaleMapper.
 * @author kangxiaolong.
 */
public interface RoomSaleService {
    public void saleBegin();
    public TRoomSale getOneRoomSale(TRoomSale bean);
    public List<TRoomSale> queryRoomSale(TRoomSale bean);
    public List<String> queryPromoTime();
}
