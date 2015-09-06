package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.model.TRoomTypeModel;
import com.mk.ots.hotel.model.TRoomTypeWithBasePrice;


/**
 * TRoomTypeMapper.
 * @author chuaiqing.
 *
 */
public interface TRoomTypeMapper {
    List<TRoomTypeModel> findTRoomTypeByHotelid(Long eHotelId);
    
    TRoomTypeModel selectByPrimaryKey(@Param("id") Long id) throws Exception;
    
    int saveTRoomType(TRoomTypeModel tRoomTypeModel);

    int delTRoomTypeById(Long id);

    int updateTRoomType(TRoomTypeModel tRoomType);
    
    TRoomTypeWithBasePrice findPriceById(@Param("id") Long id) throws Exception;
    
    List<TRoomTypeModel> findList(@Param("id") Long id, @Param("hotelid") Long hotelid, @Param("bednum") Integer bednum) throws Exception;
    
    List<TRoomTypeWithBasePrice> findHotelPrices(@Param("hotelid") Long hotelid) throws Exception;
}
