/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.SQLException;

/** 
 * IAtom support transaction of database.
 * It can be invoked in Db.tx(IAtom atom) method.
 * <br>
 * Example:<br>
 * Db.tx(new IAtom(){<br>
 *      public boolean run() throws SQLException {<br>
 *          int result1 = Db.update("update account set cash = cash - ? where id = ?", 100, 123);<br>
 *          int result2 = Db.update("update account set cash = cash + ? where id = ?", 100, 456);<br>
 *          return result1 == 1 && result2 == 1;<br>
 *      }});
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public interface IAtom {
    
    /**
     * Place codes here that need transaction support.
     * @return true if you want to commit the transaction otherwise roll back transaction
     */
    boolean run() throws SQLException;
}
