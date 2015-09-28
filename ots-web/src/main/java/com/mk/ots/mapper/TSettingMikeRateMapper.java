package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.bean.TSettingMikeRate;
import com.mk.ots.hotel.bean.TSettingMikeRateExample;

public interface TSettingMikeRateMapper {
    int countByExample(TSettingMikeRateExample example);

    int deleteByExample(TSettingMikeRateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TSettingMikeRate record);

    int insertSelective(TSettingMikeRate record);

    List<TSettingMikeRate> selectByExample(TSettingMikeRateExample example);

    TSettingMikeRate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TSettingMikeRate record, @Param("example") TSettingMikeRateExample example);

    int updateByExample(@Param("record") TSettingMikeRate record, @Param("example") TSettingMikeRateExample example);

    int updateByPrimaryKeySelective(TSettingMikeRate record);

    int updateByPrimaryKey(TSettingMikeRate record);
}