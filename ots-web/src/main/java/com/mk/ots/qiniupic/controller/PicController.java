package com.mk.ots.qiniupic.controller;

import java.util.Map;

import org.elasticsearch.common.base.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.qiniupic.sys.SysConfig;
import com.qiniu.util.Auth;


@Controller
@RequestMapping(value="/picurl", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class PicController {
	
	/**
	 * @author xiaofutao
	 * 支付完成后的响应
	 * @param picpath		图片路径
	 * @return  jeson data
	 */
	@ResponseBody
	@RequestMapping(value="/change",method=RequestMethod.POST )
	public ResponseEntity<Map<String,Object>> change(String picpath){
		if(Strings.isNullOrEmpty(picpath)){
			throw MyErrorEnum.errorParm.getMyException("picpath为空");
		}
		//七牛api地址：http://developer.qiniu.com/docs/v6/sdk/java-sdk.html#private-download
//		Auth auth = Auth.create(SysConfig.QiniuAK, SysConfig.QiniuSK);
		  //指定时长 7200秒
//		picpath = auth.privateDownloadUrl(SysConfig.Url+picpath, SysConfig.InvalidationTime);
		//公开的就这么取
		picpath=SysConfig.Url+picpath;
		Map<String,Object> map = Maps.newHashMap(); 
		map.put("success", true);
		map.put("picurl", picpath);
		return new ResponseEntity<Map<String,Object>>(map,HttpStatus.OK);
	}
	
	 
    
	
	
	
}
