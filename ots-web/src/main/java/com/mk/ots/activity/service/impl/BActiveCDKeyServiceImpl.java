package com.mk.ots.activity.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.activity.dao.IBActiveCDKeyDao;
import com.mk.ots.activity.service.IBActiveCDKeyService;

@Service
public class BActiveCDKeyServiceImpl implements IBActiveCDKeyService {

	@Autowired
	private IBActiveCDKeyDao ibActiveCDKeyDao;
	public  boolean existBatchNo(Long activeid, String batchno){
		return  ibActiveCDKeyDao.existBatchNo(activeid, batchno);
	}
}
