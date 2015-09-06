package com.mk.ots.member.dao.impl;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.member.dao.IBActiveUserBindMapper;
import com.mk.ots.member.model.BActiveUserBind;

public class BActiveUserBindDaoImp extends MyBatisDaoImpl<BActiveUserBind, String> implements
		IBActiveUserBindMapper {

	@Override
	public int deleteByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String insert(BActiveUserBind record) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public int insertSelective(BActiveUserBind record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BActiveUserBind selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * 根据组id查找组成员列表
	 */
	@Override
	public Optional<List<BActiveUserBind>> selectBindUser(String groupid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("groupid", groupid);
		return Optional.fromNullable(this.find("selectBindUser", param));
	}

	@Override
	public int updateByPrimaryKeySelective(BActiveUserBind record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(BActiveUserBind record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
