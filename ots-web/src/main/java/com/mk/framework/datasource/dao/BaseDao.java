package com.mk.framework.datasource.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mk.framework.model.Page;


/**
 * 封装原生API的DAO泛型接口.
 * 
 * 可由Hibernate/Mybatis具体实现.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 * @author birkhoff
 */
public interface BaseDao<T, PK extends Serializable> {
	
	/**
	 * 保存新增对象.
	 */
	public PK insert(final T entity);
	
	/**
	 * 根据ID删除对象.
	 * 
	 * @param id 对象的ID属性.
	 */
	
	public int delete(PK id);
	
	/**
	 * 保存修改的对象.
	 * 
	 * * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	
	public int update(final T entity);

	/**
	 * 按id获取对象.
	 */
	public T findById(final PK id);
	
	/**
	 * 查询对象列表.
	 * @return List<T> 查询结果对象列表
	 * 
	 * @param T 参数对象.
	 */
	public List<T> find(T entity);
	
	/**
	 * 查询对象列表的数量.
	 * @return 查询结果的数量
	 * 
	 * @param T 参数对象.
	 */
	
	public long findCount(T entity);
	
	/**
	 * 分页查询对象列表.
	 * @return Page<T> 查询结果的分页对象
	 * 
	 * @param page 分页参数对象 
	 * @param values 查询参数对象.
	 */
	public Page<T> find(Page<T> page, final T entity);
	
	/**
	 * @param ql
	 * @param entity
	 * @return
	 */
	public long findCount(final String ql, final T entity) ;

	
	public abstract int delete(final String ql,final Object id);

	public abstract int delete(final String ql, final Map<String, Object> map);

	public abstract int update(final String ql, final Map<String, Object> map);

	public abstract long count(final String ql, final Map<String, Object> map);

}
