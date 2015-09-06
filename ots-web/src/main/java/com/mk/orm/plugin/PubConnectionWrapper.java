package com.mk.orm.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class PubConnectionWrapper implements InvocationHandler {
	public static boolean ifDebug = true;
	private Connection connection = null;

	public PubConnectionWrapper(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//System.out.println(method.getName());
		if ( method.getName().equals("prepareStatement")) {
			System.out.println(args[0]);
		}
		return method.invoke(this.getConnection(), args);
	}

	public Connection getConnection() {
		return this.connection;
	}
}
