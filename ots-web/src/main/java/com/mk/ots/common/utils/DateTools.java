package com.mk.ots.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mk.framework.exception.MyErrorEnum;

public class DateTools {
	
	/**
	 *	一天的毫秒数 
	 */
	public static final Long DAY_MILLSECONDS = 24*60*60*1000L;
	public static final Integer BEGIN_HOUR = 6;
	
	/**
	 * 返回 两个时间之间时间列表  不添加 结束时间  只计算Date 天数
	 * @param beginTime
	 * @param endTime
	 * @return List<Date>
	 */
	public  static List<Date> getDateListShot(Date beginTime, Date endTime) {
		List<Date> dateList =new  ArrayList<Date>();
		Calendar beginCal=Calendar.getInstance();
		beginCal.setTime(beginTime);
		Calendar endCal=Calendar.getInstance();
		endCal.setTime(endTime);
		if(beginCal.compareTo(endCal)>0){
			MyErrorEnum.errorParm.getMyException("开始时间大于结束时间");
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		while (sdf.format(beginCal).compareTo(sdf.format(endCal))<0) {
			dateList.add(beginCal.getTime());
			beginCal.add(Calendar.DATE, 1);
		}
		return dateList;
	}
	/**
	 * 返回 两个时间之间时间列表  添加结束时间  多一天的价格	只计算Date 天数
	 * @param beginTime
	 * @param endTime
	 * @return List<Date>
	 */
	public  static List<Date> getDateList(Date beginTime, Date endTime) {
		List<Date> dateList =getDateListShot(beginTime, endTime);
		//多添加结束时间
		Calendar endCal=Calendar.getInstance();
		endCal.setTime(endTime);
		dateList.add(endCal.getTime());
		return dateList;
	}
	public  static List<Date> getBeginDateList(Date beginTime, Date endTime) {
		List<Date> dateList =new  ArrayList<Date>();
		if(beginTime.after(endTime)){
			MyErrorEnum.errorParm.getMyException("开始时间大于结束时间");
		}
		//开始时间
		Calendar beginCal=Calendar.getInstance();
		beginCal.setTime(beginTime);
		//结束时间
		Calendar endCal=Calendar.getInstance();
		endCal.setTime(endTime);
		while (!beginCal.after(endCal)) {
			dateList.add(beginCal.getTime());
			beginCal.add(Calendar.DATE, 1);
		}
		return dateList;
	}
	
	/**
	 * 计算日期相差天数
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static Integer getBetweenDays(Date beginDate,Date endDate){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
	    try {
			beginDate=sdf.parse(sdf.format(beginDate));
			endDate=sdf.parse(sdf.format(endDate));  
		} catch (ParseException e) {
			MyErrorEnum.errorParm.getMyException("日期解析错误");
		}  
	    Calendar cal = Calendar.getInstance();    
	    cal.setTime(beginDate);    
	    long time1 = cal.getTimeInMillis();                 
	    cal.setTime(endDate);    
	    long time2 = cal.getTimeInMillis();         
	    long between_days=(time2-time1)/(1000*3600*24);  
	   return Integer.parseInt(String.valueOf(between_days));
   }
	
	public static Long getMilliseconds(Long mills,int addDay){
		if(mills == null){
			return 0L;
		}
		Date date = new Date(mills);
		Calendar calendar = Calendar.getInstance(); 
//		calendar.setTimeZone(TimeZone.getTimeZone("UTC+8"));
		calendar.setTime(date); 
		calendar.add(Calendar.DATE, addDay);
		calendar.set(Calendar.HOUR_OF_DAY, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		return calendar.getTimeInMillis();
	}
	
	public static String getFormatBeginDate(Long mills,int addDay){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		return sdf.format(getBeginMilliS(mills,addDay,null));
	}
	
	public static Date getBeginDate(Long mills,int addDay){
		Date date = new Date();
		if(mills!=null){
			date = new Date(mills);
		}
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date); 
		int hour = calendar.get(Calendar.HOUR_OF_DAY); 
		calendar.add(Calendar.DATE, addDay);
		if(hour<BEGIN_HOUR){
			calendar.add(Calendar.DATE, -1);
		}
		return new Date(calendar.getTimeInMillis());
	}
	
	/**
	 * 
	 * @param mills 当前时间好毫秒数
	 * @param addDay 增加的天数
	 * @param hotelId 酒店ID
	 * @return 每天的开始时间毫秒数,默认每天六点
	 */
	public static Long getBeginMilliS(Long mills,int addDay,Long hotelId){
		Date date = new Date();
		if(mills!=null){
			date = new Date(mills);
		}
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date); 
		calendar.add(Calendar.DATE, addDay);
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		if(hotelId == null){
			calendar.set(Calendar.HOUR_OF_DAY, BEGIN_HOUR); //默认 ,每天6点
		}else{
			switch(hotelId.toString()){
			case "酒店ID":
				calendar.set(Calendar.HOUR_OF_DAY, 0); //酒店ID ,每天0点
				break;
			default:
				calendar.set(Calendar.HOUR_OF_DAY, BEGIN_HOUR); //默认 ,每天6点
				break;
			}
		}
		return calendar.getTimeInMillis();
	}

	public static String getBeginFormat(Long mills,int addDay,Long hotelId){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		if(mills!=null){
			date = new Date(mills);
		}
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date); 
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hotelId == null){
			if(hour<BEGIN_HOUR){//默认 ,每天6点
				calendar.add(Calendar.DATE, -1);
			}
		}else{
			switch(hotelId.toString()){
			case "酒店ID":
				break;
			default:
				if(hour<BEGIN_HOUR){//默认 ,每天6点
					calendar.add(Calendar.DATE, -1);
				} //默认 ,每天6点
				break;
			}
		}
		return sdf.format(calendar.getTime());
	}

	public static Boolean isInDates(Date time,Date beginTime,Date endTime){
		return (!time.before(beginTime))&&(!time.after(endTime));
	}
	
	//同一年
	public static Boolean isSameMonth(Date date1,Date date2){
		SimpleDateFormat sf = new SimpleDateFormat("M");
		return sf.format(date1).equals(sf.format(date2));
	}
	
	public static Map<String,List<Integer>> getMonthDays(Date beginTime,Date endTime,Map<String, Boolean> weekState){
		Map<String,List<Integer>> monthMap = new HashMap<String, List<Integer>>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		List<Integer> list = null;
		Calendar ca = Calendar.getInstance();
		ca.setTime(beginTime);
		while(!ca.getTime().after(endTime)){
			String key = sf.format(ca.getTime());
			list = new ArrayList<Integer>();
			while(ca.get(Calendar.DAY_OF_MONTH)<=ca.getActualMaximum(Calendar.DAY_OF_MONTH)){
				if(weekState.get(ca.get(Calendar.DAY_OF_WEEK)+"")){
					list.add(ca.get(Calendar.DAY_OF_MONTH));
				}
				ca.add(Calendar.DATE, 1);
			}
			monthMap.put(key, list);
		}
		return monthMap;
	}
	
	public static List<String> getDays(Date beginTime,Date endTime,Map<String, Boolean> weekState){
		List<String> dayList = new ArrayList<String>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		Calendar ca = Calendar.getInstance();
		ca.setTime(beginTime);
		while(!ca.getTime().after(endTime)){
			if(weekState.get(ca.get(Calendar.DAY_OF_WEEK)+"")){
				dayList.add(sf.format(beginTime));
			}
			ca.add(Calendar.DATE, 1);
		}
		return dayList;
	}
	
	
	
	public static  String getDate(){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
		return  sf.format(new Date());
	}
	public static void main(String[] args) throws ParseException {
//		 Calendar date1 = Calendar.getInstance();
//		    date1.set(2008,1,4);
//		    Calendar from = Calendar.getInstance();
//		    from.set(2008,1,1);
//		    Calendar to = Calendar.getInstance();
//		    to.set(2008,1,6);
////		    System.out.println(date1.after(from));
////		    System.out.println(date1.before(to));
//		   System.out.println(DateTools.isInDates(date1.getTime(),from.getTime(),to.getTime()));
		   
		   
//		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
//		 //获取前月的最后一天
//	        Calendar cale = Calendar.getInstance(); 
//	        cale.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
//	        cale.setTime(format.parse("2000-2-1"));
//	        System.out.println(cale.getActualMaximum(Calendar.DAY_OF_MONTH));
//	        System.out.println(Calendar.DAY_OF_MONTH+"-------------------------------");
//	        String lastDay = format.format(cale.getTime());
//	        System.out.println("-----2------lastDay:"+lastDay);
	}

}
