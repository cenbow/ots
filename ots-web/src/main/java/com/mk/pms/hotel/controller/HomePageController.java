package com.mk.pms.hotel.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
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

import com.mk.ots.common.enums.HotelPromoEnum;
import com.mk.ots.common.enums.HotelSortEnum;
import com.mk.ots.common.enums.ShowAreaEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.restful.input.HotelHomePageReqEntity;
import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import com.mk.ots.search.service.IPromoSearchService;
import com.mk.ots.search.service.ISearchService;
import com.mk.ots.web.ServiceOutput;

@Controller
@RequestMapping(value = "/homepage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class HomePageController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IPromoSearchService promoService;

	@Autowired
	private ISearchService searchService;

	@Autowired
	private TRoomSaleShowConfigService roomSaleShowConfigService;

	private final Integer maxAllowedThemes = 6;

	private final Integer maxAllowedRoomtypes = 3;

	private final Integer maxAllowedPopular = 6;

	@RequestMapping("/popular")
	@SuppressWarnings("unchecked")
	public ResponseEntity<Map<String, Object>> listPopular(HttpServletRequest request,
			@Valid HotelHomePageReqEntity homepageReqEntity, Errors errors) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(request.getParameterMap());
		String errorMessage = "";

		if (logger.isInfoEnabled()) {
			logger.info(String.format("listPopular begings with parameters:%s...", params));
		}

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		if (StringUtils.isNotEmpty(errorMessage = countErrors(errors))) {
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(String.format("parameters validation failed with error %s", errorMessage));

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

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

		HotelQuerylistReqEntity reqEntity = buildPopularQueryEntity(homepageReqEntity);
		try {
			RoomSaleShowConfigDto showConfig = new RoomSaleShowConfigDto();
			showConfig.setNormalId(5);
			showConfig.setIsSpecial(Constant.STR_FALSE);
			showConfig.setShowArea(ShowAreaEnum.FrontPageBottom.getCode());

			List<RoomSaleShowConfigDto> showConfigs = roomSaleShowConfigService.queryRenderableShows(showConfig);
			if (showConfigs != null && showConfigs.size() > 0) {
				rtnMap.put("promoicon", showConfigs.get(0).getPromoicon());
				rtnMap.put("promotext", showConfigs.get(0).getPromotext());
				rtnMap.put("promonote", showConfigs.get(0).getPromonote());
				rtnMap.put("promoid", showConfigs.get(0).getPromoid());
				rtnMap.put("normalid", showConfigs.get(0).getNormalId());
			} else {
				logger.warn("no show configs has been loaded...");
			}

			Map<String, Object> responseHotels = searchService.readonlySearchHotels(reqEntity);

			List<Map<String, Object>> responseHotel = (List<Map<String, Object>>) responseHotels.get("hotel");
			List<Map<String, Object>> popularHotels = new ArrayList<Map<String, Object>>();
			rtnMap.put("hotel", popularHotels);

			if (responseHotel != null && responseHotel.size() > 0) {
				popularHotels.addAll(responseHotel.size() > maxAllowedPopular
						? responseHotel.subList(0, maxAllowedPopular) : responseHotel);
			}

			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "0");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "");
		} catch (Exception ex) {
			errorMessage = "failed to listpopular...";
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(errorMessage, ex);

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@RequestMapping("/themes")
	@SuppressWarnings("unchecked")
	public ResponseEntity<Map<String, Object>> listThemes(HttpServletRequest request,
			@Valid HotelHomePageReqEntity homepageReqEntity, Errors errors) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(request.getParameterMap());
		String errorMessage = "";

		if (logger.isInfoEnabled()) {
			logger.info(String.format("listThemes begings with parameters:%s...", params));
		}

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		if (StringUtils.isNotEmpty(errorMessage = countErrors(errors))) {
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			errorMessage = "parameters validation failed with error";
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(errorMessage);

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		String callVersion = (String) homepageReqEntity.getCallversion();
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

		try {
			HotelQuerylistReqEntity reqEntity = buildThemeQueryEntity(homepageReqEntity);

			Map<String, Object> themeResponse = promoService.readonlySearchHotels(reqEntity);

			List<Map<String, Object>> hotels = (List<Map<String, Object>>) themeResponse.get("hotel");
			if (hotels == null) {
				rtnMap.put("hotel", new ArrayList<Map<String, Object>>());
			} else {
				rtnMap.put("hotel", filterThemeHotels(hotels, reqEntity));
			}

			RoomSaleShowConfigDto showConfig = new RoomSaleShowConfigDto();
			showConfig.setPromoid(Integer.parseInt(reqEntity.getPromoid()));
			showConfig.setIsSpecial(Constant.STR_TRUE);
			showConfig.setShowArea(ShowAreaEnum.FrontPageBottom.getCode());

			List<RoomSaleShowConfigDto> showConfigs = roomSaleShowConfigService.queryRenderableShows(showConfig);
			if (showConfigs != null && showConfigs.size() > 0) {
				rtnMap.put("promoicon", showConfigs.get(0).getPromoicon());
				rtnMap.put("promotext", showConfigs.get(0).getPromotext());
				rtnMap.put("promonote", showConfigs.get(0).getPromonote());
				rtnMap.put("promoid", showConfigs.get(0).getPromoid());
				rtnMap.put("normalid", showConfigs.get(0).getNormalId());
			} else {
				logger.warn("no show configs has been loaded...");
			}

			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "0");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "");
		} catch (Exception ex) {
			logger.error("failed to searchHomePageThemes...", ex);

			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "failed to searchHomePageThemes...");

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> filterThemeHotels(List<Map<String, Object>> hotels,
			HotelQuerylistReqEntity reqEntity) {
		List<Map<String, Object>> hotelFiltered = new ArrayList<Map<String, Object>>();

		if (hotels != null && hotels.size() > maxAllowedThemes) {
			hotelFiltered.addAll(hotels.subList(0, maxAllowedThemes - 1));
		}

		for (Map<String, Object> hotel : hotels) {
			if (hotel.get("hotelid") == null) {
				continue;
			}

			String hotelid = "";

			try {
				hotelid = String.valueOf(hotel.get("hotelid"));
			} catch (Exception ex) {
				logger.warn(String.format("invalid hotelid:%s, skip...", hotel.get("hotelid")));
				continue;
			}

			reqEntity.setHotelid(hotelid);
			reqEntity.setIsroomtype("T");

			List<Map<String, Object>> themeTexts = new ArrayList<Map<String, Object>>();
			hotel.put("themetexts", themeTexts);

			List<Map<String, Object>> hotelpics = (List<Map<String, Object>>) hotel.get("hotelpic");
			List<Map<String, Object>> themepics = new ArrayList<Map<String, Object>>();
			if (hotelpics != null) {
				themepics.addAll(hotelpics);
			}

			hotel.put("themepic", themepics);

			List<Map<String, Object>> roomtypes = null;
			List<Map<String, Object>> newroomtypes = new ArrayList<Map<String, Object>>();
			hotel.put("roomtype", newroomtypes);
			try {
				roomtypes = promoService.queryThemeRoomtypes(hotel);
			} catch (Exception ex) {
				logger.warn(String.format("unable to queryThemeRoomtypes %s", hotelid), ex);
				continue;
			}

			if (roomtypes != null && roomtypes.size() > 0) {
				for (int i = 0; roomtypes != null && i < ((roomtypes.size() > maxAllowedRoomtypes) ? maxAllowedRoomtypes
						: roomtypes.size()); i++) {
					Map<String, Object> themeText = new HashMap<String, Object>();
					themeTexts.add(themeText);

					String roomtypename = (String) roomtypes.get(i).get("roomtypename");

					if (roomtypes.get(i).containsKey("greetscore")) {
						roomtypes.get(i).remove("greetscore");
					}

					themeText.put("text", roomtypename);
					themeText.put("color", "");

					newroomtypes.add(roomtypes.get(i));
				}
			}
		}

		return hotelFiltered;
	}

	private HotelQuerylistReqEntity buildPopularQueryEntity(HotelHomePageReqEntity homepageReqEntity) {
		HotelQuerylistReqEntity reqEntity = new HotelQuerylistReqEntity();
		reqEntity.setCallversion(homepageReqEntity.getCallversion());
		reqEntity.setCallmethod(homepageReqEntity.getCallmethod());
		reqEntity.setCityid(homepageReqEntity.getCityid());
		reqEntity.setCallentry(null);
		reqEntity.setUserlatitude(homepageReqEntity.getUserlatitude());
		reqEntity.setUserlongitude(homepageReqEntity.getUserlongitude());
		reqEntity.setIshotelpic("T");
		reqEntity.setLimit(maxAllowedPopular * 2);
		reqEntity.setIspromoonly(null);
		reqEntity.setOrderby(HotelSortEnum.ORDERNUMS.getId());

		Date day = new Date();
		String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
		String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(day, 1), DateUtils.FORMATSHORTDATETIME);

		reqEntity.setStartdateday(strCurDay);
		reqEntity.setEnddateday(strNextDay);

		return reqEntity;
	}

	private HotelQuerylistReqEntity buildThemeQueryEntity(HotelHomePageReqEntity homepageReqEntity) {
		HotelQuerylistReqEntity reqEntity = new HotelQuerylistReqEntity();
		reqEntity.setCallversion(homepageReqEntity.getCallversion());
		reqEntity.setCallmethod(homepageReqEntity.getCallmethod());
		reqEntity.setCityid(homepageReqEntity.getCityid());
		reqEntity.setPromoid(String.valueOf(HotelPromoEnum.Theme.getCode()));
		reqEntity.setCallentry(null);
		reqEntity.setUserlatitude(homepageReqEntity.getUserlatitude());
		reqEntity.setUserlongitude(homepageReqEntity.getUserlongitude());
		reqEntity.setIshotelpic("T");
		reqEntity.setOrderby(HotelSortEnum.ORDERNUMS.getId());
		reqEntity.setLimit(maxAllowedPopular * 2);

		Date day = new Date();
		String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
		String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(day, 1), DateUtils.FORMATSHORTDATETIME);

		reqEntity.setStartdateday(strCurDay);
		reqEntity.setEnddateday(strNextDay);

		return reqEntity;
	}

	private String countErrors(Errors errors) {
		StringBuffer bfErrors = new StringBuffer();
		for (ObjectError error : errors.getAllErrors()) {
			bfErrors.append(error.getDefaultMessage()).append("; ");
		}

		return bfErrors.toString();
	}

}
