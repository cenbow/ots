package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.search.model.SLandMark;

/**
 * s_landmark mapper.
 * 
 * @author chuaiqing.
 *
 */
public interface SLandMarkMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SLandMark record);

    int insertSelective(SLandMark record);

    SLandMark selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SLandMark record);

    int updateByPrimaryKey(SLandMark record);
    
    List<SLandMark> findAll(@Param("citycode") String citycode);
}