package com.mk.ots.hotel.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.mk.framework.AppUtils;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.service.BedTypeService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.web.ServiceOutput;

/**
 * 酒店前端控制类 发布接口
 * 
 * @author LYN
 *
 */
@Controller
@RequestMapping(value = "/room", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RoomService roomService = null;
    
    @Autowired
    RoomstateService roomstateService = null;
    
    @Autowired
    private BedTypeService bedTypeService;

    
    /**
     * 查看房间当天的状态
     * 为空可预定
     * 已经改为redis 此方法无效
     * @param hotelid
     * @param roomtypeid
     * @param roomid
     * @param date
     * @return
     */
    @RequestMapping(value = "/findRoomStatus")
    public ResponseEntity<Map<String, Object>> findRoomStatus(String hotelid,String roomtypeid, String roomid,String date){
        Bean roomtemp =roomService.findRoomStatus(hotelid,roomtypeid,roomid,date);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("hotelid", hotelid);
        m.put("roomtypeid", roomtypeid);
        m.put("roomid", roomid);
        m.put("date", date);
        if(roomtemp!=null){
            m.put("ispms", roomtemp.get("ispms"));
            m.put("roomstatus", "nvc");
        }else{
            m.put("roomstatus","vc");
        }
        return new ResponseEntity<Map<String, Object>>(m,HttpStatus.OK);
    }
    
    
    
    @RequestMapping(value="/state")
    public ResponseEntity<Map<String, Object>> findState(Long hotelid, String startdateday, String enddateday) throws Exception {
        Map<String, Object> rtnMap = Maps.newHashMap();
        try {
            Date sdate = DateUtils.getDateFromString(startdateday);
            Date edate = DateUtils.getDateFromString(enddateday);
            String begindate = DateUtils.formatDate(sdate);
            String enddate = DateUtils.formatDate(edate);
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
            rtnMap.put("lockrooms", roomstateService.findBookedRoomsByHotelId(hotelid, begindate, enddate));
        } catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
    /**
     * 
     * @param hotelid
     * @param startdateday
     * @param enddateday
     * @param init
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/prices")
    public ResponseEntity<Map<String, Object>> findPrice(Long hotelid, String startdateday, String enddateday, String init) throws Exception {
        long starttime = new Date().getTime();
        Map<String, Object> rtnMap = Maps.newHashMap();
        try {
            boolean isinit = StringUtils.isBlank(init) ? false : Constant.STR_TRUE.equals(init);
            Map<String, String> priceMap = roomstateService.findHotelPrices(hotelid, isinit);
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
            rtnMap.put("hotelprices", priceMap);
            if (AppUtils.DEBUG_MODE) {
                long endtime = new Date().getTime();
                rtnMap.put("$times$", endtime - starttime + " ms");
            }
        } catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
            logger.error("service prices is error: {}", e.getMessage());
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
    /**
     * 取得酒店策略价
     * @param hotelid
     * 参数：酒店id
     * @param roomtypeid
     * 参数：房型id
     * @param init
     * 参数：是否初始化
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/timeprices")
    public ResponseEntity<Map<String, Object>> getTimePrices(Long hotelid, Long roomtypeid, String init) throws Exception {
        long starttime = new Date().getTime();
        Map<String, Object> rtnMap = Maps.newHashMap();
        try {
            boolean isinit = StringUtils.isBlank(init) ? false : Constant.STR_TRUE.equals(init);
            Map<String, String> timeprices = roomstateService.findTimePrices(hotelid, roomtypeid, isinit);
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
            rtnMap.put("timeprices", timeprices);
            if (AppUtils.DEBUG_MODE) {
                long endtime = new Date().getTime();
                rtnMap.put("$times$", endtime - starttime + " ms");
            }
        } catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
            logger.error("service timeprices is error: {}", e.getMessage());
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
    /**
     * 酒店房型门市价格
     * @param roomtypeid
     * 参数：房型id
     * @param init
     * 参数：是否初始化
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/roomtypeprice")
    public ResponseEntity<Map<String, Object>> getRoomtypePrice(Long roomtypeid, String init) throws Exception {
        long starttime = new Date().getTime();
        Map<String, Object> rtnMap = Maps.newHashMap();
        try {
            boolean isinit = StringUtils.isBlank(init) ? false : Constant.STR_TRUE.equals(init);
            BigDecimal price = roomstateService.findRoomtypePrice(roomtypeid, isinit);
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
            rtnMap.put("roomtypeprice", price);
            if (AppUtils.DEBUG_MODE) {
                long endtime = new Date().getTime();
                rtnMap.put("$times$", endtime - starttime + " ms");
            }
        } catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
            logger.error("service roomtypeprice is error: {}", e.getMessage());
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
}

