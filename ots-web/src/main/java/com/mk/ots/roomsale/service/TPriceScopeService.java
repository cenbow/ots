package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.TPriceScopeDto;

import java.util.List;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
public interface TPriceScopeService {



	public List<TPriceScopeDto> queryTPriceScopeDto(String promoId, String cityCode)throws Exception;

}
