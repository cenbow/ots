package com.mk.ots.card.controller;

import com.alibaba.fastjson.JSONObject;
import com.mk.framework.exception.MyException;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.service.impl.BCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/card")
public class CardController {

	@Autowired
	private BCardService bCardService;

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public ResponseEntity<String> test(String pwd) {
		JSONObject m = new JSONObject();
		BCard b = this.bCardService.findActivatedByPwd(pwd);
		m.put("m",b);

		return new ResponseEntity<String>(m.toJSONString(), HttpStatus.OK);
	}

}
