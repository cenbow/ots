package com.mk.ots.mapper;

import java.util.List;

import com.mk.ots.hotel.model.TBusinesszoneModel;


public interface TBusinesszoneMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TBusinesszoneModel record);

    int insertSelective(TBusinesszoneModel record);

    TBusinesszoneModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TBusinesszoneModel record);

    int updateByPrimaryKey(TBusinesszoneModel record);
    
    List<TBusinesszoneModel> selectByHotelid(Long hotelid);
}