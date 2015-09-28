package com.mk.ots.recommend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.mapper.TRecommenddetailMapper;
import com.mk.ots.mapper.TRecommenditemMapper;
import com.mk.ots.recommend.model.TRecommenddetail;
import com.mk.ots.recommend.model.TRecommenditem;

@Service
public class RecommendService {
    @Autowired
    private TRecommenditemMapper tRecommenditemMapper;
    @Autowired
    private TRecommenddetailMapper tRecommenddetailMapper;

	public List<TRecommenditem> queryRecommendItem(String position){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("position", position);
		return tRecommenditemMapper.queryRecommendItem(map);
	}
	
	public List<TRecommenditem> queryRecommendItemLimit5(String position){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("position", position);
		return tRecommenditemMapper.queryRecommendItemLimit5(map);
	}
	
	/**
	 * 根据详情itemid查询发现详情
	 * @param id
	 * @return
	 */
	public  TRecommenddetail selectByRecommendid(Long id){
		return tRecommenddetailMapper.selectByRecommendid(id);
	}
	
	/**
	 * 根据详情id查询发现详情
	 * @param id
	 * @return
	 */
	public  TRecommenddetail selectByPrimaryKey(Long id){
		return tRecommenddetailMapper.selectByPrimaryKey(id);
	}
}
