package com.mk.ots.inner.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.bill.service.BillOrderDetailService;
import com.mk.ots.bill.service.BillOrderService;
import com.mk.ots.bill.service.ServiceCostRuleService;
import com.mk.ots.common.enums.HotelSearchEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.OtsVersion;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.inner.service.IOtsAdminService;
import com.mk.ots.order.service.QiekeRuleService;
import com.mk.ots.web.ServiceOutput;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	@Autowired
	private ServiceCostRuleService serviceCostRuleService;
	@Autowired
	private BillOrderDetailService billOrderDetailService;

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
		logger.info(String.format("url /report/launchPromo params startdateday[%s],enddateday[%s]", startdateday, enddateday));
		Map<String, Object> datas = new HashMap<String, Object>();
		try {
			Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
			Date endTime = DateUtils.getDateFromString(enddateday, DateUtils.FORMATSHORTDATETIME);
			billService.createBillReport(beginTime, endTime);
		} catch (Exception ex) {
			logger.error("failed to createBillReportByHotelId", ex);
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, "failed to createBillReportByHotelId");
			return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
		}
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/genBillOrders", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> genBillOrders(String startdateday) {
		logger.info(String.format("url /report/genBillOrders params startdateday[%s]", startdateday));
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		// 获取上个月
		BillOrderDAO billOrderDAO = AppUtils.getBean(BillOrderDAO.class);
		Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
		Cat.logEvent("OtsJob", "BillOrdersJob.executeInternal", Event.SUCCESS, "");
		logger.info("BillOrdersJob::genBillOrders::{}", beginTime);
		billOrderDAO.genBillOrders(beginTime, null, null);
		logger.info("BillOrdersJob::genBillOrders::end");
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}


	@RequestMapping(value = "/report/runtWeekClearing", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> runtWeekClearing(String startdateday) {
		logger.info(String.format("url /report/runtWeekClearing params startdateday[%s]", startdateday));
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		logger.info("周结算开始");
		Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
		BillOrderService billOrderDAO = AppUtils.getBean(BillOrderService.class);
		billOrderDAO.runtWeekClearing(beginTime, null);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/updateTopInvalidReason", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updateTopInvalidReason(String startdateday) {
		logger.info(String.format("url /report/updateTopInvalidReason"));
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		QiekeRuleService qiekeRuleService = AppUtils.getBean(QiekeRuleService.class);
		Date beginTime = DateUtils.getDateFromString(startdateday, DateUtils.FORMATSHORTDATETIME);
		qiekeRuleService.updateTopInvalidReason(beginTime);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/genBillConfirmChecks", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> genBillConfirmChecks(String startdateday) {
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		logger.info("genBillConfirmChecks::{}", startdateday);
		billService.genBillConfirmChecks(DateUtils.getDateFromString(startdateday), null, null);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/genOrderDetail", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> genOrderDetail(String startdateday) {
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		logger.info("genOrderDetail::{}", startdateday);
		billOrderDetailService.genOrderDetail(DateUtils.getDateFromString(startdateday));
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/genOrderDetailWeek", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> genOrderDetailWeek(String startdateday) {
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		logger.info("genOrderDetailWeek::{}", startdateday);
		billOrderDetailService.genOrderDetailWeek(DateUtils.getDateFromString(startdateday));
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}

	@RequestMapping(value = "/report/getServiceCostByOrderType", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> getServiceCostByOrderType(String startdateday,Boolean qiekeFlag, BigDecimal price, String hotelCityCode) {
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
		logger.info("genBillConfirmChecks::{}", startdateday);
		serviceCostRuleService.getServiceCostByOrderType(DateUtils.getDateFromString(startdateday), qiekeFlag, price, hotelCityCode);
		return new ResponseEntity<Map<String, Object>>(datas, HttpStatus.OK);
	}


}
