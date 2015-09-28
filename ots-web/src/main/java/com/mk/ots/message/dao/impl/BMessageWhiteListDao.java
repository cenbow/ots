package com.mk.ots.message.dao.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.message.dao.IBMessageWhiteListDao;
import com.mk.ots.message.model.BMessageWhiteList;
import com.mk.ots.message.model.LPushLog;
import com.mk.ots.message.service.IBMessageWhiteListService;

/**
 * 
 * @author 张亚军
 *
 */
@Service
public class BMessageWhiteListDao extends MyBatisDaoImpl<BMessageWhiteList, Long> implements IBMessageWhiteListDao {
	

	final Logger logger = LoggerFactory.getLogger(BMessageWhiteListDao.class);
	public BMessageWhiteList findByPhone(String phone){
		logger.info("参数Phone:"+phone);
		Map<String, Object> param = Maps.newHashMap();
		param.put("phone", phone);
		return this.findOne("findByPhone", param);
	}

}
