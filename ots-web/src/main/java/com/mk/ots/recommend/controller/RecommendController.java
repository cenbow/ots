package com.mk.ots.recommend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.recommend.model.RecommendDetail;
import com.mk.ots.recommend.model.RecommendList;
import com.mk.ots.recommend.model.TRecommenddetail;
import com.mk.ots.recommend.model.TRecommenditem;
import com.mk.ots.recommend.service.RecommendService;


@Controller
@RequestMapping(value="/recommend", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class RecommendController {

	final Logger logger = LoggerFactory.getLogger(RecommendController.class);
	
	@Autowired
	private RecommendService recommendService;
	/**
	 * 根据位置查询banner或者列表
	 * http://127.0.0.1:8080/ots/recommend/query
	 * @param position
	 * @return
	 */
	@RequestMapping("/query")
	public ResponseEntity<Map<String, Object>> query(String position){
		Map<String, Object> rtnMap = Maps.newHashMap();
		if(StringUtils.isEmpty(position)){
			throw MyErrorEnum.errorParm.getMyException();
		}
		List<TRecommenditem> list ;
		//banner页只返回5条数据
		if (position.equals("921A") ||position.equals("921C")) {
			list= recommendService.queryRecommendItemLimit5(position);
		}else {
			list= recommendService.queryRecommendItem(position);
		}
		
		List<RecommendList> banners = new ArrayList<RecommendList>();
		
		
		if(CollectionUtils.isNotEmpty(list)){
			for (TRecommenditem tRecommendItem : list) {
				RecommendList recommendList  =new RecommendList();
				recommendList.setName(tRecommendItem.getTitle());
				recommendList.setDescription(tRecommendItem.getDescription());
				recommendList.setImgurl(tRecommendItem.getImageurl());
				recommendList.setUrl(tRecommendItem.getLink());
				recommendList.setDetailid(tRecommendItem.getDetailid());
				recommendList.setQuerytype(tRecommendItem.getViewtype());
				recommendList.setCreatetime(tRecommendItem.getCreatetime());
				banners.add(recommendList);
			}
		}
		
		rtnMap.put("banners", banners);  
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	/**
	 * 根据发现详情页id 得到详情页信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/querydetail")
	public ResponseEntity<Map<String, Object>> querydetail(String detailid){
		Map<String,Object> rtnMap = Maps.newHashMap();
		if(StringUtils.isEmpty(detailid)){
			throw MyErrorEnum.errorParm.getMyException();
		}
		TRecommenddetail detail  = recommendService.selectByPrimaryKey(Long.valueOf(detailid));
		if(detail!=null){
			RecommendDetail detail2 = new RecommendDetail();
			detail2.setImage(detail.getTopimage());
			detail2.setSubtitle(detail.getSubtitle());
			detail2.setTitle(detail.getTitle());
			detail2.setWord(detail.getContent());
			rtnMap.put("detail", detail2);
		}
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
}
