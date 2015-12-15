package com.mk.ots.hotel.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.ots.common.enums.FrontPageEnum;
import com.mk.ots.common.enums.HotelPromoEnum;
import com.mk.ots.common.enums.HotelSortEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import com.mk.ots.roomsale.service.RoomSaleService;
import com.mk.ots.search.service.IPromoSearchService;
import com.mk.ots.search.service.ISearchService;
import com.mk.ots.web.ServiceOutput;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 酒店前端控制类 发布接口
 * 
 * @author kirin
 *
 */
@Controller
@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class HomePageController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 注入酒店业务类
	 */
	@Autowired
	private HotelService hotelService;


	@Autowired
	private RoomstateService roomstateService;

	@Autowired
	private HotelPriceService hotelPriceService;

	/**
	 * 注入搜索服务类对象实例
	 */
	@Autowired
	private ISearchService searchService;
	@Autowired
	private IPromoSearchService promoSearchService;

	@Autowired
	private RoomSaleService roomSaleService;

	@Autowired
	private RoomSaleConfigInfoService roomSaleConfigInfoService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping("/homepage/ping")
	public ResponseEntity<String> ping() {
		return new ResponseEntity<String>("request default method success.", HttpStatus.OK);
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
	@RequestMapping(value = { "/homepage/promolist" })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> searchHomePageHotel(HttpServletRequest request,
			@Valid HotelQuerylistReqEntity reqentity, Errors errors) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String params = objectMapper.writeValueAsString(request.getParameterMap());
		logger.info("【/homepage/promolist】 begin...");
		logger.info("remote client request ui is: {}", request.getRequestURI());
		logger.info("【/homepage/promolist】 request params is : {}", params);
		logger.info("【/homepage/promolist】 request entity is : {}", objectMapper.writeValueAsString(reqentity));
		Cat.logEvent("/homepage/promolist", reqentity.getCallmethod(), Event.SUCCESS, params);
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		String errorMessage = "";
		if (StringUtils.isNotEmpty(errorMessage = countErrors(errors))) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, errorMessage);

			logger.error(String.format("parameters validation failed with error %s", errorMessage));
			Cat.logEvent("querylistException", reqentity.getCallmethod(), Event.SUCCESS,
					String.format("parameters validation failed with error %s", errorMessage));
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}

		try {
			HotelQuerylistReqEntity hotelEntity = new HotelQuerylistReqEntity();
			String strCurDay = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
			String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(new Date(), 1),
					DateUtils.FORMATSHORTDATETIME);

			hotelEntity.setCityid(reqentity.getCityid());
			hotelEntity.setCallversion(reqentity.getCallversion());
			hotelEntity.setUserlatitude(reqentity.getUserlatitude());
			hotelEntity.setUserlongitude(reqentity.getUserlongitude());

			hotelEntity.setPillowlongitude(reqentity.getPillowlongitude());
			hotelEntity.setPillowlatitude(reqentity.getPillowlatitude());

			hotelEntity.setStartdateday(strCurDay);
			hotelEntity.setEnddateday(strNextDay);
			hotelEntity.setIshotelpic("T");
			hotelEntity.setIspromoonly(true);
			hotelEntity.setOrderby(HotelSortEnum.DISTANCE.getId());

			if (hotelEntity.getPage() == null){
				hotelEntity.setPage(FrontPageEnum.page.getId());
			}

			if (hotelEntity.getLimit() == null){
				hotelEntity.setLimit(FrontPageEnum.recommendLimit.getId());
			}

			List<TRoomSaleConfigInfo> roomSaleConfigInfoList = roomSaleConfigInfoService.queryListBySaleTypeId(
					HotelPromoEnum.Night.getCode(), 0, 1);

			if (roomSaleConfigInfoList != null && roomSaleConfigInfoList.size() > 0){
				TRoomSaleConfigInfo saleConfigInfo = roomSaleConfigInfoList.get(0);
				long sec = DateUtils.calDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
						saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

				long nextsec = DateUtils.calNextDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
						saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());

				long endSec = DateUtils.calEndDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
						saleConfigInfo.getStartTime(), saleConfigInfo.getEndTime());
				if (sec < 0){
					sec = 0;
				}


				Map<String, Object> promoItem = promoSearchService.searchHomePromoRecommend(hotelEntity);
				if (promoItem == null){
					promoItem = new HashMap<>();
				}
				promoItem.put("promosec", sec / 1000); // 秒
				promoItem.put("promosecend", endSec / 1000); // 距离结束时间（s）
				promoItem.put("nextpromosec", nextsec / 1000); // 距离下一段结束时间（s）
				rtnMap.putAll(promoItem);
			}else {
				rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
				rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
				rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "找不到特价活动时间配置！");

			}

		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.error("【/homepage/promolist】 is error: {} ", e.getMessage());
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

}
