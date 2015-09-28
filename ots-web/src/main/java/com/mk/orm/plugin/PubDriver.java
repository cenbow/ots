package com.mk.orm.plugin;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import com.mysql.jdbc.Driver;

public class PubDriver extends Driver {

	static {
		try {
			Enumeration<java.sql.Driver> drvierEnum = DriverManager.getDrivers();
			while (drvierEnum.hasMoreElements()) {
				java.sql.Driver driver = drvierEnum.nextElement();
				if (driver instanceof Driver) {
					DriverManager.deregisterDriver(driver);
					break;
				}
			}
			java.sql.DriverManager.registerDriver(new PubDriver());
		} catch (SQLException E) {
			throw new RuntimeException("Can't register driver!");
		}
	}

	public PubDriver() throws SQLException {
		super();
	}

	@Override
	public java.sql.Connection connect(String url, Properties info) throws SQLException {
		Connection connection = super.connect(url, info);
		Class<?>[] interfaces = new Class<?>[] { java.sql.Connection.class };
		return (Connection) Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, new PubConnectionWrapper(connection));

	}
}
