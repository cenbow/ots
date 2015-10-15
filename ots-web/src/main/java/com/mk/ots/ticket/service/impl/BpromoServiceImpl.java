package com.mk.ots.ticket.service.impl;


import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.enums.BPromoStatuEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.ticket.dao.IBPromoDao;
import com.mk.ots.ticket.dao.IUPromoUserLogDao;
import com.mk.ots.ticket.model.BPromo;
import com.mk.ots.ticket.model.UPromoUserLog;
import com.mk.ots.ticket.service.IBPromoService;
import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 优惠券服务接口
 * @author nolan
 *
 */
@Service
public class BpromoServiceImpl implements IBPromoService {
	final Logger logger = LoggerFactory.getLogger(BpromoServiceImpl.class);

	@Autowired
	private IBPromoDao iBPromoDao;

	@Autowired
	private IUPromoUserLogDao iupromoUserLogDao;

	@Autowired
	private THotelMapper hotelMapper;

	public BPromo getBPromoInfoByPwd(String promoPwd){
		if(Strings.isNullOrEmpty(promoPwd)){
			throw   MyErrorEnum.customError.getMyException("卡卷密码不正确");
		}
		return  iBPromoDao.findBPromoByPromo(promoPwd);
	}



	public  boolean  checkCanUse(String  promoPwd,Long  hotelId){

		THotelModel hotelModel = hotelMapper.findHotelInfoById(hotelId);

		//判断当前酒店是否存在酒店
		if(null==hotelModel){
			throw	MyErrorEnum.promoCityCodeError.getMyException();
		}

		return this.checkCanUseByCityId(promoPwd, hotelModel.getCityid());
	}
	
	public   boolean   checkCanUseByOrder(OtaOrder otaOrder){
		return checkCanUseByCityId("qwert", Long.parseLong(otaOrder.getTCity().getAttrs().get("cityid").toString()));
	}


	public  boolean  checkCanUseByCityId(String  promoPwd,Long  cityId){
		BPromo  bPromo = iBPromoDao.findBPromoByPromo(promoPwd);
		if(null==bPromo){
			throw   MyErrorEnum.promoPswError.getMyException();
		}
		//判断是是否激活
		if(bPromo.getPromoStatus()!= BPromoStatuEnum.activite.getType()){
			throw   MyErrorEnum.promoNotActivte.getMyException();
		}

		//判断当前日期是否在有效期内
		String  nowDate = DateUtils.getDate();
		String  beginDateStr = dateToString(bPromo.getBeginDate(), "yyyy-MM-dd");
		String  endDateStr = dateToString(bPromo.getEndDate(), "yyyy-MM-dd");

		try {
			if(!dayBetween(beginDateStr,nowDate,endDateStr)){
                throw   MyErrorEnum.promoDayError.getMyException();
            }
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//判断当前时间是否在有效时间内
		String  nowTimeComp = DateUtils.getDatetime();
		String   beginTimeComp = DateUtils.getDate() + " " +  bPromo.getBeginTime();
		String   endTimeComp =  DateUtils.getDate() + " " + bPromo.getEndTime();
		try {
			if(!getCompareResult(beginTimeComp,nowTimeComp,"yyyy-MM-dd HH:mm")){
                throw   MyErrorEnum.promoTimeError.getMyException();
            }
			if(!getCompareResult(nowTimeComp,endTimeComp,"yyyy-MM-dd HH:mm")){
				throw   MyErrorEnum.promoTimeError.getMyException();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//判断是否是特价房
		if(bPromo.getPromoType()!=1){
			throw   MyErrorEnum.promoTypeError.getMyException();
		}

		//判断当前卷是否适用于酒店区域
		if(cityId!=bPromo.getPromoCityId()){
			throw	MyErrorEnum.promoCityCodeError.getMyException();
		}

		return  true;
	}

	public   void  usePromo(OtaOrder otaOrder,BPromo  bpromo,int  newPromoStatus){
		if(null==otaOrder){
			throw   MyErrorEnum.customError.getMyException("获取订单信息失败");
		}
		UPromoUserLog   upromoUserLog = new UPromoUserLog();
		upromoUserLog.setMid(otaOrder.getMid());
		upromoUserLog.setOrderId(otaOrder.getOrderId());
		upromoUserLog.setPromoId(bpromo.getId());
		upromoUserLog.setPromoPrice(otaOrder.getPrice().doubleValue());
		upromoUserLog.setCreate_time(new  Date());
		iupromoUserLogDao.add(upromoUserLog);

		iBPromoDao.updateBpromoForUse(bpromo.getPromoPwd(), newPromoStatus, DateUtils.getDatetime(), otaOrder.getMid()+"");
	}

	public  boolean   dayBetween(String startdateStr,String  compareday,String enddateStr) throws ParseException {
		if (getCompareResult(compareday,startdateStr,"yyyy-MM-dd")){
			return  false;
		}
		if (!getCompareResult(compareday, enddateStr,"yyyy-MM-dd")){
			return  false;
		}
		return  true;
	}


	/**
	 * 获取两个日期的差值
	 * @param smdate
	 * @param bdate
	 * @return  'bdate' - 'smdate'日期差
	 * @throws ParseException
	 */
	public static int daysBetween(String bdate,String smdate) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days=(time2-time1)/(1000*3600*24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 比较两个日期类型的String大小
	 * @param dataA
	 * @param dataB
	 * @return
	 * @throws ParseException
	 */
	public boolean getCompareResult(String dataA,String dataB,String  example) throws ParseException {
		if(Strings.isNullOrEmpty(example)){
			example = "yyyy-MM-dd";
		}
		DateFormat dafShort=new SimpleDateFormat(example);
		Date a=dafShort.parse(dataA);
		Date b=dafShort.parse(dataB);
		return a.before(b);
	}


	/**
	 * 时间Date类型转换为日期类型
	 * @param date  要转换的时间类型
	 * @param example  转换后的格式
	 * @return
	 */
	public static String dateToString(Date date,String  example){
		if (null == date) {
			return null;
		}
		if(Strings.isNullOrEmpty(example)){
			example = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(example);
		return sdf.format(date);
	}

	public static String getTime(String  example) {
		if(Strings.isNullOrEmpty(example)){
			example = "HH:mm:ss";
		}
		Calendar calendar = Calendar.getInstance();
		Date d = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(example);
		return sdf.format(d);
	}
}
