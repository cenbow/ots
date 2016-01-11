package com.mk.ots.dynamicprice.controller;

import com.mk.ots.dynamicprice.service.AverageDynamicPriceService;
import com.mk.ots.dynamicprice.service.BaseDynamicPriceService;
import com.mk.ots.dynamicprice.service.MinDynamicPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kirinli on 16/1/11.
 */
public class DynamicPriceController {

    @Autowired
    private BaseDynamicPriceService baseDynamicPriceService;


    @Autowired
    private AverageDynamicPriceService averageDynamicPriceService;

    @Autowired
    private MinDynamicPriceService minDynamicPriceService;


    @RequestMapping("/dynamicprice/base")
    public ResponseEntity< Map<String, Object>> getBaseDynamicPrice(String hotelid, String roomtypeid, Integer checkinoclock){
        Map<String, Object> rtnMap = new HashMap<>();
        BigDecimal price = baseDynamicPriceService.getRoomTypeDynamicPrice(hotelid, roomtypeid, checkinoclock);
        rtnMap.put("price",price);
        rtnMap.put("success", true);
        rtnMap.put("errcode", 0);
        rtnMap.put("errmsg","");

        return new ResponseEntity< Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    @RequestMapping("/dynamicprice/average")
    public ResponseEntity< Map<String, Object>> getAverageDynamicPrice(String hotelid, String roomtypeid, Integer checkinoclock){
        Map<String, Object> rtnMap = new HashMap<>();
        BigDecimal price = averageDynamicPriceService.getRoomTypeAverageDynamicPrice(hotelid, roomtypeid, checkinoclock);
        rtnMap.put("price",price);
        rtnMap.put("success", true);
        rtnMap.put("errcode", 0);
        rtnMap.put("errmsg","");

        return new ResponseEntity< Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    @RequestMapping("/dynamicprice/min")
    public ResponseEntity< Map<String, Object>> getMinDynamicPrice(String hotelid, String roomtypeid, Integer checkinoclock){
        Map<String, Object> rtnMap = new HashMap<>();
        BigDecimal price = minDynamicPriceService.getHotelMinDynamicPrice(hotelid, roomtypeid, checkinoclock);
        rtnMap.put("price",price);
        rtnMap.put("success", true);
        rtnMap.put("errcode", 0);
        rtnMap.put("errmsg","");

        return new ResponseEntity< Map<String, Object>>(rtnMap, HttpStatus.OK);
    }
}
