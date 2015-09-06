package com.mk.ots.message.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.message.dao.ILPushLogDao;
import com.mk.ots.message.model.LPushLog;

@Component
public class LPushLogDao extends MyBatisDaoImpl<LPushLog, Long> implements ILPushLogDao {
	public Long findActiveCount(LPushLog lPushLog){
		return findCount("findActiveCount", lPushLog);
	}
	
}
