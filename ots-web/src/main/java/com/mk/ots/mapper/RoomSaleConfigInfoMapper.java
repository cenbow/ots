package com.mk.ots.mapper;

import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;

import java.util.List;

public interface RoomSaleConfigInfoMapper {
    List<TRoomSaleConfigInfo> queryListBySaleTypeId(int saleTypeId,int start,int limit);
}
