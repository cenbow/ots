/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.util.Map;
import java.util.Set;

/**
 * IContainerFactory
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
@SuppressWarnings("rawtypes")
public interface IContainerFactory {
    Map getAttrsMap();
    Map getColumnsMap();
    Set getModifyFlagSet();
}
