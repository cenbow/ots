package com.mk.ots.mapper;

import com.mk.ots.hotel.model.TBaseprice;


public interface TBasepriceMapper {
    
    TBaseprice selectByPrimaryKey(Long id);
    
    TBaseprice findByRoomtypeId(Long roomtypeid);
}