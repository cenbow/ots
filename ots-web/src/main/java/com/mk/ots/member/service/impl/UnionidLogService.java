package com.mk.ots.member.service.impl;

import com.mk.ots.mapper.UnionidLogMapper;
import com.mk.ots.member.model.UUnionidLog;
import com.mk.ots.member.service.IUnionidLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnionidLogService implements IUnionidLogService{

	@Autowired
	private UnionidLogMapper unionidLogMapper;

	@Override
	public List<UUnionidLog> queryByUnionid(String unionid) {
		return this.unionidLogMapper.queryByUnionid(unionid);
	}

	@Override
	public int saveLog(UUnionidLog log) {
		return this.unionidLogMapper.saveLog(log);
	}
}