package com.mk.ots.dynamicprice.service.impl;

import com.mk.ots.dynamicprice.service.BaseDynamicPriceService;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by kirinli on 16/1/11.
 */
public class BaseDynamicPriceServiceImpl implements BaseDynamicPriceService {
    @Override
    public BigDecimal getRoomTypeDynamicPrice(String hotelId, String roomTypeId, Integer checkInOClock) {
        Random random = new Random(47);
        Integer randomPrice = random.nextInt(180) + 50;
        return new BigDecimal(randomPrice);
    }
}
