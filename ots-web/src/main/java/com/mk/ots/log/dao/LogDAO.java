package com.mk.ots.log.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.util.SqlUtil;
import com.mk.ots.log.ILogDAO;
import com.mk.ots.log.mapper.LogMapper;
import com.mk.ots.login.model.ULoginLog;

/**
 * 
 * @author shellingford
 * @version 2014年11月7日
 */
@Service
public class LogDAO   implements ILogDAO {

	@Autowired
	private DataSource dataSource;
	
	private LogMapper logMapper;
	
	@Override
	public void createLoginLogTable(String tableName) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		
			ResultSet rs=null;		
		    try {
		    	rs  = conn.getMetaData().getTables(null, null,  tableName, null );
				if (!rs.next()){
					String sql = "CREATE TABLE `"+tableName+"` (\n" +
							"	`id` bigint,\n" +
							"	`mid` bigint,\n" +
							"	`accesstoken` varchar(255),\n" +
							"	`type` int,\n" +
							"	`ostype` varchar(255),\n" +
							"	`time` datetime,\n" +
							"	`ip` varchar(255),\n" +
							"	`appversion` int\n" +
							");";
					SqlUtil.update(conn, sql);
				}
		    } catch(SQLException ex) { 
		    	ex.printStackTrace();
		    }finally {
			    try {
			    	if(rs!=null){
			    		rs.close();
			    	}
			    } catch (SQLException e) {
			    	e.printStackTrace();
			    }
		    }
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally{
			try {
		    	if(conn!=null && !conn.isClosed()){
		    		conn.close();
		    	}
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }
		}
	}

	@Override
	public void savaOrUpdate(ULoginLog loginLog, String tableName) {
		// INSERT INTO u_login_log_2014_12(mid,type,time,ostype,IP,appversion ,accesstoken) values();
		logMapper.save(tableName,loginLog.getMid(), loginLog.getAccessToken(), loginLog.getType().intValue(), loginLog.getTime(), loginLog.getOsType(), loginLog.getIp(), loginLog.getAppVersion().intValue());
	}
}
