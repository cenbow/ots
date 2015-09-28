package com.mk.ots.hotel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.hotel.bean.HotelCollection;
import com.mk.ots.hotel.service.HotelCollectionService;
import com.mk.ots.web.ServiceOutput;

/**
 * @author he
 * 酒店收藏
 */
@Controller
public class HotelCollectionController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	 @Autowired
    private HotelCollectionService hotelCollectionService;
	
	/**
	 * 收藏酒店查询
	 */
	@RequestMapping(value = "/collection/querylist", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> querylist(ParamBaseBean pbb,String citycode,Integer page,Integer limit,String token) {
    	logger.info("HotelCollectionController::querylist::params{}  begin", citycode+","+page+","+limit);
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if(StringUtils.isBlank(token)){
				throw MyErrorEnum.errorParm.getMyException("传参：token为空！");
			}
	    	if (page == null || StringUtils.isBlank(page.toString())) {
	    		throw MyErrorEnum.errorParm.getMyException("传参：页数为空!");
	    	}
	    	if (limit == null || StringUtils.isBlank(limit.toString())) {
	    		throw MyErrorEnum.errorParm.getMyException("传参：每页显示量为空!");
	    	}
	    	/*if (StringUtils.isEmpty(citycode)) {
	    		throw MyErrorEnum.errorParm.getMyException("传参：城市编码为空!");
	    	}*/
	    	
	 	    int start = (page - 1) * limit;
	 	    List<Map<String, Object>> list = hotelCollectionService.querylist(token,citycode,start,limit);
	 	    int count = hotelCollectionService.readonlyHotelCollectedCount(token);
	    	result.put("hotel", list);
	    	result.put("count", count);
	    	result.put(ServiceOutput.STR_MSG_SUCCESS, true);
    	}catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelCollectionController::querylist::error{}",e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::querylist::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
	/**
	 * 添加收藏
	 */
	@RequestMapping(value = "/collection/add", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> addCollection(ParamBaseBean pbb,String token,Long hotelid) {
		logger.info("HotelCollectionController::add::params{}  begin", token+","+hotelid);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(token)){
				throw MyErrorEnum.errorParm.getMyException("传参：token为空！");
			}
			if (hotelid == null || StringUtils.isBlank(hotelid.toString())) {
				throw MyErrorEnum.errorParm.getMyException("传参：酒店id为空!");
			}
			HotelCollection hotelCollection = hotelCollectionService.queryinfo(token, hotelid);
			if(hotelCollection==null){
				hotelCollectionService.addCollection(token,hotelid);
			}else{
				throw MyErrorEnum.errorParm.getMyException("已收藏过此酒店，不能重复收藏!");
			}
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
			result.put("state", Constant.STR_TRUE);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelCollectionController::add::error{}",e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::add::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	/**
	 * 取消收藏
	 */
	@RequestMapping(value = "/collection/delete", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> deleteCollection(ParamBaseBean pbb,String token,Long hotelid) {
		logger.info("HotelCollectionController::deleteCollection::params{}  begin", token+","+hotelid);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(token)){
				throw MyErrorEnum.errorParm.getMyException("传参：token为空！");
			}
			if (hotelid == null || StringUtils.isBlank(hotelid.toString())) {
				throw MyErrorEnum.errorParm.getMyException("传参：酒店id为空!");
			}
			hotelCollectionService.deleteCollection(token,hotelid);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
			result.put("state", Constant.STR_FALSE);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelCollectionController::deleteCollection::error{}",e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::deleteCollection::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	/**
	 * 查询酒店收藏状态
	 * @param pbb
	 * @param token
	 * @param hotelid
	 * @return
	 */
	@RequestMapping(value = "/collection/state", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> hotelISCollected(ParamBaseBean pbb,String token,Long hotelid) {
		logger.info("HotelCollectionController::hotelCollectionState::params{}  begin", token+","+hotelid);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(token)){
				throw MyErrorEnum.errorParm.getMyException("传参：token为空！");
			}
			if (hotelid == null || StringUtils.isBlank(hotelid.toString())) {
				throw MyErrorEnum.errorParm.getMyException("传参：酒店id为空!");
			}
			result = hotelCollectionService.readonlyHotelISCollected(token,hotelid);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelCollectionController::hotelCollectionState::error{}",e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::hotelCollectionState::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
