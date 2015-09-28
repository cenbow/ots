package com.mk.ots.mapper;

import java.util.List;

import com.mk.ots.search.model.PositionTypeModel;

public interface PositionTypeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PositionTypeModel record);

    int insertSelective(PositionTypeModel record);

    PositionTypeModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PositionTypeModel record);

    int updateByPrimaryKey(PositionTypeModel record);

    List<PositionTypeModel> selectAll();

	List<PositionTypeModel> findByCitycode(String citycode);
}