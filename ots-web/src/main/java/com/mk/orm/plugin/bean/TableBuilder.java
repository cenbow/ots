/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;


/**
 * TableBuilder
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class TableBuilder {
    static boolean build(List<Table> tableList, Config config) {
        Table temp = null;
        Connection conn = null;
        try {
            conn = config.dataSource.getConnection();
            TableMapping tableMapping = TableMapping.me();
            for (Table table : tableList) {
                temp = table;
                doBuild(table, conn, config);
                tableMapping.putTable(table);
                DbKit.addModelToConfigMapping(table.getModelClass(), config);
            }
            return true;
        } catch (Exception e) {
            if (temp != null)
                System.err.println("Can not create Table object, maybe the table " + temp.getName() + " is not exists.");
            throw new BeanException(e);
        }
        finally {
            config.close(conn);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void doBuild(Table table, Connection conn, Config config) throws SQLException {
        table.setColumnTypeMap(config.containerFactory.getAttrsMap());
        if (table.getPrimaryKey() == null)
            table.setPrimaryKey(config.dialect.getDefaultPrimaryKey());
        
        String sql = config.dialect.forTableBuilderDoBuild(table.getName());
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        
        for (int i=1; i<=rsmd.getColumnCount(); i++) {
            String colName = rsmd.getColumnName(i);
            String colClassName = rsmd.getColumnClassName(i);
            if ("java.lang.String".equals(colClassName)) {
                // varchar, char, enum, set, text, tinytext, mediumtext, longtext
                table.setColumnType(colName, java.lang.String.class);
            }
            else if ("java.lang.Integer".equals(colClassName)) {
                // int, integer, tinyint, smallint, mediumint
                table.setColumnType(colName, java.lang.Integer.class);
            }
            else if ("java.lang.Long".equals(colClassName)) {
                // bigint
                table.setColumnType(colName, java.lang.Long.class);
            }
            // else if ("java.util.Date".equals(colClassName)) {        // java.util.Data can not be returned
                // java.sql.Date, java.sql.Time, java.sql.Timestamp all extends java.util.Data so getDate can return the three types data
                // result.addInfo(colName, java.util.Date.class);
            // }
            else if ("java.sql.Date".equals(colClassName)) {
                // date, year
                table.setColumnType(colName, java.sql.Date.class);
            }
            else if ("java.lang.Double".equals(colClassName)) {
                // real, double
                table.setColumnType(colName, java.lang.Double.class);
            }
            else if ("java.lang.Float".equals(colClassName)) {
                // float
                table.setColumnType(colName, java.lang.Float.class);
            }
            else if ("java.lang.Boolean".equals(colClassName)) {
                // bit
                table.setColumnType(colName, java.lang.Boolean.class);
            }
            else if ("java.sql.Time".equals(colClassName)) {
                // time
                table.setColumnType(colName, java.sql.Time.class);
            }
            else if ("java.sql.Timestamp".equals(colClassName)) {
                // timestamp, datetime
                table.setColumnType(colName, java.sql.Timestamp.class);
            }
            else if ("java.math.BigDecimal".equals(colClassName)) {
                // decimal, numeric
                table.setColumnType(colName, java.math.BigDecimal.class);
            }
            else if ("[B".equals(colClassName)) {
                // binary, varbinary, tinyblob, blob, mediumblob, longblob
                // qjd project: print_info.content varbinary(61800);
                table.setColumnType(colName, byte[].class);
            }
            else {
                int type = rsmd.getColumnType(i);
                if (type == Types.BLOB) {
                    table.setColumnType(colName, byte[].class);
                }
                else if (type == Types.CLOB || type == Types.NCLOB) {
                    table.setColumnType(colName, String.class);
                }
                else {
                    table.setColumnType(colName, String.class);
                }
                // core.TypeConverter
                // throw new RuntimeException("You've got new type to mapping. Please add code in " + TableBuilder.class.getName() + ". The ColumnClassName can't be mapped: " + colClassName);
            }
        }
        
        rs.close();
        stm.close();
    }
}
