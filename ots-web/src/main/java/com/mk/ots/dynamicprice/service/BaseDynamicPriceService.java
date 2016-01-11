package com.mk.ots.dynamicprice.service;

import java.math.BigDecimal;

/**
 * Created by kirinli on 16/1/11.
 */
public interface BaseDynamicPriceService {
    public BigDecimal getRoomTypeDynamicPrice(String hotelId, String roomTypeId, Integer checkInOClock);
}
