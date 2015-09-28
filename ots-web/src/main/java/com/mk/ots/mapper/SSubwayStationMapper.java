package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.search.model.SSubwayStation;

/**
 * SSubwayStation mapper.
 * 
 * @author chuaiqing.
 *
 */
public interface SSubwayStationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SSubwayStation record);

    int insertSelective(SSubwayStation record);

    SSubwayStation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SSubwayStation record);

    int updateByPrimaryKey(SSubwayStation record);
    
    List<SSubwayStation> findStations(@Param("citycode") String citycode, @Param("lineid") String lineid);
}