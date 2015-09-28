package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.search.model.SAreaInfo;

/**
 * SAreaInfo mapper.
 * 
 * @author chuaiqing.
 *
 */
public interface SAreaInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SAreaInfo record);

    int insertSelective(SAreaInfo record);

    SAreaInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SAreaInfo record);

    int updateByPrimaryKey(SAreaInfo record);
    
    List<SAreaInfo> findAll(@Param("citycode") String citycode);
    
}