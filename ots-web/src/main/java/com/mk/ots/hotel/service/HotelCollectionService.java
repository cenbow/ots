package com.mk.ots.hotel.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.hotel.bean.HotelCollection;


public interface HotelCollectionService {
	
	/**
	 * @param token
	 * @param code
	 * @param start
	 * @param limit
	 * 酒店收藏查询
	 */
	List<Map<String, Object>> querylist(String token, String code,int start,int limit) throws Exception;
	/**
	 * @param token
	 * @param hotelid
	 * 酒店收藏
	 */
	void addCollection(String token, Long hotelid) throws Exception;
	/**
	 * @param token
	 * @param hotelid
	 * 删除收藏
	 */
	void deleteCollection(String token, Long hotelid) throws Exception;
	
	/**
	 * @param token
	 * @param hotelid
	 * 查询某用户是否收藏过某酒店
	 */
	HotelCollection queryinfo(String token,Long hotelid) throws Exception;
	/**
	 * 查询酒店是否已经收藏
	 * @param token
	 * @param hotelid
	 * @return
	 */
	Map<String, Object> readonlyHotelISCollected(String token, Long hotelid)  throws Exception;
	
	/**
	 * 查询已收藏酒店总数
	 * @param token
	 * @return
	 */
	int readonlyHotelCollectedCount(String token);
}
