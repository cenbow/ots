package com.mk.ots.member.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.member.dao.IBAppUpdateDao;
import com.mk.ots.member.model.BAppUpdate;
import com.mk.ots.member.service.IBAppUpdateService;
@Service
public class BAppUpdateService implements IBAppUpdateService{

	@Autowired
	private IBAppUpdateDao iBAppUpdateDao;
	
	@Override
	public List<BAppUpdate> findAllRecord() {
		// TODO Auto-generated method stub
		return iBAppUpdateDao.findAllRecord();
	}

}
