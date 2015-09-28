package com.mk.ots.message.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.message.model.WhiteList;

public interface IWhiteListDao extends BaseDao<WhiteList, Long> {
	public List<WhiteList> quaryAllWhiteList();
}
