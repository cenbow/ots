package com.mk.ots.member.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.member.dao.IBAppUpdateDao;
import com.mk.ots.member.model.BAppUpdate;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class BAppUpdateDao extends MyBatisDaoImpl<BAppUpdate, String> implements IBAppUpdateDao{

	@Override
	public List<BAppUpdate> findAllRecord() {
		return this.find("selectAllRecord", null);
	}

}
