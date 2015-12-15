package com.mk.ots.bill.service;

import com.common.BaseTest;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.bill.dao.ServiceCostRuleDao;
import com.mk.ots.bill.enums.ServiceCostRuleTypeEnum;
import com.mk.ots.bill.enums.ServiceCostTypeEnum;
import com.mk.ots.common.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

public class ServiceCostRuleServiceTestConfig extends BaseTest {

    @Autowired
    private ServiceCostRuleService serviceCostRuleService;
    @Autowired
    private ServiceCostRuleDao serviceCostRuleDao;

    @Test
    public void getServiceCostByOrderTypeByCityCodeAndIsQieKeTest() throws Exception {
        Date orderCreateTime = new Date();
        Boolean qiekeFlag = true;
        BigDecimal price = new BigDecimal(100);
        String hotelCityCode = "310000";
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(orderCreateTime, qiekeFlag, price, hotelCityCode);
        BigDecimal expected = new BigDecimal("10.00");
        Assert.assertEquals(true, expected.compareTo(serviceCost) == 0);
    }


    @Test
    public void getServiceCostByOrderTypeByCityCodeAndNoQieKeTest() throws Exception {
        Date orderCreateTime = new Date();
        Boolean qiekeFlag = false;
        BigDecimal price = new BigDecimal(100);
        String hotelCityCode = "310000";
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(orderCreateTime, qiekeFlag, price, hotelCityCode);
        BigDecimal expected = new BigDecimal("10.00");
        Assert.assertEquals(expected, serviceCost);
        Assert.assertEquals(true, expected.compareTo(serviceCost) == 0);
    }

    @Test
    public void getServiceCostByOrderTypeByIsDefaultTest() throws Exception {
        Date orderCreateTime = new Date();
        Boolean qiekeFlag = false;
        BigDecimal price = new BigDecimal(100);
        String hotelCityCode = "310001";
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(orderCreateTime, qiekeFlag, price, hotelCityCode);
        BigDecimal expected = new BigDecimal("0");
        Assert.assertEquals(true, expected.compareTo(serviceCost) == 0);
    }

    @Test
    public void getServiceCostByOrderTypeByIsDefault2Test() throws Exception {
        Date orderCreateTime = new Date();
        Boolean qiekeFlag = true;
        BigDecimal price = new BigDecimal(100);
        String hotelCityCode = "310001";
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(orderCreateTime, qiekeFlag, price, hotelCityCode);
        BigDecimal expected = new BigDecimal("0");
        Assert.assertEquals(true, expected.compareTo(serviceCost) == 0);
    }


    @Test
    public void getServiceCostByOrderTypeByIsDefaultOverTime1Test() throws Exception {
        Date orderCreateTime = DateUtils.parseDate("2016-01-01", DateUtils.FORMAT_DATE);
        Boolean qiekeFlag = true;
        BigDecimal price = new BigDecimal(1);
        String hotelCityCode = "430100";
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(orderCreateTime, qiekeFlag, price, hotelCityCode);
        BigDecimal expected = new BigDecimal("0.1");
        Assert.assertEquals(expected, serviceCost);
    }

    @Test
    public void getServiceCostByOrderTypeByIsDefaultOverTime2Test() throws Exception {
        Date orderCreateTime = DateUtils.parseDate("2016-01-01", DateUtils.FORMAT_DATE);
        Boolean qiekeFlag = false;
        BigDecimal price = new BigDecimal(1);
        String hotelCityCode = "430100";
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(orderCreateTime, qiekeFlag, price, hotelCityCode);
        BigDecimal expected = new BigDecimal("0.1");
        Assert.assertEquals(true, expected.compareTo(serviceCost) == 0);
    }

    @Test
    public void getServiceCostByOrderTypeByIsDefaultOverTime3Test() throws Exception {
        Date orderCreateTime = DateUtils.parseDate("2015-12-29",DateUtils.FORMAT_DATE);
        Boolean qiekeFlag = false;
        BigDecimal price = new BigDecimal(1);
        String hotelCityCode = "310000";
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(orderCreateTime, qiekeFlag, price, hotelCityCode);
        BigDecimal expected = new BigDecimal("0.1");
        Assert.assertEquals(true, expected.compareTo(serviceCost) == 0);
    }

    @Test
     public void checkTimeTest() throws Exception {
        Bean queryServiceCostRule = serviceCostRuleService.getServiceCostRuleByDefault(ServiceCostTypeEnum.ORDER_SERVICE_COST.getType());
        Assert.assertNotNull(queryServiceCostRule);
        boolean checkTimeFlag = serviceCostRuleService.checkTime(new Date(),queryServiceCostRule);
        Assert.assertEquals(true , checkTimeFlag);
        Integer resultQiekeFlag = queryServiceCostRule.getInt("qieke_flag");
        Assert.assertEquals(new Integer("1") , resultQiekeFlag);
        Integer ruleType = queryServiceCostRule.getInt("rule_type");
        Assert.assertEquals(new Integer("2") , ruleType);
        Assert.assertEquals(true , ServiceCostRuleTypeEnum.FIX.getType() == queryServiceCostRule.getInt("rule_type"));
        BigDecimal serviceCost = queryServiceCostRule.getBigDecimal("service_cost");
        Assert.assertEquals(new BigDecimal("1.00") , serviceCost);
    }

    @Test
    public void checkTime2Test() throws Exception {
        String hotelCityCode = "310000";
        Bean queryServiceCostRule = serviceCostRuleService.getServiceCostRule(hotelCityCode, ServiceCostTypeEnum.ORDER_SERVICE_COST.getType());
        Assert.assertNotNull(queryServiceCostRule);
        boolean checkTimeFlag = serviceCostRuleService.checkTime(new Date(),queryServiceCostRule);
        Assert.assertEquals(true , checkTimeFlag);
        Integer resultQiekeFlag = queryServiceCostRule.getInt("qieke_flag");
        Assert.assertEquals(new Integer("0") , resultQiekeFlag);
        Integer ruleType = queryServiceCostRule.getInt("rule_type");
        Assert.assertEquals(new Integer("1") , ruleType);
        Assert.assertEquals(true , ServiceCostRuleTypeEnum.RATIO.getType() == queryServiceCostRule.getInt("rule_type"));
        BigDecimal ratio = queryServiceCostRule.getBigDecimal("ratio");
        Assert.assertEquals(new BigDecimal("0.10") , ratio);
    }


    @Test
    public void checkTimeByOverTimeTest() throws Exception {
        Bean queryServiceCostRule = serviceCostRuleService.getServiceCostRuleByDefault(ServiceCostTypeEnum.ORDER_SERVICE_COST.getType());
        Assert.assertNotNull(queryServiceCostRule);
        Date time = DateUtils.parseDate("2016-01-01", DateUtils.FORMAT_DATE);
        boolean checkTimeFlag = serviceCostRuleService.checkTime(time, queryServiceCostRule);
        Assert.assertEquals(false , checkTimeFlag);
    }

}