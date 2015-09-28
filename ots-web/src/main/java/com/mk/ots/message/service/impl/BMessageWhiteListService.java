package com.mk.ots.message.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.message.dao.impl.BMessageWhiteListDao;
import com.mk.ots.message.model.BMessageWhiteList;
import com.mk.ots.message.service.IBMessageWhiteListService;

/**
 * 
 * @author 张亚军
 *
 */
@Service
public class BMessageWhiteListService implements IBMessageWhiteListService {
	

	final Logger logger = LoggerFactory.getLogger(BMessageWhiteListService.class);
	
	@Autowired
	private BMessageWhiteListDao bMessageWhiteListDao;
	
	public BMessageWhiteList findByPhone(String phone){
		
	   return this.bMessageWhiteListDao.findByPhone(phone);
	}

}
