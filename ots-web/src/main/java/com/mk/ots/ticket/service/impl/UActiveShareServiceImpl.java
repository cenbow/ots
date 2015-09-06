package com.mk.ots.ticket.service.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mk.ots.ticket.dao.UActiveShareDao;
import com.mk.ots.ticket.model.UActiveShare;
import com.mk.ots.ticket.service.IUActiveShareService;

@Service
public class UActiveShareServiceImpl implements IUActiveShareService {
	
	@Autowired
	private UActiveShareDao uActiveShareDao;
	
	@Override
	public void record(Long mid, Long activeid) {
		 UActiveShare uas = new  UActiveShare();
		 uas.setActiveid(activeid);
		 uas.setMid(mid);
		 uas.setCreatetime(new Date());
		 uActiveShareDao.insert(uas);
	}

	@Override
	public long count(Long activeid) {
		return this.uActiveShareDao.countNumByActiveId(activeid);
	}
	
	public long countNumByMidAndActiveIdAndTime(long mid,long activeid,String time){
		
		return uActiveShareDao.countNumByMidAndActiveIdAndTime(mid, activeid, time);
	}

}
