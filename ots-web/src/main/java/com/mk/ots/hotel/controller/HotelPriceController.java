package com.mk.ots.hotel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.hotel.bean.RoomTypePriceBean;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.room.service.PmsRoomService;

/**
 * @author he
 * 酒店价格
 */
@Controller
public class HotelPriceController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Gson gson = new Gson();
	
	@Autowired
	private PmsRoomService pmsRoomService ;
	@Autowired
	private HotelPriceService hotelPriceService ;
	
	/**
	 * 获取审核后价格相关数据
	 */
	@RequestMapping("/price/querypriceinfos")
	public ResponseEntity<Map<String,Object>> querypriceinfos(Long hotelid){
		logger.info("ots::HotelPriceController::querypriceinfos::params{}  begin", hotelid);
	
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(hotelid==null){
				throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
			}
			Map<String,Map<String,String>> map = hotelPriceService.getPriceConfigsFromCache(hotelid);
			result.put("priceinfos", map);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put(ServiceOutput.STR_MSG_SUCCESS, false);
        	result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
        	result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        	logger.info("ots::HotelPriceController::querypriceinfos  error {}, {}",hotelid,e.getMessage());
        } finally{
        	logger.info("ots::HotelPriceController::querypriceinfos  end");
        }
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	/**
	 * 刷新价格redis缓存
	 */
	@RequestMapping("/price/refreshMikePrices")
	public ResponseEntity<Map<String,Object>> refreshMikePrices(Long hotelid){
		logger.info("ots::HotelPriceController::refreshMikePrices::params{}  begin", hotelid);
		
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			/* 不传hotelid查询所有
			 * if(hotelid==null){
				throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
			}*/
			hotelPriceService.refreshMikePrices(hotelid);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.info("ots::HotelPriceController::refreshMikePrices  error {}, {}",hotelid,e.getMessage());
		} finally{
			logger.info("ots::HotelPriceController::refreshMikePrices  end");
		}
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	
	
	/**
	 * 获取动态酒店价格
	 * @param hotelid
	 * @param roomtypeid
	 * @param startdateday
	 * @param enddateday
	 * @return
	 */
	@RequestMapping("/price/queryminprices")
	public ResponseEntity<Map<String,Object>> getMikePrices(Long hotelid, Long roomtypeid, String startdateday, String enddateday) {
		logger.info("ots::HotelPriceController::getMikePrices::params{}---{}  begin", hotelid, roomtypeid);
		
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(hotelid== null){
				throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
			}
			
			//支持yyyy-MM-dd格式与yyyyMMdd格式
			startdateday = startdateday.replaceAll("-", "");
			enddateday = enddateday.replaceAll("-", "");
			
			String[] arr = null;
			if(roomtypeid == null)
				arr = hotelPriceService.getHotelMikePrices(hotelid, startdateday, enddateday);
			else 
				arr = hotelPriceService.getRoomtypeMikePrices(hotelid, roomtypeid, startdateday, enddateday);
			result.put("prices", arr);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.info("ots::HotelPriceController::refreshMikePrices  error {}, {}",hotelid,e.getMessage());
		} finally{
			logger.info("ots::HotelPriceController::refreshMikePrices  end");
		}
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	
	
	/**
	 * 获取动态酒店每日价格
	 * @param hotelid
	 * @param roomtypeid
	 * @param startdateday
	 * @param enddateday
	 * @return
	 */
	@RequestMapping("/price/queryhotelprices")
	public ResponseEntity<Map<String,Object>> getHotelPrices(Long hotelid, Long roomtypeid, String startdateday, String enddateday) {
		logger.info("ots::HotelPriceController::getMikePricesList::params{}---{}  begin", hotelid, roomtypeid);
		
		Map<String,Object> result= new HashMap<String,Object>();
		try {
			if(hotelid== null){
				throw MyErrorEnum.errorParm.getMyException("参数错误!hotelid为空");
			}
			
			//支持yyyy-MM-dd格式与yyyyMMdd格式
			startdateday = startdateday.replaceAll("-", "");
			enddateday = enddateday.replaceAll("-", "");
			
		
			List<RoomTypePriceBean> list = hotelPriceService.getRoomtypePrices(hotelid, roomtypeid, startdateday, enddateday);
			result.put("prices", list);
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.info("ots::HotelPriceController::getMikePricesList  error {}, {}",hotelid,e.getMessage());
		} finally{
			logger.info("ots::HotelPriceController::getMikePricesList  end");
		}
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.OK);
	}
	
}
