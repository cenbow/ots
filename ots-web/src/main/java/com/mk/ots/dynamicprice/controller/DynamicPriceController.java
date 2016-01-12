package com.mk.ots.dynamicprice.controller;

import com.mk.ots.dynamicprice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kirinli on 16/1/11.
 */
@Controller
@RequestMapping(method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class DynamicPriceController {

    @Autowired
    private BaseDynamicPriceService baseDynamicPriceService;


    @Autowired
    private AverageDynamicPriceService averageDynamicPriceService;

    @Autowired
    private MinDynamicPriceService minDynamicPriceService;

    @Autowired
    private InitCodeTableService initCodeTableService;

    @Autowired
    private CodeTableService codeTableService;


    @RequestMapping("/dynamicprice/base")
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
    public ResponseEntity< Map<String, Object>> getMinDynamicPrice(String hotelid,  Integer checkinoclock){
        Map<String, Object> rtnMap = new HashMap<>();
        BigDecimal price = minDynamicPriceService.getHotelMinDynamicPrice(hotelid, checkinoclock);
        rtnMap.put("price",price);
        rtnMap.put("success", true);
        rtnMap.put("errcode", 0);
        rtnMap.put("errmsg","");

        return new ResponseEntity< Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    @RequestMapping("/dynamicprice/initcode")
    @ResponseBody
    public ResponseEntity< Map<String, Object>> initCode(){
        Map<String, Object> rtnMap = new HashMap<>();
        initCodeTableService.initCriterionPriceCode2Redis();
        rtnMap.put("success", true);
        rtnMap.put("errcode", 0);
        rtnMap.put("errmsg","");

        return new ResponseEntity< Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    @RequestMapping("/dynamicprice/getcode")
    @ResponseBody
    public ResponseEntity< Map<String, Object>> getCode(Integer oclock){
        Map<String, Object> rtnMap = new HashMap<>();
        BigDecimal price = codeTableService.getCriterionPriceCode(oclock);
        rtnMap.put("price",price);
        rtnMap.put("success", true);
        rtnMap.put("errcode", 0);
        rtnMap.put("errmsg","");

        return new ResponseEntity< Map<String, Object>>(rtnMap, HttpStatus.OK);
    }
}
