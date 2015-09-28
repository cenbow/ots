/**
 * Copyright (c) 2014-2015, 上海乐住信息技术有限公司(http://www.imike.com).
 */
package com.mk.orm.plugin.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * TableMapping
 * @author IT Group of imike beijing(aiqing.chu@imike.com).
 *
 */
public class TableMapping {
    
    private final Map<Class<? extends Model<?>>, Table> modelToTableMap = new HashMap<Class<? extends Model<?>>, Table>();
    
    private static TableMapping me = new TableMapping(); 
    
    private TableMapping() {}
    
    public static TableMapping me() {
        return me;
    }
    
    public void putTable(Table table) {
        modelToTableMap.put(table.getModelClass(), table);
    }
    
    @SuppressWarnings("rawtypes")
    public Table getTable(Class<? extends Model> modelClass) {
        Table table = modelToTableMap.get(modelClass);
        if (table == null)
            throw new RuntimeException("The Table mapping of model: " + modelClass.getName() + " not exists. Please add mapping to BeanPlugin: beanplugin.addMapping(tableName, YourModel.class).");
        
        return table;
    }
}
