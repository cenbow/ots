package com.mk.ots.mapper;

import com.mk.ots.hotel.model.EHotelModel;

public interface EHotelMapper {
    int deleteByPrimaryKey(Long id);

    EHotelModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(EHotelModel record);

	EHotelModel selectByPms(String pms);
}