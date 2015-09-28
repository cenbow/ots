package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.search.model.SSubway;

/**
 * SSubway mapper.
 * 
 * @author chuaiqing.
 *
 */
public interface SSubwayMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SSubway record);

    int insertSelective(SSubway record);

    SSubway selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SSubway record);

    int updateByPrimaryKey(SSubway record);
    
    List<SSubway> findAll(@Param("citycode") String citycode);
}