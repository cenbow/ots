package com.mk.ots.appstatus.service;

import com.mk.ots.appstatus.model.AppStatus;




/**
 * @author zhangyajun
 *
 */
public interface IAppStatusService {
	
	/**
	 * 插入AppStatus对象
	 * @param appStatus
	 */
    public void save(AppStatus appStatus);
}
