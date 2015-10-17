package com.mk.ots.hotel.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Maps;
import com.mk.framework.AppUtils;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.CashBackService;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.restful.input.RoomstateQuerylistReqEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Room;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Roomtype;
import com.mk.ots.room.sale.service.RoomSaleService;
import com.mk.ots.search.service.ISearchService;
import com.mk.ots.web.ServiceOutput;

/**
 * 酒店前端控制类 发布接口
 * 
 * @author LYN
 *
 */
@Controller
@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class HotelController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 注入酒店业务类
	 */
	@Autowired
	private HotelService hotelService;

	@Autowired
	private RoomSaleService roomSaleService;

	@Autowired
	private RoomstateService roomstateService;

	@Autowired
	private HotelPriceService hotelPriceService;
	/**
	 * 日志类
	 */
	// @Autowired
	// private RoomStateLogUtil roomStateLogUtil;
	@Autowired
	private ElasticsearchProxy esProxy;

	// 返现服务类
	@Autowired
	private CashBackService cashBackService;

	/**
	 * 注入搜索服务类对象实例
	 */
	@Autowired
	private ISearchService searchService;

	private final SimpleDateFormat defaultFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	/**
	 * 
	 * @return
	 */
	@RequestMapping("/hotel/ping")
	public ResponseEntity<String> ping() {
		return new ResponseEntity<String>("request default method success.", HttpStatus.OK);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping("/hotel/init")
	@ResponseBody
	public ResponseEntity<ServiceOutput> init(String token, String cityid, String hotelid) {
		ServiceOutput output = new ServiceOutput();
		if (StringUtils.isBlank(token) || !Constant.STR_INNER_TOKEN.equals(token)) {
			output.setFault("token is invalidate.");
			return new ResponseEntity<ServiceOutput>(output, HttpStatus.OK);
		}
		if (StringUtils.isBlank(cityid)) {
			output.setFault("cityid is invalidate.");
			return new ResponseEntity<ServiceOutput>(output, HttpStatus.OK);
		}
		Date day = new Date();
		long starttime = day.getTime();
		try {
			int errors = 0;
			String errmsgs = "";
			ServiceOutput op1 = hotelService.readonlyInitPmsHotel(cityid, hotelid);
			if (!op1.isSuccess()) {
				errors += 1;
				errmsgs += op1.getErrmsg();
			}
			// 不初始化非签约酒店
			// ServiceOutput op2 = hotelService.readonlyInitNotPmsHotel(cityid,
			// hotelid);
			// if (!op2.isSuccess()) {
			// errors += 1;
			// errmsgs += op2.getErrmsg();
			// }
			if (errors > 0) {
				output.setSuccess(false);
				output.setFault(errmsgs);
			} else {
				output.setSuccess(true);
			}
		} catch (Exception e) {
			output.setFault(e.getMessage());
		}
		if (AppUtils.DEBUG_MODE) {
			long endtime = new Date().getTime();
			output.setMsgAttr("$times$", endtime - starttime + " ms");
		}
		return new ResponseEntity<ServiceOutput>(output, HttpStatus.OK);
	}

	/**
	 * 酒店综合查询接口 根据参数信息，获取酒店的集合 眯客2.1需求: 2015-07-15 处理方法都是
	 * 按照屏幕坐标找酒店，按照用户坐标与酒店坐标排序。 具体传值方法，都是客户端灵活处理的。 例如： 有关键字 1. 有用户坐标点
	 * 按默认坐标点周边60km半径查酒店 按用户与酒店距离排序 （Android 之前是将 默认坐标点当做屏幕坐标点传值，排序按照用户坐标与酒店坐标。）
	 * （微信是 将用户坐标当做 屏幕坐标以及用户坐标传值，排序按照用户坐标与酒店坐标。） 2. 无用户坐标点 按默认坐标点60km半径查酒店
	 * 按默认坐标点与酒店距离排序 （Android 是讲默认坐标当做 屏幕坐标和用户坐标传值，排序按照用户坐标与酒店坐标。 微信同样处理）
	 * 
	 * @param hotel
	 * @return
	 */
	private ResponseEntity<Map<String, Object>> getHotelList(ParamBaseBean pbb, THotel hotel,
			HttpServletRequest request) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(request.getParameterMap());
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/hotel/querylist", params,"ots");

		logger.info("【/hotel/auerylist】 begin...");
		logger.info("remote client request ui is: {}", request.getRequestURI());
		logger.info("【/hotel/querylist】 params is : {}--{}", params, pbb.toString());
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			Date day = new Date();
			long starttime = day.getTime();
			String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
			// search hotel from elasticsearch
			if (StringUtils.isBlank(hotel.getStartdateday())) {
				hotel.setStartdateday(strCurDay);
			}

			if (StringUtils.isBlank(hotel.getEnddateday())) {
				hotel.setEnddateday(strCurDay);
			}

			Map<String, Object> resultMap = hotelService.readonlyFromEsStore(hotel);
			ResponseEntity<Map<String, Object>> resultResponse = new ResponseEntity<Map<String, Object>>(resultMap,
					HttpStatus.OK);
			if (AppUtils.DEBUG_MODE) {
				long endtime = new Date().getTime();
				resultResponse.getBody().put("$times$", endtime - starttime + " ms");
			}

			logger.info("【/hotel/querylist】 end...");
			logger.info("【/hotel/querylist】response data:success::{} , count::{}\n",
					objectMapper.writeValueAsString(resultResponse.getBody().get("success")),
					resultResponse.getBody().get("count"));
			return resultResponse;
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.error("【/hotel/querylist】 is error: {} ", e.getMessage());
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	private String countErrors(Errors errors) {
		StringBuffer bfErrors = new StringBuffer();
		for (ObjectError error : errors.getAllErrors()) {
			bfErrors.append(error.getDefaultMessage()).append("; ");
		}

		return bfErrors.toString();
	}

	/**
	 * 
	 * 
	 * @param hotelEntity
	 * @param errors
	 * @return
	 */
	private Map<String, Object> invokeSearchHotels(HotelQuerylistReqEntity hotelEntity, Boolean promoOnly)
			throws Exception {
		Date day = new Date();

		// 当前日期
		String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
		// 下一天日期
		String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(day, 1), DateUtils.FORMATSHORTDATETIME);
		// search hotel from elasticsearch
		// 如果没有开始日期和截止日期，默认今住明退
		if (StringUtils.isBlank(hotelEntity.getStartdateday())) {
			hotelEntity.setStartdateday(strCurDay);
		}
		if (StringUtils.isBlank(hotelEntity.getEnddateday())) {
			hotelEntity.setEnddateday(strNextDay);
		}

		hotelEntity.setIspromoonly(promoOnly);

		Map<String, Object> resultMap = searchService.readonlySearchHotels(hotelEntity);

		return resultMap;
	}

	@RequestMapping(value = { "/hotel/querypromolist" })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> searchPromoHotels(HttpServletRequest request,
			@Valid HotelQuerylistReqEntity reqentity, Errors errors) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(request.getParameterMap());
		logger.info("【/hotel/querypromolist】 begin...");
		logger.info("remote client request ui is: {}", request.getRequestURI());
		logger.info("【/hotel/querypromolist】 request params is : {}", params);
		logger.info("【/hotel/querypromolist】 request entity is : {}", objectMapper.writeValueAsString(reqentity));
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		String errorMessage = "";
		if (StringUtils.isNotEmpty(errorMessage = countErrors(errors))) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(String.format("parameters validation failed with error %s", errorMessage));

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		try {
			Date day = new Date();
			long starttime = day.getTime();

			rtnMap = invokeSearchHotels(reqentity, Boolean.TRUE);

			ResponseEntity<Map<String, Object>> resultResponse = new ResponseEntity<Map<String, Object>>(rtnMap,
					HttpStatus.OK);
			if (AppUtils.DEBUG_MODE) {
				long endtime = new Date().getTime();
				resultResponse.getBody().put("$times$", endtime - starttime + " ms");
			}

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> hotels = (List<Map<String, Object>>) rtnMap.get("hotel");

			resultResponse.getBody().put("promotext", "重庆特价...");

			/**
			 * TODO: waiting for long's interface to get the times
			 */
			String startInternalTime = "2015-10-17 16:00";
			String endInternalTime = "2015-10-17 18:30";

			if (roomSaleService != null) {
				List<String> times = roomSaleService.queryPromoTime();
				startInternalTime = times.get(0);
				endInternalTime = times.get(1);
			}

			if (logger.isInfoEnabled()) {
				logger.info(String.format("promo time received, startTime:%s; endTime:%s", startInternalTime,
						endInternalTime));
			}

			LocalDateTime startExTime = LocalDateTime.fromDateFields(defaultFormatter.parse(startInternalTime));
			LocalDateTime endExTime = LocalDateTime.fromDateFields(defaultFormatter.parse(endInternalTime));

			resultResponse.getBody().put("ispromoting", searchService.isInPromoPeriod() ? 1 : 0);

			resultResponse.getBody().put("promostarttime",
					String.format("%s:%s", startExTime.getHourOfDay(), startExTime.getMinuteOfHour()));
			resultResponse.getBody().put("promoendtime",
					String.format("%s:%s", endExTime.getHourOfDay(), endExTime.getMinuteOfHour()));

			if (rtnMap.size() == 0) {
				resultResponse.getBody().put("promosec", 0);
			} else {
				LocalDateTime currentTime = LocalDateTime.now();
				Integer seconds = Seconds.secondsBetween(currentTime, endExTime).getSeconds();

				resultResponse.getBody().put("promosec", seconds);
			}

			logger.info("【/hotel/querypromolist】 end...");
			logger.info("【/hotel/querypromolist】response data:success::{} , count::{}\n",
					objectMapper.writeValueAsString(resultResponse.getBody().get("success")),
					resultResponse.getBody().get("count"));
			return resultResponse;
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.error("【/hotel/querypromolist】 is error: {} ", e);
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 酒店搜索API.
	 * 
	 * @param request
	 * @param reqentity
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/hotel/querylist" })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> searchHotel(HttpServletRequest request,
			@Valid HotelQuerylistReqEntity reqentity, Errors errors) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(request.getParameterMap());
		logger.info("【/hotel/querylist】 begin...");
		logger.info("remote client request ui is: {}", request.getRequestURI());
		logger.info("【/hotel/querylist】 request params is : {}", params);
		logger.info("【/hotel/querylist】 request entity is : {}", objectMapper.writeValueAsString(reqentity));

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		String errorMessage = "";
		if (StringUtils.isNotEmpty(errorMessage = countErrors(errors))) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(String.format("parameters validation failed with error %s", errorMessage));

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		try {
			Date day = new Date();
			long starttime = day.getTime();

			rtnMap = invokeSearchHotels(reqentity, null);

			ResponseEntity<Map<String, Object>> resultResponse = new ResponseEntity<Map<String, Object>>(rtnMap,
					HttpStatus.OK);
			if (AppUtils.DEBUG_MODE) {
				long endtime = new Date().getTime();
				resultResponse.getBody().put("$times$", endtime - starttime + " ms");
			}

			logger.info("【/hotel/querylist】 end...");
			logger.info("【/hotel/querylist】response data:success::{} , count::{}\n",
					objectMapper.writeValueAsString(resultResponse.getBody().get("success")),
					resultResponse.getBody().get("count"));
			return resultResponse;
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.error("【/hotel/querylist】 is error: {} ", e.getMessage());
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 查询酒店房态信息
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/roomstate/querylist" })
	public ResponseEntity<Map<String, Object>> getHotelRoomState(ParamBaseBean pbb, String roomno,
			@Valid RoomstateQuerylistReqEntity params, Errors errors) throws Exception {
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/roomstate/querylist",
		// params.toString(),"ots");
		logger.info("【/roomstate/querylist】 params is : {}", pbb.toString());
		// 办理再次入住传 roomno
		// 调用service方法
		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		StringBuffer bfErrors = new StringBuffer();
		for (ObjectError error : errors.getAllErrors()) {
			bfErrors.append(error.getDefaultMessage()).append("; ");
		}
		if (bfErrors.length() > 0) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, bfErrors.toString());
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		// 加埋点
		Transaction t = Cat.newTransaction("RoomState", "getHotelRoomState");
		t.setStatus(Transaction.SUCCESS);
		try {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			List<RoomstateQuerylistRespEntity> list = roomstateService.findHotelRoomState(roomno, params);
			rtnMap.put("hotel", list);

			// 埋点真实用户进入酒店后满房的次数begin
			if (list != null && list.size() > 0) {
				RoomstateQuerylistRespEntity result = list.get(0);
				// 空闲房间数
				int freeRoomCount = 0;
				for (Roomtype roomtype : result.getRoomtype()) {
					for (Room room : roomtype.getRooms()) {
						if ("vc".equals(room.getRoomstatus())) {
							freeRoomCount++;
						}
					}
				}
				/*
				 * if("F".equals(result.getVisible()) ||
				 * "F".equals(result.getOnline())){ freeRoomCount=0; }
				 */
				if (freeRoomCount == 0) {
					logger.info("记录埋点:{}", params.getHotelid());
					Cat.logEvent("ROOMSTATE", "fullroomnum", Event.SUCCESS, "");
				}
			}
			// 埋点真实用户进入酒店后满房的次数end
			long finishTime = new Date().getTime();
			if (AppUtils.DEBUG_MODE) {
				rtnMap.put(ServiceOutput.STR_MSG_TIMES, finishTime - startTime + "ms");
			}
		} catch (Exception e) {
			t.setStatus(e);
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			e.printStackTrace();
			logger.info("查询房态报错:{}{}", params.getHotelid(), e.getMessage());
			throw e;
		} finally {
			t.complete();
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	@RequestMapping(value = "/hotel/syncNoPMS")
	public ResponseEntity<Map<String, Object>> syncNoPMSHotel(String filename) {
		try {
			boolean succeed = hotelService.syncNoPMSHotel(filename);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("success", succeed);
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} catch (Exception e) {
			throw MyErrorEnum.errorParm.getMyException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hotel/kw/save")
	public ResponseEntity<Map<String, Object>> saveKeywords(Long hotelid, String keywords) {
		Map<String, Object> rtnMap = hotelService.saveKeywords(hotelid, keywords);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 更新酒店眯客价Redis缓存: H端在更新酒店价格时，调用该接口更新Redis中该酒店的眯客价缓存。
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @param roomtypeid
	 *            参数：房型id
	 * @param isforce
	 *            参数：是否强制更新。 T是：先删除Redis缓存，查询数据库，再放到Redis中；
	 *            F否：不删除Redis缓存，Redis有的话直接返回，没有再查询数据库，放到Redis中。
	 * @return
	 */
	@RequestMapping(value = "/hotel/updateHotelMikepricesCache")
	public ResponseEntity<Map<String, Object>> updateHotelMikepricesCache(Long hotelid, Long roomtypeid,
			String isforce) {
		boolean forceUpdate = StringUtils.isBlank(isforce) ? false : Constant.STR_TRUE.equals(isforce);
		Map<String, Object> rtnMap = roomstateService.updateHotelMikepricesCache(hotelid, roomtypeid, forceUpdate);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 酒店眯客价: 返回酒店一段时间内的眯客价和眯客价对应房型的门市价。
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @param startdateday
	 *            参数：开始日期
	 * @param enddateday
	 *            参数：截止日期
	 * @return
	 */
	@RequestMapping(value = "/hotel/mikeprices")
	public ResponseEntity<Map<String, Object>> getHotelMikePrices(Long hotelid, String startdateday,
			String enddateday) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		if (hotelid == null || StringUtils.isBlank(hotelid.toString())) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "参数hotelid不能为空.");
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		if (StringUtils.isBlank(startdateday)) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "参数startdateday不能为空.");
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		if (StringUtils.isBlank(enddateday)) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "参数enddateday不能为空.");
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		// 调用service方法
		long startTime = new Date().getTime();
		try {
			String[] prices = null;
			if (hotelPriceService.isUseNewPrice())
				prices = hotelPriceService.getHotelMikePrices(hotelid, startdateday, enddateday);
			else
				prices = roomstateService.getHotelMikePrices(hotelid, startdateday, enddateday);
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			if (prices != null && prices.length > 0) {
				rtnMap.put("minmikeprice", prices[0]);
				rtnMap.put("roomtypeprice", prices[1]);
			}
			if (AppUtils.DEBUG_MODE) {
				long finishTime = new Date().getTime();
				rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
			}
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			e.printStackTrace();
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 眯客2.5需求 用户登录后查询该用户已住过的历史酒店。
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @param page
	 *            参数：分页页码（如果有分页）
	 * @param limit
	 *            参数：每页条数（如果有分页）
	 * @param token
	 *            参数：token验证码
	 */
	@RequestMapping(value = "/history/querylist", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> queryHistoryHotels(ParamBaseBean pbb, String citycode, Integer page,
			Integer limit, String token) {
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/history/querylist",
		// citycode+","+page+","+limit,"ots");
		logger.info("HotelController::queryHistoryHotels::params{}  begin", citycode + "," + page + "," + limit);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(token)) {
				throw MyErrorEnum.errorParm.getMyException("传参：token为空！");
			}
			if (page == null || StringUtils.isBlank(page.toString())) {
				throw MyErrorEnum.errorParm.getMyException("传参：页数为空!");
			}
			if (limit == null || StringUtils.isBlank(limit.toString())) {
				throw MyErrorEnum.errorParm.getMyException("传参：每页显示量为空!");
			}
			int start = (page - 1) * limit;
			List<Map<String, Object>> list = hotelService.queryHistoryHotels(token, citycode, start, limit);
			result.put("hotel", list);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelController::queryHistoryHotels::error{}", e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelController::queryHistoryHotels  end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/**
	 * 眯客2.5需求 查询已住历史酒店次数
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @param token
	 *            参数：token验证码
	 */
	@RequestMapping(value = "/history/querycount", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> queryHistoryHotelsCount(ParamBaseBean pbb, String citycode,
			String token) {
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/history/querycount",
		// token+","+citycode,"ots");
		logger.info("HotelController::queryHistoryHotelsCount::params{}  begin", token + "," + citycode);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(token)) {
				throw MyErrorEnum.errorParm.getMyException("传参：token为空！");
			}
			long hotelcount = hotelService.queryHistoryHotelsCount(token, citycode);
			result.put("count", hotelcount);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelController::queryHistoryHotelsCount::error{}", e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelController::queryHistoryHotelsCount  end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/**
	 * 眯客2.5需求 删除某会员某酒店住店历史纪录
	 * 
	 * @param token
	 *            参数：token验证码
	 * @param hotelid
	 *            参数：酒店id
	 */
	@RequestMapping(value = "/history/deleterecords", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> deleteHotelStats(ParamBaseBean pbb, String token, Long hotelid) {
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/history/deleterecords",
		// token+","+hotelid,"ots");
		logger.info("HotelController::deleteHotelStats::params{}  begin", token + "," + hotelid);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(token)) {
				throw MyErrorEnum.errorParm.getMyException("传参：token为空！");
			}
			if (hotelid == null || StringUtils.isBlank(hotelid.toString())) {
				throw MyErrorEnum.errorParm.getMyException("传参：酒店id为空!");
			}
			hotelService.deleteHotelStats(token, hotelid);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelController::deleteHotelStats::error{}", e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelController::deleteHotelStats  end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/**
	 * 酒店信息查询接口 根据参数信息，获取酒店信息 眯客2.5需求: 2015-08-19
	 * 
	 * @param hotel
	 * @return
	 */
	@RequestMapping(value = { "/hoteldetail/queryinfo" })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getHotelDetail(ParamBaseBean pbb, Long hotelid,
			HttpServletRequest request) throws Exception {
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/hoteldetail/queryinfo",
		// String.valueOf(hotelid), "ots");
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		if (hotelid == null) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "缺少必须参数hotelid");
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		try {
			Map<String, Object> hotelMap = hotelService.readonlyHotelDetail(hotelid);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			resultList.add(hotelMap);

			rtnMap.put("hotel", resultList);
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 酒店房型信息查询接口 根据参数信息，获取酒店信息 眯客2.5需求: 2015-08-19
	 * 
	 * @param hotel
	 * @return
	 */
	@RequestMapping(value = { "/roomtype/queryinfo" })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getRoomTypeDetail(ParamBaseBean pbb, Long roomtypeid,
			HttpServletRequest request) throws Exception {
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/roomtype/queryinfo",
		// roomtypeid+"","ots");
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			if (roomtypeid == null) {
				throw MyErrorEnum.errorParm.getMyException("缺少必要参数!");
			}
			rtnMap = hotelService.readonlyRoomTypeInfo(roomtypeid);
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 用户查看酒店图片信息 根据参数信息，获取酒店所有图片信息 眯客2.5需求: 2015-08-19
	 * 
	 * @param hotel
	 * @return
	 */
	@RequestMapping(value = { "/hotelpic/querylist" })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getHotelPics(ParamBaseBean pbb, Long hotelid, HttpServletRequest request)
			throws Exception {
		// 日志
		// roomStateLogUtil.sendLog(pbb.getHardwarecode(),pbb.getCallmethod(),
		// pbb.getCallversion(), pbb.getIp(), "/hotelpic/querylist",
		// String.valueOf(hotelid) ,"ots");
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			if (hotelid == null) {
				rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
				rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
				rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "缺少必须参数hotelid");
				return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
			}
			rtnMap = hotelService.readonlyHotelPics(hotelid);
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 更新ES中酒店的眯客价: 更新一段时间的酒店眯客价数据到ES酒店信息中，供酒店搜索使用。
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @return
	 */
	@RequestMapping(value = "/hotel/updatemikeprices")
	public ResponseEntity<Map<String, Object>> updateEsMikePrice(Long hotelid) {
		logger.info("updateEsMikePrice method begin...");
		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			if (hotelid == null) {
				// 如果不传hotelid参数，批量更新所有酒店的眯客价。
				hotelService.batchUpdateEsMikePrice();
			} else {
				// 如果传hotelid，更新指定酒店的眯客价。
				hotelService.updateEsMikePrice(hotelid);
			}
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			if (AppUtils.DEBUG_MODE) {
				long finishTime = new Date().getTime();
				rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
			}
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
			e.printStackTrace();
			logger.error("更新ES眯客价出错: " + e.getLocalizedMessage(), e);
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 返回酒店类型 HMSHOTEL(1,"旅馆"), THEMEDHOTEL(2,"主题酒店"), PLAZAHOTEL(3,"精品酒店"),
	 * APARTMENTHOTEL(4,"公寓"), HOSTELS(5,"招待所"), INNER(6,"客栈");
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @return
	 */
	@RequestMapping(value = "/hoteltype/querylist")
	public ResponseEntity<Map<String, Object>> getHotelTypes(ParamBaseBean pbb, Long hotelid) {
		logger.info("【/hoteltype/querylist】 params is : {}--{}", hotelid, pbb.toString());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			List<Map<String, String>> datas = hotelService.readonlyFindHotelTypes();
			resultMap.put("datas", datas);
			resultMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			resultMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			resultMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			resultMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
			logger.error("返回酒店类型出错: {}", e.getLocalizedMessage());
			throw e;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
	}

	/**
	 * 强制更新指定城市酒店的眯客价Redis缓存（/hotel/updateHotelMikepricesCache）。
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @return
	 */
	@RequestMapping(value = "/hotel/updatemikepricecache")
	public ResponseEntity<Map<String, Object>> updateMikePriceCache(String citycode) {
		logger.info("updateMikePriceCache method begin...");
		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			if (StringUtils.isNotBlank(citycode)) {
				hotelService.batchUpdateRedisMikePrice(citycode);
			}
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			if (AppUtils.DEBUG_MODE) {
				long finishTime = new Date().getTime();
				rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
			}
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
			e.printStackTrace();
			logger.error("更新眯客价redis缓存出错: " + e.getLocalizedMessage(), e);
		}
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 清除es中的垃圾酒店数据
	 * 
	 * @return
	 */
	// @RequestMapping(value="/hotel/cleareshotelnotinthotel")
	// public ResponseEntity<Map<String, Object>> clearESHotelNotInThotel(String
	// citycode) {
	// return new
	// ResponseEntity<Map<String,Object>>(hotelService.readonlyClearEsHotelNotInTHotel(citycode),HttpStatus.OK);
	// }
}
