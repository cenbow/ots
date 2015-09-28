package com.mk.pms.hotel.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.hotel.jsonbean.HotelOnlineOfflineInfoJsonBean;
import com.mk.ots.hotel.jsonbean.HotelOnlineOfflineJsonBean;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.rpc.service.HmsHotelService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.hotel.service.NewPMSHotelService;
import com.mk.pms.hotel.service.PMSHotelService;
import com.mk.pms.order.control.PmsUtilController;
import com.mk.pms.room.service.PmsRoomService;

/**
 * PMS2.0 酒店同步房间
 * @author LYN
 *
 */

@RestController
@RequestMapping("/pms")
public class PMSHotelController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Gson gson = new Gson();
	
	@Autowired
    private PmsRoomService pmsRoomService;
    @Autowired
    private HmsHotelService hmsHotelService;
    @Autowired
    private PMSHotelService pmsHotelService;
	
	@Autowired
	private NewPMSHotelService newPMSHotelService;
	
	@RequestMapping(value = "/synchotelinfo", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> syncHotelInfo(String json) {		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(StringUtils.isEmpty(json)){
			throw MyErrorEnum.errorParm.getMyException("PMS2.0同步房间失败，参数不能为空.");
		}else{
			JSONObject jsonOBJ = null;
			try{
				jsonOBJ = JSON.parseObject(json);
			}catch(Exception e){
				logger.error("同步房间，json转换错误:{}", e.fillInStackTrace());
				throw MyErrorEnum.errorParm.getMyException("PMS2.0同步房间失败，json转换错误.");
			}
			if(jsonOBJ != null ){
				try{
					resultMap = newPMSHotelService.syncHotelInfo(jsonOBJ);
				}catch(Exception e){
					throw MyErrorEnum.errorParm.getMyException("PMS2.0同步房间失败.");
				}
			}
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
	}
	
	/**
	 * 安装PMS2.0 
	 * @param hotelPMS 酒店PMS号
	 * @return
	 */
	@RequestMapping(value = "/initPMS", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> initPMS(String hotelPMS){
		if(StringUtils.isEmpty(hotelPMS)){
			logger.info("PMS2.0 安装失败，参数hotelPMS不能为空");
			throw MyErrorEnum.errorParm.getMyException("PMS2.0 安装失败，参数hotelPMS不能为空");
		}
		logger.info("PMS2.0安装PMS开始:hotelPMS:{}", hotelPMS);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = newPMSHotelService.initPMS(hotelPMS);

		logger.info("PMS2.0安装发送同步酒店消息完成.");
		return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
	}
	
	
	
	/**
     * PMS会批量给出酒店在线、离线状态切换，OTA需要判断状态time来规避消息到达顺序不一致的情况
     */
    @RequestMapping("/changeonlinestatus")
	public ResponseEntity<Map<String,Object>> changeonlinestatus(String json){
		logger.info("ots::HotelController::changeonlinestatus::params{}  begin", json);
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(StringUtils.isEmpty(json)){
				throw MyErrorEnum.errorParm.getMyException("参数错误!没有传json数据");
			}
		    //解析json
			HotelOnlineOfflineJsonBean hotelOnlineOfflineJsonBean = gson.fromJson(json, HotelOnlineOfflineJsonBean.class);
			if(hotelOnlineOfflineJsonBean==null || hotelOnlineOfflineJsonBean.getData().size()==0){
				throw MyErrorEnum.errorParm.getMyException("参数错误!data为空");
			}
			//StringBuffer msg = new StringBuffer();
			for (HotelOnlineOfflineInfoJsonBean info:hotelOnlineOfflineJsonBean.getData()) {
				String pms = info.getHotelid();
				if(StringUtils.isEmpty(pms)){
					throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
				}
				String action = info.getAction();
				if(StringUtils.isEmpty(action)){
					throw MyErrorEnum.errorParm.getMyException("参数错误!action为空");
				}
				Long time = info.getTime();
				if(time==null){
					throw MyErrorEnum.errorParm.getMyException("参数错误!time为空");
				}
				//判断时间
				THotelModel tHotelModel = pmsRoomService.selectThotelByPms(pms,time);
				if(tHotelModel==null){
					//throw MyErrorEnum.errorParm.getMyException("数据库中不存在此酒店:pms"+pms);
					logger.info("数据库中不存在此酒店:{}{}", pms,time);
					continue;
				}
				
				if(action.equalsIgnoreCase("online")){
					pmsHotelService.pmsOnline(tHotelModel.getId(), time);
					//更新es
					hmsHotelService.updateHotelById(tHotelModel.getId().toString());
				}else if(action.equalsIgnoreCase("offline")){
					pmsHotelService.pmsOffline(tHotelModel.getId(), time);
					//更新es
					hmsHotelService.updateHotelById(tHotelModel.getId().toString());
				}
			}
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put(ServiceOutput.STR_MSG_SUCCESS, false);
        	result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
        	result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        	logger.info("ots::HotelController::changeonlinestatus  error {} {}",json,e.getMessage());
        } finally{
        	logger.info("ots::HotelController::changeonlinestatus  end");
        }
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
    
    /**
	 * ots通过接口方式，申请酒店上线(hms端酒店安装过程中调用)
	 * @param hotelPMS 酒店PMS号
	 * @return
	 */
	@RequestMapping(value = "/installpms", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> online(String hotelPMS,String hotelname){
		logger.info("ots::PMSHotelController::installpms::params{}  begin", hotelPMS);
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(StringUtils.isEmpty(hotelPMS)){
				logger.error("安装失败：参数hotelPMS不能为空");
				throw MyErrorEnum.errorParm.getMyException("安装失败：参数hotelPMS不能为空");
			}
			if(StringUtils.isEmpty(hotelname)){
				logger.error("安装失败：参数hotelname不能为空");
				throw MyErrorEnum.errorParm.getMyException("安装失败：参数hotelname不能为空");
			}
			result = newPMSHotelService.installPms(hotelPMS,hotelname);
		}catch (Exception e) {
        	e.printStackTrace();
        	result.put(ServiceOutput.STR_MSG_SUCCESS, false);
        	result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
        	result.put(ServiceOutput.STR_MSG_ERRMSG, "pms安装失败");
        	logger.error("ots::PMSHotelController::installpms  error {} {}",hotelPMS,e.getMessage());
        } finally{
        	logger.info("ots::PMSHotelController::installpms  end");
        }
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	/**
	 * @param hotelPMS
	 * 通知酒店离线接口
	 */
	@RequestMapping(value = "/sendofflinemsg", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sendofflinemsg(final String hotelPMS){
		logger.info("ots::PMSHotelController::sendofflinemsg::params{}  begin", hotelPMS);
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(StringUtils.isEmpty(hotelPMS)){
				logger.error("安装失败：参数hotelPMS不能为空");
				throw MyErrorEnum.errorParm.getMyException("安装失败：参数hotelPMS不能为空");
			}
			//异步调用通知下线
			PmsUtilController.pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						newPMSHotelService.sendOfflineMsg(hotelPMS);
					} catch (Exception e) {
						logger.error("pms下线失败:hotelPMS:{},error{}", hotelPMS, e.getMessage());
					}
				}
			});
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		}catch (Exception e) {
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, "pms安装失败");
			logger.error("ots::PMSHotelController::sendofflinemsg  error {} {}",hotelPMS,e.getMessage());
		} finally{
			logger.info("ots::PMSHotelController::sendofflinemsg  end");
		}
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
}
