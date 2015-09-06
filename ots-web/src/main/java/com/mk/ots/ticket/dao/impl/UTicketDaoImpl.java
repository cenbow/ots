package com.mk.ots.ticket.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.UTicket;

@Component
public class UTicketDaoImpl extends MyBatisDaoImpl<UTicket, Long> implements UTicketDao{
	final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public List<UTicket> findUTicket(long mid, int status) {
		Map<String,Object> of = Maps.newHashMap();
		of.put("mid", mid);
		of.put("status", status);
		return super.find("findUTicket", of);
	}

	@Override
	public List<UTicket> findUTicketByMid(Long mid, Boolean status) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		if(status!=null){
			param.put("status", status ? 1 : 2);
		} 
		return super.find("findUTicketByMid", param);
	}
	
	@Override
	public List<UTicket> findUTicketByMidAndActiveid(Long mid, Long activeid) {
		Map<String,Object> of = Maps.newHashMap();
		of.put("mid", mid);
		of.put("activeid", activeid);
		return super.find("findUTicketByMidAndActiveid", of);
	}

	@Override
	public List<UTicket> findUTicketByPromotionAndMid(List<Long> promotionids,
			long mid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("promotionids", promotionids);
		param.put("mid", mid);
		return super.find("findUTicketByPromotionAndMid", param);
	}
	
	@Override
	public UTicket findUTicketByPromoIdAndMid(Long promotionid, Long mid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("promotionid", promotionid);
		param.put("mid", mid);
		return super.findOne("findUTicketByPromoIdAndMid", param);
	}
	
	@Override
	public List<UTicket> findUTicketByPromotionType(long mid, PromotionTypeEnum promotiontype){
		Map<String,Object> param = Maps.newHashMap();
		param.put("promotiontype", promotiontype);
		param.put("mid", mid);
		return super.find("findUTicketByPromotionType", param);
	}
	
	@Override
	public boolean isCheckTodayHotelRoom(long hotelid, long roomid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("hotelid", hotelid);
		param.put("roomid", roomid);
		return this.count("isCheckTodayHotelRoom", param)>0;
	}
	
	@Override
	public boolean isCheckTodayUser(long mid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		return this.count("isCheckTodayUser", param)>0;	
	}
	
	@Override
	public boolean isCheckMonthHotel(long hotelid, long mid){
		int limit = 4;
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("hotelid", hotelid);
		return this.count("isCheckMonthHotel", param)>limit;	 
	}
	
	@Override
	public List<Map> queryCurMonthUTicketNumAndOrderNum(){
		Map<String, Object> newHashMap = Maps.newHashMap();
		return this.findObjectList("queryCurMonthUTicketNumAndOrderNum", newHashMap);
	}
	
	@Override
	public List<Long> queryActiveMemberRuleList(){
		Map<String, Object> newHashMap = Maps.newHashMap();
		return this.findObjectList("queryActiveMemberRuleList",newHashMap);
	}
	
	@Override
	public List<Long> queryUnActiveMemberRuleList(){
		Map<String, Object> newHashMap = Maps.newHashMap();
		return this.findObjectList("queryUnActiveMemberRuleList",newHashMap);
	}
	 
	@Override
	public void saveOrUpdate(UTicket uTicket) {
		 if(uTicket.getId()!=null){
			 this.update(uTicket);
		 }else{
			 this.insert(uTicket);
		 }
	}
	 
	 @Override
	public UTicket findByPromotionId(long promotionid) {
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("promotionid", promotionid);
		return this.findOne("findByPromotionId",newHashMap);
	}
	 
	 @Override
	public long findByPromotionIdAndOrderId(Long orderid, Long promotionid) {
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("orderid", orderid);
		newHashMap.put("promotionid", promotionid);
		return this.count("findByPromotionIdAndOrderId", newHashMap);
	}
	 
	 @Override
	public long countByMidAndActiveId(Long mid, Long activeid) {
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("activeid", activeid);
		return this.count("countByMidAndActiveId", newHashMap);
	}
	 
	@Override
	public long countByMidAndActiveIdAndTime(Long mid, Long activeid, Date starttime, Date endtime){
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("activeid", activeid);
		newHashMap.put("starttime", starttime);
		newHashMap.put("endtime", endtime);
		return this.count("countByMidAndActiveIdAndTime", newHashMap);
	}
	
	@Override
	public  List<UTicket> findUTicketByMidAndActiveidReturnUTicket (long mid,long activityid){
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("activityid", activityid);
		//param.put("status", status);
		return this.find("findUTicketByMidAndActiveidReturnUTicket", param);
	}
//	 @Override
//	public boolean isCheckNumToday(Long mid){
//		 //1. 判断当前时间是否在当天6:00前
//		 String limitNumStr = "0";//SysConfigManager.getInstance().readOne(Constant.mikewebtype, Constant.QIKE_TODAY_LIMITNUM);
//		if(Strings.isNullOrEmpty(limitNumStr)){ 
//			logger.info("未设置单天切客次数限制. {}({}):{}", Constant.QIKE_TODAY_LIMITNUM, Constant.mikewebtype, limitNumStr);
//			throw MyErrorEnum.customError.getMyException("未设置单天切客次数限制.");
//		}
//		long limit = Cast.to(limitNumStr,0l);
//		
//		LocalDateTime nowtime = LocalDateTime.now();
//		Date startdate = null;
//		Date enddate = null;
//		org.joda.time.LocalDateTime.Property e = nowtime.hourOfDay();
//		LocalDateTime timeline = e.withMaximumValue().withHourOfDay(6).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
//		if(nowtime.isBefore(timeline)){
//			//1.1 在6:00前
//			startdate = nowtime.plusDays(-1).hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
//			enddate = nowtime.hourOfDay().withMaximumValue().withHourOfDay(5).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
//		}else if(nowtime.isAfter(timeline) || nowtime.isEqual(timeline)){
//			//1.2 在6:00后
//			startdate = nowtime.hourOfDay().withMaximumValue().withHourOfDay(6).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
//			enddate = nowtime.plusDays(1).hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
//		}
//		 
//		Map<String, Object> newHashMap = Maps.newHashMap();
//		newHashMap.put("mid", mid);
//		newHashMap.put("startdate", startdate);
//		newHashMap.put("enddate", enddate);
//		long count = this.count("isCheckNumToday", newHashMap);
//		boolean b = count <= limit;
//		logger.info("今天({})切客次数:{} <= 系统限制次数:{} ＝ {}", new Date(), count, limit, b);
//		return b;
//	 }
//	 
//	 @Override
//	public boolean isCheckNumMonth(Long mid, Long hotelid){
//		 String limitNumStr = "3"; //SysConfigManager.getInstance().readOne(Constant.mikewebtype, Constant.QIKE_MONTH_LIMITNUM);
//		 if(Strings.isNullOrEmpty(limitNumStr)){
//			logger.info("未设置当月切客次数限制. {}({}):{}", Constant.QIKE_MONTH_LIMITNUM, Constant.mikewebtype, limitNumStr);
//			throw MyErrorEnum.customError.getMyException("未设置当月切客次数限制.");
//		 }
//		 long limit = Cast.to(limitNumStr,0l);
//		 
//		 LocalDateTime nowtime = LocalDateTime.now();
//		 Date startdate = null;
//		 Date enddate = null;
//		 org.joda.time.LocalDateTime.Property e = nowtime.hourOfDay();
//		 LocalDateTime timeline = e.withMaximumValue().withHourOfDay(6).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
//		 if(nowtime.isBefore(timeline)){
//			//1.1 在6:00前
//			startdate = nowtime.dayOfMonth().withMinimumValue().plusDays(-1).hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
//			enddate = nowtime.dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().withHourOfDay(5).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
//		 }else if(nowtime.isAfter(timeline) || nowtime.isEqual(timeline)){
//			//1.2 在6:00后
//			startdate = nowtime.dayOfMonth().withMinimumValue().hourOfDay().withMaximumValue().withHourOfDay(6).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
//			enddate = nowtime.dayOfMonth().withMaximumValue().plusDays(1).hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
//		 }
//		
//		 Map<String, Object> newHashMap = Maps.newHashMap();
//		 newHashMap.put("mid", mid);
//		 newHashMap.put("hotelid", hotelid);
//		 newHashMap.put("startdate", startdate);
//		 newHashMap.put("enddate", enddate);
//		 long count = this.count("isCheckNumMonth", newHashMap);
//		 boolean b = count <= 3;
//		 logger.info("今月({})切客次数:{} <= 系统限制次数:{} = {}", new Date(), count, limit, b);
//		 return b;
//	 }
	 
	 public boolean isCheckNumToday(Long mid){
		LocalDateTime nowtime = LocalDateTime.now();
		Date startdate = null;
		Date enddate = null;
		LocalDateTime timeline = nowtime.hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		if(nowtime.isBefore(timeline)){
			//1.1 在AM12:00前
			startdate = nowtime.plusDays(-1).hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
			enddate = nowtime.hourOfDay().withMaximumValue().withHourOfDay(11).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
		}else if(nowtime.isAfter(timeline) || nowtime.isEqual(timeline)){
			//1.2 在AM12:00后
			startdate = nowtime.hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
			enddate = nowtime.plusDays(1).hourOfDay().withMaximumValue().withHourOfDay(11).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
		}
		
		int limit = 0;
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("startdate", startdate);
		newHashMap.put("enddate", enddate);
		long count = this.count("isCheckNumToday", newHashMap);
		boolean b = count <= limit;
		logger.info("今天({})切客次数:{} <= 系统限制次数:{} ＝ {}", new Date(), count, limit, b);
		return b;
	 }
	 
	 public boolean isCheckNumMonth(Long mid, Long hotelid){
		 LocalDateTime nowtime = LocalDateTime.now();
		 Date startdate = null;
		 Date enddate = null;
		 LocalDateTime timeline = nowtime.hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		 if(nowtime.isBefore(timeline)){
			//1.1 在AM12:00前
			startdate = nowtime.dayOfMonth().withMinimumValue().plusDays(-1).hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
			enddate = nowtime.dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().withHourOfDay(11).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
		 }else if(nowtime.isAfter(timeline) || nowtime.isEqual(timeline)){
			//1.2 在AM12:00后
			startdate = nowtime.dayOfMonth().withMinimumValue().hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
			enddate = nowtime.dayOfMonth().withMaximumValue().plusDays(1).hourOfDay().withMaximumValue().withHourOfDay(11).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
		 }
		 
		 int limit = 3;
		 Map<String, Object> newHashMap = Maps.newHashMap();
		 newHashMap.put("mid", mid);
		 newHashMap.put("hotelid", hotelid);
		 newHashMap.put("startdate", startdate);
		 newHashMap.put("enddate", enddate);
		 long count = this.count("isCheckNumMonth", newHashMap);
		 boolean b = count <= limit;
		 logger.info("今月({})切客次数:{} <= 系统限制次数:{} = {}", new Date(), count, limit, b);
		 return b;
	 }
	 
	 @Override
	public List<UTicket> findUTicketsByOrderIdAndMid(Long otaorderid, Long mid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("otaorderid", otaorderid);
		param.put("mid", mid);
		return super.find("findUTicketsByOrderIdAndMid", param);
	 }

	@Override
	public void updateUTicketAvailable(Long orderid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("otaorderid", orderid);
		super.update("updateUTicketAvailable", param);
	}
	@Override
	public void updateByMidAndPromotionId(UTicket uTicket) {

		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", uTicket.getMid());
		param.put("promotionid", uTicket.getPromotionid());
		param.put("otaorderid", uTicket.getOtaorderid());
		param.put("usetime", uTicket.getUsetime());
		param.put("stauts", uTicket.getStatus());
		super.update("updateByMidAndPromotionId", param);
	}

	@Override
	public Map<String, Object> findMaxAndMinUTicketId(Map<String, Object> paramMap) {
		return (Map<String, Object>)findObjectList("findMaxAndMinUTicketId", paramMap).get(0);
	}

	@Override
	public List<UTicket> findUTicketList(Map<String, Object> paramMap) {
		return find("findUTicketList", paramMap);
	}
	

    @Override
	public Long getHandGetPromotionCount(Long mid) {
    	Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		return super.count("getHandGetPromotionCount", param);
	}
	 
	@Override
	public List<UTicket> getNotActivePromotions(Long mid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		return super.find("getNotActivePromotionCount", param);
	}

	@Override
	public boolean activatePromotion(Long mid, Long promotionid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("promotionid", promotionid);
		return super.update("activatePromotion", param) > 0;
	}

	
	/**
	 * 校验订单是否使用了优惠券
	 */
	@Override
	public boolean checkOrderUsedTicket(Long mid, Long otaorderid) {
		UTicket ticket = new UTicket();
		ticket.setMid(mid);
		ticket.setOtaorderid(otaorderid);
		Long count = super.findCount("checkOrderUsedTicket", ticket);
		return  count>0;
	}

    @Override
    public UTicket findUnactiveUTicket(Long mid, long liveThreeActive, Integer id) {
        Map<String,Object> param = Maps.newHashMap();
        param.put("mid", mid);
        param.put("activityid", liveThreeActive);
        param.put("status", id);
        return super.findOne("findUnactiveUTicket", param);
    }
}
