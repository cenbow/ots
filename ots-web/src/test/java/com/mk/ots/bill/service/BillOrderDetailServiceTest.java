package com.mk.ots.bill.service;

import com.common.BaseTest;
import com.mk.ots.common.utils.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;

public class BillOrderDetailServiceTest extends BaseTest {

    @Autowired
    private BillOrderDetailService billOrderDetailService;

    @Test
    public void testGenOrderDetail() throws Exception {
        Date date = DateUtils.getDateFromString("2015-11-01");
        billOrderDetailService.genOrderDetail(date);
    }

    @Test
    public void testGenOrderDetail1() throws Exception {
        //查询订单信息
        String billDate1 = "2015-11-01";
        String billDate2 = "2015-12-01";
        Date billBeginDate = null;
        Date billEndDate = null;
        try {
            billBeginDate = DateUtils.parseDate(billDate1, DateUtils.FORMAT_DATE) ;
            billEndDate = DateUtils.parseDate(billDate2, DateUtils.FORMAT_DATE) ;
        } catch (ParseException e) {
            logger.error("genOrderDetail get bill date exception" , e);
        }
        int pageSize = 1000;
        int pageIndex = 0;
        billOrderDetailService.genOrderDetail(billBeginDate, billEndDate, pageSize, pageIndex);
    }

    @Test
    public void testCreateBillOrderDetailList() throws Exception {

    }

    @Test
    public void testBuildOrderDetail() throws Exception {

    }

    @Test
    public void testSaveOrderDetailByBatch() throws Exception {

    }
}