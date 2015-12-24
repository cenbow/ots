package com.mk.ots.search.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.restful.output.SearchPositiontypesRespEntity;

public interface IPromoSearchService {


	/**
	 * 酒店综合查询接口方法
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, Object> readonlySearchHotels(HotelQuerylistReqEntity params);



	/**
	 * check if it is in current promotion period
	 * 
	 * @return
	 */
	public boolean isInPromoPeriod();

	/**
	 * search entry used in home page
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> searchHomePromos(HotelQuerylistReqEntity params) throws Exception;

	/**
	 * search normal entry used in home page
	 */

	public List<Map<String, Object>> searchHomeNormals(HotelQuerylistReqEntity params) throws Exception;

	/**
	 * search specifically for theme rooms
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> searchThemes(HotelQuerylistReqEntity params) throws Exception;

	/**
	 * search homepage thems
	 * 
	 * @param reqentity
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> searchHomePageThemes(HotelQuerylistReqEntity reqentity) throws Exception;

	/**
	 * 
	 * @param cityId
	 * @param promoId
	 * @return
	 * @throws Exception
	 */
	public Integer queryByPromoId(Integer promoId) throws Exception;

	public List<Map<String, Object>> queryThemeRoomtypes(Map<String, Object> hotel) throws Exception;
	
	public Map<String, Object> searchHomePromoRecommend(HotelQuerylistReqEntity params) throws Exception;	
}
