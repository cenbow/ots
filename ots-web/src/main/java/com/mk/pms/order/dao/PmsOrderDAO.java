package com.mk.pms.order.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;

import cn.com.winhoo.mikeweb.dao.IPmsOrderDAO;
import cn.com.winhoo.mikeweb.orderpojo.PmsOrder;
import cn.com.winhoo.mikeweb.util.DateTools;

//@DbDaoMapping(dbname="hotelSource",readDb="")

@Repository(value="pmsOrderDAO4pms")
public class PmsOrderDAO  {
	
	

	public List<Bean> findNotOverPmsOrder(Integer days) {
		//查找未结束的酒店预订单（PmsOrder  条件 cancel是false  orderNum 和planNum不想等的  begintime和endtime 在3个月内 ）//days天内
		Calendar ca=Calendar.getInstance();
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		Long begin=ca.getTimeInMillis();
		ca.add(Calendar.DATE, days+2);
//		Long daySeconds = 24*60*60*1000L; //每天的毫秒数 
//		Long msds = date.getTime() - (date.getTime()%daySeconds);//当天0点毫秒数
		Long end=ca.getTimeInMillis();
		
		String con1Str=" cancel='F'";
		String con2Str=" orderNum != planNum";
		String con3Str=" beginTime<="+begin;
		String con4Str=" endTime>="+begin;
		String and2="("+con3Str +" and "+ con4Str+")";
		String con5Str=" begintime between "+begin+" and "+end;
		
		String or= and2+" or "+ con5Str;
		String and=con1Str+" and "+ con2Str+" and "+or;
		String sql=" select * from b_pmsorder where ";
		List<Bean> l= Db.find(sql);
		return l;
	}

	public Map<String, Integer> getOrderNoPlanNumByDay(Long roomTypeId,
			Integer reserveDayNum) {
		Map<String, Integer> map = new HashMap<>();
			Date date =new Date();
			Long curDay = DateTools.getMilliseconds(date.getTime(),0);
			Long endDay = DateTools.getMilliseconds(date.getTime(),reserveDayNum);
			StringBuilder sb = new StringBuilder();
			sb.append("select  aa.date,sum(aa.noPlannum) as noPlannum from (");
			while(curDay<endDay){
				sb.append("select "+curDay+" as 'date',(sum(ordernum)-SUM(Plannum))as noPlannum from b_pmsorder where RoomTypeId='"+roomTypeId+"' and Cancel='F' and Ordernum>Plannum");
				curDay = DateTools.getMilliseconds(curDay,1);
				sb.append(" and Begintime<"+curDay+" and Endtime>="+curDay+" ");
				if(curDay<endDay){
					sb.append("union all ");
				}
			}
			sb.append(") as aa ");
			sb.append("where aa.noPlannum is not null and aa.noPlannum <> 0 ");
			sb.append("group by aa.date");
			List<Bean> ll=Db.find(sb.toString());
			for(Bean b:ll){
				Integer noPlanNum=b.getInt("noPlannum");
				if(noPlanNum == null||noPlanNum == 0){
					continue;
				}
				map.put(b.getStr("date"),noPlanNum);
			}
		return map;
	}
	
	public Map<String, Integer> getOrderNoPlanNumByDay(Long roomTypeId, Long curDay, Long endDay) {
		Map<String, Integer> map = new HashMap<>();
			StringBuilder sb = new StringBuilder();
			sb.append("select  aa.date,sum(aa.noPlannum) as noPlannum from (");
			while(curDay<endDay){
				sb.append("select "+curDay+" as 'date',(sum(ordernum)-SUM(Plannum))as noPlannum from b_pmsorder where RoomTypeId='"+roomTypeId+"' and Cancel='F' and Ordernum>Plannum");
				curDay = DateTools.getBeginMilliS(curDay,1,null);
				sb.append(" and Begintime<"+curDay+" and Endtime>="+DateTools.getMilliseconds(curDay, 0)+" ");
				sb.append(" and visible='T' ");
				if(curDay<endDay){
					sb.append("union all ");
				}
			}
			sb.append(") as aa ");
			sb.append("where aa.noPlannum is not null and aa.noPlannum <> 0 ");
			sb.append("group by aa.date");
			
			List<Bean> ll=Db.find(sb.toString());
			Integer noPlanNum=null;
			for(Bean b:ll){
				noPlanNum=b.getInt("noPlannum");
				if(noPlanNum == null||noPlanNum == 0){
					continue;
				}
				//key:"yyyyMMdd" value:Integer
				Long millis = Long.parseLong(b.getStr("date"));
				Date date = new Date(millis);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
				map.put(sdf.format(date),noPlanNum);
			}
			
		return map;
	}
}