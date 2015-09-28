package com.mk.ots.ticket.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.BPrize;

public interface BPrizeDao extends BaseDao<BPrize, Long> {
	public List<BPrize> findBPrizeByActiveid(Long activeid,boolean flag);
	
	public void saveOrUpdate(BPrize bPrize);
	
	/**
	 * 根据奖品id查询该奖品的详细信息
	 * @param prizeId
	 * @return
	 */
	public BPrize findPrizeById(long prizeId);
	
}
