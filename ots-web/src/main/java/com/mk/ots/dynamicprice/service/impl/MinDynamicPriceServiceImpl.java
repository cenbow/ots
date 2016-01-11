package com.mk.ots.dynamicprice.service.impl;

import com.mk.ots.dynamicprice.service.MinDynamicPriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by kirinli on 16/1/11.
 */
@Service
public class MinDynamicPriceServiceImpl implements MinDynamicPriceService{
    @Override
    public BigDecimal getHotelMinDynamicPrice(String hotelId, String roomTypeId, Integer checkInOCloc) {
        Random random = new Random(47);
        Integer randomPrice = random.nextInt(180) + 50;
        return new BigDecimal(randomPrice);
    }
}
