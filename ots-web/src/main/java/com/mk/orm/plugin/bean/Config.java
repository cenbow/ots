/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mk.orm.kit.StrKit;
import com.mk.orm.plugin.bean.cache.ICache;
import com.mk.orm.plugin.bean.dialect.Dialect;
import com.mk.orm.plugin.bean.dialect.MysqlDialect;

/**
 * Config
 *
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class Config {

	String name;

	// private final ThreadLocal<Connection> threadLocal = new
	// ThreadLocal<Connection>();

	DataSource dataSource;
	int transactionLevel = Connection.TRANSACTION_READ_COMMITTED;

	ICache cache = null;
	boolean showSql = false;
	boolean devMode = false;
	Dialect dialect = new MysqlDialect();

	IContainerFactory containerFactory = new IContainerFactory() {
		@Override
		public Map<String, Object> getAttrsMap() {
			return new HashMap<String, Object>();
		}

		@Override
		public Map<String, Object> getColumnsMap() {
			return new HashMap<String, Object>();
		}

		@Override
		public Set<String> getModifyFlagSet() {
			return new HashSet<String>();
		}
	};

	/**
	 * For DbKit.brokenConfig = new Config();
	 */
	Config() {
	}

	/**
	 * Constructor with DataSource
	 *
	 * @param dataSource
	 *            the dataSource, can not be null
	 */
	public Config(String name, DataSource dataSource) {
		if (StrKit.isBlank(name)) {
			throw new IllegalArgumentException("Config name can not be blank");
		}
		if (dataSource == null) {
			throw new IllegalArgumentException("DataSource can not be null");
		}

		this.name = name.trim();
		this.dataSource = dataSource;
	}

	/**
	 * Constructor with DataSource and Dialect
	 *
	 * @param dataSource
	 *            the dataSource, can not be null
	 * @param dialect
	 *            the dialect, can not be null
	 */
	public Config(String name, DataSource dataSource, Dialect dialect) {
		if (StrKit.isBlank(name)) {
			throw new IllegalArgumentException("Config name can not be blank");
		}
		if (dataSource == null) {
			throw new IllegalArgumentException("DataSource can not be null");
		}
		if (dialect == null) {
			throw new IllegalArgumentException("Dialect can not be null");
		}

		this.name = name.trim();
		this.dataSource = dataSource;
		this.dialect = dialect;
	}

	/**
	 * Constructor with full parameters
	 *
	 * @param dataSource
	 *            the dataSource, can not be null
	 * @param dialect
	 *            the dialect, set null with default value: new MysqlDialect()
	 * @param showSql
	 *            the showSql,set null with default value: false
	 * @param devMode
	 *            the devMode, set null with default value: false
	 * @param transactionLevel
	 *            the transaction level, set null with default value:
	 *            Connection.TRANSACTION_READ_COMMITTED
	 * @param containerFactory
	 *            the containerFactory, set null with default value: new
	 *            IContainerFactory(){......}
	 * @param cache
	 *            the cache, set null with default value: new EhCache()
	 */
	public Config(String name, DataSource dataSource, Dialect dialect, Boolean showSql, Boolean devMode, Integer transactionLevel, IContainerFactory containerFactory, ICache cache) {
		if (StrKit.isBlank(name)) {
			throw new IllegalArgumentException("Config name can not be blank");
		}
		if (dataSource == null) {
			throw new IllegalArgumentException("DataSource can not be null");
		}

		this.name = name.trim();
		this.dataSource = dataSource;

		if (dialect != null) {
			this.dialect = dialect;
		}
		if (showSql != null) {
			this.showSql = showSql;
		}
		if (devMode != null) {
			this.devMode = devMode;
		}
		if (transactionLevel != null) {
			this.transactionLevel = transactionLevel;
		}
		if (containerFactory != null) {
			this.containerFactory = containerFactory;
		}
		if (cache != null) {
			this.cache = cache;
		}
	}

	public String getName() {
		return this.name;
	}

	public Dialect getDialect() {
		return this.dialect;
	}

	public ICache getCache() {
		return this.cache;
	}

	public int getTransactionLevel() {
		return this.transactionLevel;
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public IContainerFactory getContainerFactory() {
		return this.containerFactory;
	}

	public boolean isShowSql() {
		return this.showSql;
	}

	public boolean isDevMode() {
		return this.devMode;
	}

	// --------

	/**
	 * Support transaction with Transaction interceptor
	 */
	public final void setThreadLocalConnection(Connection connection) {
		// threadLocal.set(connection);
	}

	public final void removeThreadLocalConnection() {
		// threadLocal.remove();
	}

	/**
	 * Get Connection. Support transaction if Connection in ThreadLocal
	 */
	public final Connection getConnection() throws SQLException {
		boolean accquireConnPermitted = TransactionSynchronizationManager.getCurrentTransactionName() != null;
		if (!accquireConnPermitted) {
			throw new RuntimeException("DB Connection can be accquired only in service class(@Service).");
		}
		return DataSourceUtils.getConnection(this.getDataSource());
	}

	/**
	 * Helps to implement nested transaction. Tx.intercept(...) and Db.tx(...)
	 * need this method to detected if it in nested transaction.
	 */
	public final Connection getThreadLocalConnection() {
		ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(this.getDataSource());
		if (conHolder.getConnection() != null) {
			return conHolder.getConnection();
		}
		return null;
		// return threadLocal.get();
	}

	/**
	 * Close ResultSet、Statement、Connection ThreadLocal support declare
	 * transaction.
	 */
	public final void close(ResultSet rs, Statement st, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
			}
		}

		// if (threadLocal.get() == null) { // in transaction if conn in
		// // threadlocal
		// if (conn != null) {
		// try {
		// conn.close();
		// } catch (SQLException e) {
		// throw new BeanException(e);
		// }
		// }
		// }
	}

	public final void close(Statement st, Connection conn) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
			}
		}

		// if (threadLocal.get() == null) { // in transaction if conn in
		// // threadlocal
		// if (conn != null) {
		// try {
		// conn.close();
		// } catch (SQLException e) {
		// throw new BeanException(e);
		// }
		// }
		// }
	}

	public final void close(Connection conn) {
		// if (threadLocal.get() == null) // in transaction if conn in
		// threadlocal
		// if (conn != null)
		// try {
		// conn.close();
		// } catch (SQLException e) {
		// throw new BeanException(e);
		// }
	}
}
