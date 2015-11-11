package com.mk.ots.mapper;


import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;
import com.mk.ots.roomsale.model.TRoomSaleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RoomSaleShowConfigMapper {
    public List<TRoomSaleShowConfig> queryTRoomSaleShowConfig(HashMap  map);

    public  List<TRoomSaleCity>    queryTRoomSaleCity(HashMap  map);

}
