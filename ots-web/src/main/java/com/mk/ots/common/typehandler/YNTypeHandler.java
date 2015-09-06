//package com.mk.ots.common.typehandler;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//
//public class YNTypeHandler extends BaseTypeHandler<Boolean> {
//
//        private static final Log log = LogFactory.getLog(YNTypeHandler.class);
//
//        private static final String YES = "Y";
//        private static final String NO = "N";
//
//        protected String yes() {
//                return YES;
//        }
//
//        protected String no() {
//                return NO;
//        }
//
//        private boolean valueByString(String value) {
//                if (log.isDebugEnabled()) log.debug("valueByString (value:" + value + ")");
//                return (value != null && value.equals(yes()));
//        }
//
//        @Override
//        public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
//                if (log.isDebugEnabled()) log.debug("setNonNullParameter at (" + i + " ) value (" + parameter + ")");
//                ps.setString(i, parameter.booleanValue() ? yes() : no());
//        }
//
//        @Override
//        public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
//                if (log.isDebugEnabled()) log.debug("getNullableResult 1 at (" + columnName + " )");
//                return valueByString(rs.getString(columnName));
//        }
//
//        @Override
//        public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//                if (log.isDebugEnabled()) log.debug("getNullableResult 2 at (" + columnIndex + " )");
//                return valueByString(rs.getString(columnIndex));
//        }
//
//        @Override
//        public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//                if (log.isDebugEnabled()) log.debug("getNullableResult 3 at (" + columnIndex + " )");
//                return valueByString(cs.getString(columnIndex));
//        }
//
//}