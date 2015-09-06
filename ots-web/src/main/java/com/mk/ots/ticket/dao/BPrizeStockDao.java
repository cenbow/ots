package com.mk.ots.ticket.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.BPrizeStock;
import com.mk.ots.ticket.model.UTicket;

public interface BPrizeStockDao extends BaseDao<BPrizeStock, Long> {
	/**
	 * @param merchantid
	 * @return
	 */
	public BPrizeStock findBPrizeStockByPrizeid(Long prizeid,Long activeid);
	
	/**
	 * @param bPrizeStock
	 */
	public void saveOrUpdate(BPrizeStock bPrizeStock); 
	
	/**
	 * 根据code获取对象
	 * @param code
	 * @return
	 */
	public BPrizeStock findBPrizeStockById(long bPrizeStockId);
	
	
	/**
	 * 根据prizeid和status获取库存表中某一类三方券的库存量
	 * @param prizeid
	 * @param status 0：未使用券；1：已使用券
	 * @return
	 */
	public long findStockCountByPrizeIDAndStatus(long prizeid,long status);
}
