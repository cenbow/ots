package com.mk.ots.mapper;

import java.util.List;
import java.util.Map;

import com.mk.ots.recommend.model.TRecommenditem;

public interface TRecommenditemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TRecommenditem record);

    int insertSelective(TRecommenditem record);

    TRecommenditem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TRecommenditem record);

    int updateByPrimaryKey(TRecommenditem record);
    

	/**
	 * 查询发现列表或者banner
	 * @return
	 */
	public List<TRecommenditem> queryRecommendItem(Map<String, Object> map);
	
	/**
	 * 查询发现列表或者banner
	 * @return
	 */
	public List<TRecommenditem> queryRecommendItemLimit5(Map<String, Object> map);
}