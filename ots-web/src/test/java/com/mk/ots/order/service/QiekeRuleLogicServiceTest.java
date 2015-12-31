package com.mk.ots.order.service;

import com.common.BaseTest;
import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.OtaFreqTrvEnum;
import com.mk.ots.order.bean.OtaOrder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class QiekeRuleLogicServiceTest extends BaseTest {

    @Autowired
    private QiekeRuleLogicService qiekeRuleLogicService;

    @Test
    public void testGetQiekeRuleReason() throws Exception {
        OtaOrder otaOrder = new OtaOrder();
        Map<String,String> resultCacheMap = new HashMap<>();
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleLogicService.getQiekeRuleReason(otaOrder, resultCacheMap);
        Assert.assertEquals(otaFreqTrvEnum.getId(), OtaFreqTrvEnum.CARD_ID_IS_NULL.getId());
    }

    @Test
    public void testExecuteQiekeRuleReason() throws Exception {
        OtaOrder otaOrder = new OtaOrder();
        QiekeRuleService qiekeRuleService = AppUtils.getBean(QiekeRuleService.class);
        String methodName = OtaFreqTrvEnum.CARD_ID_IS_NULL.getInvokeMethod();
        qiekeRuleLogicService.executeQiekeRuleReason(otaOrder, qiekeRuleService, methodName);
    }

    @Test
    public void testGetPromoList() throws Exception {
        OtaOrder otaOrder = new OtaOrder();
        qiekeRuleLogicService.getPromoList(otaOrder);
    }
}