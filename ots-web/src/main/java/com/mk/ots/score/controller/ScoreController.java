package com.mk.ots.score.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.score.service.ScoreService;

/**
 * 评份
 * @author LYN
 *
 */
@Controller
@RequestMapping(method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class ScoreController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ScoreService scoreService=null;
	
	@RequestMapping("/score/modify")
	public ResponseEntity<Map<String,Object>> modifyHotelscore(HttpServletRequest request){
		String orderid= request.getParameter("orderid");
		if(StringUtils.isBlank(orderid)){
			logger.debug("缺少必须参数定单id.");
			throw MyErrorEnum.errorParm.getMyException("缺少必须参数定单id.");
		}
		
		String actiontype= request.getParameter("actiontype");
		if(StringUtils.isBlank(actiontype) || (!actiontype.equals("i") 
				&& !actiontype.equals("m") && !actiontype.equals("d"))){
			logger.debug("缺少必须参数操作类型i,m,d.");
			throw MyErrorEnum.errorParm.getMyException("缺少必须参数操作类型i,m,d.");
		}
		String score= request.getParameter("score");//评价内容
		//TODO: 图片暂不做处理，确定json格式
		String picStr= request.getParameter("scorepics");//图片
		
		Map<String,Object> param= new HashMap<String,Object>();
		param.put("orderid", request.getParameter("orderid"));
		param.put("action", actiontype);
		param.put("score", request.getParameter("score"));//评价内容 
		param.put("pics", picStr);
		
		String gradeStr= request.getParameter("grades");//评价价分数
		if(StringUtils.isBlank(score) && StringUtils.isBlank(gradeStr)){
			logger.info("订单:{}评价信息不完整", orderid);
			throw MyErrorEnum.errorParm.getMyException("订单"+ orderid +"评分信息不完整.");
		}
		
		if(StringUtils.isNotBlank(gradeStr)){
			try{
				JSONArray gradesJSONArray = JSONArray.fromObject(gradeStr);
				//TODO: 判断格式数据完整性
				param.put("grades", gradesJSONArray);
			}catch(Exception e){
				logger.info("grades格式错误:",gradeStr,e.getStackTrace());
				throw MyErrorEnum.errorParm.getMyException("grades格式错误.");
			}
		}else{
			logger.info("订单:{}评分为空", orderid);
		}
		
		boolean isSuccess=scoreService.modifyScore(param);
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("success", isSuccess);
		return new ResponseEntity<Map<String,Object>>(m, HttpStatus.OK);
	}

	/**
	 * 查询酒店评价信息
	 * @param hotelid
	 * @return
	 */
	@RequestMapping(value = {"/score/querylist"} )
	public ResponseEntity<Map<String,Object>> getHotelscore(HttpServletRequest request){		
		String hotelid= request.getParameter("hotelid");
		if(StringUtils.isBlank(hotelid)){
			logger.debug("缺少必须参数id.");
			throw MyErrorEnum.errorParm.getMyException("缺少必须参数酒店id.");
		}

		String token = request.getParameter("token");
        Long mid = null;
        if(StringUtils.isNotEmpty(token)){
        	mid = MyTokenUtils.getMidByToken(token);
        }
        logger.info("查询酒店评价信息:hotelid:{},token:{},mid:{}", hotelid, token, mid);

		String page= request.getParameter("page");
		String limit = request.getParameter("limit");
		
		String roomtypeid = request.getParameter("roomtypeid");
		String roomid= request.getParameter("roomid");
		
		Map<String,Object> resultMap= new HashMap<String,Object>();
		if(StringUtils.isNotBlank(roomid)){
			logger.debug("查询房间下的结果");
			Map<String,Object> roomMap = scoreService.findSubjectScoreByRoom( roomid);
			List l = new ArrayList();
			l.add(roomMap);
			resultMap.put("room", l);
		}else if(StringUtils.isNotBlank(roomtypeid)){
			logger.debug("查询房型结果");
			Map<String,Object> roomTypeMap = scoreService.findScoreRoomtype(hotelid, roomtypeid, mid);
			List l = new ArrayList();
			l.add(roomTypeMap);
			resultMap.put("roomtype", l);
		}else {
			//查询酒店评价
			logger.debug("查询酒店级下的结果");
			Map<String,Object> hotelMap= scoreService.findSubjectScoreHotel(hotelid, mid);
			List l = new ArrayList();
			l.add(hotelMap);
			resultMap.put("hotel", l);
		}
		
		//查询详细信息
		String isDetail = request.getParameter("isdetail");
		Map<String,Object> detailMap= new HashMap<String,Object>();
		if(StringUtils.isNotBlank(isDetail) && isDetail.equals("T")){
			if(StringUtils.isBlank(page) || StringUtils.isBlank(limit)){
				logger.info("获取酒店评价详细信息为T:{},page:{},limit:{}不能为空",isDetail,page,limit);
				throw MyErrorEnum.errorParm.getMyException("获取酒店评价详细信息为T:则page,limit不能为空");
			}
			logger.debug("开始加载详细信息");
			String maxgrade= request.getParameter("maxgrade");
			String mingrade = request.getParameter("mingrade");
			String subjectid= request.getParameter("subjectid");
			String orderby = request.getParameter("orderby");
			String startdateday = request.getParameter("startdateday");
			String enddateday = request.getParameter("enddateday");
			String starttime = request.getParameter("starttime");
			String endtime = request.getParameter("endtime");

			List<Map<String,Object>> scoreDetailList=scoreService.findScoreMxStatus(hotelid,roomtypeid,roomid,maxgrade,mingrade,subjectid,orderby,startdateday,enddateday,starttime,endtime,page,limit,mid);
			long mxCount=scoreService.findScoreMxCount(hotelid,roomtypeid,roomid,maxgrade,mingrade,subjectid,orderby,startdateday,enddateday,starttime,endtime,mid);
			resultMap.put("scoremxcount", mxCount);
			resultMap.put("scoremx", scoreDetailList);
		}else{
			logger.info("不显示详细信息");
		}
		resultMap.put("success", true);
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
	/**
	 * 计算各项平均值
	 * @return
	 */
	@RequestMapping("/score")
	public String score(){
		boolean isSuccess= scoreService.score();
		return ""+isSuccess;
	}
	
	/**
	 * 获取评分项信息
	 * @return
	 */
	@RequestMapping("/score/subject/querylist")
	public ResponseEntity<Map<String,Object>> getallsubject(String subjectid){
		List<Bean> l=scoreService.findSubject(subjectid);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("success", true);
		List<Map<String,Object>> resultList= new ArrayList<Map<String,Object>>();
		// fixed bug - OTS-148 : 获取评分项信息接口返回值中各评分项的subjectid相同.
		for(Bean b :l){
			Map<String,Object> rm= new HashMap<String,Object>();
			rm.put("subjectid", b.get("subjectid"));
			rm.put("subjectname", b.get("subjectname"));
			rm.put("minno", b.get("minno"));
			rm.put("maxno", b.get("maxno"));
			rm.put("dno", b.get("dno"));
			rm.put("mno", b.get("mno"));
			rm.put("wfun", b.get("weightfunction"));
			resultList.add(rm);
		}
		resultMap.put("subjects", resultList);
		ResponseEntity<Map<String,Object>> result = new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		return result;
	}
	
	/**
	 * 查询酒店分类评价数量
	 * @param hotelid
	 * @param scoregroup
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	@RequestMapping("/scoregroupcount/query")
	public ResponseEntity<Map<String,Object>> scoreGroupCount(String hotelid,String scoregroup,String startdateday,String enddateday, String token){
		if(StringUtils.isBlank(hotelid)){
			throw MyErrorEnum.errorParm.getMyException("缺少必须参数酒店id.");
		}
		if(StringUtils.isBlank(scoregroup)){
			throw MyErrorEnum.errorParm.getMyException("缺少必须参数评分数范围集合.");
		}
        Long mid = null;
        if(StringUtils.isNotEmpty(token)){
        	mid = MyTokenUtils.getMidByToken(token);
        }
        logger.info("查询酒店评价信息:hotelid:{},token:{},mid:{}", hotelid, token, mid);
		List<Bean> l=scoreService.scoreGroupCount(hotelid, scoregroup, startdateday, enddateday, mid);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("success",true);
		resultMap.put("hotelid", hotelid);
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for( Bean b: l){
			Map<String,Object> r= new HashMap<String,Object>();
			r.put("scoregroup",b.get("scoregroup"));
			r.put("scorecount", b.get("scorecount"));
			resultList.add(r);
		}
		resultMap.put("scoregroups", resultList);
		ResponseEntity<Map<String,Object>> result = new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		return result;
	}
}
