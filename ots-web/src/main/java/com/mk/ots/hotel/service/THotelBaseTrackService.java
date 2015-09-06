/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 * 2015年6月26日下午2:53:08
 * zhaochuanbin
 */
package com.mk.ots.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.model.THotelBaseTrackModel;
import com.mk.ots.mapper.THotelBaseTrackMapper;


/**
 * @author zhaochuanbin
 *
 */
@Service
public class THotelBaseTrackService {
	
	@Autowired
	THotelBaseTrackMapper tHotelBaseTrackMapper;
	
	public int saveTHotelBaseTrack(THotelBaseTrackModel tHotelBaseTrackModel){
		return this.tHotelBaseTrackMapper.saveHotelBaseTrack(tHotelBaseTrackModel);
	}
}
