package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.bean.EDailyRate;
import com.mk.ots.hotel.bean.ERackRate;
import com.mk.ots.hotel.bean.ERackRateExample;

public interface ERackRateMapper {
    int countByExample(ERackRateExample example);

    int deleteByExample(ERackRateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ERackRate record);

    int insertSelective(ERackRate record);

    List<ERackRate> selectByExample(ERackRateExample example);

    ERackRate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ERackRate record, @Param("example") ERackRateExample example);

    int updateByExample(@Param("record") ERackRate record, @Param("example") ERackRateExample example);

    int updateByPrimaryKeySelective(ERackRate record);

    int updateByPrimaryKey(ERackRate record);

}