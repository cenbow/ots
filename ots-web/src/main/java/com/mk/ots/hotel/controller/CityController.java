package com.mk.ots.hotel.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.hotel.bean.TCity;
import com.mk.ots.hotel.service.CityService;
import com.mk.ots.web.ServiceOutput;

/**
 * 省市地区接口
 * @author LYN
 *
 */
@Controller
@RequestMapping(value="/city")
public class CityController {

	@Autowired
	private CityService cityService=null;
	
	/**
	 * 获取省份
	 * @param md5
	 * @return
	 */
	@RequestMapping(value="/province/list",method=RequestMethod.POST)
	public String findProvince(String md5){
		List<Bean> provinceList= cityService.findProvince();
		List provinceJsonList= new ArrayList();
		//组装 json对象
		JSONObject provinceJSON= new JSONObject();
		provinceJSON.element("success", true);
		provinceJSON.element("noupdate", false);
		provinceJSON.element("md5",md5);
		
		for(Bean b: provinceList){
			Map provinceMap= new HashMap();
			provinceMap.put("proid", b.get("proid"));
			provinceMap.put("proname", b.get("proname"));
			provinceMap.put("code", b.get("code"));
			provinceJsonList.add(provinceMap);
		}
		provinceJSON.element("pro", provinceJsonList);
		return "";
	}
	
	/**
	 * 获取市
	 * @param procode,md5
	 * @return
	 */
	@RequestMapping(value="/city/list",method=RequestMethod.POST)
	public String findCity(String procode,String md5){
		if(StringUtils.isBlank(procode)){
			JSONObject resultJSON= new JSONObject();
			resultJSON.element("success",false);
			resultJSON.element("errorcode","-1");
			resultJSON.element("errormsg", "缺少省编号参数");
			return resultJSON.toString();
		}
		
		List<Bean> cityList= cityService.findCity(procode);
		//组装 json对象
		List cityJsonList= new ArrayList();
		JSONObject cityJSON= new JSONObject();
		cityJSON.element("success", true);
		cityJSON.element("noupdate", false);
		cityJSON.element("md5",md5);
		for(Bean b: cityList){
			Map cityMap= new HashMap();
			cityMap.put("cityid", b.get("cityid"));
			cityMap.put("cityname", b.get("cityname"));
			cityMap.put("code", b.get("code"));
			cityMap.put("lat", b.get("latitude"));
			cityMap.put("lon", b.get("longitude"));
			cityJsonList.add(cityMap);
		}
		cityJSON.element("city", cityJsonList);
		return cityJSON.toString();
	}
	
	/**
	 * 获取区县
	 * @param citycode
	 * @return
	 */
	@RequestMapping(value="/dis/list",method=RequestMethod.POST)
	public String findDis(String citycode,String md5){
		if(StringUtils.isBlank(citycode)){
			JSONObject resultJSON= new JSONObject();
			resultJSON.element("success",false);
			resultJSON.element("errorcode","-1");
			resultJSON.element("errormsg", "缺少市编号参数");
			return resultJSON.toString();
		}
		List<Bean> disList= cityService.findDistrict(citycode);
		
		//组装 json对象
		JSONObject disJSON= new JSONObject();
		List disJsonList= new ArrayList();
		disJSON.element("success", true);
		disJSON.element("noupdate", false);
		disJSON.element("md5",md5);
		for(Bean b: disList){
			Map disMap= new HashMap();
			disMap.put("disid", b.get("disid"));
			disMap.put("disname", b.get("disname"));
			disMap.put("code", b.get("code"));
			disJsonList.add(disMap);
		}
		disJSON.element("dis", disJsonList);
		return disJSON.toString();
	}
	
	
	/**
	 * 
	 * 查询可查询城市集合
	 * 
	 * <li> 
	 * 	url：http://ip:port/ots/city/querylist 
	 * 	<br>
	 * 	根据t_city 表中 isSelect(是否查询城市) 字段查询 城市信息并返回 
	 * </li>
	 * 
	 * @return  cityname:, //城市全名
	 *			citycode:,//城市ID（6位数）
	 *			capital:, //城市名称首字母（大写）
	 *			simplename:, //城市简称
	 *			ishotcity:, //是否是热门城市
	 *			doublelat:, //城市中心点的纬度坐标
	 *			doublelng:, //城市中心点的经度坐标
	 *			range://城市半径长度(与城市中心点坐标结合后，能覆盖全市的长度)
	 *
	 */
	@RequestMapping(value="/querylist", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> getSelectCityList() {
		Map<String,Object> rtnMap = Maps.newHashMap();
		try {
			List<TCity> cities = cityService.findSelectCity();
			List<Map<String, Object>> cityArray = new ArrayList<Map<String,Object>>();
			for(TCity city : cities) {
				Map<String, Object> cityObject = new HashMap<String, Object>();
				cityObject.put("cityname", (city.getQuerycityname() == null ? "" : city.getQuerycityname()));
				cityObject.put("citycode", city.getCode());
				cityObject.put("capital", city.getCityname().trim().substring(0, 1));
				cityObject.put("simplename", (city.getSimplename() == null ? "" : city.getSimplename()));
				cityObject.put("ishotcity", (city.getIshotcity() == null ? "" : city.getIshotcity()));
				cityObject.put("doublelat", city.getLatitude());
				cityObject.put("doublelng", city.getLongitude());
				cityObject.put("range", (city.getRange() == null ? 0 : city.getRange()));
				cityArray.add(cityObject);
			}
			rtnMap.put("hasupdate", "T");
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put("cityes", cityArray);
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
            e.printStackTrace();
		}
		return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
	}
}
