package com.mk.ots.log;

import java.util.Date;

import com.mk.ots.login.model.ULoginLog;

public interface ILogService {

	void createLoginLogTable(String tableName);

	void savaOrUpdate(ULoginLog loginLog, Date time);
	
}
