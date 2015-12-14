package com.mk.ots.search.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.mk.ots.hotel.bean.TCity;
import com.mk.ots.hotel.service.CityService;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.mk.framework.AppUtils;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.restful.input.SearchPositionsFuzzyReqEntity;
import com.mk.ots.restful.input.SearchPositionsReqEntity;
import com.mk.ots.restful.input.SearchReqEntity;
import com.mk.ots.restful.output.SearchPositiontypesRespEntity;
import com.mk.ots.search.service.IPromoSearchService;
import com.mk.ots.search.service.ISearchService;
import com.mk.ots.web.ServiceOutput;

/**
 * 搜索API接口类.
 * @author chuaiqing.
 *
 */
@Controller
@RequestMapping(method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController {
    /** 日志输出 */
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ISearchService searchService;

    @Autowired
    private CityService cityService;
    
    /**
     * 联想搜索接口API.
     * @param params
     * @param errors
     * @return
     */
    @RequestMapping(value = {"/search"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> search(@Valid SearchReqEntity params, Errors errors) {
        long startTime = new Date().getTime();
        Map<String, Object> rtnMap = Maps.newHashMap();
        StringBuffer bfErrors = new StringBuffer();
        for (ObjectError error : errors.getAllErrors()) {
            bfErrors.append(error.getDefaultMessage()).append("; ");
        }
        if (bfErrors.length() > 0) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, bfErrors.toString());
            return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
        }



        // service业务处理开始
        // service业务处理结束
        long finishTime = new Date().getTime();
        if (AppUtils.DEBUG_MODE) {
            rtnMap.put(ServiceOutput.STR_MSG_TIMES, finishTime - startTime + "ms");
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
    /**
     * 城市位置区域接口API.
     * @param params
     * @param errors
     * @return
     */
    @RequestMapping(value = {"/search/positions"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPositions(@Valid SearchPositionsReqEntity params, Errors errors) {
        long startTime = new Date().getTime();
        Map<String, Object> rtnMap = Maps.newHashMap();
        StringBuffer bfErrors = new StringBuffer();
        for (ObjectError error : errors.getAllErrors()) {
            bfErrors.append(error.getDefaultMessage()).append("; ");
        }
        if (bfErrors.length() > 0) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, bfErrors.toString());
            return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
        }
        // service业务处理开始
        rtnMap= searchService.readonlyPositions(params.getCitycode(), params.getPtype());
        rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
        
        // service业务处理结束
        long finishTime = new Date().getTime();
        if (AppUtils.DEBUG_MODE) {
            rtnMap.put(ServiceOutput.STR_MSG_TIMES, finishTime - startTime + "ms");
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }


    /**
     * 城市位置区域接口API.
     * @param params
     * @param errors
     * @return
     */
    @RequestMapping(value = {"/search/nearstation"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNeartation(@Valid SearchPositionsReqEntity params,Double userlongitude, Double userlatitude, Errors errors) {
        long startTime = new Date().getTime();
        Map<String, Object> rtnMap = Maps.newHashMap();
        StringBuffer bfErrors = new StringBuffer();
        for (ObjectError error : errors.getAllErrors()) {
            bfErrors.append(error.getDefaultMessage()).append("; ");
        }
        if (bfErrors.length() > 0) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, bfErrors.toString());
            return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
        }

        if (userlatitude == null || userlongitude ==null) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "参数userlatitude 或userlongitude 不能为空！ ");
            return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
        }

        // service业务处理开始
        rtnMap= searchService.readonlyNearPositions(params.getCitycode(), params.getPtype(), userlatitude, userlongitude);
        rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);

        // service业务处理结束
        long finishTime = new Date().getTime();
        if (AppUtils.DEBUG_MODE) {
            rtnMap.put(ServiceOutput.STR_MSG_TIMES, finishTime - startTime + "ms");
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
    /**
     * 城市位置区域模糊搜索.
     * @param params
     * @param errors
     * @return
     */
    @RequestMapping(value = {"/search/positionfuzzy"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPositionsFuzzy(@Valid SearchPositionsFuzzyReqEntity params, Errors errors) {
        long startTime = new Date().getTime();
        Map<String, Object> rtnMap = Maps.newHashMap();
        StringBuffer bfErrors = new StringBuffer();
        for (ObjectError error : errors.getAllErrors()) {
            bfErrors.append(error.getDefaultMessage()).append("; ");
        }
        if (bfErrors.length() > 0) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, bfErrors.toString());
            return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
        }
        // service业务处理开始
        rtnMap= searchService.readonlyPositionsFuzzy(params.getCitycode(), params.getWord());
        rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
        
        // service业务处理结束
        long finishTime = new Date().getTime();
        if (AppUtils.DEBUG_MODE) {
            rtnMap.put(ServiceOutput.STR_MSG_TIMES, finishTime - startTime + "ms");
        }
        return new ResponseEntity<Map<String,Object>>(rtnMap,HttpStatus.OK);
    }
    
    /**
     * 
     * @param pbb
     * @param citycode
     * @param positiontypeid
     * @return
     */
    @RequestMapping(value = {"/search/positiontypes"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPositionTypes(ParamBaseBean pbb, String citycode, Long positiontypeid) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	if(StringUtils.isBlank(citycode)){
    		throw MyErrorEnum.errorParm.getMyException("传参：citycode不能为空！");
    	}
    	try{
    		 List<SearchPositiontypesRespEntity> positionTypes = searchService.readonlyPositionTypes(citycode, positiontypeid);
    		resultMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
    		resultMap.put("datas", positionTypes);
    	}catch(Exception e){
    		logger.error("获取位置类型失败:{}",e.fillInStackTrace());
    		resultMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
    		resultMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
    		resultMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
    	}
    	
        return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.OK);
    }
    
    /**
     * 城市位置区域模糊搜索.
     * @param params
     * @param errors
     * @return
     */
    @RequestMapping(value = {"/search/positioninit"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> initPosition(String citycode, String typeid, String isforce) {
        boolean forceUpdate = Constant.STR_TRUE.equals(isforce);
        return new ResponseEntity<Map<String, Object>>(searchService.readonlySyncCityPOI(citycode, typeid, forceUpdate), HttpStatus.OK);
    }

    @RequestMapping(value = {"/indexer/positioninit"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> initAllPosition(String typeid, String isforce) {
        boolean forceUpdate = Constant.STR_TRUE.equals(isforce);
        List<TCity> allSelectCitys = cityService.findSelectCity();

        for (TCity city: allSelectCitys){
            searchService.readonlySyncCityPOI(city.getCode(), typeid, forceUpdate);
        }
        return new ResponseEntity<Map<String, Object>>(, HttpStatus.OK);
    }

}
