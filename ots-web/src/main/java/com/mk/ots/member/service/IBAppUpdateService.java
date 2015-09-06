package com.mk.ots.member.service;

import java.util.List;

import com.mk.ots.member.model.BAppUpdate;

public interface IBAppUpdateService {

	/**
	 * 获取所有记录
	 * @return
	 */
	public List<BAppUpdate> findAllRecord();
}
