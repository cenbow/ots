package com.mk.ots.mapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mk.ots.score.model.THotelScore;

/**
 * 
 * @author chuaiqing.
 *
 */
public interface THotelScoreMapper {
    int deleteByPrimaryKey(Long id);

    int insert(THotelScore record);

    int insertSelective(THotelScore record);

    THotelScore selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(THotelScore record);

    int updateByPrimaryKeyWithBLOBs(THotelScore record);

    int updateByPrimaryKey(THotelScore record);
    
    List<Map<String, String>> findHotelScoresByHotelid(Long hotelid);

   public  Long  findHotelScoreNumByHotelId(Long hotelId);

    public  Long  findHotelScoreNumByMid(HashMap hm);

    public BigDecimal findHotelScoreMoneyByMid(HashMap hm);




}