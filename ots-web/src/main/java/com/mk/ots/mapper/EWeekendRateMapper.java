package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.bean.EWeekendRate;
import com.mk.ots.hotel.bean.EWeekendRateExample;

public interface EWeekendRateMapper {
    int countByExample(EWeekendRateExample example);

    int deleteByExample(EWeekendRateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EWeekendRate record);

    int insertSelective(EWeekendRate record);

    List<EWeekendRate> selectByExample(EWeekendRateExample example);

    EWeekendRate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EWeekendRate record, @Param("example") EWeekendRateExample example);

    int updateByExample(@Param("record") EWeekendRate record, @Param("example") EWeekendRateExample example);

    int updateByPrimaryKeySelective(EWeekendRate record);

    int updateByPrimaryKey(EWeekendRate record);
}