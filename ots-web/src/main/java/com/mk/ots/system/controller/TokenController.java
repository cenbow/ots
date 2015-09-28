package com.mk.ots.system.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.system.model.Result;
import com.mk.ots.system.model.UToken;
import com.mk.ots.system.service.ITokenService;
import com.mk.ots.utils.Tools;

@Controller
@RequestMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenController {

	public static final Logger logger = LoggerFactory
			.getLogger(TokenController.class);

	@Autowired
	private ITokenService itokenService;

	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public ResponseEntity<Result> getToken(HttpServletRequest request) {

		Result result = new Result();
		try {
			String accesstoken = request.getParameter("accesstoken");

			logger.info("查询token信息,accesstoken = {}", accesstoken);
			UToken uToken = itokenService.findTokenByAccessToken(accesstoken);
			if (uToken == null) {
				result.setSuccess(false);
				result.setCode(MyErrorEnum.tokenError.getErrorCode());
				result.setReason(MyErrorEnum.tokenError.getErrorMsg());
				logger.warn("token不存在");
			} else {
				String json = Tools.obj2json(uToken);
				result.setSuccess(true);
				result.setData(json);
				logger.info("token信息查询完成,token = {}", json);
			}
		} catch (Exception e) {
			logger.error("查询token异常", e);
			result.setSuccess(false);
			result.setCode(MyErrorEnum.tokenError.getErrorCode());
			result.setReason(MyErrorEnum.tokenError.getErrorMsg());
		}

		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
}
