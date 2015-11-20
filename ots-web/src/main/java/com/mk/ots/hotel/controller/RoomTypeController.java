package com.mk.ots.hotel.controller;

import com.google.common.collect.Maps;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.hotel.bean.TBedType;
import com.mk.ots.hotel.service.BedTypeService;
import com.mk.ots.web.ServiceOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * 酒店前端控制类 发布接口
 * 
 * @author LYN
 *
 */
@Controller
@RequestMapping(value = "/roomtype", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomTypeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BedTypeService bedTypeService;

    
    /**
     * 酒店房型搜索
     * @param pbb
     * @return
     */
    @RequestMapping(value="/querylist")
    public ResponseEntity<Map<String, Object>> getRoombedtype(ParamBaseBean pbb) {
    	
    	logger.info("【/roomtype/querylist】 params is : {}", pbb.toString());
    	Map<String, Object> rtnMap = Maps.newHashMap();
    	try {
			List<TBedType> bedtypes = bedTypeService.getRoombedtype();
            TBedType defaultBedType = new TBedType();
            Long id = new Long(-1);
            defaultBedType.setId(id);
            defaultBedType.setName("不限");

            bedtypes.add(defaultBedType);

			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put("datas", bedtypes);
		} catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
    	return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
}

