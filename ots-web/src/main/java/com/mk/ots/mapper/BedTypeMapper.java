/**
 * 
 */
package com.mk.ots.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mk.ots.hotel.bean.TBedType;

/**
 * @author YuB
 *
 */
@Repository
public interface BedTypeMapper {
	
	/**
	 *  查询酒店房型      
	 * @return
	 */
	public List<TBedType> getRoombedtype();
	
	/**
	 * 查询酒店床型
	 * @param hotelid
	 * 参数：酒店id
	 * @return
	 */
	public List<Map<String, Object>> selectBedtypesByHotelId(String hotelid);
}
