package com.mk.ots.ticket.dao.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.UGiftRecordDao;
import com.mk.ots.ticket.model.UGiftRecord;

@Component
public class UGiftRecordDaoImpl extends MyBatisDaoImpl<UGiftRecord, Long> implements UGiftRecordDao {

	@Override
	public long getRecordNumToday(Long mid, Long activeid) {
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("activeid", activeid);
		return this.count("getRecordNumToday", newHashMap);
	}

	@Override
	public long getTotalRecordNum(Long mid, Long activeid, Date begintime,
			Date endtime) {
		Map<String, Object> newHashMap = Maps.newHashMap();
		newHashMap.put("mid", mid);
		newHashMap.put("activeid", activeid);
		newHashMap.put("starttime", begintime);
		newHashMap.put("endtime", endtime);
		return this.count("getTotalRecordNum", newHashMap);
	}

}
