package com.mk.ots.mapper;

import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.ticket.model.BPromotionCity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THotelMapper.
 * @author chuaiqing.
 *
 */
@Repository
public interface TPromotionCityMapper {

    /**
     * 优惠劵Id
     * @param promotionid
     * @return
     */
    public String findCityCodeByPromotionId(Long promotionid);


    public List<BPromotionCity> findPromotionCityByCityCode(HashMap  ma);

}
