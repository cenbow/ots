package com.mk.ots.roomsale.controller;

import com.google.common.collect.Maps;
import com.mk.ots.roomsale.service.RoomSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Map;

/**
* RoomSaleMapper.
* @author kangxiaolong.
*/
@Controller
@RequestMapping(value="/roomsale", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomSaleController {
	@Autowired
	private RoomSaleService roomSaleService;

	@RequestMapping("/saleBegin")
	 public ResponseEntity<Map<String, Object>> saleBegin() {
		roomSaleService.saleBegin();
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}


}
