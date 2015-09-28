package com.mk.pms.hotel.controller;

import java.util.HashMap;
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

import com.google.gson.Gson;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.hotel.jsonbean.hotelprice.DailyRateJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.RackRateJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.WeekendRateJsonBean;
import com.mk.ots.hotel.model.EHotelModel;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.room.service.PmsRoomService;

/**
 * @author he
 * pms同步酒店价格
 */
@Controller
public class PmsHotelPriceController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Gson gson = new Gson();
	
	@Autowired
	private PmsRoomService pmsRoomService ;
	@Autowired
	private HotelPriceService hotelPriceService ;
	
	
	
	
	
	/**
	 * pms同步酒店价格
	 * @param hotelPMS 酒店PMS号
	 * @return
	 */
	@RequestMapping(value = "/price/synchotelprice", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sendSyncPriceMessToPMS(String hotelPMS){
		if(StringUtils.isEmpty(hotelPMS)){
			logger.info("PMS2.0 同步酒店价格失败，参数hotelPMS不能为空");
			throw MyErrorEnum.errorParm.getMyException("PMS2.0 同步酒店价格失败，参数hotelPMS不能为空");
		}
		logger.info("PMS2.0同步酒店价格开始:hotelPMS:{}", hotelPMS);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = hotelPriceService.sendSyncPriceMessToPMS(hotelPMS);

		logger.info("PMS2.0安装时同步酒店价格完成.");
		return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
	}
	
	/**
     * 同步平日门市价
     */
    @RequestMapping("/pms/syncrackrate")
	public ResponseEntity<Map<String,Object>> syncrackrate(String json){
		logger.info("ots::HotelPriceController::syncrackrate::params{}  begin", json);
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(StringUtils.isEmpty(json)){
				throw MyErrorEnum.errorParm.getMyException("参数错误!没有传json数据");
			}
		    //解析json
			RackRateJsonBean rackRateJsonBean = gson.fromJson(json, RackRateJsonBean.class);
			if(rackRateJsonBean==null){
				throw MyErrorEnum.errorParm.getMyException("json数据错误:json--"+json);
			}
			if(StringUtils.isEmpty(rackRateJsonBean.getHotelid())){
				throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
			}
			if(rackRateJsonBean.getRoomtype().size()==0){
				throw MyErrorEnum.errorParm.getMyException("参数错误!roomtype数据为空");
			}
			//e_hotel 根据pms查询
			EHotelModel eHotelModel = pmsRoomService.selectEhotelByPms(rackRateJsonBean.getHotelid());
			if(eHotelModel==null){
				throw MyErrorEnum.errorParm.getMyException("未找到pms为 "+rackRateJsonBean.getHotelid()+" 的酒店信息"); 
			}
			Long ehotelid = eHotelModel.getId();
			boolean ischange = hotelPriceService.syncrackrate(rackRateJsonBean,ehotelid);
			if(ischange){
				//修改酒店审核状态为待审核
				eHotelModel.setState(5);
				hotelPriceService.updateHotel(eHotelModel);
			}
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put(ServiceOutput.STR_MSG_SUCCESS, false);
        	result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
        	result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        	logger.info("ots::HotelPriceController::syncrackrate  error {} {}",json,e.getMessage());
        } finally{
        	logger.info("ots::HotelPriceController::syncrackrate  end");
        }
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}

    
    
	/**
	 * 同步周末价
	 */
	@RequestMapping("/pms/syncweekend")
	public ResponseEntity<Map<String,Object>> syncweekend(String json){
		logger.info("ots::HotelPriceController::syncweekend::params{}  begin", json);
	
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(StringUtils.isEmpty(json)){
				throw MyErrorEnum.errorParm.getMyException("参数错误!没有传json数据");
			}
		    //解析json 
			WeekendRateJsonBean weekendRateJsonBean = gson.fromJson(json, WeekendRateJsonBean.class);
			if(weekendRateJsonBean==null){
				throw MyErrorEnum.errorParm.getMyException("json数据错误:json--"+json);
			}
			if(StringUtils.isEmpty(weekendRateJsonBean.getHotelid())){
				throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
			}
			if(weekendRateJsonBean.getRoomtype().size()==0){
				throw MyErrorEnum.errorParm.getMyException("参数错误!roomtype数据为空");
			}
			//e_hotel 根据pms查询
			EHotelModel eHotelModel = pmsRoomService.selectEhotelByPms(weekendRateJsonBean.getHotelid());
			if(eHotelModel==null){
				throw MyErrorEnum.errorParm.getMyException("未找到pms为 "+weekendRateJsonBean.getHotelid()+" 的酒店信息"); 
			}
			Long ehotelid = eHotelModel.getId();
			boolean ischange = hotelPriceService.syncweekend(weekendRateJsonBean,ehotelid);
			if(ischange){
				//修改酒店审核状态为待审核
				eHotelModel.setState(5);
				hotelPriceService.updateHotel(eHotelModel);
			}
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put(ServiceOutput.STR_MSG_SUCCESS, false);
        	result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
        	result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        	logger.info("ots::HotelPriceController::syncweekend  error {} {}",json,e.getMessage());
        } finally{
        	logger.info("ots::HotelPriceController::syncweekend  end");
        }
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
    
    
       
   
	/**
	 * 同步酒店特殊价
	 * @param json
	 * @return
	 */
    @RequestMapping("/pms/syncdailyrate")
	public ResponseEntity<Map<String,Object>> syncdailyrate(String json){
		logger.info("ots::HotelPriceController::syncdailyrate::params{}  begin", json);
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(StringUtils.isEmpty(json)){
				throw MyErrorEnum.errorParm.getMyException("参数错误!没有传json数据");
			}
		    //解析json
			DailyRateJsonBean dailyRateJsonBean = gson.fromJson(json, DailyRateJsonBean.class);
			if(dailyRateJsonBean == null){
				throw MyErrorEnum.errorParm.getMyException("json数据错误:json--" + json);
			}
			if(StringUtils.isEmpty(dailyRateJsonBean.getHotelid())){
				throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
			}
			if(dailyRateJsonBean.getRoomtype().size() == 0){
				throw MyErrorEnum.errorParm.getMyException("参数错误!roomtype数据为空");
			}
			
			//e_hotel 根据pms查询
			EHotelModel eHotelModel = pmsRoomService.selectEhotelByPms(dailyRateJsonBean.getHotelid());
			if(eHotelModel==null){
				throw MyErrorEnum.errorParm.getMyException("未找到pms为 " + dailyRateJsonBean.getHotelid() + " 的酒店信息"); 
			}
			Long ehotelid = eHotelModel.getId();
			
			Integer res = hotelPriceService.syncDailyRate(dailyRateJsonBean, ehotelid);
			
			if(res > 0){
				//修改酒店审核状态为待审核
				eHotelModel.setState(5);
				hotelPriceService.updateHotel(eHotelModel);
			}
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put(ServiceOutput.STR_MSG_SUCCESS, false);
        	result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
        	result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        	logger.info("ots::HotelPriceController::syncdailyrate  error {} {}",json,e.getMessage());
        } finally{
        	logger.info("ots::HotelPriceController::syncdailyrate  end");
        }
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}

}
