package com.mk.ots.mapper;


import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;

import java.util.HashMap;
import java.util.List;

public interface RoomSaleShowConfigMapper {
    public List<TRoomSaleShowConfig> queryRoomSaleShowConfigByParams(RoomSaleShowConfigDto bean);
    public  List<TRoomSaleCity>    queryTRoomSaleCity(HashMap  map);
    public List<TRoomSaleShowConfig> queryRenderableShows(RoomSaleShowConfigDto bean);
    public List<TRoomSaleShowConfig> queryRenderableHeaderShows(RoomSaleShowConfigDto bean);
    
}
