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
import com.mk.ots.common.utils.DateTools;

import java.text.ParseException;
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
		String  beginDateStr = DateTools.dateToString(bPromo.getBeginDate(), "yyyy-MM-dd");
		String  endDateStr = DateTools.dateToString(bPromo.getEndDate(), "yyyy-MM-dd");

		try {
			if(!DateTools.dayBetween(beginDateStr, nowDate, endDateStr)){
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
			if(!DateTools.getCompareResult(beginTimeComp, nowTimeComp, "yyyy-MM-dd HH:mm")){
                throw   MyErrorEnum.promoTimeError.getMyException();
            }
			if(!DateTools.getCompareResult(nowTimeComp, endTimeComp, "yyyy-MM-dd HH:mm")){
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
}
