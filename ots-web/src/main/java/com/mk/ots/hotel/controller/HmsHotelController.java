package com.mk.ots.hotel.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mk.ots.hotel.dao.CityDAO;
import com.mk.ots.rpc.service.HmsHotelService;
import com.mk.ots.web.ServiceOutput;


@Controller
@RequestMapping(value="/hms", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class HmsHotelController {
    
    private Logger logger = org.slf4j.LoggerFactory.getLogger(HmsHotelController.class);
    
    @Autowired
    private HmsHotelService hmsHotelService;
    
    @Autowired
    private CityDAO cityDAO;
    
    /**
     * ping
     * @return
     */
    @RequestMapping(value="/hotel/ping", method=RequestMethod.GET)
    public ResponseEntity<String> ping() {
        return new ResponseEntity<String>("request default method success.", HttpStatus.OK);
    }
    
    /**
     * 新增酒店
     * @return
     */
    @RequestMapping(value="/hotel/add")
    public ResponseEntity<Map<String, Object>> save(String hotelid, HttpServletRequest request) {
        logger.info("--=================  mapping 【/hotel/add】 begin ... =================--");
        logger.info("--=================  request getRemoteAddr is: {} ", request.getRemoteAddr());
        logger.info("--=================  request getRemoteHost is: {} ", request.getRemoteHost());
        logger.info("--=================  request getRemotePort is: {} ", request.getRemotePort());
        logger.info("--=================  request getRemoteUser is: {} ", request.getRemoteUser());
        logger.info("--=================  request getRequestURI is: {} ", request.getRequestURI());
        logger.info("--=================  request getRequestURL is: {} ", request.getRequestURL());
        logger.info("HmsHotelController request mapping is:【/hotel/add】, parameter is: {} ", hotelid);
        ResponseEntity<Map<String, Object>> result = new ResponseEntity<Map<String, Object>>(hmsHotelService.addHmsHotelById(hotelid), HttpStatus.OK);
        ObjectMapper om = new ObjectMapper();
        try {
            logger.info("HmsHotelController request mapping【/hotel/add】, response body is: {} ", om.writeValueAsString(result.getBody()));
        } catch (Exception e) {
            logger.error("mapping 【/hotel/add】 is error: {} " , e.getMessage());
        }
        logger.info("--================= mapping 【/hotel/add】 end ... =================--");
        return result;
    }
    
    /**
     * 更新酒店
     * @param hotelid
     * 参数：酒店id
     * @param isonline
     * 参数：酒店在线状态
     * @return
     */
    @RequestMapping(value="/hotel/update")
    public ResponseEntity<Map<String, Object>> update(String hotelid, HttpServletRequest request) {
        logger.info("--=================  mapping 【/hotel/update】 begin ... =================--");
        logger.info("--=================  request getRemoteAddr is: {} ", request.getRemoteAddr());
        logger.info("--=================  request getRemoteHost is: {} ", request.getRemoteHost());
        logger.info("--=================  request getRemotePort is: {} ", request.getRemotePort());
        logger.info("--=================  request getRemoteUser is: {} ", request.getRemoteUser());
        logger.info("--=================  request getRequestURI is: {} ", request.getRequestURI());
        logger.info("--=================  request getRequestURL is: {} ", request.getRequestURL());
        logger.info("HmsHotelController request mapping is:【/hotel/update】, parameter is: {} ", hotelid);
        ResponseEntity<Map<String, Object>> result = new ResponseEntity<Map<String, Object>>(hmsHotelService.updateHotelById(hotelid), HttpStatus.OK);
        ObjectMapper om = new ObjectMapper();
        try {
            logger.info("HmsHotelController request mapping【/hotel/update】, response body is: {} ", om.writeValueAsString(result.getBody()));
        } catch (Exception e) {
            logger.error("mapping 【/hotel/update】 is error: {} " , e.getMessage());
        }
        logger.info("--================= mapping 【/hotel/update】 end ... =================--");
        return result;
    }
    
    /**
     * 
     * @return
     */
    @RequestMapping(value="/hotel/online")
    public ResponseEntity<Map<String, Object>> online(String hotelid, HttpServletRequest request) {
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        try {
            boolean issuccess = hmsHotelService.online(hotelid);
            if (issuccess) {
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
                logger.info("H端酒店 hotelid : {} 上线成功.", hotelid);
            } else {
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
                rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
                rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "H端酒店上线失败.");
                logger.info("H端酒店 hotelid : {} 上线失败.", hotelid);
            }
        } catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "H端酒店上线出错.\n" + e.getMessage());
            logger.info("H端酒店 hotelid : {} 上线出错. {}", hotelid, e.getMessage());
        }
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }
    
    /**
     * 
     * @return
     */
    @RequestMapping(value="/hotel/offline")
    public ResponseEntity<Map<String, Object>> offline(String hotelid, HttpServletRequest request) {
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        try {
            boolean issuccess = hmsHotelService.offline(hotelid);
            if (issuccess) {
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
                logger.info("H端酒店 hotelid : {} 下线成功.", hotelid);
            } else {
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
                rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
                rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "H端酒店下线失败.");
                logger.info("H端酒店 hotelid : {} 下线失败.", hotelid);
            }
        } catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "H端酒店下线出错.\n" + e.getMessage());
            logger.info("H端酒店 hotelid : {} 下线出错. {}", hotelid, e.getMessage());
        }
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }
}
