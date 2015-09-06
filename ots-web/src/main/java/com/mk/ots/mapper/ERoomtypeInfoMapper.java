package com.mk.ots.mapper;

import com.mk.ots.hotel.model.ERoomtypeInfoModel;

public interface ERoomtypeInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ERoomtypeInfoModel record);

    int insertSelective(ERoomtypeInfoModel record);

    ERoomtypeInfoModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ERoomtypeInfoModel record);

    int updateByPrimaryKeyWithBLOBs(ERoomtypeInfoModel record);

    int updateByPrimaryKey(ERoomtypeInfoModel record);
}