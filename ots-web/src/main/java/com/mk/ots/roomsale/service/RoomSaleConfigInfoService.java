package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;

import java.util.List;

public interface RoomSaleConfigInfoService {
    /**
     *
     * @param saleTypeId
     * @return
     */
    List<TRoomSaleConfigInfo> queryListBySaleTypeId(int saleTypeId,int start,int limit);
}
