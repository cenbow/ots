package com.mk.ots.roomsale.controller;

import com.google.common.collect.Maps;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
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

import java.util.List;
import java.util.Map;

/**
* RoomSaleMapper.
* @author kangxiaolong.
*/
@Controller
@RequestMapping(value="/promo", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleShowController {
	final Logger logger = LoggerFactory.getLogger(SaleShowController.class);

	@Autowired
	private TRoomSaleShowConfigService tRoomSaleShowConfigService;
	@RequestMapping("/queryinfo")
	public ResponseEntity<Map<String, Object>>  queryActivityListByCity(ParamBaseBean pbb,RoomSaleShowConfigDto bean) {
		logger.info("【/promo/queryinfo】 begin...");
		Map<String, Object> rtnMap = Maps.newHashMap();
		if(Strings.isNullOrEmpty(bean.getCityid())){
			bean.setShowArea(HotelPromoEnum);
		}

		List<TRoomSaleShowConfig> troomSaleShowConfigList= tRoomSaleShowConfigService.queryTRoomSaleShowConfig(cityid,"index_h");
		if(CollectionUtils.isEmpty(troomSaleShowConfigList)) {
			rtnMap.put("promo",null);
			logger.info(" query  troomSaleShowConfigList  is  null");
		} else {
			rtnMap.put("promo", troomSaleShowConfigList);
			logger.info(" query  troomSaleShowConfigList ",troomSaleShowConfigList);
		}
		rtnMap.put("errmsg",null);
		rtnMap.put("errcode",0);
		rtnMap.put("success", true);
		logger.info("【/promo/queryinfo】 end...");
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
}
