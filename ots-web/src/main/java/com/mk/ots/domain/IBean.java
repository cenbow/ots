package com.mk.ots.domain;

import java.io.Serializable;
import java.util.List;


public interface IBean extends Serializable {
	/**
	 * 获取key对应的值,如果没有返回null
	 * 
	 * @param key
	 *            key
	 * @return rtnObj rtnObj
	 */
	Object get(String key);

	/**
	 * 存储Key-Value(名-值)对
	 * 
	 * @param key
	 *            key
	 * @param obj
	 *            value
	 */
	void set(String key, Object obj);

	/**
	 * 获取所有key
	 * 
	 * @return rtnKeys rtnKeys/null
	 */
	List<String> getKeys();

	/**
	 * 比较两个Bean中的字段值 内容
	 * 
	 * @param ibean
	 *            被比较Bean
	 * @return 字段值内容是否相同
	 */
	boolean compare(IBean ibean);

	/**
	 * 比较两个Bean中的指定字段值内容
	 * 
	 * @param iBean
	 *            被比较Bean
	 * @param keyCodes
	 *            指定字段
	 * @return 字段值内容是否相同
	 */
	boolean compare(IBean iBean, String keyCodes);

	/**
	 * 检查该BEAN中是否存在此编码
	 * 
	 * @param key
	 *            编码
	 * @return 编码对应值
	 */
	boolean contains(String key);

	/**
	 * 是否为空
	 * 
	 * @return true/false
	 */
	boolean isEmpty();

	/**
	 * 获取字符串型字段值
	 * 
	 * @param key
	 *            编码字段
	 * @param def
	 *            缺省值
	 * @return 字符串值
	 */
	String get(String key, String def);

	/**
	 * 获取整型字段值
	 * 
	 * @param key
	 *            编码字段
	 * @param def
	 *            缺省值
	 * @return 整型字段值
	 */
	int get(String key, int def);

	/**
	 * 获取双精度浮点型字段值
	 * 
	 * @param key
	 *            编码字段
	 * @param def
	 *            缺省值
	 * @return 双精度浮点型字段值
	 */
	double get(String key, double def);
	
	/**
	 * 获取布尔型字段值
	 * 
	 * @param key
	 *            编码字段
	 * @param def
	 *            缺省值
	 * @return 布尔型字段值
	 */
	boolean get(String key, boolean def);

	/**
	 * @param key
	 * @return
	 */
	Object remove(String key);
	/**
	 * @return
	 */
	public String toString() ;
	
	/**
	 * @return
	 */
	public String toJson();

	/**
	 * 清除所有内容
	 */
	public void clear();
}