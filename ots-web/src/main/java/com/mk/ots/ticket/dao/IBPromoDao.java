package com.mk.ots.ticket.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.BPromo;

public interface IBPromoDao extends BaseDao<BPromo, Long> {
	/**
	 * @param merchantid
	 * @return
	 */
	public BPromo findBPromoByPromo(String  promoPwd);
	
	/**
	 * @param bPrizeStock
	 */
	public void saveOrUpdate(BPromo bpromo);
	
	/**
	 * 根据code获取对象
	 * @param code
	 * @return
	 */
	public BPromo findBPromoById(long promoid);

	public void updateBpromoForUse(String promoPwd,int promoStatus,String  updateTime,String  updateBy) ;

}
