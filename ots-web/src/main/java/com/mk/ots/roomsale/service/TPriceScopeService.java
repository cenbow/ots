package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.*;

import java.util.List;


/**
 * RoomSaleMapper.
 * 
 * @author jinxin.
 */
public interface TPriceScopeService {

	public List<TPriceScopeDto> queryTPriceScopeDto(String promoId, String cityCode)throws Exception;

}
