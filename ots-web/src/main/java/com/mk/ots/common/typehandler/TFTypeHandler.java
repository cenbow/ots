package com.mk.ots.common.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class TFTypeHandler extends BaseTypeHandler<Boolean> {

        private static final String YES = "T";
        private static final String NO = "F";
        
        protected String yes() {
                return YES;
        }
        
        protected String no() {
                return NO;
        }
        
        private boolean valueByString(String value) {
                return (value != null && value.equals(yes()));
        }
        
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
                ps.setString(i, parameter.booleanValue() ? yes() : no());
        }

        @Override
        public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
                return valueByString(rs.getString(columnName));
        }

        @Override
        public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
                return valueByString(rs.getString(columnIndex));
        }

        @Override
        public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
                return valueByString(cs.getString(columnIndex));
        }
        
}