package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.bean.TWeekendRate;
import com.mk.ots.hotel.bean.TWeekendRateExample;

public interface TWeekendRateMapper {
    int countByExample(TWeekendRateExample example);

    int deleteByExample(TWeekendRateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TWeekendRate record);

    int insertSelective(TWeekendRate record);

    List<TWeekendRate> selectByExample(TWeekendRateExample example);

    TWeekendRate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TWeekendRate record, @Param("example") TWeekendRateExample example);

    int updateByExample(@Param("record") TWeekendRate record, @Param("example") TWeekendRateExample example);

    int updateByPrimaryKeySelective(TWeekendRate record);

    int updateByPrimaryKey(TWeekendRate record);
}