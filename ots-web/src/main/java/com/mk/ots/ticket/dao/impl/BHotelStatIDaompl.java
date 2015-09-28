package com.mk.ots.ticket.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.StatisticInvalidTypeEnum;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.ticket.dao.BHotelStatDao;
import com.mk.ots.ticket.model.BHotelStat;

/**
 * Created by jjh on 15/7/29.
 */
@Component
public class BHotelStatIDaompl extends MyBatisDaoImpl<BHotelStat, Long> implements BHotelStatDao {


    @Override
    public void saveOrUpdate(BHotelStat bHotelStat) {
        if(bHotelStat.getId()!=null){
            this.update(bHotelStat);
        }else{
            this.insert(bHotelStat);
        }

    }

    @Override
    public BHotelStat getBHotelStatByOtaorderid(Long otaOrderId) {
        Map<String,Object> param = Maps.newHashMap();
        param.put("otaorderid", otaOrderId);
        return super.findOne("getBHotelStatByOtaorderid", param);
    }

    @Override
    public List<Long> getCountValid(int statisticInvalid, int liveHotelNum, int batDataNum) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("statisticInvalid", statisticInvalid);
        param.put("liveHotelNum", liveHotelNum);
        param.put("batDataNum", batDataNum);
        return super.findObjectList("getCountValid", param);
    }

    @Override
    public void updateInvalidByMid(Long mid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("statisticInvalid", StatisticInvalidTypeEnum.statisticInvalid.getType());
        param.put("statisticInvalidPre", StatisticInvalidTypeEnum.statisticValid.getType());
        param.put("mid", mid);
        super.update("updateInvalidByMid", param);
    }

	@Override
	public List<THotelModel> queryHistoryHotels(String token, String code,int start,int limit) {
		Map<String, Object> param = Maps.newHashMap();
        param.put("token", token);
        param.put("code", code);
        param.put("start", start);
        param.put("limit", limit);
        return super.findObjectList("queryHistoryHotels", param);
	}

	@Override
	public long queryHistoryHotelsCount(String token, String code) {
		Map<String, Object> param = Maps.newHashMap();
        param.put("token", token);
        param.put("code", code);
        return super.count("queryHistoryHotelsCount", param);
	}
	
	@Override
    public void deleteHotelStats(String token,Long hotelid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("token", token);
        param.put("hotelid", hotelid);
        super.update("deleteHotelStats", param);
    }
	
	
}
