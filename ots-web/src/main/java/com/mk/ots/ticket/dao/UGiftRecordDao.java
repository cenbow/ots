package com.mk.ots.ticket.dao;

import java.util.Date;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.UGiftRecord;

public interface UGiftRecordDao extends BaseDao<UGiftRecord, Long> {

	public long getRecordNumToday(Long mid, Long activeid);

	public long getTotalRecordNum(Long mid, Long activeid, Date begintime,
			Date endtime);
}
