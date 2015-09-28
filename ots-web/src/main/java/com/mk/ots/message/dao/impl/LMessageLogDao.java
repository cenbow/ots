package com.mk.ots.message.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.message.dao.ILMessageLogDao;
import com.mk.ots.message.model.LMessageLog;

@Component
public class LMessageLogDao extends MyBatisDaoImpl<LMessageLog, Long> implements ILMessageLogDao {
	
}
