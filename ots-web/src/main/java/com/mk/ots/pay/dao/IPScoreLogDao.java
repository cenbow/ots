package com.mk.ots.pay.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.pay.model.PScoreLog;

public interface IPScoreLogDao extends BaseDao<PScoreLog, Long>{

	public abstract void saveOrUpdate(PScoreLog pScoreLog);
	
	public abstract void deleteByInfoid(Long infoid);
	
	
}
