package com.mk.ots.bill.dao;

import com.common.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BillOrderDetailDAOTest extends BaseTest {

    @Autowired
    private  BillOrderDAO billOrderDAO;

    @Test
    public void getNowShowListTest() throws Exception {
        List<Map<String, Object>>  nowShowList = billOrderDAO.getNowShowList(new Date());
        Assert.assertEquals(11, nowShowList.size());
    }
}