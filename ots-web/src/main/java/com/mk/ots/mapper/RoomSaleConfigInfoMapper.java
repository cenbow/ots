package com.mk.ots.mapper;

import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;

import java.util.List;
import java.util.Map;

public interface RoomSaleConfigInfoMapper {
    List<TRoomSaleConfigInfo> queryListBySaleTypeId(Map<String,Object> map);
}
