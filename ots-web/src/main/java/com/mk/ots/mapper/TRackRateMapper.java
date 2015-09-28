package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.bean.TRackRate;
import com.mk.ots.hotel.bean.TRackRateExample;

public interface TRackRateMapper {
    int countByExample(TRackRateExample example);

    int deleteByExample(TRackRateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TRackRate record);

    int insertSelective(TRackRate record);

    List<TRackRate> selectByExample(TRackRateExample example);

    TRackRate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TRackRate record, @Param("example") TRackRateExample example);

    int updateByExample(@Param("record") TRackRate record, @Param("example") TRackRateExample example);

    int updateByPrimaryKeySelective(TRackRate record);

    int updateByPrimaryKey(TRackRate record);
}