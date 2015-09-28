package com.mk.ots.mapper;

import com.mk.ots.hotel.model.ERoomtypeFacilityModel;

public interface ERoomtypeFacilityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ERoomtypeFacilityModel record);

    int insertSelective(ERoomtypeFacilityModel record);

    ERoomtypeFacilityModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ERoomtypeFacilityModel record);

    int updateByPrimaryKey(ERoomtypeFacilityModel record);
}