package com.mk.ots.dynamicprice.service.impl;

import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.dynamicprice.service.DiscountDynamicPriceService;
import com.mk.ots.hotel.service.RoomstateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by kirinli on 16/1/12.
 */
@Service
public class DiscountDynamicPriceServiceImpl implements DiscountDynamicPriceService {
    @Autowired
    private RoomstateService roomstateService;
    @Override
    public Double getDiscountDynamic(String hotelId, String roomTypeId, String startDateDay, String endDateDay) {
        Date startDate = DateUtils.getDateFromString(startDateDay);
        Date endDate = DateUtils.getDateFromString(endDateDay);
        Double w1 = roomOccupancyRateFactor();
        Double w2 = currentTimeAndCheckInTimeFactor();
        Double occupancyRateValue = occupancyRate(hotelId, roomTypeId, startDateDay, endDateDay);
        Double timeRateValue = 0.0;
        return w1 *  occupancyRateValue + w2 * timeRateValue;
    }

    final private Double occupancyRate(String hotelId, String roomTypeId, String startDateDay, String endDateDay){
        Long hotelIdL = Long.valueOf(hotelId);
        Long roomTypeIdL = Long.valueOf(roomTypeId);
        return roomstateService.getRoomTypeOccupancyRate(hotelIdL, roomTypeIdL, startDateDay, endDateDay);
    }

    final private Double roomOccupancyRateFactor(){
        //TODO w1
        return 0.1;
    }


    final private Double currentTimeAndCheckInTimeFactor(){
        //TODO w2
        return 0.1;
    }
}
