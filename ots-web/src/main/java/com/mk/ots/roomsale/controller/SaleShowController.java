package com.mk.ots.roomsale.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.common.enums.ShowAreaEnum;
import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
@Controller
@RequestMapping(value = "/promo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleShowController {
	final Logger logger = LoggerFactory.getLogger(SaleShowController.class);

	@Autowired
	private TRoomSaleShowConfigService tRoomSaleShowConfigService;

	@RequestMapping("/queryinfo")
	public ResponseEntity<Map<String, Object>> queryActivityListByCity(ParamBaseBean pbb, RoomSaleShowConfigDto bean) {
		logger.info("【/promo/queryinfo】 begin...");
		Map<String, Object> rtnMap = Maps.newHashMap();
		if (Strings.isNullOrEmpty(bean.getCityid())) {
			rtnMap.put("errmsg", "城市编码不能为空");
			rtnMap.put("errcode", -1);
			logger.info("【/promo/queryinfo】 end cityId is null...");
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		List<TRoomSaleCity> roomSaleCityList = tRoomSaleShowConfigService.queryTRoomSaleCity(bean.getCityid());
		if (CollectionUtils.isEmpty(roomSaleCityList)) {
			rtnMap.put("errmsg", "该城市没有参加特价活动");
			rtnMap.put("errcode", -1);
			logger.info("【/promo/queryinfo】 end is not promo...");
			return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
		}
		List<RoomSaleShowConfigDto> resultList = new ArrayList<>();
		bean.setShowArea(ShowAreaEnum.FrontPageHead.getCode());
		for (TRoomSaleCity saleCity : roomSaleCityList) {
			bean.setPromoid(saleCity.getSaleTypeId());
			List<RoomSaleShowConfigDto> saleShowConfigList = tRoomSaleShowConfigService
					.queryRoomSaleShowConfigByParams(bean);
			if (!CollectionUtils.isEmpty(saleShowConfigList)) {
				resultList.add(saleShowConfigList.get(0));
			}
		}

		bean.setPromoid(-1);
		bean.setIsSpecial("F");
		bean.setNormalId(1);

		List<RoomSaleShowConfigDto> troomSaleShowConfigList = tRoomSaleShowConfigService
				.queryRoomSaleShowConfigByParams(bean);
		resultList.addAll(troomSaleShowConfigList);

		List<Map<String, Object>> promoList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> normalList = new ArrayList<Map<String, Object>>();
		for (RoomSaleShowConfigDto configDto : resultList) {
			Map<String, Object> promo = new HashMap<String, Object>();
			promo.put("promoid", configDto.getPromoid());
			promo.put("normalid", configDto.getNormalId());
			promo.put("promoicon", configDto.getPromoicon());
			promo.put("promotext", configDto.getPromotext());

			if (configDto.getPromoid() != null && configDto.getPromoid() > 0) {
				promoList.add(promo);
			} else if (configDto.getNormalId() != null && configDto.getNormalId() > 0) {
				normalList.add(promo);
			} else {
				logger.warn(String.format("invalid data types promoid:%s; normalid:%s", configDto.getPromoid(),
						configDto.getNormalId()));
			}
		}

		promoList.addAll(normalList);

		rtnMap.put("promo", promoList);
		logger.info(" query  troomSaleShowConfigList ", troomSaleShowConfigList);
		rtnMap.put("errmsg", "");
		rtnMap.put("errcode", 0);
		logger.info("【/promo/queryinfo】 end...");
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
}
