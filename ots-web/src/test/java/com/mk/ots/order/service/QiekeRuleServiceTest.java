package com.mk.ots.order.service;

import com.common.BaseTest;
import com.mk.ots.common.enums.OtaFreqTrvEnum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


public class QiekeRuleServiceTest extends BaseTest {
    @Autowired
    private QiekeRuleService qiekeRuleService;

    @Test
    public void checkUserAddersByBothCloseTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = false;
        boolean checkDistanceSwitchOpen = false;
        BigDecimal userLongitude = new BigDecimal("120");
        BigDecimal userLatitude = new BigDecimal("120");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }

    @Test
    public void checkUserAddersByIsNullCloseTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = false;
        boolean checkDistanceSwitchOpen = true;
        BigDecimal userLongitude = null;
        BigDecimal userLatitude = null;
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }


    @Test
    public void checkUserAddersByBothOpenTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = true;
        BigDecimal userLongitude = null;
        BigDecimal userLatitude = null;
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "90");
    }

    @Test
    public void checkUserAddersByLess1KmTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = false;
        BigDecimal userLongitude = new BigDecimal("120.0001");
        BigDecimal userLatitude = new BigDecimal("120.0001");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }

    @Test
    public void checkUserAddersByThan1KmTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = false;
        BigDecimal userLongitude = new BigDecimal("126");
        BigDecimal userLatitude = new BigDecimal("120");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }

    @Test
    public void checkUserAddersByThan1KmTest2() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = true;
        BigDecimal userLongitude = new BigDecimal("126");
        BigDecimal userLatitude = new BigDecimal("120");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "90");
    }
}