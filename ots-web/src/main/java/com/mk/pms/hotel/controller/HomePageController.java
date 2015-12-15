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
import com.mk.ots.common.enums.ShowAreaEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.restful.input.HotelHomePageReqEntity;
import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import com.mk.ots.search.service.IPromoSearchService;
import com.mk.ots.web.ServiceOutput;

@Controller
@RequestMapping(value = "/homepage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class HomePageController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IPromoSearchService promoService;

	@Autowired
	private TRoomSaleShowConfigService roomSaleShowConfigService;

	private final Integer maxAllowedThemes = 3;

	private final Integer maxAllowedRoomtypes = 3;

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
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(String.format("parameters validation failed with error %s", errorMessage));

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		String callVersion = (String) rtnMap.get("callversion");
		if ("3.3".compareTo(callVersion) <= 0) {
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			errorMessage = "callversion is lower than 3.3, not accessible in this function... ";
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(errorMessage);

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		try {
			HotelQuerylistReqEntity reqEntity = buildThemeQueryEntity(homepageReqEntity);

			Map<String, Object> themeResponse = promoService.readonlySearchHotels(reqEntity);

			List<Map<String, Object>> hotels = (List<Map<String, Object>>) themeResponse.get("hotel");
			rtnMap.put("hotel", filterHotels(hotels));

			RoomSaleShowConfigDto showConfig = new RoomSaleShowConfigDto();
			showConfig.setPromoid(Integer.parseInt(reqEntity.getPromoid()));
			showConfig.setIsSpecial(Constant.STR_FALSE);
			showConfig.setShowArea(ShowAreaEnum.FrontPageBottom.getCode());

			List<RoomSaleShowConfigDto> showConfigs = roomSaleShowConfigService.queryRenderableShows(showConfig);
			if (showConfigs != null && showConfigs.size() > 0) {
				rtnMap.put("promoicon", showConfigs.get(0).getPromoicon());
				rtnMap.put("promotext", showConfigs.get(0).getPromotext());
				rtnMap.put("promonote", showConfigs.get(0).getPromonote());
			}
		} catch (Exception ex) {
			logger.error("failed to searchHomePageThemes...", ex);

			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "failed to searchHomePageThemes...");

			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> filterHotels(List<Map<String, Object>> hotels) {
		List<Map<String, Object>> hotelFiltered = new ArrayList<Map<String, Object>>();

		if (hotels != null && hotels.size() > maxAllowedThemes) {
			hotelFiltered.addAll(hotels.subList(0, maxAllowedThemes - 1));
		}

		for (Map<String, Object> hotel : hotels) {
			List<Map<String, Object>> roomtypes = (List<Map<String, Object>>) hotel.get("roomtype");
			List<Map<String, Object>> themeTexts = new ArrayList<Map<String, Object>>();
			hotel.put("themetexts", themeTexts);
			
			for (int i = 0; roomtypes != null
					&& i < ((roomtypes.size() > maxAllowedRoomtypes) ? maxAllowedRoomtypes : 0); i++) {
				Map<String, Object> themeText = new HashMap<String, Object>();
				themeTexts.add(themeText);
				
				String roomtypename = (String)roomtypes.get(i).get("roomtypename");
				
				themeText.put("text", roomtypename);
				themeText.put("color", "");
			}
		}

		return hotelFiltered;
	}

	private HotelQuerylistReqEntity buildThemeQueryEntity(HotelHomePageReqEntity homepageReqEntity) {
		HotelQuerylistReqEntity reqEntity = new HotelQuerylistReqEntity();
		reqEntity.setCallversion(homepageReqEntity.getCallversion());
		reqEntity.setCallmethod(homepageReqEntity.getCallmethod());
		reqEntity.setCityid(homepageReqEntity.getCityid());
		reqEntity.setPromoid(String.valueOf(HotelPromoEnum.Theme.getCode()));
		reqEntity.setCallentry(null);

		Date day = new Date();
		String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
		String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(day, 1), DateUtils.FORMATSHORTDATETIME);

		reqEntity.setStartdateday(strCurDay);
		reqEntity.setStartdateday(strNextDay);

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
