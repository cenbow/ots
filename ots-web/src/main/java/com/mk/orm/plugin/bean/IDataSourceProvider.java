/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import javax.sql.DataSource;

/**
 * IDataSourceProvider
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public interface IDataSourceProvider {
    DataSource getDataSource();
}
