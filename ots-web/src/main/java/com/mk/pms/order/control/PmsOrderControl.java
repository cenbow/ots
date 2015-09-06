package com.mk.pms.order.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mk.ots.hotel.dao.EHotelDAO;
import com.mk.pms.order.service.NewPmsOrderService;

@RestController
@RequestMapping("/pms")
public class PmsOrderControl {
	private static final Logger logger = LoggerFactory.getLogger(PmsOrderControl.class);
	@Autowired
	NewPmsOrderService newPmsOrderService;
	@Autowired
	EHotelDAO eHotelDAO;

	@RequestMapping(value = "/savecustomerno", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> saveCustomerNo(String json) {
		JSONObject param = newPmsOrderService.genJsonDataSaveCustomerNo(json);
		return new ResponseEntity<JSONObject>(param, HttpStatus.OK);
	}
	

	/**
	 * 房费清单
	 */
	@RequestMapping(value = "/roomcharge", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> roomCharge(String json) {
		JSONObject param = newPmsOrderService.genJsonDataRoomCharge(json);
		return new ResponseEntity<JSONObject>(param, HttpStatus.OK);
	}
	
	/**
	 * 订单结算
	 */
	@RequestMapping(value = "/ordercharge", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> orderCharge(String json) {
		JSONObject param = newPmsOrderService.genJsonDataOrderCharge(json);
		return new ResponseEntity<JSONObject>(param, HttpStatus.OK);
	}

	/**
	 * 发送所有当前有效客单
	 */
	@RequestMapping(value = "/synorder", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> synOrder(String json) {
		JSONObject param = newPmsOrderService.genJsonDataSynOrder(json);
		return new ResponseEntity<JSONObject>(param, HttpStatus.OK);
	}
}
