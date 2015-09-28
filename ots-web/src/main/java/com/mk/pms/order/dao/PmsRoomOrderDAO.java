package com.mk.pms.order.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import cn.com.winhoo.mikeweb.myenum.PmsRoomOrderStatusEnum;
import cn.com.winhoo.mikeweb.myenum.PriceTypeEnum;
import cn.com.winhoo.mikeweb.util.DateTools;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.order.bean.PmsRoomOrder;

@Repository
public class PmsRoomOrderDAO  {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	public List<PmsRoomOrder> findPmsRoomOrder(Long roomTypeId,Integer days) {
		//查找未结束的酒店预订单（PmsOrder  条件 cancel是false  orderNum 和planNum不想等的  begintime和endtime 在3个月内 ）//days天内
		Calendar ca=Calendar.getInstance();
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MILLISECOND, 0); 
		Long begin=ca.getTimeInMillis();
		ca.add(Calendar.DATE, days);
//				Long daySeconds = 24*60*60*1000L; //每天的毫秒数 
//				Long msds = date.getTime() - (date.getTime()%daySeconds);//当天0点毫秒数
		Long end=ca.getTimeInMillis();
		
		String con1Str=" roomTypeId="+roomTypeId;
		String con2Str=" orderType="+ PriceTypeEnum.R;
		String con3Str=" begintime<="+begin;
		String con4Str=" endtime>="+begin;
		String and2=con3Str+" and "+ con4Str;
		String and3= " begintime between "+begin+" and "+end;
		String or=" ("+and2+" or "+ and3+") ";
		String and= con1Str+" and "+con2Str+" and "+or;
		String sql=" select * from b_pmsroomorder where "+and;
		List<PmsRoomOrder> ll= new PmsRoomOrder().find(sql);
		return ll;
	}
	
	public List<PmsRoomOrder> findPmsRoomOrder(Long roomTypeId,Long begin,Long end,List<PmsRoomOrderStatusEnum> statusList) {
		//查找未结束的酒店预订单（PmsOrder  条件 cancel是false  orderNum 和planNum不想等的  begintime和endtime 在3个月内 ）//days天内
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginStr =sdf.format(new Date(begin));
		String endStr= sdf.format(new Date(end));
		String st="";
		for(int i=0;i<statusList.size();i++){
			PmsRoomOrderStatusEnum se= statusList.get(i);
			st+="'";
			st+=se.getId()+"',";
		}
		st=st.substring(0,st.length()-1);
		
		String where=" roomtypeid="+roomTypeId+" and ordertype="+PriceTypeEnum.R+" and status not in("+st+")" +
		" and (begintime<='"+beginStr+"' and endtime>='"+beginStr+"' or begintime between '"+beginStr+"' and '"+endStr+"' ) and visible='T' ";
		String sql=" select * from b_pmsroomorder where "+where;
		logger.info("获取PMSRoomOrder: sql:{}",sql);
		List<PmsRoomOrder> ll= new PmsRoomOrder().find(sql);
		return ll;
	}

	public Map<String, Integer> getRoomOrderNumByDay(Long roomTypeId,Integer reserveDayNum){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Integer> map = new HashMap<>();
			Date date =new Date();
			Long curDay = DateTools.getMilliseconds(date.getTime(),0);
			Long endDay = DateTools.getMilliseconds(date.getTime(),reserveDayNum);
			StringBuilder sb = new StringBuilder();
			sb.append("select  aa.date,sum(aa.num)as num from (");
			while(curDay<endDay){
				String beginStr =sdf.format(new Date(curDay));
				sb.append("select "+curDay+" as 'date',COUNT(*) as 'num' from b_pmsroomorder  where RoomTypeId='"+roomTypeId+"' and Ordertype=2 ");
				curDay = DateTools.getMilliseconds(curDay,1);
				String endStr=sdf.format(new Date(curDay));
				sb.append("and Begintime<'"+beginStr+"' and Endtime>='"+endStr+"' ");
				if(curDay<endDay){
					sb.append("union all ");
				}
			}
			sb.append(") as aa where aa.num is not null and aa.num <> 0 group by aa.date");
			String sql =sb.toString();
			List<Bean> l=Db.find(sql);
			Integer num=null;
			for(Bean b: l){
				num = b.getInt("num");
				if(num == null||num == 0){
					continue;
				}
				map.put(b.getStr("date"),num);
			}
		return map;
	}
	
	public Map<String, Integer> getRoomOrderNumByDay(Long roomTypeId,Long curDay,Long endDay,List<PmsRoomOrderStatusEnum> list){
		StringBuilder stateSB = new StringBuilder();
		for (PmsRoomOrderStatusEnum s : list) {
			if(stateSB.length()==0){
				stateSB.append("'"+s.toString()+"'");
			}else{
				stateSB.append(",'"+s.toString()+"'");
			}
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("从PMSRoomOrder获取定单:roomTypeId:{},curDay{},endDay{},statusList:{},statusStr:{}",roomTypeId,curDay,endDay,list,stateSB);
		Map<String, Integer> map = new HashMap<>();
			StringBuilder sb = new StringBuilder();
			sb.append("select  aa.date,sum(aa.num)as num from (");
			while(curDay<endDay){
				String beginStr =sdf.format(new Date(curDay));
				
				sb.append("select "+curDay+" as 'date',COUNT(*) as 'num' from b_pmsroomorder  where RoomTypeId='"+roomTypeId+"' and Ordertype=2 ");
				curDay = DateTools.getBeginMilliS(curDay,1,null);
				String endStr=sdf.format(new Date(DateTools.getMilliseconds(curDay, 0)));
				sb.append("and Begintime<'"+beginStr+"' and Endtime>='"+endStr+"' ");
				sb.append("and status not in(");
				sb.append(stateSB);
				sb.append(") ");
				sb.append(" and visible='T' ");
				if(curDay<endDay){
					sb.append("union all ");
				}
			}
			sb.append(") as aa where aa.num is not null and aa.num <> 0 group by aa.date");
			
			String sql =sb.toString();
			List<Bean> l=Db.find(sql);
			BigDecimal num=null;
			for(Bean b: l){
				num = b.get("num"); 
				if(num == null||num.intValue() == 0){
					continue;
				}
				//key:"yyyyMMdd" value:Integer
				Long millis = Long.parseLong(b.get("date").toString());
				Date date = new Date(millis);
				map.put(sdf.format(date),num.intValue());
			}
		return map;
	}
	public static void main(String[] args) {
		//获取当天零点时间毫秒数

		Date date = new Date(); 
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTimeZone(TimeZone.getTimeZone("UTC+8"));
		calendar.setTime(date); 
		calendar.set(Calendar.HOUR_OF_DAY, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		System.out.println(calendar.getTimeInMillis()/1000);//第一种方式：不解释，都懂吧？

//		1419436800000
//		1419465600000
		long l = 24*60*60*1000; //每天的毫秒数 
		System.out.println((date.getTime() - (date.getTime()%l))); //第二种：现在的毫秒数 减去 当天零点到现在的毫秒数，理论上等于零点的毫秒数
	}

	/**
	 * 获取当天12点未离店定单
	 * @return
	 */
	public List<Bean> getPMSRoomOrderISinAt12PM(String date) {		
		date= date+" 12:00:00";
		String sql="select po.*,h.hotelname,c.code as citycode from b_pmsroomorder po left join t_hotel h on po.hotelid=h.id left join t_district d on d.id= h.disId left join t_city c on c.cityid= d.CityID where po.status='IN' and po.visible='T' and po.endtime<=?";
		List<Bean> pmsRoomOrders =Db.find(sql,date);
		return pmsRoomOrders;
	}

}
