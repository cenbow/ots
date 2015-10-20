package com.mk.ots.mapper;


import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;

import java.util.List;
import java.util.Map;

public interface RoomSaleConfigInfoMapper {

    public List<TRoomSaleConfigInfo> queryRoomSaleConfigInfoList();

    public int saveRoomSaleConfigInfo(Map<String, Object> map );

    public TRoomSaleConfigInfo queryRoomSaleConfigById(Integer Id);

    public int updateTRoomSaleConfigInfo(Map<String, Object> map);

}
