/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import com.mk.orm.log.Logger;

/**
 * SqlReporter
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class SqlReporter implements InvocationHandler {
    
    private Connection conn;
    private static boolean loggerOn = false;
    private static final Logger log = Logger.getLogger(SqlReporter.class);
    
    SqlReporter(Connection conn) {
        this.conn = conn;
    }
    
    public static void setLogger(boolean on) {
        SqlReporter.loggerOn = on;
    }
    
    @SuppressWarnings("rawtypes")
    Connection getConnection() {
        Class clazz = conn.getClass();
        return (Connection)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{Connection.class}, this);
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (method.getName().equals("prepareStatement")) {
                String info = "Sql: " + args[0];
                if (loggerOn)
                    log.info(info);
                else
                    System.out.println(info);
            }
            return method.invoke(conn, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
