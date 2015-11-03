package com.mk.ots.inner.controller;

import java.text.SimpleDateFormat;
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

import com.mk.ots.bill.service.BillOrderService;
import com.mk.ots.common.enums.HotelSearchEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.OtsVersion;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.inner.service.IOtsAdminService;
import com.mk.ots.web.ServiceOutput;

/**
 * OTS Administrator.
 * 
 * @author chuaiqing.
 *
 */
@Controller
@RequestMapping(value = "/admin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class OtsAdminController {

	final Logger logger = LoggerFactory.getLogger(OtsAdminController.class);

	@Autowired
	private IOtsAdminService otsAdminService;

	@Autowired
	private HotelService hotelService;

	@Autowired
	private BillOrderService billService;

	private final SimpleDateFormat defaultDateFormatter = new SimpleDateFormat(DateUtils.FORMATSHORTDATETIME);

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> ping() {
		return new ResponseEntity<Map<String, String>>(new OtsVersion().getVersionInfo(), HttpStatus.OK);
	}

	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/imp", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> impCityPoiDatas(String citycode, Integer typeid, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (StringUtils.isBlank(citycode)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "citycode参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid == null) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "typeid参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid != HotelSearchEnum.SAREA.getId() && typeid != HotelSearchEnum.SUBWAY.getId()) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "不支持的类型id: " + typeid);
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		try {
			datas = otsAdminService.readonlyImportPoiDatas(citycode, typeid);
			datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
		}
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/position/delete", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> deleteCityPoiDatas(String citycode, Integer typeid, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (StringUtils.isBlank(citycode)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "citycode参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid == null) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "typeid参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		if (typeid != HotelSearchEnum.SAREA.getId() && typeid != HotelSearchEnum.SUBWAY.getId()) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "不支持的类型id: " + typeid);
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		try {
			datas = otsAdminService.readonlyDeletePoiDatas(citycode, typeid);
		} catch (Exception e) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
		}
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/batchupdate/hotelbedtype", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> batchupdateHotelbedtype(String citycode, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		datas.putAll(hotelService.readonlyBatchUpdateHotelBedtype(citycode, token));
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 
	 * @param citycode
	 * @param typeid
	 * @return
	 */
	@RequestMapping(value = "/batchupdate/hoteltype", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> batchupdateHoteltype(String citycode, String token) {
		Map<String, Object> datas = new HashMap<String, Object>();
		if (StringUtils.isBlank(token)) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "token参数不能为空。");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		datas.putAll(hotelService.readonlyBatchUpdateHoteltype(citycode, token));
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	/**
	 * 特价房间跑财务对账数据
	 * @param startdateday
	 * @param enddateday
	 * @return
	 */
	@RequestMapping(value = "/report/launchPromo", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> launchBillRPPromotions(String startdateday, String enddateday) {
		Map<String, Object> datas = new HashMap<String, Object>();

		try {
			Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMAT_DATE);
			Date endTime = DateUtils.getDateFromString(enddateday, DateUtils.FORMAT_DATE);
			billService.createBillReport(beginTime, endTime);
		} catch (Exception ex) {
			logger.error("failed to createBillReport", ex);
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "failed to createBillReport");
		}

		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}
}
