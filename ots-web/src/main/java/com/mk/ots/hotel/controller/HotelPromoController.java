package com.mk.ots.hotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.web.ServiceOutput;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 特价类型接口
 */
@Controller
public class HotelPromoController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	

	/**
	 * 收藏酒店查询
	 */
	@RequestMapping(value = "/promo/querytypelist", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Map<String, Object>> querytypelist(ParamBaseBean pbb,String cityid,Integer page,Integer limit) {
    	logger.info("HotelPromoController::querytypelist::params{}  begin", cityid+","+page+","+limit);
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {

	    	if (page == null || StringUtils.isBlank(page.toString())) {
	    		page = 1;
	    	}
	    	if (limit == null || StringUtils.isBlank(limit.toString())) {
				limit = 10;
	    	}

	    	
	 	    int start = (page - 1) * limit;

			//TODO 小龙 service
	 	  // List<Map<String, Object>> list = hotelCollectionService.querylist(token,citycode,start,limit);
	 	   // int count = hotelCollectionService.readonlyHotelCollectedCount(token);
			List<JSONObject> list  = new ArrayList<JSONObject>();

			JSONObject ptype1 = new JSONObject();
			ptype1.put("promotypeid", 1);
			ptype1.put("promotypetext", "经济房");
			ptype1.put("promotypeprice", 50);
			ptype1.put("promosec", 1890);
			list.add(ptype1);

			JSONObject ptype2 = new JSONObject();
			ptype2.put("promotypeid", 2);
			ptype2.put("promotypetext", "舒适房");
			ptype2.put("promotypeprice", 80);
			ptype2.put("promosec", 1995);
			list.add(ptype2);

			JSONObject ptype3 = new JSONObject();
			ptype3.put("promotypeid", 3);
			ptype3.put("promotypetext", "豪华房");
			ptype3.put("promotypeprice", 120);
			ptype3.put("promosec", 2090);
			list.add(ptype3);



			result.put("promotypes", list);

	    	result.put(ServiceOutput.STR_MSG_SUCCESS, true);
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelPromoController::querytypelist::error{}",e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::querylist::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

}
