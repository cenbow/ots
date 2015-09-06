package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.model.TPricetime;
import com.mk.ots.hotel.model.TPricetimeWithBLOBs;
import com.mk.ots.hotel.model.TPricetimeWithPrices;

/**
 * 
 * @author chuaiqing.
 *
 */
public interface TPricetimeMapper {

    TPricetimeWithBLOBs selectByPrimaryKey(@Param("id") Long id);
    
    List<TPricetime> findList(@Param("hotelid") Long hotelid);
    
    List<TPricetimeWithPrices> findTimePriceList(@Param("hotelid") Long hotelid, @Param("roomtypeid") Long roomtypeid);
    
//    List<TPricetimeWithPrices> findTimePriceList(@Param("hotelid") Long hotelid);

}