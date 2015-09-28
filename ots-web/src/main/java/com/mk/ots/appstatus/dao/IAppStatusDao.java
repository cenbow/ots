package com.mk.ots.appstatus.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.appstatus.model.AppStatus;

/**
 * app状态推送接口
 * @author nolan
 *
 */
public interface IAppStatusDao extends BaseDao<AppStatus, Long>{

	public abstract List<AppStatus> findByMid(Long mid);

}
