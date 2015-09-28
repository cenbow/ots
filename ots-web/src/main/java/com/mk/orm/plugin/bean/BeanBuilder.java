/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BeanBuilder
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class BeanBuilder {
    
    @SuppressWarnings("unchecked")
    public static final List<Bean> build(Config config, ResultSet rs) throws SQLException {
        List<Bean> result = new ArrayList<Bean>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] labelNames = new String[columnCount + 1];
        int[] types = new int[columnCount + 1];
        buildLabelNamesAndTypes(rsmd, labelNames, types);
        while (rs.next()) {
            Bean bean = new Bean();
            bean.setColumnsMap(config.containerFactory.getColumnsMap());
            Map<String, Object> columns = bean.getColumns();
            for (int i=1; i<=columnCount; i++) {
                Object value;
                if (types[i] < Types.BLOB)
                    value = rs.getObject(i);
                else if (types[i] == Types.CLOB)
                    value = ModelBuilder.handleClob(rs.getClob(i));
                else if (types[i] == Types.NCLOB)
                    value = ModelBuilder.handleClob(rs.getNClob(i));
                else if (types[i] == Types.BLOB)
                    value = ModelBuilder.handleBlob(rs.getBlob(i));
                else
                    value = rs.getObject(i);
                
                columns.put(labelNames[i], value);
            }
            result.add(bean);
        }
        return result;
    }
    
    private static final void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types) throws SQLException {
        for (int i=1; i<labelNames.length; i++) {
            labelNames[i] = rsmd.getColumnLabel(i);
            types[i] = rsmd.getColumnType(i);
        }
    }
}
