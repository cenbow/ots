package com.mk.ots.hotel.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.CashBackModel;
import com.mk.ots.mapper.CashBackMapper;

/**
 * 
 * @author LYN
 *
 * 返现 服务类
 */
@Service
public class CashBackService {
	private static Logger logger = LoggerFactory.getLogger(CashBackService.class);

	@Autowired
	private CashBackMapper cashBackMapper;
	
	/**
	 * 返回酒店返现情况
	 * 1.订单按照第一天返现金额进行返现；若第一天无返现则根据最近一天的返现金额进行返现
	 * 2.每单最多返现一次
	 * 3.查看不同日期房态时，展示当日的返现金额，
	 * 4.查询多日，显示最近一天的返现。
	 * 5.查询日期，入住日期，不含离店日期
	 * @param hotelid
	 * @param startdateday "20150915" 预住日期
	 * @param enddateday "20150916"   预离日期
	 * @return {"iscashback":"F", "roomtype1":{"iscashback":"F","cashbackcost":0},
	 * "roomtypeid1":{"iscashback":"F","cashbackcost":0}}
	 */
	public Map<Long, Object> getCashBackByHotelId(Long hotelid, String startdateday, String enddateday){
		this.logger.info("getCashBackByHotelId:hotelid:{},begintime:{},endtime:{}", hotelid, startdateday, enddateday);	
		if(StringUtils.isBlank(startdateday)){
			startdateday = DateUtils.getDate();
		}
		if(StringUtils.isBlank(enddateday)){
			enddateday = DateUtils.getDate();
		}
		//不包含结束日期
		Date endday = DateUtils.getDateFromString(enddateday);
		endday = DateUtils.addDays(endday, -1);
		enddateday = DateUtils.formatDateTime(endday, DateUtils.FORMATSHORTDATETIME);
		this.logger.info("查询酒店返现日期-1天getCashBackByHotelId:hotelid:{},begintime:{},endtime-1:{}", hotelid, startdateday, enddateday);
		Map<Long, Object> hotelResult = new HashMap<Long, Object>();//this.mockCashData(hotelid);
		List<CashBackModel> cashBacks = cashBackMapper.findCashBack(hotelid, null, startdateday, enddateday);
		for(CashBackModel cb : cashBacks){
			Long roomtypeid = cb.getRoomTypeId();
			if(hotelResult.containsKey(roomtypeid)){
				continue;
			}
			Map<String, Object> re = new HashMap<String, Object>();
			re.put("iscashback", Constant.STR_TRUE);
			re.put("cashbackcost", cb.getReturnPrice());
			hotelResult.put(roomtypeid, re);
		}
		this.logger.info("getCashBackByHotelId,hotelid:{}:roomtypeResult:{}", hotelid, hotelResult);
		return hotelResult;
	}
	
	public boolean isCashBackHotelId(Long hotelid, String startdateday, String enddateday){
		Map<Long, Object> cbs = this.getCashBackByHotelId(hotelid, startdateday, enddateday);
		if(cbs.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 返回酒店返现情况
	 * 1.订单按照第一天返现金额进行返现；若第一天无返现则根据最近一天的返现金额进行返现
	 * 2.每单最多返现一次
	 * 3.查看不同日期房态时，展示当日的返现金额，
	 * 4.查询多日，显示最近一天的返现。
	 * 5.查询日期，入住日期，不含离店日期
	 * @param roomtypeid
	 * @param startdateday "20150915" 预住日期
	 * @param enddateday "20150915"  预离日期
	 * @return {"iscashback":"F", "roomtype1":{"iscashback":"F","cashbackcost":0},
	 * "roomtypeid1":{"iscashback":"F","cashbackcost":0}}
	 */
	public Map<String, Object> getCashBackByRoomtypeId(Long roomtypeid, String startdateday, String enddateday){
		this.logger.info("getCashBackByRoomtypeId:roomTypeId:{},begintime:{},endtime:{}", roomtypeid, startdateday, enddateday);
		
		if(StringUtils.isBlank(startdateday)){
			startdateday = DateUtils.getDate();
		}
		if(StringUtils.isBlank(enddateday)){
			enddateday = DateUtils.getDate();
		}
		//不包含结束日期
		Date endday = DateUtils.getDateFromString(enddateday);
		endday = DateUtils.addDays(endday, -1);
		enddateday = DateUtils.formatDateTime(endday, DateUtils.FORMATSHORTDATETIME);
		this.logger.info("查询房型返现日期-1天getCashBackByRoomtypeId roomtypeid:{},begintime:{},endtime-1:{}", roomtypeid, startdateday, enddateday);
		Map<String, Object> roomtypeResult = new HashMap<String, Object>();
		List<CashBackModel> cashBacks = cashBackMapper.findCashBack(null, roomtypeid, startdateday, enddateday);
		if(cashBacks!= null && cashBacks.size()>0 ){
			for(CashBackModel cb : cashBacks){
				roomtypeResult.put("iscashback", Constant.STR_TRUE);
				roomtypeResult.put("cashbackcost", cb.getReturnPrice());
				break;
			}
		}else{
			roomtypeResult.put("iscashback", Constant.STR_FALSE);
			roomtypeResult.put("cashbackcost", 0L);
		}
		this.logger.info("getCashBackByRoomtypeId,roomtypeid:{}:roomtypeResult:{}", roomtypeid, roomtypeResult);
		return roomtypeResult;
	}
	
	
}
