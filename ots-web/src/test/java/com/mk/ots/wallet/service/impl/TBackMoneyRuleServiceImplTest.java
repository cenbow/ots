package com.mk.ots.wallet.service.impl;

import com.common.BaseTest;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.wallet.service.ITBackMoneyRuleService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class TBackMoneyRuleServiceImplTest extends BaseTest {
    @Autowired
    private ITBackMoneyRuleService itBackMoneyRuleService;

    @Test
    public void getBackMoneyByOrderTest() throws Exception {
        OtaOrder order = new OtaOrder();
        order.setPromoType("1");
        order.setHotelId(2220L);
        BigDecimal backMoneyByOrder = itBackMoneyRuleService.getBackMoneyByOrder(order);
        Assert.assertEquals(new BigDecimal("5"), backMoneyByOrder.setScale(0));
    }
}