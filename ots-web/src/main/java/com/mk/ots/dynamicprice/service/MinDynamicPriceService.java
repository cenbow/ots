package com.mk.ots.dynamicprice.service;

import java.math.BigDecimal;

/**
 * Created by kirinli on 16/1/11.
 */
public interface MinDynamicPriceService {
    public BigDecimal getHotelMinDynamicPrice(String hotelId, String roomTypeId, Integer checkInOClock);
}
