package com.mk.ots.ticket.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.ticket.dao.UGiftRecordDao;
import com.mk.ots.ticket.model.UGiftRecord;
import com.mk.ots.ticket.service.IUGiftRecordService;

@Service
public class UGiftRecordServiceImpl implements IUGiftRecordService{

	@Autowired
	private UGiftRecordDao uGiftRecordDao;
	
	@Override
	public void record(Long mid, Long activeid) {
		UGiftRecord record = new UGiftRecord();
		record.setActiveid(activeid);
		record.setMid(mid);
		record.setCreatetime(new Date());
		uGiftRecordDao.insert(record);
	}

	@Override
	public long getRecordNumToday(Long mid, Long activeid) {
		return this.uGiftRecordDao.getRecordNumToday(mid, activeid);
	}

	@Override
	public long getTotalRecordNum(Long mid, Long activeid, Date begintime, Date endtime) {
		return this.uGiftRecordDao.getTotalRecordNum(mid, activeid, begintime, endtime);
	}

}
