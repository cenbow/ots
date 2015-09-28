package com.mk.ots.message.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.message.model.LPushLog;

/**
 * 消息查询
 * @author nolan
 *
 */
public interface ILPushLogDao extends BaseDao<LPushLog, Long> {
	public Long findActiveCount(LPushLog lPushLog);
}
