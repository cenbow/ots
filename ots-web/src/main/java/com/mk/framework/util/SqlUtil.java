package com.mk.framework.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shellingford
 * @version 创建时间：2012-6-25 上午11:11:20
 *
 */
public class SqlUtil {

	private static Logger logger = LoggerFactory.getLogger(SqlUtil.class);

	public static void close(ResultSet ob) {
		try {
			if (ob != null) {
				ob.close();
			}
		} catch (Exception e) {
			SqlUtil.logger.warn("", e);
		}
	}

	public static void close(Statement ob) {
		try {
			if (ob != null) {
				ob.close();
			}
		} catch (Exception e) {
			SqlUtil.logger.warn("", e);
		}

	}

	public static int update(Connection conn, String sql) throws SQLException {
		Statement cmd = null;
		try {
			cmd = conn.createStatement();
			return cmd.executeUpdate(sql);
		} finally {
			SqlUtil.close(cmd);
		}
	}

	public static int update(Connection conn, String sql, Object... args) throws SQLException {
		if (args == null) {
			return SqlUtil.update(conn, sql);
		}
		PreparedStatement cmd = null;
		try {
			cmd = conn.prepareStatement(sql);
			SqlUtil.PrepareCommand(cmd, args);
			return cmd.executeUpdate();
		} finally {
			SqlUtil.close(cmd);
		}
	}

	public static int update(Connection conn, String sql, Map<String, Object> map) throws SQLException {
		if ((map == null) || (map.size() == 0)) {
			return SqlUtil.update(conn, sql);
		}
		Statement cmd = null;
		try {
			cmd = conn.createStatement();
			sql = SqlUtil.setProperties(sql, map);
			return cmd.executeUpdate(sql);
		} finally {
			SqlUtil.close(cmd);
		}
	}

	public static String setProperties(String sql, Map<String, Object> map) {
		Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (iter.hasNext()) {
			Map.Entry<String, Object> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			while (sql.contains(":" + key + " ") || sql.endsWith(":" + key) || sql.contains(":" + key + ";")) {
				if (value instanceof Date) {
					if (sql.endsWith(":" + key)) {
						sql = sql.substring(0, sql.length() - (":" + key).length()) + "'" + StringEscapeUtils.escapeSql(format.format((Date) value))
								+ "'";
					} else if (sql.contains(":" + key + ";")) {
						sql = sql.replace(":" + key + ";", "'" + StringEscapeUtils.escapeSql(format.format((Date) value)) + "';");
					} else {
						sql = sql.replace(":" + key + " ", "'" + StringEscapeUtils.escapeSql(format.format((Date) value)) + "' ");
					}
				} else {
					if (sql.endsWith(":" + key)) {
						sql = sql.substring(0, sql.length() - (":" + key).length()) + "'" + StringEscapeUtils.escapeSql(value.toString()) + "'";
					} else if (sql.contains(":" + key + ";")) {
						sql = sql.replace(":" + key + ";", "'" + StringEscapeUtils.escapeSql(value.toString()) + "';");
					} else {
						sql = sql.replace(":" + key + " ", "'" + StringEscapeUtils.escapeSql(value.toString()) + "' ");
					}
				}
			}
		}
		return sql;
	}

	// public static void setProperties(PreparedStatement cmd, Object... args)
	// throws SQLException {
	// if (args == null || args.length == 0) {
	// return;
	// }
	// for (int i = 0; i < args.length; i++) {
	// Object ob = args[i];
	// if (ob instanceof String) {
	// cmd.setString(i + 1, (String) ob);
	// } else if (ob instanceof Integer) {
	// cmd.setInt(i + 1, (Integer) ob);
	// } else if (ob instanceof Long) {
	// cmd.setLong(i + 1, (Long) ob);
	// } else if (ob instanceof Double) {
	// cmd.setDouble(i + 1, (Double) ob);
	// } else if (ob instanceof Float) {
	// cmd.setFloat(i + 1, (Float) ob);
	// } else if (ob instanceof Blob) {
	// cmd.setBlob(i + 1, (Blob) ob);
	// } else if (ob instanceof Byte) {
	// cmd.setByte(i + 1, (Byte) ob);
	// } else if (ob instanceof byte[]) {
	// cmd.setBytes(i + 1, (byte[]) ob);
	// } else if (ob instanceof Date) {
	// cmd.setDate(i + 1, new java.sql.Date(((Date) ob).getTime()));
	// } else {
	// cmd.setObject(i + 1, ob);
	// }
	// }
	// }

	/**
	 * 准备SQL参数
	 *
	 * @param pstm
	 * @param params
	 * @throws SQLException
	 */
	public static void PrepareCommand(PreparedStatement pstm, Object... params) throws SQLException {
		if ((params == null) || (params.length == 0)) {
			return;
		}
		for (int i = 0; i < params.length; i++) {
			int parameterIndex = i + 1;
			// null
			if (params[i] == null) {
				pstm.setString(parameterIndex, null);
				continue;
			}
			// String
			if (params[i].getClass() == String.class) {
				pstm.setString(parameterIndex, params[i].toString());
			}
			// Short
			else if (params[i].getClass() == Short.class) {
				pstm.setShort(parameterIndex, Short.parseShort(params[i].toString()));
			}
			// Long
			else if ((params[i].getClass() == Long.class) || (params[i].getClass() == long.class)) {
				pstm.setLong(parameterIndex, Long.parseLong(params[i].toString()));
			}
			// Integer
			else if ((params[i].getClass() == Integer.class) || (params[i].getClass() == int.class)) {
				pstm.setInt(parameterIndex, Integer.parseInt(params[i].toString()));
			}
			// Date
			else if (params[i].getClass() == java.util.Date.class) {
				java.util.Date dt = (java.util.Date) params[i];
				pstm.setDate(parameterIndex, new java.sql.Date(dt.getTime()));
			}
			// Byte[]
			else if (params[i].getClass() == " ".getBytes().getClass()) {
				pstm.setBytes(parameterIndex, (byte[]) params[i]);
			}
			// Byte
			else if (params[i].getClass() == byte.class) {
				pstm.setByte(parameterIndex, (Byte) params[i]);
			}
			// Float
			else if ((params[i].getClass() == Float.class) || (params[i].getClass() == float.class)) {
				pstm.setFloat(parameterIndex, Float.parseFloat(params[i].toString()));
			}
			// Boolean
			else if ((params[i].getClass() == boolean.class) || (params[i].getClass() == Boolean.class)) {
				pstm.setBoolean(parameterIndex, Boolean.parseBoolean(params[i].toString()));
			} else if (params[i].getClass() == Double.class) {
				pstm.setDouble(parameterIndex, Double.parseDouble(params[i].toString()));
			} else if (params[i].getClass() == double.class) {
				pstm.setDouble(parameterIndex, Double.parseDouble(params[i].toString()));

			} else if (params[i].getClass() == BigDecimal.class) {
				pstm.setDouble(parameterIndex, Double.parseDouble(params[i].toString()));

			} else {
				throw new RuntimeException("参数准备出错:数据类型不可见" + params[i].getClass().toString());
			}
		}
	}

	public static String collectionToString(Collection<? extends Object> c) {
		StringBuilder sb = new StringBuilder();
		if ((c == null) || (c.size() == 0)) {
			return "()";
		} else {
			sb.append("(");
			Iterator<? extends Object> iter = c.iterator();
			while (iter.hasNext()) {
				String str = iter.next().toString();
				sb.append("'").append(StringEscapeUtils.escapeSql(str)).append("'");
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}

	public static String arrayToString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		if ((list == null) || (list.size() == 0)) {
			return "()";
		} else {
			sb.append("(");
			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				String str = iter.next();
				sb.append("'").append(StringEscapeUtils.escapeSql(str)).append("'");
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}

	public static String arrayToString(String strs[]) {
		StringBuilder sb = new StringBuilder();
		if ((strs == null) || (strs.length == 0)) {
			return "()";
		} else {
			sb.append("(");
			for (int i = 0; i < strs.length; i++) {
				String str = strs[i];
				sb.append("'").append(StringEscapeUtils.escapeSql(str)).append("'");
				if (i < (strs.length - 1)) {
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}

	public static String longarrayToString(List<Long> list) {
		StringBuilder sb = new StringBuilder();
		if ((list == null) || (list.size() == 0)) {
			return "()";
		} else {
			sb.append("(");
			Iterator<Long> iter = list.iterator();
			while (iter.hasNext()) {
				Long str = iter.next();
				sb.append(str);
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}

	public static String longarrayToString(Long strs[]) {
		StringBuilder sb = new StringBuilder();
		if ((strs == null) || (strs.length == 0)) {
			return "()";
		} else {
			sb.append("(");
			for (int i = 0; i < strs.length; i++) {
				Long str = strs[i];
				sb.append(str);
				if (i < (strs.length - 1)) {
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}

	public static String longarrayToString(Set<Long> set) {
		StringBuilder sb = new StringBuilder();
		if ((set == null) || (set.size() == 0)) {
			return "()";
		} else {
			sb.append("(");
			Iterator<Long> iter = set.iterator();
			while (iter.hasNext()) {
				Long str = iter.next();
				sb.append(str);
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}

	public static String arrayToString(Set<String> set) {
		StringBuilder sb = new StringBuilder();
		if ((set == null) || (set.size() == 0)) {
			return "()";
		} else {
			sb.append("(");
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()) {
				String str = iter.next();
				sb.append("'").append(StringEscapeUtils.escapeSql(str)).append("'");
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}
}
