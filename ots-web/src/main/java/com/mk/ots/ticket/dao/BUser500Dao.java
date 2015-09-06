package com.mk.ots.ticket.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.BUser500;

public interface BUser500Dao extends BaseDao<BUser500, Long>{

	public abstract void updateStatusTByPhone(String phone);

	public abstract List<BUser500> findStatusNullList();

}
