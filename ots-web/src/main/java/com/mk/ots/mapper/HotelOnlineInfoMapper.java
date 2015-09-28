package com.mk.ots.mapper;

import com.mk.ots.hotel.model.HotelOnlineInfo;

public interface HotelOnlineInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HotelOnlineInfo record);

    int insertSelective(HotelOnlineInfo record);

    HotelOnlineInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HotelOnlineInfo record);

    int updateByPrimaryKey(HotelOnlineInfo record);
}