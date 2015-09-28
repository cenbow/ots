/**
 * 
 */
package com.mk.ots.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mk.ots.hotel.bean.TCity;

/**
 * @author YuB
 *
 */
@Repository
public interface CityMapper {
	
	/**
	 * 查询可查询城市
	 * @return
	 */
	public List<TCity> getSelectCity();
}
