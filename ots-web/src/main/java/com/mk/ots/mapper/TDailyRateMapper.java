package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.bean.TDailyRate;
import com.mk.ots.hotel.bean.TDailyRateExample;

public interface TDailyRateMapper {
    int countByExample(TDailyRateExample example);

    int deleteByExample(TDailyRateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TDailyRate record);

    int insertSelective(TDailyRate record);

    List<TDailyRate> selectByExample(TDailyRateExample example);

    TDailyRate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TDailyRate record, @Param("example") TDailyRateExample example);

    int updateByExample(@Param("record") TDailyRate record, @Param("example") TDailyRateExample example);

    int updateByPrimaryKeySelective(TDailyRate record);

    int updateByPrimaryKey(TDailyRate record);
}