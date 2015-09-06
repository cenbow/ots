/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.log;

/**
 * Logger
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public abstract class Logger {
    private static ILoggerFactory factory;
    static {
        init();
    }
    
    public static void setLoggerFactory(ILoggerFactory loggerFactory) {
        if (loggerFactory != null)
            Logger.factory = loggerFactory;
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return factory.getLogger(clazz);
    }
    
    public static Logger getLogger(String name) {
        return factory.getLogger(name);
    }
    
    public static void init() {
        if (factory != null)
            return ;
        try {
            Class.forName("org.apache.log4j.Logger");
            Class<?> log4jLoggerFactoryClass = Class.forName("com.mk.orm.log.Log4jLoggerFactory");
            factory = (ILoggerFactory)log4jLoggerFactoryClass.newInstance();    // return new Log4jLoggerFactory();
        } catch (Exception e) {
            factory = new JdkLoggerFactory();
        }
    }
    
    public abstract void debug(String message);
    
    public abstract void debug(String message, Throwable t);
    
    public abstract void info(String message);
    
    public abstract void info(String message, Throwable t);
    
    public abstract void warn(String message);
    
    public abstract void warn(String message, Throwable t);
    
    public abstract void error(String message);
    
    public abstract void error(String message, Throwable t);
    
    public abstract void fatal(String message);
    
    public abstract void fatal(String message, Throwable t);
    
    public abstract boolean isDebugEnabled();

    public abstract boolean isInfoEnabled();

    public abstract boolean isWarnEnabled();

    public abstract boolean isErrorEnabled();
    
    public abstract boolean isFatalEnabled();
}
