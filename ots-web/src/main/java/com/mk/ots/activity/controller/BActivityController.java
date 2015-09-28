package com.mk.ots.activity.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.com.winhoo.mikeweb.exception.MyException;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.model.Page;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.activity.model.BActivity;
import com.mk.ots.activity.service.IBActivityService;
import com.mk.ots.member.dao.IBActiveUserBindMapper;
import com.mk.ots.member.dao.IBUserGroupMapper;
import com.mk.ots.member.model.BActiveUserBind;
import com.mk.ots.member.model.BUserGroup;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;

/**
 * 优惠活动接口,用于查询活动信息及参与酒店信息
 * @author nolan
 *
 */
@Controller
@RequestMapping(value = "/active", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class BActivityController {
	final Logger logger = LoggerFactory.getLogger(BActivityController.class);
	
	@Autowired
	private IBActivityService iBActivityService;
	
	@Autowired
	private IMemberService iMemberService;
	
	
	@RequestMapping("/querylist")
	public ResponseEntity<Map<String, Object>> querylist(String token, String hotelid, String activeid,
			String isactivedetil, String isacticepic, String startdateday,
			String enddateday, String begintime, String endtime, Integer page,
			String limit) {
		logger.info("查询活动(/querylist), activeid:{}.", activeid);
		page = page==null ? 1 : page;
		Page<BActivity> pageEntity;
		if (Strings.isNullOrEmpty(token)) {
			pageEntity= iBActivityService.query(null,hotelid,activeid,  isactivedetil,   isacticepic,  startdateday, enddateday,   begintime,   endtime,   page, limit);
		}else {
			Long mid=MyTokenUtils.getMidByToken(token);
			if (mid==null) {
				throw MyErrorEnum.accesstokenTimeOut.getMyException();
			}else {
				pageEntity= iBActivityService.query(mid.toString(),hotelid,activeid,  isactivedetil,   isacticepic,  startdateday, enddateday,   begintime,   endtime,   page, limit);
			}
			
		}
		
		//pageEntity = iBActivityService.query(hotelid,   activeid,  isactivedetil,   isacticepic,  startdateday, enddateday,   begintime,   endtime,   page, limit);
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("actives", pageEntity.getResult());
		logger.info("查询活动(/querylist), 结果:{}.", pageEntity);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK); 
	}
	
	/**
	 * 活动情况查询, 用于查询活动对应的优惠券领取情况信息
	 * @param token
	 * @param activeid
	 * @return
	 */
	@RequestMapping("/queryinfo")
	public ResponseEntity<Map<String, Object>> queryinfo(String token, String activeid) {
		//1. 校验参数
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不允许为空.");
		}
		
		//2. 查询用户参与某活动的具体信息 
		List<String> itemList= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(activeid);
		List<Map<String, Object>> infoList  = this.iBActivityService.queryinfo(MyTokenUtils.getMidByToken(token), itemList);
		
		//3. 构造返回
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("actives", infoList);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK); 
	}
	
	
}
