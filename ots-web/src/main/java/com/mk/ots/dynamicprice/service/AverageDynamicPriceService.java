package com.mk.ots.dynamicprice.service;

import java.math.BigDecimal;

/**
 * Created by kirinli on 16/1/11.
 */
public interface AverageDynamicPriceService {
    public BigDecimal getRoomTypeAverageDynamicPrice(String hotelId, String roomTypeId, Integer checkInOClock);
}
