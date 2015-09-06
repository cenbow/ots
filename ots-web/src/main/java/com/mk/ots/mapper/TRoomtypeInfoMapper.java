package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.model.TRoomTypeInfoModel;

/**
 * 
 * @author chuaiqing.
 *
 */
public interface TRoomtypeInfoMapper {
    public TRoomTypeInfoModel selectByRoomtypeid(@Param("roomtypeid") Long roomtypeid);
    
    public TRoomTypeInfoModel findByRoomtypeid(@Param("roomtypeid") Long roomtypeid);
    
    int insertSelective(TRoomTypeInfoModel record);
    
    public List<TRoomTypeInfoModel> findByHotelid(Long hotelid);
}
