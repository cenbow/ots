/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ICallback
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public interface ICallback {
    
    /**
     * Place codes here that need call back by active record plugin.
     * @param conn
     * 参数: JDBC连接，不需要手动关闭，插件会自动关闭。
     */
    Object call(Connection conn) throws SQLException;
}
