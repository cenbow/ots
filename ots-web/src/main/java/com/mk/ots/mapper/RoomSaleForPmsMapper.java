package com.mk.ots.mapper;


import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.model.TRoomSalePms;

import java.util.List;


public interface RoomSaleForPmsMapper {

    public TRoomSaleConfig getRoomTypeByPms(String pms);
    public TRoomSaleConfig getRoomTypeById(Integer id);
    public Integer updateRoomSaleNum(TRoomSaleConfig bean);
    public List<TRoomSaleConfig> queryRoomSaleConfigByParams(TRoomSaleConfig bean);
    public List<TRoomSalePms> getRoomSalePms();
    public List<TRoomSaleConfig> getRoomSaleByPmsHotel(String pmsHotelId);
    public TRoomSaleConfig getConfigInfoById(Integer id);

}
