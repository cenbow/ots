package com.mk.ots.order.dao;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.order.bean.UUseTicketRecord;

@Component
public class UUseTicketRecordDao extends MyBatisDaoImpl<UUseTicketRecord, Long>
		implements IUUseTicketRecordDao {
	@Override
	public void saveOrUpdate(UUseTicketRecord uUseTicketRecord) {
		if(uUseTicketRecord.getId()!=null){
			this.update(uUseTicketRecord);
		}else{
			this.insert(uUseTicketRecord);
		}
	}
	
	public void deleteByPayidAndMid(Map<String,Object>  map ){
		super.delete("deleteByPayidAndMid", map);
	}
}
