package com.mk.ots.member.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.member.dao.IBAppUpdateDao;
import com.mk.ots.member.model.BAppUpdate;
@Component
public class BAppUpdateDao extends MyBatisDaoImpl<BAppUpdate, String> implements IBAppUpdateDao{

	@Override
	public List<BAppUpdate> findAllRecord() {
		// TODO Auto-generated method stub
		return this.find("selectAllRecord", null);
	}

}
