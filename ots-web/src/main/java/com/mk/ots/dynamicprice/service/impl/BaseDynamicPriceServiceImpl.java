package com.mk.ots.dynamicprice.service.impl;

import com.mk.ots.dynamicprice.service.BaseDynamicPriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by kirinli on 16/1/11.
 */
@Service
public class BaseDynamicPriceServiceImpl implements BaseDynamicPriceService {
    @Override
    public BigDecimal getRoomTypeDynamicPrice(String hotelId, String roomTypeId, Integer checkInOClock) {
        Integer randomPrice = (int)(Math.random()*10)+ 50;
        return new BigDecimal(randomPrice);
    }
}
