/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean.dialect;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Table;

/**
 * MysqlDialect
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class MysqlDialect extends Dialect {
    
    public String forTableBuilderDoBuild(String tableName) {
        return "select * from `" + tableName + "` where 1 = 2";
    }
    
    public void forModelSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras) {
        sql.append("insert into `").append(table.getName()).append("`(");
        StringBuilder temp = new StringBuilder(") values(");
        for (Entry<String, Object> e: attrs.entrySet()) {
            String colName = e.getKey();
            if (table.hasColumnLabel(colName)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                    temp.append(", ");
                }
                sql.append("`").append(colName).append("`");
                temp.append("?");
                paras.add(e.getValue());
            }
        }
        sql.append(temp.toString()).append(")");
    }
    
    public String forModelDeleteById(Table table) {
        String primaryKey = table.getPrimaryKey();
        StringBuilder sql = new StringBuilder(45);
        sql.append("delete from `");
        sql.append(table.getName());
        sql.append("` where `").append(primaryKey).append("` = ?");
        return sql.toString();
    }
    
    public void forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, String primaryKey, Object id, StringBuilder sql, List<Object> paras) {
        sql.append("update `").append(table.getName()).append("` set ");
        for (Entry<String, Object> e : attrs.entrySet()) {
            String colName = e.getKey();
            if (!primaryKey.equalsIgnoreCase(colName) && modifyFlag.contains(colName) && table.hasColumnLabel(colName)) {
                if (paras.size() > 0)
                    sql.append(", ");
                sql.append("`").append(colName).append("` = ? ");
                paras.add(e.getValue());
            }
        }
        sql.append(" where `").append(primaryKey).append("` = ?");  // .append(" limit 1");
        paras.add(id);
    }
    
    public String forModelFindById(Table table, String columns) {
        StringBuilder sql = new StringBuilder("select ");
        if (columns.trim().equals("*")) {
            sql.append(columns);
        }
        else {
            String[] columnsArray = columns.split(",");
            for (int i=0; i<columnsArray.length; i++) {
                if (i > 0)
                    sql.append(", ");
                sql.append("`").append(columnsArray[i].trim()).append("`");
            }
        }
        sql.append(" from `");
        sql.append(table.getName());
        sql.append("` where `").append(table.getPrimaryKey()).append("` = ?");
        return sql.toString();
    }
    
    public String forDbFindById(String tableName, String primaryKey, String columns) {
        StringBuilder sql = new StringBuilder("select ");
        if (columns.trim().equals("*")) {
            sql.append(columns);
        }
        else {
            String[] columnsArray = columns.split(",");
            for (int i=0; i<columnsArray.length; i++) {
                if (i > 0)
                    sql.append(", ");
                sql.append("`").append(columnsArray[i].trim()).append("`");
            }
        }
        sql.append(" from `");
        sql.append(tableName.trim());
        sql.append("` where `").append(primaryKey).append("` = ?");
        return sql.toString();
    }
    
    public String forDbDeleteById(String tableName, String primaryKey) {
        StringBuilder sql = new StringBuilder("delete from `");
        sql.append(tableName.trim());
        sql.append("` where `").append(primaryKey).append("` = ?");
        return sql.toString();
    }
    
    public void forDbSave(StringBuilder sql, List<Object> paras, String tableName, Bean bean) {
        sql.append("insert into `");
        sql.append(tableName.trim()).append("`(");
        StringBuilder temp = new StringBuilder();
        temp.append(") values(");
        
        for (Entry<String, Object> e: bean.getColumns().entrySet()) {
            if (paras.size() > 0) {
                sql.append(", ");
                temp.append(", ");
            }
            sql.append("`").append(e.getKey()).append("`");
            temp.append("?");
            paras.add(e.getValue());
        }
        sql.append(temp.toString()).append(")");
    }
    
    public void forDbUpdate(String tableName, String primaryKey, Object id, Bean bean, StringBuilder sql, List<Object> paras) {
        sql.append("update `").append(tableName.trim()).append("` set ");
        for (Entry<String, Object> e: bean.getColumns().entrySet()) {
            String colName = e.getKey();
            if (!primaryKey.equalsIgnoreCase(colName)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                }
                sql.append("`").append(colName).append("` = ? ");
                paras.add(e.getValue());
            }
        }
        sql.append(" where `").append(primaryKey).append("` = ?");  // .append(" limit 1");
        paras.add(id);
    }
    
    public void forPaginate(StringBuilder sql, int pageNumber, int pageSize, String select, String sqlExceptSelect) {
        int offset = pageSize * (pageNumber - 1);
        sql.append(select).append(" ");
        sql.append(sqlExceptSelect);
        sql.append(" limit ").append(offset).append(", ").append(pageSize); // limit can use one or two '?' to pass paras
    }
}
