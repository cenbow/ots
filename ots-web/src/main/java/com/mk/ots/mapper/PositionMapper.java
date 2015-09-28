package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.search.model.PositionModel;

public interface PositionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PositionModel record);

    int insertSelective(PositionModel record);

    PositionModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PositionModel record);

    int updateByPrimaryKey(PositionModel record);

	List<PositionModel> findByCitycodeAndType(@Param("citycode") String citycode, @Param("ptype") String ptype);
}