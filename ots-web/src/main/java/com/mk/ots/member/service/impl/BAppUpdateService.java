package com.mk.ots.member.service.impl;

import com.mk.ots.member.dao.IBAppUpdateDao;
import com.mk.ots.member.model.BAppUpdate;
import com.mk.ots.member.service.IBAppUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BAppUpdateService implements IBAppUpdateService{

	@Autowired
	private IBAppUpdateDao iBAppUpdateDao;
	
	@Override
	public List<BAppUpdate> findAllRecord() {
		return iBAppUpdateDao.findAllRecord();
	}

}
