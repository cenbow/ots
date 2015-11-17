package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleCity;

import java.util.List;

public interface TRoomSaleShowConfigService {
    public List<RoomSaleShowConfigDto> queryRoomSaleShowConfigByParams(RoomSaleShowConfigDto bean);
    public  List<TRoomSaleCity> queryTRoomSaleCity(String cityid);
    public List<RoomSaleShowConfigDto> queryRenderableShows(RoomSaleShowConfigDto bean) throws Exception;
}
