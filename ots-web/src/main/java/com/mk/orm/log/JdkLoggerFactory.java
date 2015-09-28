/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.log;

/**
 * JdkLoggerFactory
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class JdkLoggerFactory implements ILoggerFactory {
    public Logger getLogger(Class<?> clazz) {
        return new JdkLogger(clazz);
    }
    
    public Logger getLogger(String name) {
        return new JdkLogger(name);
    }
}
