/**
 * 
 */
package com.mk.ots.mapper;

import java.util.List;

import com.mk.ots.hotel.bean.EDailyRate;

/**
 * @author yub
 *
 */
public interface EDailyRateMapper {

	
	/**
	 * 根据酒店id查询酒店特殊价
	 * @param hotelid
	 * @return
	 */
	public List<EDailyRate> selectByHotelid(Long hotelid);

	/**
	 * 批量添加酒店特殊价
	 * @param insertBatchList
	 * @return
	 */
	public Integer saveBatch(List<EDailyRate> insertBatchList);

	/**
	 * 批量更新酒店特殊价
	 * @param updateBatchList
	 * @return
	 */
	public Integer updateBatch(List<EDailyRate> updateBatchList);

}
