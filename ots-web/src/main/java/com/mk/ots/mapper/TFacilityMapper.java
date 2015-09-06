package com.mk.ots.mapper;

import java.util.List;

import com.mk.ots.hotel.model.TFacilityModel;

public interface TFacilityMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(TFacilityModel record);

    int insertSelective(TFacilityModel record);

    TFacilityModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TFacilityModel record);

    int updateByPrimaryKey(TFacilityModel record);
    
    List<TFacilityModel> findByIds(String[] ids);
    
    List<TFacilityModel> findByHotelid(Long hotelid);

	List<TFacilityModel> findByRoomtypeId(Long roomtypeid);
}