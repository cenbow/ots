package com.mk.ots.bill.service;

import com.common.BaseTest;
import com.mk.ots.common.utils.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class BillOrderServiceTest extends BaseTest {

    @Autowired
    private BillOrderService billOrderService;

    @Test
    public void testRuntWeekClearing() throws Exception {
        Date beginTime = DateUtils.getDateFromString(DateUtils.formatDateTime(new Date(), DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE);
        billOrderService.runtWeekClearing(beginTime, null);
    }
}