package com.mk.ots.order.service;

import com.common.BaseTest;
import com.mk.ots.order.bean.OrderPromoPayRuleJson;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceImplTest extends BaseTest {

    @Autowired
    private OrderServiceImpl orderService;
    @Test
    public void getOrderPromoPayRuleTest() throws Exception {
        Integer promoType = 0;
        OrderPromoPayRuleJson orderPromoPayRuleJson = orderService.getOrderPromoPayRule(promoType);
        Assert.assertNotNull(orderPromoPayRuleJson);
        Assert.assertEquals(new Integer(1),orderPromoPayRuleJson.getIsOnlinePay());
    }

    @Test
    public void getOrderPromoPayRule1Test() throws Exception {
        Integer promoType = 1;
        OrderPromoPayRuleJson orderPromoPayRuleJson = orderService.getOrderPromoPayRule(promoType);
        Assert.assertNotNull(orderPromoPayRuleJson);
        Assert.assertEquals(new Integer(0),orderPromoPayRuleJson.getIsTicketPay());
    }

    @Test
    public void getOrderPromoPayRule2Test() throws Exception {
        Integer promoType = 1;
        OrderPromoPayRuleJson orderPromoPayRuleJson = orderService.getOrderPromoPayRule(promoType);
        Assert.assertNotNull(orderPromoPayRuleJson);
        Assert.assertEquals(new Integer(1),orderPromoPayRuleJson.getIsOnlinePay());
        Assert.assertEquals(new Integer(0),orderPromoPayRuleJson.getIsTicketPay());
    }
}