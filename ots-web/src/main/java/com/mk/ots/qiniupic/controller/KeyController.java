package com.mk.ots.qiniupic.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.mk.ots.qiniupic.sys.SysConfig;
import com.qiniu.util.Auth;

@Controller
// @RequestMapping(value="/picwindinfo", method=RequestMethod.POST, produces =
// MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/picwindinfo")
public class KeyController {

	/**
	 * @author xiaofutao 获取图片云授权数据
	 * @return jeson data
	 */
	// @RequestMapping("/get")
	@ResponseBody
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> get() {
		Map<String, Object> map = Maps.newHashMap();
		// api地址：http://developer.qiniu.com/docs/v6/sdk/java-sdk.html
		Auth auth = Auth.create(SysConfig.QiniuAK, SysConfig.QiniuSK);
		// 指定上传到哪个目录
		String s = auth.uploadToken(SysConfig.UploadSpace);
//		String s = auth.uploadToken(SysConfig.UploadSpace, null, SysConfig.uploadTime, null, true);
		map.put("success", true);
		map.put("picwindinfo", s);
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

}
