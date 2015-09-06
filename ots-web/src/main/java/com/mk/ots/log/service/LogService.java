package com.mk.ots.log.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.log.ILogDAO;
import com.mk.ots.log.ILogService;
import com.mk.ots.login.model.ULoginLog;

/**
 * 日志记录
 * @author nolan
 *
 */
@Service
public class LogService implements ILogService {
	
	@Autowired
    private ILogDAO iLogDAO;

	@Override
	public void createLoginLogTable(String talbeName) {
		iLogDAO.createLoginLogTable(talbeName);

	}
	
	public void savaOrUpdate(ULoginLog loginLog, Date time) {
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy_MM");
		String tableName = "u_login_log_"+dateformat.format(time);
		iLogDAO.savaOrUpdate(loginLog,tableName);
	}
}
