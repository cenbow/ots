package com.mk.ots.member.service;

import com.mk.ots.member.model.UUnionidLog;

import java.util.List;


/**
 *
 */
public interface IUnionidLogService {

	public List<UUnionidLog> queryByUnionid(String unionid);

	public int saveLog(UUnionidLog log);
}
