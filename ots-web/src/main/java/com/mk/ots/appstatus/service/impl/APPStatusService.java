package com.mk.ots.appstatus.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.mk.ots.appstatus.dao.IAppStatusDao;
import com.mk.ots.appstatus.model.AppStatus;
import com.mk.ots.appstatus.service.IAppStatusService;
@Service
public class APPStatusService implements IAppStatusService{

	@Autowired
	private IAppStatusDao iAppStatusDao;
	
	@Override
	public void save(AppStatus appStatus) {
		// TODO Auto-generated method stub
		iAppStatusDao.insert(appStatus);
	}

}
