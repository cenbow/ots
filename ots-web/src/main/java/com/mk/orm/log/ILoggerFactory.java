/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.log;

/**
 * ILoggerFactory
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public interface ILoggerFactory {
    Logger getLogger(Class<?> clazz);
    Logger getLogger(String name);
}
