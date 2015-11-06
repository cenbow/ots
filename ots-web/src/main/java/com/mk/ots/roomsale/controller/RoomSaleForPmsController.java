package com.mk.ots.roomsale.controller;

import com.google.common.collect.Maps;
import com.mk.ots.roomsale.model.TRoomSaleConfigForPms;
import com.mk.ots.roomsale.model.TRoomSaleForPms;
import com.mk.ots.roomsale.service.RoomSaleForPmsService;
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
public class RoomSaleForPmsController {
	@Autowired
	private RoomSaleForPmsService roomSaleForPmsService;

	@RequestMapping("/gethotelroomsale")
	 public ResponseEntity<Map<String, Object>> getHotelRoomSale(TRoomSaleConfigForPms bean) {
		TRoomSaleForPms roomSaleForPms=roomSaleForPmsService.getHotelRoomSale(bean);
		Map<String, Object> rtnMap = Maps.newHashMap();
//		if(roomSaleForPms!=null&&roomSaleForPms.getInfo()!=null){
//			rtnMap.put("isOpen", "T");
//		}else{
//			rtnMap.put("isOpen", "F");
//		}
		rtnMap.put("isOpen", "T");
		rtnMap.put("role",roomSaleForPms);
		rtnMap.put("msg", "OK,");
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	@RequestMapping("/updatesalecount")
	public ResponseEntity<Map<String, Object>> updateTRoomSaleConfig(TRoomSaleConfigForPms bean) {
		String rs=roomSaleForPmsService.updateTRoomSaleConfig(bean);
		Map<String, Object> rtnMap = Maps.newHashMap();
		  rtnMap.put("msg", rs);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

}
