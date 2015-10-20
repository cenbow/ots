package com.mk.ots.roomsale.controller;

import com.google.common.collect.Maps;
import com.mk.ots.roomsale.model.TRoomSale;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.service.RoomSaleService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
@Controller
@RequestMapping(value = "/roomsale", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomSaleController {
	@Autowired
	private RoomSaleService roomSaleService;
	private final Logger logger = org.slf4j.LoggerFactory.getLogger(RoomSaleController.class);

	@RequestMapping("/saleBegin")
	public ResponseEntity<Map<String, Object>> saleBegin() {
		roomSaleService.saleBegin();
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@RequestMapping("/getOneRoomSale")
	public ResponseEntity<TRoomSale> getOneRoomSale(TRoomSale bean) {
		TRoomSale result = roomSaleService.getOneRoomSale(bean);
		return new ResponseEntity<TRoomSale>(result, HttpStatus.OK);
	}

	@RequestMapping("/queryRoomSale")
	public ResponseEntity<List<TRoomSale>> queryRoomSale(TRoomSale bean) {
		List<TRoomSale> result = roomSaleService.queryRoomSale(bean);
		return new ResponseEntity<List<TRoomSale>>(result, HttpStatus.OK);
	}

	@RequestMapping("/queryPromoByTypeId")
	public ResponseEntity<Map<String, Object>> queryPromoByTypeId(String roomTypeId) {
		Map<String, Object> result = null;
		try {
			result = roomSaleService.queryRoomPromoByType(roomTypeId);
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("failed to queryPromoByTypeId", ex);
			return new ResponseEntity<Map<String, Object>>(new HashMap<String, Object>(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping("/queryPromoByHotelId")
	public ResponseEntity<List<Map<String, Object>>> queryPromoByHotelId(String hotelId) {
		List<Map<String, Object>> result = null;

		try {
			TRoomSaleConfig tRoomSaleConfig = new TRoomSaleConfig();
			tRoomSaleConfig.setHotelId(Integer.valueOf(hotelId));
			//List<RoomPromoDto> rms = roomSaleService.queryRoomPromoByHotel(tRoomSaleConfig);
			return new ResponseEntity<List<Map<String, Object>>>(result, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("failed to queryPromoByHotelId", ex);
			return new ResponseEntity<List<Map<String, Object>>>(result, HttpStatus.OK);
		}
	}

	@RequestMapping("/queryRoomSaleConfigInfo")
	public ResponseEntity<List<TRoomSaleConfigInfo>> queryRoomSaleInfo() {
		List<TRoomSaleConfigInfo> result = new ArrayList<TRoomSaleConfigInfo>();
		TRoomSaleConfigInfo info = new TRoomSaleConfigInfo();
		info.setId(1);
		info.setSaleValue("50");
		info.setSaleLabel("经济房");
		result.add(info);
		return new ResponseEntity<List<TRoomSaleConfigInfo>>(result, HttpStatus.OK);
	}
	@RequestMapping("/checkRoomSale")
	public ResponseEntity checkRoomSale(TRoomSaleConfig bean) {
		Boolean result =roomSaleService.checkRoomSale(bean);
		return new ResponseEntity(result, HttpStatus.OK);
	}
}
