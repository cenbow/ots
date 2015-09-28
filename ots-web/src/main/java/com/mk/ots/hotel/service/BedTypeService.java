/**
 * 
 */
package com.mk.ots.hotel.service;

import java.util.List;

import com.mk.ots.hotel.bean.TBedType;

/**
 * @author yub
 *
 */
public interface BedTypeService {

	
	/**
	 * 酒店房型搜索
	 * @return
	 */
	public List<TBedType> getRoombedtype();
}
