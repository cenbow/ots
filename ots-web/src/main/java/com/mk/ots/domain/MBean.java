package com.mk.ots.domain;


import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.util.Cast;


public class MBean implements IBean {
    private static final long serialVersionUID = -3530352869815683174L;

    /** 数据map */
    private Map<String, Object> dataMap = Maps.newConcurrentMap();

    public MBean() {
	}

    public MBean(Map data) {
    	this.dataMap = data;
    }
    /**
     * {@inheritDoc}
     * 
     * @param iBean IBean
     * @return boolean
     */
    public boolean compare(IBean iBean) {
        boolean isEqual = true;
        for (String key : this.getKeys()) {
            isEqual = compare(iBean, key);
            if (!isEqual) {
                break;
            }
        }
        return isEqual;
    }

    /**
     * {@inheritDoc}
     * 
     * @param iBean IBean
     * @param keyCodes String
     * @return boolean
     */
    public boolean compare(IBean iBean, String keyCodes) {
        boolean isEqual = true;
        String[] keyArray = keyCodes.split(",");
        for (String key : keyArray) {
            if (key!=null && key.trim().length()>0) {
                String dataVal = Cast.to(get(key), "");
                if (!dataVal.equals(Cast.to(iBean.get(key), ""))) {
                    isEqual = false;
                    break;
                }
            }
        }
        return isEqual;
    }

    /**
     * {@inheritDoc}
     * 
     * @param key String
     * @return boolean
     */
    public boolean contains(String key) {
        return this.dataMap.containsKey(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @param key String
     * @return Object
     */
    public Object get(String key) {
        return this.dataMap.get(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @return List<String>
     */
    public List<String> getKeys() {
        return Lists.newArrayList(this.dataMap.keySet());
    }

    /**
     * {@inheritDoc}
     * 
     * @return boolean
     */
    public boolean isEmpty() {
        return this.dataMap.size() == 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @param key String
     * @param obj Object
     */
    public void set(String key, Object obj) {
        this.dataMap.put(key, obj);
    }

    /**
     * 获取字符串型字段值
     * 
     * @param key 编码字段
     * @param def 缺省值
     * @return 字符串值
     */
    public String get(String key, String def) {
        return Cast.to(get(key), def);
    }

    /**
     * 获取整型字段值
     * 
     * @param key 编码字段
     * @param def 缺省值
     * @return 整型字段值
     */
    public int get(String key, int def) {
        return Cast.to(get(key), def);
    }

    /**
     * 获取双精度浮点型字段值
     * 
     * @param key 编码字段
     * @param def 缺省值
     * @return 双精度浮点型字段值
     */
    public double get(String key, double def) {
        return Cast.to(get(key), def);
    }

    /**
     * 获取布尔型字段值
     * 
     * @param key 编码字段
     * @param def 缺省值
     * @return 布尔型字段值
     */
    public boolean get(String key, boolean def) {
        return Cast.to(get(key), def);
    }

    @Override
    public Object remove(String key) {
    	return this.dataMap.remove(key);
    }
    
    @Override
	public void clear(){
    	this.dataMap.clear();
    }
    
	@Override
    public String toString() {
        ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
        for(String key: this.dataMap.keySet()){
        	stringHelper.add(key, get(key));
        }
		return stringHelper.toString();
    }
	@Override
	public String toJson() {
		try {
			return new ObjectMapper().writeValueAsString(this.dataMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}
}