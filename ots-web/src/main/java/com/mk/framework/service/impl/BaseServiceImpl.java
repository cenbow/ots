package com.mk.framework.service.impl;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.framework.model.Page;
import com.mk.framework.service.BaseService;

@Transactional
public abstract class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK > {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public abstract BaseDao<T, PK> getDao();

	public PK insert(T entity) {
		return getDao().insert(entity);
	}

	public int delete(PK id) {
		return getDao().delete(id);
	}
	
	public int update(T entity) {
		return getDao().update(entity);
	}

	 
	public T findById(PK id) {
		return getDao().findById(id);
	}

	public List<T> find(T entity) {
		return getDao().find(entity);
	}
	
	public long findCount(T entity) {
		return getDao().findCount(entity);
	}

	public Page<T> find(Page<T> page, T entity) {
		return getDao().find(page, entity);
	}
}
