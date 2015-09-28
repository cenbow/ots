package com.mk.ots.pay.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.pay.dao.IPScoreLogDao;
import com.mk.ots.pay.model.PScoreLog;

@Component
public class PScoreLogDao extends MyBatisDaoImpl<PScoreLog, Long> implements
		IPScoreLogDao {
	
	@Override
	public void saveOrUpdate(PScoreLog pScoreLog){
		if(pScoreLog.getId()!=null){
			this.update(pScoreLog);
		}else{
			this.insert(pScoreLog);
		}
	}
	
	@Override
	public  void deleteByInfoid(Long id){
		this.delete("deleteByInfoid",id);
	}
}
