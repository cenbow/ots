package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;

import java.util.List;

public interface RoomSaleConfigInfoService {
    /**
     *
     * @param cityid
     * @param saleTypeId
     * @param start
     * @param limit
     * @return
     */
    List<TRoomSaleConfigInfo> queryListBySaleTypeId(String cityid,int saleTypeId,int start,int limit);
    
    public List<TRoomSaleConfigInfo> querybyPromoType(Integer promotype);
}
