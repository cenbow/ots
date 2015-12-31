package com.mk.ots.mapper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;

public interface RoomSaleShowConfigMapper {
    public List<TRoomSaleShowConfig> queryRoomSaleShowConfigByParams(RoomSaleShowConfigDto bean);
    public  List<TRoomSaleCity>    queryTRoomSaleCity(HashMap  map);
    public List<TRoomSaleShowConfig> queryRenderableShows(RoomSaleShowConfigDto bean);
    public List<TRoomSaleShowConfig> queryRenderableHeaderShows(RoomSaleShowConfigDto bean);
    public List<Map<String,Object>> queryRoomtypeGreetScore(Map<String, Object> parameters);
}
