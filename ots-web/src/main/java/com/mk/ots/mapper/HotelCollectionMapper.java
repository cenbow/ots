package com.mk.ots.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.bean.HotelCollection;
import com.mk.ots.hotel.bean.HotelCollectionExample;

public interface HotelCollectionMapper {
    int countByExample(HotelCollectionExample example);

    int deleteByExample(HotelCollectionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(HotelCollection record);

    int insertSelective(HotelCollection record);

    List<HotelCollection> selectByExample(HotelCollectionExample example);

    HotelCollection selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") HotelCollection record, @Param("example") HotelCollectionExample example);

    int updateByExample(@Param("record") HotelCollection record, @Param("example") HotelCollectionExample example);

    int updateByPrimaryKeySelective(HotelCollection record);

    int updateByPrimaryKey(HotelCollection record);
    
    List<HotelCollection> querylist(Map<String, Object> param);

	int queryCount(String token);
}