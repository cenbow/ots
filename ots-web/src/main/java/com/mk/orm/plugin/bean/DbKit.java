/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * DbKit
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class DbKit {
    
    /**
     * The main Config object for system
     */
    static Config config = null;
    
    /**
     * For Model.getAttrsMap()/getModifyFlag() and Bean.getColumnsMap()
     * while the BeanPlugin not start or the Exception throws of HashSessionManager.restorSession(..) by Jetty
     */
    static Config brokenConfig = new Config();
    
    private static Map<Class<? extends Model>, Config> modelToConfig = new HashMap<Class<? extends Model>, Config>();
    private static Map<String, Config> configNameToConfig = new HashMap<String, Config>();
    
    static final Object[] NULL_PARA_ARRAY = new Object[0];
    public static final String MAIN_CONFIG_NAME = "main";
    
    private DbKit() {}
    
    /**
     * Add Config object
     * @param config the Config contains DataSource, Dialect and so on
     */
    public static void addConfig(Config config) {
        if (config == null)
            throw new IllegalArgumentException("Config can not be null");
        if (configNameToConfig.containsKey(config.getName()))
            throw new IllegalArgumentException("Config already exists: " + config.getName());
        
        configNameToConfig.put(config.getName(), config);
        
        /** 
         * Replace the main config if current config name is MAIN_CONFIG_NAME
         */
        if (MAIN_CONFIG_NAME.equals(config.getName()))
            DbKit.config = config;
        
        /**
         * The configName may not be MAIN_CONFIG_NAME,
         * the main config have to set the first comming Config if it is null
         */
        if (DbKit.config == null)
            DbKit.config = config;
    }
    
    static void addModelToConfigMapping(Class<? extends Model> modelClass, Config config) {
        modelToConfig.put(modelClass, config);
    }
    
    public static Config getConfig() {
        return config;
    }
    
    public static Config getConfig(String configName) {
        return configNameToConfig.get(configName);
    }
    
    public static Config getConfig(Class<? extends Model> modelClass) {
        return modelToConfig.get(modelClass);
    }
    
    static final void closeQuietly(ResultSet rs, Statement st) {
        if (rs != null) {try {rs.close();} catch (SQLException e) {}}
        if (st != null) {try {st.close();} catch (SQLException e) {}}
    }
    
    static final void closeQuietly(Statement st) {
        if (st != null) {try {st.close();} catch (SQLException e) {}}
    }
    
    public static String replaceFormatSqlOrderBy(String sql) {
        sql = sql.replaceAll("(\\s)+", " ");
        int index = sql.toLowerCase().lastIndexOf("order by");
        if (index > sql.toLowerCase().lastIndexOf(")")) {
            String sql1 = sql.substring(0, index);
            String sql2 = sql.substring(index);
            sql2 = sql2.replaceAll("[oO][rR][dD][eE][rR] [bB][yY] [\u4e00-\u9fa5a-zA-Z0-9_.]+((\\s)+(([dD][eE][sS][cC])|([aA][sS][cC])))?(( )*,( )*[\u4e00-\u9fa5a-zA-Z0-9_.]+(( )+(([dD][eE][sS][cC])|([aA][sS][cC])))?)*", "");
            return sql1 + sql2;
        }
        return sql;
    }
}
