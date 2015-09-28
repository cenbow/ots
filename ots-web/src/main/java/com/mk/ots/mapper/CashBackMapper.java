package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.model.CashBackModel;

public interface CashBackMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CashBackModel record);

    int insertSelective(CashBackModel record);

    CashBackModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CashBackModel record);

    int updateByPrimaryKey(CashBackModel record);
    List<CashBackModel> findCashBack(@Param("hotelid") Long hotelid, 
    								@Param("roomtypeid") Long roomtypeid, 
    								@Param("begindate") String begindate, 
    								@Param("enddate")String enddate);
}