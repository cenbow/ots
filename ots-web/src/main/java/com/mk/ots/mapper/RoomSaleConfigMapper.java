package com.mk.ots.mapper;


import com.mk.ots.roomsale.model.TRoomSaleConfig;

import java.util.List;
import java.util.Map;


public interface RoomSaleConfigMapper {

    public List<TRoomSaleConfig> getRoomTypeByPms(Map<String, Object> map);
    public Integer saveRoomSaleConfig(Map<String, Object> map);
    public Integer delTRoomTypeById(Integer id);
    public Integer updateRoomSaleConfig(TRoomSaleConfig bean);
    public  Integer  updateRoomSaleConfigValid(Map<String, Object> map);
    public  Integer  updateRoomSaleConfigStarted(Map<String, Object> map);
    public List<TRoomSaleConfig> queryRoomSaleConfigByValid(String valid);
    public List<TRoomSaleConfig> queryRoomSaleConfigByStarted(String started);
    public TRoomSaleConfig queryRoomSaleConfigById(Integer id);
    public TRoomSaleConfig queryRoomSaleConfigByType(Integer id);
    public TRoomSaleConfig checkRoomSale(TRoomSaleConfig bean);
    public TRoomSaleConfig checkRoomSaleWithOldRoomType(TRoomSaleConfig bean);
    public List<TRoomSaleConfig> getRoomSaleByParams(TRoomSaleConfig bean);
    public List<TRoomSaleConfig> getRoomSaleByParamsNew(TRoomSaleConfig bean);
    public List<TRoomSaleConfig> queryRoomSaleConfigByParams(Map<String, Object> map);
}
