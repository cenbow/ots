package com.mk.ots.system.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mk.ots.system.dao.impl.ISyConfigService;

@Controller
@RequestMapping(value="/sysconfig_", produces=MediaType.APPLICATION_JSON_VALUE)
public class SystemController {
	final Logger logger = LoggerFactory.getLogger(SystemController.class);
	
	@Autowired
	private ISyConfigService iSyConfigService;
	
	@RequestMapping(value="/mikeweb/{key}", method=RequestMethod.GET)	
	public ResponseEntity<String> getMikeWeb(@PathVariable String key){
		logger.info("查询mikeweb变量. {}",key);
		return new ResponseEntity<String>(this.iSyConfigService.findValue(key, "mikeweb"), HttpStatus.OK);
	}
	
	@RequestMapping(value="/mikeweb/{key}",method=RequestMethod.POST)
	public ResponseEntity<String> putMikeWeb(@PathVariable String key, String value){
		logger.info("更新mikeweb变量. {}:{}",key,value);
		this.iSyConfigService.update(key, "mikeweb", value);
		return new ResponseEntity<String>(value, HttpStatus.OK);
	}
	
	@RequestMapping(value="/sys/{key}", method=RequestMethod.GET)
	public ResponseEntity<String> getSys(@PathVariable String key){
		logger.info("查询sys变量. {}",key);
		return new ResponseEntity<String>(this.iSyConfigService.findValue(key, "sys"), HttpStatus.OK);
	}
	
	@RequestMapping(value="/sys/{key}",method=RequestMethod.POST)
	public ResponseEntity<String> putSys(@PathVariable String key, String value){
		logger.info("更新sys变量. {}:{}",key,value);
		this.iSyConfigService.update(key, "sys", value);
		return new ResponseEntity<String>(value, HttpStatus.OK);
	}
}
