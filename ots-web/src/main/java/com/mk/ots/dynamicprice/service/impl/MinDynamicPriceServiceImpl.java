package com.mk.ots.dynamicprice.service.impl;

import com.mk.ots.dynamicprice.service.MinDynamicPriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by kirinli on 16/1/11.
 */
@Service
public class MinDynamicPriceServiceImpl implements MinDynamicPriceService{
    @Override
    public BigDecimal getHotelMinDynamicPrice(String hotelId, String roomTypeId, Integer checkInOCloc) {
        Integer randomPrice = (int)(Math.random()*10)+ 30;
        return new BigDecimal(randomPrice);
    }
}
