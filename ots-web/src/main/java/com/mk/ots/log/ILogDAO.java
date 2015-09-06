package com.mk.ots.log;

import com.mk.ots.login.model.ULoginLog;

public interface ILogDAO {

	void createLoginLogTable(String tableName);

	void savaOrUpdate(ULoginLog loginLog, String tableName);

}
