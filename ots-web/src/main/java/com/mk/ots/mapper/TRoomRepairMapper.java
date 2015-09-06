package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.model.TRoomRepairModel;

/**
 * 
 * @author chuaiqing.
 *
 */
public interface TRoomRepairMapper {
    
    public TRoomRepairModel selectById(@Param("hotelid") Long hotelid);
    
    public List<TRoomRepairModel> findList(@Param("hotelid") Long hotelid);
}
