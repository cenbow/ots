package com.mk.ots.view.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.mk.ots.view.service.ISyViewLogService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 钱包统一入口
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Controller
@RequestMapping(value = "/sys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class SyViewLogController {

    final Logger logger = LoggerFactory.getLogger(SyViewLogController.class);

    @Autowired
    private ISyViewLogService syViewLogService;


    @RequestMapping(value = "/viewevent")
    public ResponseEntity<Map<String, Object>> addviewevent(String data) {

        logger.info("【sys/addviewevent】 data is : {}", data);
        JSONArray  ja = JSONArray.parseArray(data);
        Map<String, Object> resultrtnMap = Maps.newHashMap();
        if (StringUtils.isEmpty(data)) {
            logger.error("获取目标data失败.");
            resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
            resultrtnMap.put("errmsg", "获取目标data失败!");
            return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
        }

        boolean result = false;
        try{
            //组织数据响应
            syViewLogService.pushSyViewLog(ja);
            result = true;
        }catch(Exception   e){
            result =false;
        }finally{
            resultrtnMap.put("success", result);
            if (result) {
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            } else {
                resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
                resultrtnMap.put("errmsg", "添加日志失败");
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            }
        }

    }
}
