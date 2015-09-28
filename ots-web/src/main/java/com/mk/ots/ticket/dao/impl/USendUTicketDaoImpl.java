package com.mk.ots.ticket.dao.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.StatisticInvalidTypeEnum;
import com.mk.ots.ticket.dao.USendUTicketDao;
import com.mk.ots.ticket.model.USendUticket;

@Component
public class USendUTicketDaoImpl extends MyBatisDaoImpl<USendUticket, Long> implements USendUTicketDao{
	final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public List<USendUticket> getNeedSendCountValid(int statisticInvalid,int batDataNum) {

        Map<String, Object> param = Maps.newHashMap();
        param.put("statisticinvalid", statisticInvalid);
        param.put("batDataNum", batDataNum);
        return super.findObjectList("getNeedSendCountValid", param);
	}

	@Override
	public void updateSendTicketInvalidByMid(Long mid) {
		Map<String, Object> param = Maps.newHashMap();
        param.put("statisticinvalid", StatisticInvalidTypeEnum.statisticInvalid.getType());
        param.put("statisticInvalidPre", StatisticInvalidTypeEnum.statisticValid.getType());
        param.put("mid", mid);
        super.update("updateSendTicketInvalidByMid", param);
	}
	
	
}
