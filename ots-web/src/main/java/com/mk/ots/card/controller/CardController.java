package com.mk.ots.card.controller;

import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.ots.card.service.impl.BCardService;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.service.HomeService;
import com.mk.ots.order.service.OrderServiceImpl;
import org.elasticsearch.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/card")
public class CardController {

	@Autowired
	private BCardService bCardService;

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public ResponseEntity<String> test(String pwd) {
		JSONObject m = new JSONObject();
		this.bCardService.findActivatedByPwd(pwd);

		return new ResponseEntity<String>(m.toJSONString(), HttpStatus.OK);
	}

}
