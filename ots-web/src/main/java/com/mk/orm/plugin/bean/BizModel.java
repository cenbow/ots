package com.mk.orm.plugin.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mk.framework.AppUtils;
import com.mk.framework.util.StringUtils;
import com.mk.ots.manager.OtsCacheManager;

/**
 * BizModel: 所有业务相关的Model都要继承该抽象类
 * 
 * @author chuaiqing.
 * @param <M>
 */
@Component
public abstract class BizModel<M extends Model<M>> extends Model<M> {
	
    // comment by chuaiqing at 2015-05-17 11:12:00
	////protected static String cacheSchema = "MIKE:JFINAL:";

	/**
     *
     */
	private static final long serialVersionUID = 7504464990216253793L;

	private static Logger logger = LoggerFactory.getLogger(BizModel.class);

	@PostConstruct
	private void postConstruct() {
	}

	/**
	 * 获取Model的映射表对象
	 * 
	 * @return Table
	 */
	public Table getTable() {
		return TableMapping.me().getTable(this.getClass());
	}

	/**
	 * 获取自定义主键值
	 * 
	 * @return String
	 */
	public Object getPKValue() {
		return this.get(this.getTable().getPrimaryKey());
	}

	/**
	 * 重写save方法，支持RedisCache
	 */
	@Override
	public boolean save() {
//		boolean result = false;
//		try {
			// 如果明确声明了使用UUID，则程序生成UUID作为主键值
			if ((this.getInt("$USE_UUID$") != null) && (1 == this.getInt("$USE_UUID$"))) {
				// 获得UUID
				String uuid = StringUtils.getUuidByJdk(true);
				// 保存前给当前bean设置pk
				this.set(this.getTable().getPrimaryKey(), uuid);
			}
			return super.save();
			
			// comment by chuaiqing at 2015-05-17 11:12:00
			// 保存数据时不再往redis缓存
			////if (result) {
			////	// put data to RedisCache
			////	this.getOtsCacheManager().put(cacheSchema+this.getTable().getName(), this.getPKValue(), this);
			////}
//		} catch (Exception e) {
//			BizModel.logger.error("BizModel save method error:\n" + e.getMessage());
//		}
//		return result;
	}

	//// comment by chuaiqing at 2015-05-21
//	/**
//	 * 重写update方法，支持RedisCache
//	 */
//	@Override
//	public boolean update() {
//		boolean result = false;
//		try {
//			result = super.update();
//			// comment by chuaiqing at 2015-05-17 11:12:00
//            // 保存数据时不再往redis缓存
//			////if (result) {
//			////	this.getOtsCacheManager().put(cacheSchema+this.getTable().getName(), this.getPKValue(), this);
//			////}
//		} catch (Exception e) {
//			BizModel.logger.error("BizModel update method error:\n" + e.getMessage());
//		}
//		return result;
//	}

	/**
	 * 重写delete方法，支持RedisCache
	 */
	@Override
	public boolean delete() {
		return this.deleteById(this.getPKValue());
	}

	/**
	 * 重写deleteById方法，支持RedisCache
	 */
	@Override
	public boolean deleteById(Object id) {
	    return super.deleteById(id);
//		boolean result = false;
//		try {
//			result = super.deleteById(id);
//			// comment by chuaiqing at 2015-05-17 11:12:00
//            // 删除数据时不再往redis缓存
//			////if (result) {
//			////	// remove from RedisCache
//			////	this.getOtsCacheManager().remove(cacheSchema+this.getTable().getName(), id);
//			////}
//		} catch (Exception e) {
//			BizModel.logger.error("BizModel method deleteById error:\n" + e.getMessage());
//		}
//		return result;
	}

	/**
	 * 重写findByCache方法，支持RedisCache
	 */
	@Override
	public List<M> findByCache(String cacheName, Object key, String sql, Object... paras) {
		List<M> result = null;
		try {
			result = (List<M>) this.getOtsCacheManager().get(cacheName, key);
			if (result == null) {
				result = super.find(sql, paras);
				// put result data to RedisCache
				this.getOtsCacheManager().put(cacheName, key, result);
			}
		} catch (Exception e) {
			BizModel.logger.error("BizModel findByCache method error:\n" + e.getMessage());
		}
		return result;
	}

	/**
	 * 重写findByCache方法，支持RedisCache
	 */
	@Override
	public List<M> findByCache(String cacheName, Object key, String sql) {
		return this.findByCache(cacheName, key, sql, DbKit.NULL_PARA_ARRAY);
	}

	// comment by chuaiqing at 2015-05-17 11:12:00
    // 按照主键查询数据时不再往redis缓存
	/*
	@Override
	public M findById(Object id) {
		M obj = (M) this.getOtsCacheManager().get(cacheSchema+this.getTable().getName(), id);
		if (obj == null) {
		    obj = super.findById(id); 
		    this.getOtsCacheManager().put(cacheSchema+this.getTable().getName(), id, obj);
		}
		return obj;
	}
	*/
	
	// comment by chuaiqing at 2015-05-17 11:12:00
    // 不再往redis缓存
	public List<M> findByIds(List<Long> ids) {
		List<M> ms = new ArrayList<>();
		StringBuffer _ids = new StringBuffer();
		for (Long id : ids) {
		    //// modify by chuaiqing : begin
		    /*
			M obj = (M) this.getOtsCacheManager().get(cacheSchema + this.getTable().getName(), id);
			if (obj == null) {
				_ids.append(id).append(",");
			} else {
				ms.add(obj);
			}
			*/
		    _ids.append(id).append(",");
		    //// modify by chuaiqing : end
		}
		// 是否有需要db的
		if (_ids.length() > 0) {
			_ids.setLength(_ids.length() - 1);
			// 查找cache里没有的数据
			List<M> _ms = find("select * from " + this.getTable().getName() + " where " + this.getTable().getPrimaryKey() + " in(?)", _ids.toString());
			//// modify by chuaiqing : begin
			/*
			for (M m : _ms) {
				// 放入缓存里
				this.getOtsCacheManager().put(cacheSchema + this.getTable().getName(), m.get(this.getTable().getPrimaryKey()), m);
			}
			*/
			//// modify by chuaiqing : end
			ms.addAll(_ms);
		}
		return ms;
	}
	
	private OtsCacheManager getOtsCacheManager(){
		return AppUtils.getBean(OtsCacheManager.class);
	}
}
