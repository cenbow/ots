package com.mk.ots.system.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.google.common.collect.Maps;

/**
 * 系统时间
 * @author xiaofutao
 *
 */
@Controller
@RequestMapping(method=RequestMethod.POST)
public class SysTimeController {

	/**
	 * 获取系统时间
	 * @return  json
	 */
	@RequestMapping(value="/systime/query" )
	public ResponseEntity<Map<String,Object>> getSystemTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("systime", df.format(new Date()));
		return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
	}
	
	/**
	 * 此接口用于查询由非必录时间返回的可预订日期和可预订天数
	 * 可预订日期、可预订天数查询
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value="/avlblodrdate/querylist" )
	public ResponseEntity<Map<String,Object>> getAvlblodrdate(String currenttime) throws ParseException{//yyyyMMddHHmmss
		
		Calendar calendar = Calendar.getInstance();
		
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		//可预订开始日期
		
		//判断是否是2：00之前
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		if(StringUtils.isNotEmpty(currenttime)){
			Date date = new Date();
			date = sdf1.parse(currenttime);
			calendar.setTime(date);
		}
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hour<2){
			//小于2点算昨天
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		rtnMap.put("avlblodrdate", sdf.format(calendar.getTime()));
		//可预订天数
		rtnMap.put("avlblodrdays", 7);
		return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
	}
}
