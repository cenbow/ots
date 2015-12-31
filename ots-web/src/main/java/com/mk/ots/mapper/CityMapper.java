/**
 * 
 */
package com.mk.ots.mapper;

import com.mk.ots.hotel.bean.TCity;
import org.springframework.stereotype.Repository;

import java.util.List;

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

	public List<TCity> getAllCity();
}
