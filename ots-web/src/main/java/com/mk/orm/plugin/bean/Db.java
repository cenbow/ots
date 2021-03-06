/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Db
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
@SuppressWarnings("rawtypes")
public class Db {
    
    private static DbPro dbPro = null;
    
    static void init() {
        dbPro = DbPro.use();
    }
    
    public static DbPro use(String configName) {
        return DbPro.use(configName);
    }
    
    static <T> List<T> query(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        return dbPro.query(config, conn, sql, paras);
    }
    
    /**
     * @see #query(String, String, Object...)
     */
    public static <T> List<T> query(String sql, Object... paras) {
        return dbPro.query(sql, paras);
    }
    
    /**
     * @see #query(String, Object...)
     * @param sql an SQL statement
     */
    public static <T> List<T> query(String sql) {
        return dbPro.query(sql);
    }
    
    /**
     * Execute sql query and return the first result. I recommend add "limit 1" in your sql.
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param paras the parameters of sql
     * @return Object[] if your sql has select more than one column,
     *          and it return Object if your sql has select only one column.
     */
    public static <T> T queryFirst(String sql, Object... paras) {
        return dbPro.queryFirst(sql, paras);
    }
    
    /**
     * @see #queryFirst(String, Object...)
     * @param sql an SQL statement
     */
    public static <T> T queryFirst(String sql) {
        return dbPro.queryFirst(sql);
    }
    
    // 26 queryXxx method below -----------------------------------------------
    /**
     * Execute sql query just return one column.
     * @param <T> the type of the column that in your sql's select statement
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param paras the parameters of sql
     * @return List<T>
     */
    public static <T> T queryColumn(String sql, Object... paras) {
        return dbPro.queryColumn(sql, paras);
    }
    
    public static <T> T queryColumn(String sql) {
        return dbPro.queryColumn(sql);
    }
    
    public static String queryStr(String sql, Object... paras) {
        return dbPro.queryStr(sql, paras);
    }
    
    public static String queryStr(String sql) {
        return dbPro.queryStr(sql);
    }
    
    public static Integer queryInt(String sql, Object... paras) {
        return dbPro.queryInt(sql, paras);
    }
    
    public static Integer queryInt(String sql) {
        return dbPro.queryInt(sql);
    }
    
    public static Long queryLong(String sql, Object... paras) {
        return dbPro.queryLong(sql, paras);
    }
    
    public static Long queryLong(String sql) {
        return dbPro.queryLong(sql);
    }
    
    public static Double queryDouble(String sql, Object... paras) {
        return dbPro.queryDouble(sql, paras);
    }
    
    public static Double queryDouble(String sql) {
        return dbPro.queryDouble(sql);
    }
    
    public static Float queryFloat(String sql, Object... paras) {
        return dbPro.queryFloat(sql, paras);
    }
    
    public static Float queryFloat(String sql) {
        return dbPro.queryFloat(sql);
    }
    
    public static java.math.BigDecimal queryBigDecimal(String sql, Object... paras) {
        return dbPro.queryBigDecimal(sql, paras);
    }
    
    public static java.math.BigDecimal queryBigDecimal(String sql) {
        return dbPro.queryBigDecimal(sql);
    }
    
    public static byte[] queryBytes(String sql, Object... paras) {
        return dbPro.queryBytes(sql, paras);
    }
    
    public static byte[] queryBytes(String sql) {
        return dbPro.queryBytes(sql);
    }
    
    public static java.util.Date queryDate(String sql, Object... paras) {
        return dbPro.queryDate(sql, paras);
    }
    
    public static java.util.Date queryDate(String sql) {
        return dbPro.queryDate(sql);
    }
    
    public static java.sql.Time queryTime(String sql, Object... paras) {
        return dbPro.queryTime(sql, paras);
    }
    
    public static java.sql.Time queryTime(String sql) {
        return dbPro.queryTime(sql);
    }
    
    public static java.sql.Timestamp queryTimestamp(String sql, Object... paras) {
        return dbPro.queryTimestamp(sql, paras);
    }
    
    public static java.sql.Timestamp queryTimestamp(String sql) {
        return dbPro.queryTimestamp(sql);
    }
    
    public static Boolean queryBoolean(String sql, Object... paras) {
        return dbPro.queryBoolean(sql, paras);
    }
    
    public static Boolean queryBoolean(String sql) {
        return dbPro.queryBoolean(sql);
    }
    
    public static Number queryNumber(String sql, Object... paras) {
        return dbPro.queryNumber(sql, paras);
    }
    
    public static Number queryNumber(String sql) {
        return dbPro.queryNumber(sql);
    }
    // 26 queryXxx method under -----------------------------------------------
    
    /**
     * Execute sql update
     */
    static int update(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        return dbPro.update(config, conn, sql, paras);
    }
    
    /**
     * Execute update, insert or delete sql statement.
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param paras the parameters of sql
     * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
     *         or <code>DELETE</code> statements, or 0 for SQL statements 
     *         that return nothing
     */
    public static int update(String sql, Object... paras) {
        return dbPro.update(sql, paras);
    }
    
    /**
     * @see #update(String, Object...)
     * @param sql an SQL statement
     */
    public static int update(String sql) {
        return dbPro.update(sql);
    }
    
    static List<Bean> find(Config config, Connection conn, String sql, Object... paras) throws SQLException {
        return dbPro.find(config, conn, sql, paras);
    }
    
    /**
     * @see #find(String, String, Object...)
     */
    public static List<Bean> find(String sql, Object... paras) {
        return dbPro.find(sql, paras);
    }
    
    /**
     * @see #find(String, String, Object...)
     * @param sql the sql statement
     */
    public static List<Bean> find(String sql) {
        return dbPro.find(sql);
    }
    
    /**
     * Find first record. I recommend add "limit 1" in your sql.
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param paras the parameters of sql
     * @return the Bean object
     */
    public static Bean findFirst(String sql, Object... paras) {
        return dbPro.findFirst(sql, paras);
    }
    
    /**
     * @see #findFirst(String, Object...)
     * @param sql an SQL statement
     */
    public static Bean findFirst(String sql) {
        return dbPro.findFirst(sql);
    }
    
    /**
     * Find record by id.
     * Example: Bean user = Db.findById("user", 15);
     * @param tableName the table name of the table
     * @param idValue the id value of the record
     */
    public static Bean findById(String tableName, Object idValue) {
        return dbPro.findById(tableName, idValue);
    }
    
    /**
     * Find record by id. Fetch the specific columns only.
     * Example: Bean user = Db.findById("user", 15, "name, age");
     * @param tableName the table name of the table
     * @param idValue the id value of the record
     * @param columns the specific columns separate with comma character ==> ","
     */
    public static Bean findById(String tableName, Number idValue, String columns) {
        return dbPro.findById(tableName, idValue, columns);
    }
    
    /**
     * Find record by id.
     * Example: Bean user = Db.findById("user", "user_id", 15);
     * @param tableName the table name of the table
     * @param primaryKey the primary key of the table
     * @param idValue the id value of the record
     */
    public static Bean findById(String tableName, String primaryKey, Number idValue) {
        return dbPro.findById(tableName, primaryKey, idValue);
    }
    
    /**
     * Find record by id. Fetch the specific columns only.
     * Example: Bean user = Db.findById("user", "user_id", 15, "name, age");
     * @param tableName the table name of the table
     * @param primaryKey the primary key of the table
     * @param idValue the id value of the record
     * @param columns the specific columns separate with comma character ==> ","
     */
    public static Bean findById(String tableName, String primaryKey, Object idValue, String columns) {
        return dbPro.findById(tableName, primaryKey, idValue, columns);
    }
    
    /**
     * Delete record by id.
     * Example: boolean succeed = Db.deleteById("user", 15);
     * @param tableName the table name of the table
     * @param id the id value of the record
     * @return true if delete succeed otherwise false
     */
    public static boolean deleteById(String tableName, Object id) {
        return dbPro.deleteById(tableName, id);
    }
    
    /**
     * Delete record by id.
     * Example: boolean succeed = Db.deleteById("user", "user_id", 15);
     * @param tableName the table name of the table
     * @param primaryKey the primary key of the table
     * @param id the id value of the record
     * @return true if delete succeed otherwise false
     */
    public static boolean deleteById(String tableName, String primaryKey, Object id) {
        return dbPro.deleteById(tableName, primaryKey, id);
    }
    
    /**
     * Delete record.
     * Example: boolean succeed = Db.delete("user", "id", user);
     * @param tableName the table name of the table
     * @param primaryKey the primary key of the table
     * @param bean the record
     * @return true if delete succeed otherwise false
     */
    public static boolean delete(String tableName, String primaryKey, Bean bean) {
        return dbPro.delete(tableName, primaryKey, bean);
    }
    
    /**
     * Example: boolean succeed = Db.delete("user", user);
     * @see #delete(String, String, Bean)
     */
    public static boolean delete(String tableName, Bean bean) {
        return dbPro.delete(tableName, bean);
    }
    
    static Page<Bean> paginate(Config config, Connection conn, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) throws SQLException {
        return dbPro.paginate(config, conn, pageNumber, pageSize, select, sqlExceptSelect, paras);
    }
    
    /**
     * @see #paginate(String, int, int, String, String, Object...)
     */
    public static Page<Bean> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
        return dbPro.paginate(pageNumber, pageSize, select, sqlExceptSelect, paras);
    }
    
    /**
     * @see #paginate(String, int, int, String, String, Object...)
     */
    public static Page<Bean> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
        return dbPro.paginate(pageNumber, pageSize, select, sqlExceptSelect);
    }
    
    static boolean save(Config config, Connection conn, String tableName, String primaryKey, Bean bean) throws SQLException {
        return dbPro.save(config, conn, tableName, primaryKey, bean);
    }
    
    /**
     * Save record.
     * @param tableName the table name of the table
     * @param primaryKey the primary key of the table
     * @param bean the record will be saved
     * @param true if save succeed otherwise false
     */
    public static boolean save(String tableName, String primaryKey, Bean bean) {
        return dbPro.save(tableName, primaryKey, bean);
    }
    
    /**
     * @see #save(String, String, Bean)
     */
    public static boolean save(String tableName, Bean bean) {
        return dbPro.save(tableName, bean);
    }
    
    static boolean update(Config config, Connection conn, String tableName, String primaryKey, Bean bean) throws SQLException {
        return dbPro.update(config, conn, tableName, primaryKey, bean);
    }
    
    /**
     * Update Bean.
     * @param tableName the table name of the Bean save to
     * @param primaryKey the primary key of the table
     * @param bean the Bean object
     * @param true if update succeed otherwise false
     */
    public static boolean update(String tableName, String primaryKey, Bean bean) {
        return dbPro.update(tableName, primaryKey, bean);
    }
    
    /**
     * Update Bean. The primary key of the table is: "id".
     * @see #update(String, String, Bean)
     */
    public static boolean update(String tableName, Bean bean) {
        return dbPro.update(tableName, bean);
    }
    
    /**
     * @see #execute(String, ICallback)
     */
    public static Object execute(ICallback callback) {
        return dbPro.execute(callback);
    }
    
    /**
     * Execute callback. It is useful when all the API can not satisfy your requirement.
     * @param config the Config object
     * @param callback the ICallback interface
     */
    static Object execute(Config config, ICallback callback) {
        return dbPro.execute(config, callback);
    }
    
    /**
     * Execute transaction.
     * @param config the Config object
     * @param transactionLevel the transaction level
     * @param atom the atom operation
     * @return true if transaction executing succeed otherwise false
     */
    static boolean tx(Config config, int transactionLevel, IAtom atom) {
        return dbPro.tx(config, transactionLevel, atom);
    }
    
    public static boolean tx(int transactionLevel, IAtom atom) {
        return dbPro.tx(transactionLevel, atom);
    }
    
    /**
     * Execute transaction with default transaction level.
     * @see #tx(int, IAtom)
     */
    public static boolean tx(IAtom atom) {
        return dbPro.tx(atom);
    }
    
    /**
     * Find Bean by cache.
     * @see #find(String, Object...)
     * @param cacheName the cache name
     * @param key the key used to get date from cache
     * @return the list of Bean
     */
    public static List<Bean> findByCache(String cacheName, Object key, String sql, Object... paras) {
        return dbPro.findByCache(cacheName, key, sql, paras);
    }
    
    /**
     * @see #findByCache(String, Object, String, Object...)
     */
    public static List<Bean> findByCache(String cacheName, Object key, String sql) {
        return dbPro.findByCache(cacheName, key, sql);
    }
    
    /**
     * Paginate by cache.
     * @see #paginate(int, int, String, String, Object...)
     * @return Page
     */
    public static Page<Bean> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
        return dbPro.paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect, paras);
    }
    
    /**
     * @see #paginateByCache(String, Object, int, int, String, String, Object...)
     */
    public static Page<Bean> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select, String sqlExceptSelect) {
        return dbPro.paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect);
    }
    
    /**
     * @see #batch(String, String, Object[][], int)
     */
    public static int[] batch(String sql, Object[][] paras, int batchSize) {
        return dbPro.batch(sql, paras, batchSize);
    }
    
    /**
     * @see #batch(String, String, String, List, int)
     */
    public static int[] batch(String sql, String columns, List modelOrRecordList, int batchSize) {
        return dbPro.batch(sql, columns, modelOrRecordList, batchSize);
    }
    
    /**
     * @see #batch(String, List, int)
     */
    public static int[] batch(List<String> sqlList, int batchSize) {
        return dbPro.batch(sqlList, batchSize);
    }
    
	public static int[] batchBeans(List<BizModel> beans, int num) {
		List<Object[]> objs = new ArrayList<>();
		List<Object[]> objs2 = new ArrayList<>();
		String sql = null;
		String sql2 = null;
		for (BizModel m : beans) {
			if (!m.getAttrs().containsKey(m.getTable().getPrimaryKey())) {
				Map<String, Object[]> sl = m.insertSql();
				sql = (String) sl.keySet().toArray()[0];
				objs.add(sl.get(sql));
			} else {
				Map<String, Object[]> sl = m.updateSql();
				sql2 = (String) sl.keySet().toArray()[0];
				objs2.add(sl.get(sql2));
			}
		}
		int[] inserts = {}, updates = {};
		if (objs.size() > 0) {
			inserts = batch(sql, toObjs(objs), num);
		}
		if (objs2.size() > 0) {
			updates = batch(sql2, toObjs(objs2), num);
		}
		return contact(inserts, updates);
	}
    public static Object[][] toObjs(List<Object[]> list){
    	Object[][] objs = new Object[list.size()][];
    	for (int i = 0; i < list.size(); i++) {
			objs[i] = list.get(i);
		}
    	return objs;
    }
	public static int[] contact(int a[], int b[]) {
		int[] f = new int[a.length + b.length];
		for (int i = 0; i < f.length; i++)
			if (i < a.length)
				f[i] = a[i];
			else
				f[i] = b[i - a.length];
		return f;
	}
}
