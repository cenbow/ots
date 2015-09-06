/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Model;
import com.mk.orm.plugin.bean.Page;
import com.mk.orm.plugin.bean.Table;

/**
 * Dialect
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public abstract class Dialect {
    public abstract String forTableBuilderDoBuild(String tableName);
    public abstract void forModelSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras);
    public abstract String forModelDeleteById(Table table);
    public abstract void forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, String pKey, Object id, StringBuilder sql, List<Object> paras);
    public abstract String forModelFindById(Table table, String columns);
    public abstract String forDbFindById(String tableName, String primaryKey, String columns);
    public abstract String forDbDeleteById(String tableName, String primaryKey);
    public abstract void forDbSave(StringBuilder sql, List<Object> paras, String tableName, Bean bean);
    public abstract void forDbUpdate(String tableName, String primaryKey, Object id, Bean bean, StringBuilder sql, List<Object> paras);
    public abstract void forPaginate(StringBuilder sql, int pageNumber, int pageSize, String select, String sqlExceptSelect);
    
    public boolean isOracle() {
        return false;
    }
    
    public boolean isTakeOverDbPaginate() {
        return false;
    }
    
    public Page<Bean> takeOverDbPaginate(Connection conn, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) throws SQLException {
        throw new RuntimeException("You should implements this method in " + getClass().getName());
    }
    
    public boolean isTakeOverModelPaginate() {
        return false;
    }
    
    @SuppressWarnings("rawtypes")
    public Page takeOverModelPaginate(Connection conn, Class<? extends Model> modelClass, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) throws Exception {
        throw new RuntimeException("You should implements this method in " + getClass().getName());
    }
    
    public void fillStatement(PreparedStatement pst, List<Object> paras) throws SQLException {
        for (int i=0, size=paras.size(); i<size; i++) {
            pst.setObject(i + 1, paras.get(i));
        }
    }
    
    public void fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
        for (int i=0; i<paras.length; i++) {
            pst.setObject(i + 1, paras[i]);
        }
    }
    
    public String getDefaultPrimaryKey() {
        return "id";
    }
}
