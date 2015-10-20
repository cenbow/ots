package com.mk.ots.mapper;


import com.mk.ots.roomsale.model.TRoomSaleType;

import java.util.List;
import java.util.Map;

public interface RoomSaleTypeMapper {
    public List<TRoomSaleType> queryRoomSaleType(Map<String, Object> map);
    public int saveRoomSaleType(Map<String, Object> map);
    public int deleteRoomSaleType(int id);
    public int updateRoomSaleType(Map<String, Object> map);
}
