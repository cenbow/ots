package com.mk.ots.roomsale.service;

import com.mk.ots.roomsale.model.*;

import java.util.List;
import java.util.Map;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
public interface TPriceScopeService {



	public List<TPriceScopeDto> queryTPriceScopeDto(String  promoId,String cityCode)throws Exception;

}
