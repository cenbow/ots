package com.mk.ots.system.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.system.model.SyConfig;

public interface ISyConfigDao extends BaseDao<SyConfig, Long>{

	public abstract void updateByKeyAndType(String key, String type,
			String value);

	public abstract String findByKeyAndType(String key, String type);

}
