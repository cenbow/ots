package com.mk.ots.recommend.service;

import com.mk.ots.mapper.TRecommendItemAreaMapper;
import com.mk.ots.mapper.TRecommenddetailMapper;
import com.mk.ots.mapper.TRecommenditemMapper;
import com.mk.ots.recommend.model.TRecommendItemArea;
import com.mk.ots.recommend.model.TRecommenddetail;
import com.mk.ots.recommend.model.TRecommenditem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendService {
    @Autowired
    private TRecommenditemMapper tRecommenditemMapper;
    @Autowired
    private TRecommenddetailMapper tRecommenddetailMapper;

	@Autowired
	private TRecommendItemAreaMapper tRecommendItemAreaMapper;

	public List<TRecommenditem> queryRecommendItem(String position, Integer platform){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("position", position);

		if (platform != null){
			map.put("platform", platform);
		}
		return tRecommenditemMapper.queryRecommendItem(map);
	}
	
	public List<TRecommenditem> queryRecommendItemLimit5(String position, Integer platform){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("position", position);

		if (platform != null){
			map.put("platform", platform);
		}

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

	/**
	 * 根据 cityid 获得推荐位地区表
	 * @param cityId
	 * @return
	 */
	public List<TRecommendItemArea> selectItemAreaByCityId(Integer cityId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cityid", cityId);
		return  tRecommendItemAreaMapper.selectByCityId(map);}
}
