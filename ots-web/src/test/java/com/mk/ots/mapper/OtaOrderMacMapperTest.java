package com.mk.ots.mapper;

import com.common.BaseTest;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.order.model.OtaOrderMac;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class OtaOrderMacMapperTest extends BaseTest {
    @Autowired
    private OtaOrderMacMapper otaOrderMacMapper;

    @Test
    public void testSelectByUuid() throws Exception {
        Map<String, Object> param = new HashMap<>();
        String uuid = "864595029063587 14:f6:5a:80:fb:2d 14:F6:5A:00:FB:2D";
        param.put("uuid",uuid);
        param.put("statusList", Arrays.asList(520));
        List<OtaOrderMac> otaOrderMacList = otaOrderMacMapper.selectByUuid(param);
        Assert.assertNotNull(otaOrderMacList);
    }

    @Test
    public void testSelectByUuid2() throws Exception {
        Map<String, Object> param = new HashMap<>();
        String uuid = "864595029063587 14:f6:5a:80:fb:2d 14:F6:5A:00:FB:2D";
        param.put("uuid",uuid);
        param.put("statusList", Arrays.asList("520"));
        Date firstDayOfMonth = DateUtils.getDateFromString("2015-11-1", DateUtils.FORMAT_DATE);
        Date lastDayOfMonth = DateUtils.getDateFromString("2015-12-1", DateUtils.FORMAT_DATE);
        param.put("createBeginTime", firstDayOfMonth);
        param.put("createEndTime", DateUtils.addDays(lastDayOfMonth, 1));
        List<OtaOrderMac> otaOrderMacList = otaOrderMacMapper.selectByUuid(param);
        Assert.assertNotNull(otaOrderMacList);
    }

    @Test
    public void testSelectByDeviceimei() throws Exception {
        Map<String, Object> param = new HashMap<>();
        String deviceimei = "867271029976718";
        param.put("deviceimei",deviceimei);
        param.put("statusList", Arrays.asList("513"));
        List<OtaOrderMac> otaOrderMacList = otaOrderMacMapper.selectByDeviceimei(param);
        Assert.assertNotNull(otaOrderMacList);
    }

    @Test
    public void testSelectByDeviceimei2() throws Exception {
        Map<String, Object> param = new HashMap<>();
        String deviceimei = "867271029976718";
        param.put("deviceimei",deviceimei);
        param.put("statusList", Arrays.asList("513"));

        Date firstDayOfMonth = DateUtils.getDateFromString("2015-11-1", DateUtils.FORMAT_DATE);
        Date lastDayOfMonth = DateUtils.getDateFromString("2015-12-1", DateUtils.FORMAT_DATE);
        param.put("createBeginTime", firstDayOfMonth);
        param.put("createEndTime", DateUtils.addDays(lastDayOfMonth, 1));
        List<OtaOrderMac> otaOrderMacList = otaOrderMacMapper.selectByDeviceimei(param);
        Assert.assertNotNull(otaOrderMacList);
    }

}