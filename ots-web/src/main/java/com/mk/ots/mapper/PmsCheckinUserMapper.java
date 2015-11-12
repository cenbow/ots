package com.mk.ots.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Thinkpad on 2015/10/29.
 */
@Repository
public interface PmsCheckinUserMapper {
    public List<String> getCardId(@Param("otaOrderId")Long otaOrderId);

    public Long getCardCountByCardId(@Param("otaOrderId")Long otaOrderId, @Param("cardId")String cardId);
}
