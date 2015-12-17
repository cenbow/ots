package com.mk.ots.roomsale.service;

import java.util.List;
import com.mk.ots.roomsale.model.TPriceScopeDto;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
public interface TPriceScopeService {


	public List<TPriceScopeDto> queryTPriceScopeDto(String promoId, String cityCode)throws Exception;


}
