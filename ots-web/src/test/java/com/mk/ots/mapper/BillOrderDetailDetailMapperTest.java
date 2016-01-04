package com.mk.ots.mapper;

import com.common.BaseTest;
import com.mk.ots.bill.domain.BillOrder;
import com.mk.ots.common.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class BillOrderDetailDetailMapperTest extends BaseTest {

    @Autowired
    private BillOrderDetailMapper billOrderDetailMapper;

    @Test
    public void testGetBillOrderList() throws Exception {
        Date billDate = DateUtils.getDateFromString("2015-11-01");
        Date beginDate = null;
        Date billEndDate = null;
        try {
            beginDate = DateUtils.parseDate(DateUtils.formatDateTime(billDate, DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE) ;
            billEndDate = DateUtils.parseDate(DateUtils.formatDateTime(DateUtils.addDays(billDate, 1), DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE) ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<BillOrder> billOrderDetailList = billOrderDetailMapper.getBillOrderList(beginDate, billEndDate, 2, 0);
        Assert.assertEquals(22, billOrderDetailList.size());
    }
}