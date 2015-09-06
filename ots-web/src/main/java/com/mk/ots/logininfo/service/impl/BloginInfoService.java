package com.mk.ots.logininfo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.logininfo.dao.IBloginInfoDao;
import com.mk.ots.logininfo.model.BLoginInfo;
import com.mk.ots.logininfo.service.IBloginInfoService;

@Service
public class BloginInfoService implements IBloginInfoService{

	@Autowired
	private IBloginInfoDao iBloginInfoDao;
	@Override
	public void save(BLoginInfo bLoginInfo) {
		// TODO Auto-generated method stub
		this.iBloginInfoDao.save(bLoginInfo);
	}

}
