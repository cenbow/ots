package com.mk.pms.hotel.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
/**
 * PMS2.0 同步房间
 * 
 * @author LYN
 *
 */
public interface NewPMSHotelService {
	
	public Map<String,Object> initPMS(String hotelPMS);
	
    public Map<String, Object> syncHotelInfo(JSONObject jsonOBJ);

	public Map<String, Object> doLogin(String hotelId, String hotelName);

	public Map<String, Object> doOffLine(String hotelid);

	public Map<String, Object> changeHotelInfo(String hotelid);
}
