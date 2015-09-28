package com.mk.ots.message.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.framework.model.Page;
import com.mk.ots.message.dao.IWhiteListDao;
import com.mk.ots.message.model.WhiteList;
@Component
public class WhiteListDao extends MyBatisDaoImpl<WhiteList, Long> implements
		IWhiteListDao {


	@Override
	public List<WhiteList> quaryAllWhiteList() {
		Map<String, Object> param = Maps.newHashMap();
		return this.find("queryAllWhiteLists", param);
	}

}
