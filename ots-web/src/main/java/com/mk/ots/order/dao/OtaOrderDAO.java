package com.mk.ots.order.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.order.model.BOtaorder;
import com.mk.ots.order.model.FirstOrderModel;

/**
 * otaorder dao
 * 
 * @author tankai
 *
 */
@Repository
public class OtaOrderDAO extends MyBatisDaoImpl<BOtaorder, Long> {
	public Map<String, Object> findMaxAndMinOrderId(Map<String, Object> paramMap) {
		return (Map<String, Object>)findObjectList("findMaxAndMinOrderId", paramMap).get(0);
	}

	public List<BOtaorder> findOtaOrderList(Map<String, Object> paramMap) {
		return find("findOtaOrderList", paramMap);
	}
	public Map<String, Object> findPromotionMaxAndMinMId(Map<String, Object> paramMap) {
		return (Map<String, Object>)findObjectList("findPromotionMaxAndMinMId", paramMap).get(0);
	}
	
	public List<BOtaorder> findPromotionMidList(Map<String, Object> paramMap) {
		return find("findPromotionMidList", paramMap);
	}
	
	public List<BOtaorder> findOtaOrderListByMidAndHotelId(Map<String, Object> paramMap) {
		return find("selectByMidAndHotelId", paramMap);
	}
	public Long findMemberOnlyOneOrderCount(BOtaorder bOtaorder) {
		return findCount("findMemberOnlyOneOrderCount", bOtaorder);
	}
	
	public boolean isCheckNumToday(Long mid, Date checkintime) {
		LocalDateTime nowtime = LocalDateTime.now();
		if(checkintime != null){
			nowtime = LocalDateTime.fromDateFields(checkintime);
		}
		Date startdate = null;
		Date enddate = null;
		LocalDateTime timeline = nowtime.hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		if (nowtime.isBefore(timeline)) {
			// 1.1 在AM12:00前
			startdate = nowtime.plusDays(-1).hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
			enddate = nowtime.hourOfDay().withMaximumValue().withHourOfDay(11).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
		} else if (nowtime.isAfter(timeline) || nowtime.isEqual(timeline)) {
			// 1.2 在AM12:00后
			startdate = nowtime.hourOfDay().withMaximumValue().withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
			enddate = nowtime.plusDays(1).hourOfDay().withMaximumValue().withHourOfDay(11).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0).toDate();
		}

		int limit = 0;
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("startdate", startdate);
		newHashMap.put("enddate", enddate);
		long count = this.count("isCheckNumTodayForB", newHashMap);
		boolean b = count <= limit;
		logger.info("今天({})切客次数:{} <= 系统限制次数:{} ＝ {}", new Date(), count, limit, b);
		return b;
	}
	
	 public boolean isCheckNumMonth(Long mid, Long hotelid,Date checkintime){
		 LocalDateTime nowtime = LocalDateTime.now();
		if(checkintime != null){
			nowtime = LocalDateTime.fromDateFields(checkintime);
		}
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
		 long count = this.count("isCheckNumMonthForB", newHashMap);
		 boolean b = count <= limit;
		 logger.info("今月({})切客次数:{} <= 系统限制次数:{} = {}", new Date(), count, limit, b);
		 return b;
	 }
	 
	 public boolean isFirstOrder(List<Long> midList) {
		boolean result=false;
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("midList", midList);
		long count = this.count("isFirstOrder", newHashMap);
		if (count==0) {
			result=true;
		}
		return result;
	}
}
