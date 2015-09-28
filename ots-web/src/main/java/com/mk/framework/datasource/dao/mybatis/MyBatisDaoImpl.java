package com.mk.framework.datasource.dao.mybatis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.mk.framework.datasource.dao.BaseDao;
import com.mk.framework.model.Page;
import com.mk.framework.util.ReflectionUtils;


/**
 * 封装mybatis原生API的DAO泛型基类.
 * 
 * @param <T>
 *            DAO操作的对象类型
 * @param <PK>
 *            主键类型
 * 
 * @author birkhoff
 */
@SuppressWarnings("unchecked")
public class MyBatisDaoImpl<T, PK extends Serializable> extends	SqlSessionDaoSupport implements BaseDao<T, PK> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected SqlSessionFactory sqlSessionFactory;

	/**
	 * Entity的类型
	 */
	protected Class<T> entityClass;

	/**
	 * Entity的主键类型
	 */
	protected Class<PK> pkClass;

	public String sqlMapNamespace = null;

	public static final String POSTFIX_INSERT = "insert";

	public static final String POSTFIX_UPDATE = "updateByPrimaryKeySelective";

	public static final String POSTFIX_DELETE = "deleteByPrimaryKey";

	public static final String POSTFIX_GET = "selectByPrimaryKey";

	public static final String POSTFIX_SELECT = "find";
	
	public static final String POSTFIX_SELECT_COUNT = "findCount";

	public static final String POSTFIX_SELECTPAGE = "findByPage";

	public static final String POSTFIX_SELECTPAGE_COUNT = "findByPageCount";

	/**
	 * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
	 * MyBatisDaoImpl<User, Long>
	 */
	public MyBatisDaoImpl() {
		this.entityClass = ReflectionUtils.getClassGenricType(getClass());
		this.pkClass = ReflectionUtils.getClassGenricType(getClass(), 1);
		this.sqlMapNamespace = entityClass.getName();
	}

	@Resource(name = "sqlSessionFactory")
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public String getSqlMapNamespace() {
		return sqlMapNamespace;
	}

	public PK insert(T entity) {
		try {
			getSqlSession().insert(sqlMapNamespace + "." + POSTFIX_INSERT,entity);
			return pkClass.getConstructor(String.class).newInstance(String.valueOf(ReflectionUtils.invokeGetter(entity, "id")));
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public int update(T entity) {
		try {
			getSqlSession().update(sqlMapNamespace + "." + POSTFIX_UPDATE,entity);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 1;
	}
	
	@Override
	public int update(final String ql, final Map<String, Object> map) {
		try {
			getSqlSession().update(sqlMapNamespace + "." + ql, map);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 1;
	}
	
	public int delete(PK id) {
		return getSqlSession().delete(sqlMapNamespace + "." + POSTFIX_DELETE, id);
	}
	@Override
	public  int delete(String ql, Object id){
		try {
			getSqlSession().delete(sqlMapNamespace + "." + ql, id);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 1;
	}
 
	@Override
	public int delete(final String ql, final Map<String, Object> map) {
		try {
			getSqlSession().delete(sqlMapNamespace + "." + ql, map);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 1;
	}
	
	public T findById(final PK id) {
		return (T) getSqlSession().selectOne(sqlMapNamespace + "." + POSTFIX_GET, id);
	}

	public List<T> find(final T entity) {
		return getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECT, entity);
	}
	
	public final long findCount(final T entity) {
		return getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECT, entity).size();
	}

	public T findOne(final String ql, final Map<String, Object> map){
		return getSqlSession().selectOne(sqlMapNamespace + "." + ql, map);
	}
	
	// mybtis执行复杂sql语句，并传入多个参数
	public List<T> find(final String ql, final Map<String, Object> map) {
		return getSqlSession().selectList(sqlMapNamespace + "." + ql, map);
	}
	
	public List findObjectList(final String ql, final Map<String, Object> map) {
		return getSqlSession().selectList(sqlMapNamespace + "." + ql, map);
	}
	
	public Page<T> find(final Page<T> page, final T entity) {
		RowBounds rowBounds = new RowBounds((page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
		page.setResult((List<T>) getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECTPAGE, entity, rowBounds));
		page.setTotalCount(findCount(POSTFIX_SELECTPAGE_COUNT, entity));
		return page;
	}
	
	public long findCount(final String ql, final T entity) {
		Long result = (Long) getSqlSession().selectOne(sqlMapNamespace + "." + ql, entity);
		return result != null ? result.longValue() : 0;
	}
	
	@Override
	public long count(final String ql, final Map<String, Object> map) {
		Long result = getSqlSession().selectOne(sqlMapNamespace + "." + ql, map);
		return result != null ? result.longValue() : 0;
	}
}
