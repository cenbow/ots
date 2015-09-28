/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.mk.orm.kit.StrKit;
import com.mk.orm.plugin.IPlugin;
import com.mk.orm.plugin.bean.cache.ICache;
import com.mk.orm.plugin.bean.dialect.Dialect;


/**
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class BeanPlugin implements IPlugin {
    
    private String configName = DbKit.MAIN_CONFIG_NAME;
    private Config config = null;
    
    private DataSource dataSource;
    private IDataSourceProvider dataSourceProvider;
    private Integer transactionLevel = null;
    private ICache cache = null;
    private Boolean showSql = null;
    private Boolean devMode = null;
    private Dialect dialect = null;
    private IContainerFactory containerFactory = null;
    
    private boolean isStarted = false;
    private List<Table> tableList = new ArrayList<Table>();
    
    public BeanPlugin(Config config) {
        if (config == null)
            throw new IllegalArgumentException("Config can not be null");
        this.config = config;
    }
    
    public BeanPlugin(DataSource dataSource) {
        this(DbKit.MAIN_CONFIG_NAME, dataSource);
    }
    
    public BeanPlugin(String configName, DataSource dataSource) {
        this(configName, dataSource, Connection.TRANSACTION_READ_COMMITTED);
    }
    
    public BeanPlugin(DataSource dataSource, int transactionLevel) {
        this(DbKit.MAIN_CONFIG_NAME, dataSource, transactionLevel);
    }
    
    public BeanPlugin(String configName, DataSource dataSource, int transactionLevel) {
        if (StrKit.isBlank(configName))
            throw new IllegalArgumentException("configName can not be blank");
        if (dataSource == null)
            throw new IllegalArgumentException("dataSource can not be null");
        this.configName = configName.trim();
        this.dataSource = dataSource;
        this.setTransactionLevel(transactionLevel);
    }
    
    public BeanPlugin(IDataSourceProvider dataSourceProvider) {
        this(DbKit.MAIN_CONFIG_NAME, dataSourceProvider);
    }
    
    public BeanPlugin(String configName, IDataSourceProvider dataSourceProvider) {
        this(configName, dataSourceProvider, Connection.TRANSACTION_READ_COMMITTED);
    }
    
    public BeanPlugin(IDataSourceProvider dataSourceProvider, int transactionLevel) {
        this(DbKit.MAIN_CONFIG_NAME, dataSourceProvider, transactionLevel);
    }
    
    public BeanPlugin(String configName, IDataSourceProvider dataSourceProvider, int transactionLevel) {
        if (StrKit.isBlank(configName))
            throw new IllegalArgumentException("configName can not be blank");
        if (dataSourceProvider == null)
            throw new IllegalArgumentException("dataSourceProvider can not be null");
        this.configName = configName.trim();
        this.dataSourceProvider = dataSourceProvider;
        this.setTransactionLevel(transactionLevel);
    }
    
    public BeanPlugin addMapping(String tableName, String primaryKey, Class<? extends Model<?>> modelClass) {
        tableList.add(new Table(tableName, primaryKey, modelClass));
        return this;
    }
    
    public BeanPlugin addMapping(String tableName, Class<? extends Model<?>> modelClass) {
        tableList.add(new Table(tableName, modelClass));
        return this;
    }
    
    /**
     * Set transaction level define in java.sql.Connection
     * @param transactionLevel only be 0, 1, 2, 4, 8
     */
    public BeanPlugin setTransactionLevel(int transactionLevel) {
        int t = transactionLevel;
        if (t != 0 && t != 1  && t != 2  && t != 4  && t != 8)
            throw new IllegalArgumentException("The transactionLevel only be 0, 1, 2, 4, 8");
        this.transactionLevel = transactionLevel;
        return this;
    }
    
    public BeanPlugin setCache(ICache cache) {
        if (cache == null)
            throw new IllegalArgumentException("cache can not be null");
        this.cache = cache;
        return this;
    }
    
    public BeanPlugin setShowSql(boolean showSql) {
        this.showSql = showSql;
        return this;
    }
    
    public BeanPlugin setDevMode(boolean devMode) {
        this.devMode = devMode;
        return this;
    }
    
    public Boolean getDevMode() {
        return devMode;
    }
    
    public BeanPlugin setDialect(Dialect dialect) {
        if (dialect == null)
            throw new IllegalArgumentException("dialect can not be null");
        this.dialect = dialect;
        return this;
    }
    
    public BeanPlugin setContainerFactory(IContainerFactory containerFactory) {
        if (containerFactory == null)
            throw new IllegalArgumentException("containerFactory can not be null");
        this.containerFactory = containerFactory;
        return this;
    }
    
    public boolean start() {
        if (isStarted)
            return true;
        
        if (dataSourceProvider != null)
            dataSource = dataSourceProvider.getDataSource();
        if (dataSource == null)
            throw new RuntimeException("ActiveRecord start error: BeanPlugin need DataSource or DataSourceProvider");
        
        if (config == null)
            config = new Config(configName, dataSource, dialect, showSql, devMode, transactionLevel, containerFactory, cache);
        DbKit.addConfig(config);
        
        boolean succeed = TableBuilder.build(tableList, config);
        if (succeed) {
            Db.init();
            isStarted = true;
        }
        return succeed;
    }
    
    public boolean stop() {
        isStarted = false;
        return true;
    }
}
