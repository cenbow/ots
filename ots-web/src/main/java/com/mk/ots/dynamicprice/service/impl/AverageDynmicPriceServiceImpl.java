package com.mk.ots.dynamicprice.service.impl;

import com.mk.ots.dynamicprice.service.AverageDynamicPriceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by kirinli on 16/1/11.
 */
@Service
public class AverageDynmicPriceServiceImpl implements AverageDynamicPriceService {
    @Override
    public BigDecimal getRoomTypeAverageDynamicPrice(String hotelId, String roomTypeId, Integer checkInOClock) {
        Random random = new Random(47);
        Integer randomPrice = random.nextInt(180) + 50;
        return new BigDecimal(randomPrice);
    }
}
