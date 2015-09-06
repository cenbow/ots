package com.mk.framework.component.cache;
//package com.mk.ots.cache;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.net.UnknownHostException;
//import java.util.List;
//
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//
//import com.mk.ots.component.cache.DBCacheManager;
//
//public abstract class IBaseCacheDAO<T> {
//	
//	private Class<T> classzz;
//	
//	private DBCacheManager cacheManager;
//	/**
//	 * 操作mongodb库模板类 
//	 */
//	protected MongoTemplate mongoTemplate;
//	
//	public IBaseCacheDAO() throws UnknownHostException {
//		Type genType = getClass().getGenericSuperclass();
//		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
//		classzz = (Class) params[0];
//		
//		cacheManager = DBCacheManager.getInstance();
//	}
//	
//	/**
//	 * 根据条件查询数据记录
//	 * @param query 查询条件
//	 * @return
//	 */
//	public List<T> find(Query query) {
//		return cacheManager.find(query, this.classzz);
//	}
//
//	/**
//	 * 根据条件查询单条记录
//	 * @param query 查询条件
//	 * @return
//	 */
//	public T findOne(Query query) {
//		return cacheManager.findOne(query, classzz);
//	}
//
//	/**
//	 * 根据查询条件更新记录数据
//	 * @param query
//	 * @param update
//	 */
//	public void update(Query query, Update update) {
//		cacheManager.update(query, update, classzz);
//	}
//
//	/**
//	 * 添加数据
//	 * @param bean
//	 * @return
//	 */
//	public T save(T bean) {
//		cacheManager.save(bean);
//		return bean;
//	}
//
//	/**
//	 * 删除数据
//	 * @param bean
//	 */
//	public void remove(T bean){
//		cacheManager.remove(bean);
//	}
//	
//	/**
//	 * 根据id获取单条记录
//	 * @param id¥
//	 * @return
//	 */
//	public T get(String id) {
//		return cacheManager.get(id, classzz);
//	}
//}