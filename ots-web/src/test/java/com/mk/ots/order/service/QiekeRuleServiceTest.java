package com.mk.ots.order.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.BaseTest;
import com.mk.ots.common.enums.OtaFreqTrvEnum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


public class QiekeRuleServiceTest extends BaseTest {
    @Autowired
    private QiekeRuleService qiekeRuleService;

    @Test
    public void checkUserAddersByBothCloseTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = false;
        boolean checkDistanceSwitchOpen = false;
        BigDecimal userLongitude = new BigDecimal("120");
        BigDecimal userLatitude = new BigDecimal("120");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }

    @Test
    public void checkUserAddersByIsNullCloseTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = false;
        boolean checkDistanceSwitchOpen = true;
        BigDecimal userLongitude = null;
        BigDecimal userLatitude = null;
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }


    @Test
    public void checkUserAddersByBothOpenTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = true;
        BigDecimal userLongitude = null;
        BigDecimal userLatitude = null;
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "90");
    }

    @Test
    public void checkUserAddersByLess1KmTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = false;
        BigDecimal userLongitude = new BigDecimal("120.0001");
        BigDecimal userLatitude = new BigDecimal("120.0001");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }

    @Test
    public void checkUserAddersByThan1KmTest() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = false;
        BigDecimal userLongitude = new BigDecimal("126");
        BigDecimal userLatitude = new BigDecimal("120");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "-1");
    }

    @Test
    public void checkUserAddersByThan1KmTest2() throws Exception {
        boolean checkAddressIsNullSwitchOpen = true;
        boolean checkDistanceSwitchOpen = true;
        BigDecimal userLongitude = new BigDecimal("126");
        BigDecimal userLatitude = new BigDecimal("120");
        BigDecimal hotelLongitude = new BigDecimal("120");
        BigDecimal hotelLatitude = new BigDecimal("120");
        OtaFreqTrvEnum otaFreqTrvEnum = qiekeRuleService.checkUserAdders(checkAddressIsNullSwitchOpen, checkDistanceSwitchOpen,
                userLongitude, userLatitude, hotelLongitude, hotelLatitude);
        Assert.assertEquals(otaFreqTrvEnum.getId(), "90");
    }

    @Test
    public void jsonIsNotNullTest(){
        String json = "{\"contact\":\"\",\"customerno\":[{\"arrivetime\":\"2015-11-17 19:05:00:000\",\"checkintime\":\"2015-11-17 19:05:00:000\",\"checkintype\":\"pms\",\"checkouttime\":\"2015-11-18 12:00:00:000\",\"checkouttype\":\"pms\",\"customeno\":\"0jF4pYEm15BVxPaD3vXteNa\",\"day\":[{\"Price\":1,\"roomid\":\"3NgB70S6cZ6eFXwvXwONgLno\",\"time\":\"20151117\"},{\"Price\":1,\"roomid\":\"3NgB70S6cZ6eFXwvXwONgLno\",\"time\":\"20151118\"}],\"isinback\":\"false\",\"leavetime\":\"2015-11-18 12:00:00:000\",\"orderid\":\"2393610\",\"ordertype\":\"R\",\"otacustomno\":\"2391541\",\"roomno\":\"3NgB70S6cZ6eFXwvXwONgLno\",\"roomtypeid\":\"3s9pMyA7h7J8UZHFmrSR10\",\"status\":\"OK\",\"totalcost\":2,\"totalpayment\":2,\"type\":\"2\",\"user\":[{\"idno\":\"10000023135\",\"idtype\":\"93\",\"ispermanent\":2,\"isscan\":\"0\",\"name\":\"测试名字是否正确\",\"phone\":\"\"}]}],\"function\":\"savecustomerno\",\"hotelid\":\"0RmZHCd1xqNcSoVmMp255X7b\",\"memo\":\"备注\",\"otaorderid\":\"2393610\",\"phone\":\"13671724887\",\"pmstypeid\":\"1\",\"timestamp\":\"20151117070621\",\"uuid\":\"984d2bea46cb48b399e88bfbf9d7a267\",\"waitintitle\":\"请尽快入住\",\"waitintype\":\"1\"}";
        JSONObject param = JSON.parseObject(json);
        JSONArray datalist = param.getJSONArray("customerno");
        JSONObject customNo = datalist.getJSONObject(0);
        JSONArray users = customNo.getJSONArray("user");
        JSONObject user = users.getJSONObject(0);
        Assert.assertNotNull(user.get("isscan"));
        Assert.assertNotNull(user.getString("isscan"));
    }

    @Test
    public void jsonIsNullTest(){
        String json = "{\"timestamp\":\"20151117175006\",\"customerno\":[{\"leavetime\":\"20151118120000\",\"checkouttype\":\"\",\"totalcost\":0,\"status\":\"IN\",\"ordertype\":\"R\",\"type\":\"2\",\"arrivetime\":\"20151117194800\",\"checkintime\":\"20151117175006\",\"checkouttime\":\"\",\"roomno\":\"3ojZ18Bdx3d8HXBxiOoMUEN\",\"roomtypeid\":\"3s9pMyA7h7J8UZHFmrSR10\",\"otacustomno\":\"2391379\",\"customeno\":\"1md49sdBheipcRsRw6tB17\",\"orderid\":\"25Ujqz85RbZEWMyOIDfG3m\",\"day\":[{\"roomid\":\"3ojZ18Bdx3d8HXBxiOoMUEN\",\"time\":\"20151117\",\"price\":1}],\"totalpayment\":0,\"user\":[{\"phone\":\"18521085368\",\"idtype\":\"11\",\"name\":\"潘高朗\",\"ispermanent\":0,\"idno\":\"5CE9EB307244753D5CF58DD3C6C3D3A1E7D2BF34A906C6B1B10B6D43A48258C5\"}],\"checkintype\":\"\",\"isinback\":false}],\"hotelid\":\"0RmZHCd1xqNcSoVmMp255X7b\",\"uuid\":\"3DvFgKhEZ5toxZuaxJq1Diq\",\"pmstypeid\":\"2\",\"function\":\"saveCustomerNo\"}";
        JSONObject param = JSON.parseObject(json);
        JSONArray datalist = param.getJSONArray("customerno");
        JSONObject customNo = datalist.getJSONObject(0);
        JSONArray users = customNo.getJSONArray("user");
        JSONObject user = users.getJSONObject(0);
        Assert.assertNull(user.get("isscan"));
    }
}