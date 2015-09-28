package com.mk.ots.suggest.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.suggest.model.USuggest;
import com.mk.ots.suggest.service.USuggestService;

/**
 * 意见反馈
 * @author xiaofutao
 *
 */
@Controller
@RequestMapping(value="/suggest", method=RequestMethod.POST)
public class USuggestController {

	@Autowired
	private USuggestService usggestService;
	
	/**
	 * 意见反馈
	 * @param   token：用户
	 * @param   suggest：意见
	 * @return  jeson
	 */
	@RequestMapping(value="/push")
	public ResponseEntity<Map<String,Object>> suggestPush(String token,String suggest, HttpServletRequest request){
		if(Strings.isNullOrEmpty(suggest)){
			throw MyErrorEnum.errorParm.getMyException("意见不允许为空.");
		}
		USuggest us=new USuggest();
		us.setCreatetime(new Date());
		us.setMid(MyTokenUtils.getMidByToken(token));
		us.setContent(suggest);
		usggestService.save(us);
		
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String,Object>>(rtnMap, HttpStatus.OK);
	}
	
	 
}