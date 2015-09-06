package com.mk.ots.member.dao;

import java.util.List;

import com.mk.ots.member.model.BAppUpdate;

public interface IBAppUpdateDao {

	/**
	 * 获取所以记录
	 * @return
	 */
	public List<BAppUpdate> findAllRecord();
}
