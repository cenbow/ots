package com.mk.ots.roomsale.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.common.enums.HotelPromoEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.restful.input.HotelHomePageReqEntity;
import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import com.mk.ots.roomsale.service.RoomSaleService;
import com.mk.ots.search.service.IPromoSearchService;
import com.mk.ots.web.ServiceOutput;

/**
 *
 * 特价类型接口
 */
@Controller
public class HotelPromoController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoomSaleConfigInfoService roomSaleConfigInfoService;
	@Autowired
	private RoomSaleService roomSaleService;
	@Autowired
	private IPromoSearchService promoSearchService;

	/**
	 * 活动查询
	 **/
	@RequestMapping(value = "/hotel/querytypelist", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> querytypelist(ParamBaseBean pbb, String cityid, String saletypeid,
			Integer page, Integer limit) {
		logger.info("HotelPromoController::querytypelist::params{}  begin",
				pbb + "," + saletypeid + "," + cityid + "," + page + "," + limit);
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			if (page == null || StringUtils.isBlank(page.toString())) {
				page = 1;
			}
			if (limit == null || StringUtils.isBlank(limit.toString())) {
				limit = 10;
			}

			int start = (page - 1) * limit;

			if (StringUtils.isEmpty(saletypeid)) {
				result.put(ServiceOutput.STR_MSG_SUCCESS, false);
				result.put(ServiceOutput.STR_MSG_ERRCODE, "404");
				result.put(ServiceOutput.STR_MSG_ERRMSG, "没有活动");
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}

			List<TRoomSaleConfigInfo> roomSaleConfigInfoList = roomSaleConfigInfoService
					.queryListBySaleTypeId(Integer.parseInt(saletypeid), start, limit);

			List<JSONObject> list = new ArrayList<JSONObject>();
			if (CollectionUtils.isNotEmpty(roomSaleConfigInfoList)) {
				for (TRoomSaleConfigInfo saleConfigInfo : roomSaleConfigInfoList) {
					long sec = DateUtils.calDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

					long nextsec = DateUtils.calNextDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

					long endSec = DateUtils.calEndDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());
					if (sec < 0) {
						continue;
					}
					JSONObject ptype1 = new JSONObject();

					if (logger.isInfoEnabled()) {
						logger.info(String.format("promotypeid: %s; promotypetext: %s; promotypeprice:%s",
								saleConfigInfo.getId(), saleConfigInfo.getSaleLabel(), saleConfigInfo.getSaleValue()));
					}

					ptype1.put("promotypeid", saleConfigInfo.getId());
					ptype1.put("promotypetext", saleConfigInfo.getSaleLabel());
					ptype1.put("promotypeprice", saleConfigInfo.getSaleValue());
					ptype1.put("promosec", sec / 1000); // 秒
					ptype1.put("promosecend", endSec / 1000); // 距离结束时间（s）
					ptype1.put("nextpromosec", nextsec / 1000); // 距离下一段结束时间（s）
					list.add(ptype1);
				}
			}

			result.put("promotypes", list);

			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelPromoController::querytypelist::error{}", e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::querylist::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/promo/ispromocity", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> ispromocity(ParamBaseBean pbb, String cityid) {
		logger.info("HotelPromoController::ispromocity::params{}  begin", pbb + "," + cityid);
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean isPromoCity = roomSaleService.checkPromoCity(cityid);
		result.put("result", isPromoCity);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/promo/onedollarlist", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> onedollarlist(HotelHomePageReqEntity homepageReqEntity) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(homepageReqEntity);
		String errorMessage = "";
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("onedollarlist begins with parameters:%s...", params));
		}

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		String callVersion = (String) homepageReqEntity.getCallversion();
		Double latitude = (Double) homepageReqEntity.getUserlatitude();
		Double longitude = (Double) homepageReqEntity.getUserlongitude();

		if (StringUtils.isNotBlank(callVersion) && "3.3".compareTo(callVersion) > 0) {
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			errorMessage = "callversion is lower than 3.3, not accessible in this function... ";
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(errorMessage);

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		} else if (StringUtils.isBlank(callVersion)) {
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			errorMessage = "callversion is a must... ";
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(errorMessage);

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		if (latitude == null || longitude == null) {
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			errorMessage = "latitude/longitude is a must... ";
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(errorMessage);

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		HotelQuerylistReqEntity queryReq = buildOnedollarQueryEntity(homepageReqEntity);

		Long promosec = 0L;
		Long promosecond = 0L;
		Long nextpromosec = 0L;

		try {
			List<TRoomSaleConfigInfo> roomSales = roomSaleConfigInfoService
					.queryListBySaleTypeId(HotelPromoEnum.OneDollar.getCode(), 0, 1);

			if (roomSales != null && roomSales.size() > 0) {
				TRoomSaleConfigInfo saleConfigInfo = roomSales.get(0);

				promosec = DateUtils.calDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
						saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

				nextpromosec = DateUtils.calNextDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
						saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

				promosecond = DateUtils.calEndDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
						saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());
				if (promosec < 0) {
					promosec = 0L;
				}

				rtnMap.put("promosec", promosec / 1000);
				rtnMap.put("promosecend", promosecond / 1000);
				rtnMap.put("nextpromosec", nextpromosec / 1000);
				rtnMap.put("promonote", "");

				Map<String, Object> hotels = promoSearchService.readonlySearchHotels(queryReq);
				rtnMap.putAll(hotels);
				rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "0");
				rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "");
				rtnMap.remove("success");
			} else {
				rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
				rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "failed to do onedollarlist query...");
			}

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("failed to do onedollarlist query...", ex);

			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "failed to do onedollarlist query...");
		}

		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	private HotelQuerylistReqEntity buildOnedollarQueryEntity(HotelHomePageReqEntity homepageReqEntity) {
		HotelQuerylistReqEntity reqEntity = new HotelQuerylistReqEntity();
		reqEntity.setCallversion(homepageReqEntity.getCallversion());
		reqEntity.setCallmethod(homepageReqEntity.getCallmethod());
		reqEntity.setCityid(homepageReqEntity.getCityid());
		reqEntity.setPromoid(String.valueOf(HotelPromoEnum.OneDollar.getCode()));
		reqEntity.setCallentry(null);
		reqEntity.setUserlatitude(homepageReqEntity.getUserlatitude());
		reqEntity.setUserlongitude(homepageReqEntity.getUserlongitude());
		reqEntity.setIshotelpic("T");

		Date day = new Date();
		String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
		String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(day, 1), DateUtils.FORMATSHORTDATETIME);

		reqEntity.setStartdateday(strCurDay);
		reqEntity.setEnddateday(strNextDay);

		return reqEntity;
	}

	@RequestMapping(value = "/promo/queryrange", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> queryrange(ParamBaseBean pbb, String promoid) {
		logger.info("HotelPromoController::queryrange::params{}  begin", pbb + "," + promoid);
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			Integer limit = 10;
			Integer page = 1;

			int start = (page - 1) * limit;
			if (StringUtils.isEmpty(promoid)) {
				result.put(ServiceOutput.STR_MSG_SUCCESS, false);
				result.put(ServiceOutput.STR_MSG_ERRCODE, "404");
				result.put(ServiceOutput.STR_MSG_ERRMSG, "没有活动");
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}

			List<TRoomSaleConfigInfo> roomSaleConfigInfoList = roomSaleConfigInfoService
					.queryListBySaleTypeId(Integer.parseInt(promoid), start, limit);

			List<JSONObject> list = new ArrayList<JSONObject>();
			if (CollectionUtils.isNotEmpty(roomSaleConfigInfoList)) {
				for (TRoomSaleConfigInfo saleConfigInfo : roomSaleConfigInfoList) {
					long sec = DateUtils.calDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

					long nextsec = DateUtils.calNextDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

					long endSec = DateUtils.calEndDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());
					if (sec < 0) {
						continue;
					}
					JSONObject ptype1 = new JSONObject();

					if (logger.isInfoEnabled()) {
						logger.info(String.format("promotypeid: %s; promotypetext: %s; promotypeprice:%s",
								saleConfigInfo.getId(), saleConfigInfo.getSaleLabel(), saleConfigInfo.getSaleValue()));
					}

					ptype1.put("promotypeid", saleConfigInfo.getId());
					ptype1.put("promotypetext", saleConfigInfo.getSaleLabel());
					ptype1.put("promotypeprice", saleConfigInfo.getSaleValue());
					ptype1.put("promosec", sec / 1000); // 秒
					ptype1.put("promosecend", endSec / 1000); // 距离结束时间（s）
					ptype1.put("nextpromosec", nextsec / 1000); // 距离下一段结束时间（s）
					list.add(ptype1);
				}
			}

			result.put("promotypes", list);

			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelPromoController::querytypelist::error{}", e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::querylist::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

}
