package com.mk.ots.pay.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.pay.model.PPayStatusErrorOrder;

public interface IPayStatusErrorDao  extends BaseDao<PPayStatusErrorOrder, Long>  {
	
	public Long insertErrorOrder(PPayStatusErrorOrder errorOrder);
	
	public int deleteErrorOrder(Long orderId);

}
