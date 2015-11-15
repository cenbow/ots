package com.mk.ots.score.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mk.care.kafka.common.CopywriterTypeEnum;
import com.mk.care.kafka.common.MessageTypeEnum;
import com.mk.care.kafka.model.Message;
import com.mk.ots.kafka.message.OtsCareProducer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.common.enums.OtaOrderFlagEnum;
import com.mk.ots.mapper.THotelScoreMapper;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.score.dao.ScoreDAO;
import com.mk.ots.score.model.THotelScore;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.service.IWalletCashflowService;

/**
 * 评分服务类
 * 
 * @author LYN
 *
 */
@Service
public class ScoreService {

	private static final int SCORE_TYPE_USER = 1;// 评价类型 1:用户评价
	private static final int SCORE_TYPE_REPLY = 2;// 评价类型 2:评价回复

	private static final String REPLY_USER = "眯客回复:"; //回复用户统一显示为 眯客

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ScoreDAO scoreDAO = null;

	@Autowired
	private OrderService orderService = null;
	
	@Autowired
	private THotelScoreMapper tHotelScoreMapper;
	
	@Autowired
	private IWalletCashflowService walletCashflowService;
	
	@Autowired
	private OrderBusinessLogService orderBusinessLogService;

	@Autowired
	private OtsCareProducer careProducer;

	private Gson gson = new Gson();

	/**
	 * 保存分数
	 * @param param
	 * @return
	 */
	public boolean modifyScore(Map<String, Object> param) {
		logger.info("评价信息参数:{}", param.toString());
		String orderid = param.get("orderid").toString();
		OtaOrder otaOrder = orderService.findOtaOrderById(Long.parseLong(orderid));
		
		String action = param.get("action").toString();
		if(otaOrder==null){
			logger.info("modifyScore::未找到要评价的订单:{}", orderid);
			throw MyErrorEnum.errorParm.getMyException("未找到要评价的订单"+ orderid);
		}else{
			String orderstate= String.valueOf( otaOrder.get("orderstatus"));
			if(orderstate!=null && StringUtils.isNotBlank(orderstate)){
				if(!canScore(orderstate)){
					logger.info("modifyScore::订单:{} 状态为 非 待评价状态:{}", orderid, orderstate);
					throw MyErrorEnum.errorParm.getMyException("非待评价订单"+ orderid);
				}
			}else{
				logger.info("modifyScore::订单:{} 状态为{}", orderid, orderstate);
				throw MyErrorEnum.errorParm.getMyException("要评价的订单 "+ orderid +" 状态为空");	
			}
		}
		//判断是否已经评价
		//如果订单未评价，不能修改和删除(动作:m, d )。
		String isScoreStr=String.valueOf(otaOrder.get("isscore"));
		if(!isScoreStr.equals("T") && (action.equals("m") || action.equals("d"))){
			logger.info("modifyScore::订单:{} 未评价，不能修改,删除{}", orderid, action);
			throw MyErrorEnum.errorParm.getMyException("订单"+ orderid +"未评价，不能修改,删除");	
		}
		//如果订单已评价，不能再次评价(动作:i)。
		if(isScoreStr.equals("T") && action.equals("i")){
			logger.info("modifyScore::订单:{} 评价状态为:{}，不能重新执行插入操作:{}", orderid, isScoreStr, action);
			throw MyErrorEnum.errorParm.getMyException("订单" + orderid +"已经评价,不能重复评价!");	
		}
		/**
		 *判断订单评价是否已经回复，已经回复的评价不能修改，删除。
		 *	type字段: 1:评价, 2:酒店回复
		 * status字段:1:待审核, 2:审核不通过, 3:已删除, 4:待回复, 5:已回复, 6:有效回复 
		 */
		Bean scoreBean= scoreDAO.findScorebyOrderId(orderid);
		if("m".equals( action ) || "d".equals(action)){
			int isaduit = scoreBean.getInt("status");
			if(1 != isaduit ){
				logger.info("modifyScore::订单{},审核状态为{},不能再次修改或删除{}!", orderid, isaduit, action);
				throw MyErrorEnum.errorParm.getMyException("订单" + orderid +" 已经审核,不能修改或删除.");
			}
		}
		if(scoreBean != null && "i".equals(action)){
			logger.info("modifyScore::订单{},已经评价,不能重复评价!", orderid, action);
			throw MyErrorEnum.errorParm.getMyException("订单" + orderid +"已经评价,不能重复评价!");
		}
		
		List<OtaRoomOrder> roomOrderList = otaOrder.getRoomOrderList();	
		if(roomOrderList==null || roomOrderList.size()==0){
			logger.error("modifyScore::查找客单失败"+orderid);
			throw MyErrorEnum.errorParm.getMyException("查找客单失败"+orderid);
		}

		/**
		 * "现阶段一个订单下肯定会有一个且一个客单"
		 */
		OtaRoomOrder otaRoomOrder = roomOrderList.get(0);
		logger.info("现阶段一个订单下肯定会有一个且一个客单");
		String roomid = otaRoomOrder.get("roomid").toString();
		String roomtypeid = otaRoomOrder.get("roomtypeid").toString();
		String hotelid = otaRoomOrder.get("hotelid").toString();

		// 各项评价分数
		List sList = new ArrayList();

		List<Bean> subjectList = scoreDAO.findSubject("");

		BigDecimal subSUM = new BigDecimal(0);
		BigDecimal weightSUM = new BigDecimal(0);
		JSONArray gradeJSONArray = (JSONArray) param.get("grades");
		if(gradeJSONArray!=null){
			for (Bean subBean : subjectList) {
				String subjectid = subBean.get("subjectid").toString();
				if (StringUtils.isNotBlank(subjectid)) {
					BigDecimal weight = subBean.getBigDecimal("weightfunction");
					for(int i=0;i<gradeJSONArray.size();i++){
						JSONObject gradeJSON= gradeJSONArray.getJSONObject(i);
						String subjectidFromJSON= gradeJSON.getString("subjectid");
						if(subjectid.equals(subjectidFromJSON)){
							double value = gradeJSON.getDouble("grade");
							subSUM=subSUM.add(weight.multiply(BigDecimal.valueOf(value)));
							weightSUM=weightSUM.add(weight);
							List l = new ArrayList();
							l.add(roomid);
							l.add(roomtypeid);
							l.add(hotelid);
							l.add(value);
							l.add(subjectid);
							l.add(orderid);
							sList.add(l);
						}
					}
				}
			}
		}
		
		// 评价内容 参数
		List cList = new ArrayList();
		String scoreStr = param.get("score").toString();
		String picStr = (String)param.get("pics");
		String isDefault = (String) param.get("isdefault");
		cList.add(roomid);
		cList.add(roomtypeid);
		cList.add(hotelid);
		cList.add(scoreStr);
		cList.add(picStr);
		if(!weightSUM.equals(BigDecimal.ZERO)){
			cList.add(subSUM.divide(weightSUM,1,BigDecimal.ROUND_HALF_UP));
		}else{
			cList.add(0L);
		}
		cList.add(isDefault);
		cList.add(orderid);
		
		boolean isSuccess = true;
		switch (action) {
		case ("i"):
			cList.add(otaOrder.getMid());
			isSuccess = scoreDAO.insert(cList, sList,orderid);
			otaOrder.set("isscore","T").saveOrUpdate();
			break;
		case ("m"):
			isSuccess = scoreDAO.update(cList, sList,orderid);
			otaOrder.set("isscore","T").saveOrUpdate();
			break;
		case ("d"):
			isSuccess = scoreDAO.del(orderid);
			otaOrder.set("isscore","F").saveOrUpdate();
			break;
		}		

		return isSuccess;
	}

	/**
	 * 判断订单是否可评价
	 * 待付款： 订单未进行支付（显示 立即付款按钮 取消订单）【110 120 】
	 *	已预订： 在线支付及选择前台现付的（可以退款，时间待定） 【140 】
	 * 订单取消：用户主动取消的订单【513 】
	 * 订单过期： 用户未在限定时间内支付【 511 】 
	 * 退款中：     用户办理退款【510  】
	 * 退款完成： 用户退款完成【512】
	 * 已完成：用户完成入住。   【190  200  】
	 * 未入住：用户付款超时未办理入住【520】
	 * 待评价： 已经入住、 用户住宿时间结束、完成状态  【160 180  190  200  】，且未评价过
	 * @param orderstate
	 * @return
	 */
	private boolean canScore(String orderstate){
		if(orderstate.equals("160") || orderstate.equals("180")
				|| orderstate.equals("190") || orderstate.equals("200")){
			return true;
		}
		return false;
	}
	/**
	 * 查询评分项
	 * 
	 * @return
	 */
	public List<Bean> findSubject(String subjectid) {
		return scoreDAO.findSubject(subjectid);
	}

	/**
	 * 查询酒店评价信息
	 * 
	 * @param hotelid
	 * @return
	 */
	public Map<String,Object> findSubjectScoreHotel(String hotelid, Long mid) {
		// 酒店总评分
		Bean scoreS = scoreDAO.findScoreSByHotelid(hotelid, mid);
		Map<String,Object> hotelMap = new HashMap<String,Object>();
		hotelMap.put("hotelid", hotelid);
		BigDecimal s= new BigDecimal(5);//如果没有则设置为5分
		if(scoreS != null && null != scoreS.get("grade")	){
			s= scoreS.get("grade");
		}
		hotelMap.put("hotelgrade", s);
		if(scoreS!= null && scoreS.get("scorecount")!=null){
			hotelMap.put("scorecount", scoreS.get("scorecount"));
		}else{
			hotelMap.put("scorecount", 0);
		}
		// 查询酒店单项评分
		List<Map<String,Object>> hotelscorelist = new ArrayList<Map<String,Object>>();
		hotelscorelist = findSubjectScoreByHotel(hotelid);
		hotelMap.put("hotelscoresubject", hotelscorelist);
		return hotelMap;
	}

	//查询酒店单项总分
	public List<Map<String,Object>> findSubjectScoreByHotel(String hotelid){
		List<Bean> subjectList = scoreDAO.findSubject("");
		List<Bean> scoreList =scoreDAO.findSubjectScoreByHotel(hotelid);
		Map<String,BigDecimal> scoreMap = new HashMap<String,BigDecimal>();		
		for(Bean score: scoreList){
			String subject=String.valueOf( score.get("subjectid"));
			BigDecimal pr= score.getBigDecimal("pr"); 
			scoreMap.put(subject,pr);
		}
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(Bean subject: subjectList){
			String subjectid= String.valueOf(subject.get("subjectid"));
			BigDecimal maxScore= subject.getBigDecimal("maxno");
			BigDecimal score= scoreMap.get(subjectid);
			if(score!=null){
				maxScore = score;
			}
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("subjectid", subjectid);
			m.put("subjectname", subject.get("subjectname"));
			m.put("grade", maxScore);
			resultList.add(m);
		}
		return resultList;
	}
	
	/**
	 * 查询酒店房型评价
	 * 
	 * @param hotelid
	 * @param roomtypeid
	 * @return
	 */
	public Map<String,Object> findScoreRoomtype(String hotelid, String roomtypeid, Long mid) {
		// 酒店房型评分
		Bean sBean = scoreDAO.findScoreByHotelidRoomtypeid(hotelid, roomtypeid, mid);
		if(sBean==null){
			throw MyErrorEnum.errorParm.getMyException("无此酒店评价信息");
		}
		
		Map<String,Object> roomtypeMap = new HashMap<String,Object>();
		roomtypeMap.put("hotelid", sBean.get("hotelid"));
		roomtypeMap.put("roomtypeid", sBean.get("roomtypeid"));
		BigDecimal rgrade= sBean.get("grades");
		if(rgrade==null){
			rgrade= BigDecimal.ZERO;
		}else{
			rgrade = rgrade.setScale(1,BigDecimal.ROUND_UP);
		}
		roomtypeMap.put("roomtypegrade", rgrade);
		roomtypeMap.put("scorecount", sBean.get("itemc"));
		
		// 查询单项评分
		List<Bean> list = scoreDAO.findScoreSubjectByHotelidRoomtypeid(hotelid,
				roomtypeid);
		List<Bean> subjectList = scoreDAO.findSubject("");
		Map<String,BigDecimal> sMap = new HashMap<String,BigDecimal>();
		for(Bean b: list){
			String subject=String.valueOf( b.get("subjectid"));
			BigDecimal pr= b.getBigDecimal("grades"); 
			sMap.put(subject,pr);
		}
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(Bean subject: subjectList){
			String subjectid= String.valueOf(subject.get("subjectid"));
			BigDecimal maxScore= subject.getBigDecimal("maxno");
			BigDecimal grade= sMap.get(subjectid);
			if(grade!=null){
				maxScore = grade;
			}
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("subjectid", subjectid);
			m.put("subjectname", subject.get("subjectname"));
			m.put("grade", maxScore);
			resultList.add(m);
		}
		
		roomtypeMap.put("roomtypescoresubject", resultList);
		return roomtypeMap;
	}

	/**
	 * 查询房间评分
	 * 
	 * @param roomtypeid
	 * @param roomid
	 * @return
	 */
	public Map<String,Object> findSubjectScoreByRoom(String roomid) {
		// 房间总分
		Bean sBean = scoreDAO.findScoreByRoomid(roomid);
		if(sBean==null){
			throw MyErrorEnum.errorParm.getMyException("无此酒店评价信息");
		}

		Map<String,Object> roomMap = new HashMap<String,Object>();
		roomMap.put("roomtypeid", sBean.get("roomtypeid"));
		roomMap.put("roomid", sBean.get("roomid"));
		BigDecimal rgrade= sBean.get("grades");
		if(rgrade==null){
			rgrade= BigDecimal.ZERO;
		}else{
			rgrade = rgrade.setScale(1,BigDecimal.ROUND_UP);
		}
		roomMap.put("roomgrade", rgrade);
		roomMap.put("scorecount", sBean.get("itemc"));
		
		
		List<Bean> subjectList = scoreDAO.findSubject("");
		// 单项房间评分
		List<Bean> list = scoreDAO.findScoreSubjectByRoomid(roomid);
		Map<String,BigDecimal> sMap = new HashMap<String,BigDecimal>();
		for(Bean b: list){
			String subject=String.valueOf( b.get("subjectid"));
			BigDecimal pr= b.getBigDecimal("grades"); 
			sMap.put(subject,pr);
		}
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(Bean subject: subjectList){
			String subjectid= String.valueOf(subject.get("subjectid"));
			BigDecimal maxScore= subject.getBigDecimal("maxno");
			BigDecimal grade= sMap.get(subjectid);
			if(grade!=null){
				maxScore = grade;
			}
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("subjectid", subjectid);
			m.put("subjectname", subject.get("subjectname"));
			m.put("grade", maxScore);
			resultList.add(m);
		}
		
		roomMap.put("roomscoresubject", resultList);
		return roomMap;
	}

	/**
	 * 计算各项平均分
	 * 
	 * @return
	 */
	public boolean score() {
		scoreDAO.scoreAverage();
		scoreDAO.scoreR();
		boolean issuccess = scoreDAO.scoreS();
		return issuccess;
	}

	/**
	 * 返回评价明细
	 * 
	 * @param maxgrade
	 * @param mingrade
	 * @param subjectid
	 * @param orderby
	 * @param startdateday
	 * @param enddateday
	 * @param starttime
	 * @param endtime
	 */
	public List<Map<String,Object>> findScoreMxStatus(String hotelid,String roomtypeid,String roomid, String maxgrade, String mingrade,
			String subjectid, String orderby, String startdateday,
			String enddateday, String starttime, String endtime,String page,String limit, Long mid, String gradetype) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int pageInt=1;
		int limitInt=10;
		if(StringUtils.isNotBlank(page)){
			pageInt = Integer.parseInt(page);
		}
		if(StringUtils.isNotBlank(limit)){
			limitInt= Integer.parseInt(limit);			
		}			
		
		List<Bean> list = scoreDAO.findScoreMx2(hotelid,roomtypeid,roomid, maxgrade, mingrade,
				subjectid, orderby, startdateday, enddateday, starttime,
				endtime,pageInt, limitInt, SCORE_TYPE_USER, mid, gradetype);
		
		//根据ids 查询回得数据
		List<Long> idList = new ArrayList<Long>();
		StringBuffer ids = new StringBuffer();
		List<Long> midList = new ArrayList<Long>();
		StringBuffer mids = new StringBuffer();
		
		for(Bean b: list){
			idList.add(b.getLong("id"));
			ids.append("?,");
			
			midList.add(b.getLong("mid"));
			mids.append("?,");
		}

		List<Bean> memberList = new ArrayList<Bean>();
		List<Bean> replyList = new ArrayList<Bean>();
		if(ids.length()>1){
			ids.deleteCharAt(ids.length()-1);
			replyList = scoreDAO.findScoreReplyByParentIds(ids.toString(), idList);
		}
		if(mids.length()>1){
			mids.deleteCharAt(mids.length()-1);
			memberList = scoreDAO.findMemberByIds(mids.toString(), midList);
		}
		Map<Long, Bean> replyMap = listToMap(replyList, "parentid");
		Map<Long, Bean> memberMap = listToMap(memberList, "mid");
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for (Bean bb : list) {
			String orderid = bb.get("orderid").toString();
			List<Bean> mxlstis = scoreDAO.findSubjectByOrderid(orderid);
			List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();
			for (Bean b : mxlstis) {
				Map<String,Object> scoreMap = new HashMap<String,Object>();
				scoreMap.put("subjectid", b.get("subjectid").toString());
				scoreMap.put("subjectname", b.get("subjectname").toString());
				scoreMap.put("grade", b.get("grade"));
				rlist.add(scoreMap);
			}
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("orderid", bb.get("orderid"));
			//m.put("hotelid", bb.get("hotelid").toString());
			m.put("roomtypeid", bb.get("roomtypeid"));
			Long mmid = bb.getLong("mid");
			if(null != memberMap.get(mmid)){
				Bean memberBean = memberMap.get(mmid);
				m.put("phone", memberBean.get("phone"));
			}else{
				m.put("phone", "");
			}
			m.put("roomid", bb.get("roomid"));
			m.put("score", bb.get("score"));
//			String allgrade= bb.get("grade")==null?"0":String.valueOf(bb.get("grade"));
			BigDecimal gr= bb.get("grade");
			if(gr==null){gr= new BigDecimal("0");}
			m.put("allgrade", gr);
			Date createtime = bb.getDate("createtime");
			m.put("createtime", sdf.format(createtime));
			
			Long id= bb.getLong("id");
			Bean replyBean = replyMap.get(id);
			if( null != replyBean ){
				if(null != replyBean.get("score")){
					m.put("hotelreply", replyBean.get("score"));
				}
				if(null != bb.get("createtime")){
					Date hotelreplytime = replyBean.getDate("createtime");
					m.put("hotelreplytime", sdf.format(hotelreplytime));
				}
				m.put("replyuser",  REPLY_USER);
			}else{
				m.put("hotelreply", "");
				m.put("hotelreplytime", "");
			}
			
			// fixed bug - OTS-143 : 查询酒店评价信息返回图片格式不对
			List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> picResult = new ArrayList<Map<String, Object>>();
			String scorepic = bb.getStr("pics");
			if (!StringUtils.isBlank(scorepic)) {
			    try {
			        pics = (new Gson()).fromJson(scorepic, List.class);
			        Map<String,Object> picmap = new HashMap<String,Object>();
			        for(Map<String,Object> pic: pics){
			        	picmap = new HashMap<String,Object>();
			            String picurl=String.valueOf(pic.get("scorepicurl"));
			            picmap.put("url", picurl);	
			            picResult.add(picmap);
			        }
                } catch (Exception e) {
                    logger.error("解析酒店评价图片数据: {}出错. {} ", scorepic, e.getMessage());
                }
			}
			
			m.put("scorepic", picResult);
			m.put("roomscoresubject", rlist);
			resultList.add(m);
		}
		return resultList;
	}

	private Map<Long, Bean> listToMap(List<Bean> list, String keyStr){
		Map<Long, Bean> map = new HashMap<Long, Bean>();
		for(Bean b : list){
			Long keyValue = b.getLong(keyStr);
			if(!map.containsKey(keyValue)){
				map.put(keyValue, b);
			}
		}
		return map;
	}
	
	
	/**
	 * 分组统计
	 * @param hotelid
	 * @param scoregroups
	 * @param startdate
	 * @param enddate
	 */
	public List<Bean> scoreGroupCount(String hotelid, String scoregroups,
			String startdate, String enddate, Long mid, String gradetype) {
		List<Bean> resultList = new ArrayList<Bean>();
		if(StringUtils.isBlank(gradetype)){
			resultList = scoreDAO.findScoreGroup(hotelid, scoregroups, startdate, enddate, mid);
		}else{
			resultList = scoreDAO.findScoreGroupByGrade(hotelid, gradetype, startdate, enddate, mid);
		}
		return resultList;
	}
	/**
	 * 计算总数
	 * @param hotelid
	 * @param roomtypeid
	 * @param roomid
	 * @param maxgrade
	 * @param mingrade
	 * @param subjectid
	 * @param orderby
	 * @param startdateday
	 * @param enddateday
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public long findScoreMxCount(String hotelid, String roomtypeid,
			String roomid, String maxgrade, String mingrade, String subjectid,
			String orderby, String startdateday, String enddateday,
			String starttime, String endtime, Long mid, String gradetype) {
		return  scoreDAO.findScoreMxCount(hotelid,roomtypeid,roomid, maxgrade, mingrade,
				subjectid, orderby, startdateday, enddateday, starttime, endtime, mid, gradetype);
	}

	/**
	 * 返现
	 * @param scoreid
	 * @return
	 */
	public Map<String, Object> scoreBackCash(Long scoreid) {
		THotelScore tHotelScore = tHotelScoreMapper.selectByPrimaryKey(scoreid);
        if(tHotelScore==null){
            throw MyErrorEnum.errorParm.getMyException("此评价不存在.");
        }

		String isBacked = tHotelScore.getIscashbacked();
		if(StringUtils.isNotBlank(isBacked) && isBacked.equals("T")){
			throw MyErrorEnum.errorParm.getMyException("此评价已经返现.");
		}
		BigDecimal backcost = walletCashflowService.entry(tHotelScore.getMid(), CashflowTypeEnum.CASHBACK_HOTEL_IN, tHotelScore.getId());
		if(backcost == null ){
			backcost = BigDecimal.ZERO;
		}
		tHotelScore.setIscashbacked("T");
		tHotelScore.setBackcashcost(backcost);
		
		orderBusinessLogService.saveLog(tHotelScore.getOrderid(), OtaOrderFlagEnum.SCORE_CASHBACK.getId(), "点评返现, ¥" + backcost + "红包已放入您的账户");
//		this.logger.info("返现开始下发乐住币，延迟时间【" + 10 + "】分钟订单号：{}，详细信息：{}", tHotelScore.getId(), gson.toJson(tHotelScore));
		OtaOrder  order  = orderService.findOtaOrderById(tHotelScore.getOrderid());
//		Boolean bl = orderService.afterScoreSendMessage(order,10,backcost);
		int changecount = tHotelScoreMapper.updateByPrimaryKey(tHotelScore);

		Message message=  new Message();
		message.setMid(tHotelScore.getMid());
		message.setOrderId(tHotelScore.getOrderid());
		message.setMessageTypeEnum(MessageTypeEnum.sms);
		message.setCopywriterTypeEnum(CopywriterTypeEnum.order_comment_return);
		message.setPhone(order.getContactsPhone());

		careProducer.sendSmsMsg(message);

		//发送app消息
		message.setMessageTypeEnum(MessageTypeEnum.app);
		careProducer.sendAppMsg(message);


		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
		resultMap.put("cashbackcost", backcost);
		return resultMap;
	}
	/**
	 * 获取orderid
	 * @param scoreid
	 * @return
	 */
	public THotelScore findScoreByScoreid(Long scoreid){
		return tHotelScoreMapper.selectByPrimaryKey(scoreid);
	}
}
