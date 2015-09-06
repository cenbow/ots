package com.mk.ots.log.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.log.model.BLogOrder;

/**
 * @author nolan
 *
 */
public interface IBLogOrderDao extends BaseDao<BLogOrder, Long>{

	public abstract BLogOrder log(Long orderid, String oldValue, String newValue,
			String note);

	public abstract BLogOrder log(Long orderid, String oldValue, String newValue);

}
