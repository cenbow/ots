package com.mk.ots.common.utils;

import com.common.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PropertiesUtilsTest extends BaseTest {
    @Autowired
    private PropertiesUtils propertiesUtils;

    @Test
    public void getTransferCheckinUsernameTimeTest() throws Exception {
        Assert.assertEquals("900000", propertiesUtils.getTransferCheckinUsernameTime().toString());
        Assert.assertEquals("http://sms.imike.cn/BMS_WEIXINMESSAGE_SALESINTERFACE.saveAndSendMessage.do?OTAOrderId=", propertiesUtils.getInformSaleUrl());
    }
}