package com.mk.ots.dynamicprice.service;

/**
 * Created by kirinli on 16/1/11.
 */
public interface DiscountDynamicPriceService {
    public Double getDiscountDynamic(String hotelId, String roomTypeId, String startDateDay, String endDateDay);
}
