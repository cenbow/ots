/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.log;

import java.util.logging.Level;

/**
 * JdkLogger
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class JdkLogger extends Logger {

    private java.util.logging.Logger log;
    private String clazzName;
    
    JdkLogger(Class<?> clazz) {
        log = java.util.logging.Logger.getLogger(clazz.getName());
        clazzName = clazz.getName();
    }
    
    JdkLogger(String name) {
        log = java.util.logging.Logger.getLogger(name);
        clazzName = name;
    }
    
    public void debug(String message) {
        log.logp(Level.FINE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }
    
    public void debug(String message,  Throwable t) {
        log.logp(Level.FINE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
    }
    
    public void info(String message) {
        log.logp(Level.INFO, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }
    
    public void info(String message, Throwable t) {
        log.logp(Level.INFO, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
    }
    
    public void warn(String message) {
        log.logp(Level.WARNING, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }
    
    public void warn(String message, Throwable t) {
        log.logp(Level.WARNING, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
    }
    
    public void error(String message) {
        log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }
    
    public void error(String message, Throwable t) {
        log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
    }
    
    /**
     * JdkLogger fatal is the same as the error.
     */
    public void fatal(String message) {
        log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }
    
    /**
     * JdkLogger fatal is the same as the error.
     */
    public void fatal(String message, Throwable t) {
        log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message, t);
    }
    
    public boolean isDebugEnabled() {
        return log.isLoggable(Level.FINE);
    }
    
    public boolean isInfoEnabled() {
        return log.isLoggable(Level.INFO);
    }
    
    public boolean isWarnEnabled() {
        return log.isLoggable(Level.WARNING);
    }
    
    public boolean isErrorEnabled() {
        return log.isLoggable(Level.SEVERE);
    }
    
    public boolean isFatalEnabled() {
        return log.isLoggable(Level.SEVERE);
    }
}
