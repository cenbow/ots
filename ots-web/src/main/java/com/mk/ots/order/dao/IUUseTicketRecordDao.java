package com.mk.ots.order.dao;

import java.util.Map;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.order.bean.UUseTicketRecord;

public interface IUUseTicketRecordDao extends BaseDao<UUseTicketRecord, Long> {
	
	public void saveOrUpdate(UUseTicketRecord uUseTicketRecord);
	
	public void deleteByPayidAndMid(Map<String, Object>  map);
	
}
