package com.mk.ots.wechat.controller;

import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.web.ServiceOutput;
import com.mk.ots.wechat.search.service.WechatSearchService;

@Controller
@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class WechatController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 注入ES代理服务类实例
	 */
	@Autowired
	private ElasticsearchProxy esProxy;

	@Autowired
	private WechatSearchService wechatService;

	private String countErrors(Errors errors) {
		StringBuffer bfErrors = new StringBuffer();
		for (ObjectError error : errors.getAllErrors()) {
			bfErrors.append(error.getDefaultMessage()).append("; ");
		}

		return bfErrors.toString();
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
	@RequestMapping(value = { "/wechat/search/queryhotels" })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> queryHotels(HttpServletRequest request,
			@Valid HotelQuerylistReqEntity reqentity, Errors errors) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonRequest = objectMapper.writeValueAsString(reqentity);
		logger.info("wechat begin...");
		logger.info("/wechat/search/queryhotels request entity is : {}", objectMapper.writeValueAsString(reqentity));

		Cat.logEvent("/wechat/search/queryhotels", reqentity.getCallmethod(), Event.SUCCESS, jsonRequest);

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
			Date day = new Date();
			long starttime = day.getTime();

			rtnMap = wechatService.readonlySearchHotels(reqentity);

			ResponseEntity<Map<String, Object>> resultResponse = new ResponseEntity<Map<String, Object>>(rtnMap,
					HttpStatus.OK);
			if (AppUtils.DEBUG_MODE) {
				long endtime = new Date().getTime();
				resultResponse.getBody().put("$times$", endtime - starttime + " ms");
			}

			logger.info("/wechat/search/queryhotels end...");
			logger.info("/wechat/search/queryhotels response data:success::{} , count::{}\n",
					objectMapper.writeValueAsString(resultResponse.getBody().get("success")),
					resultResponse.getBody().get("count"));

			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "0");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "");
			return resultResponse;
		} catch (Exception e) {
			logger.error("/wechat/search/queryhotels is error...", e.getCause());

			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "");

		}

		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
}
