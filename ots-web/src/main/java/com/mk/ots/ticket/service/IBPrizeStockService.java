package com.mk.ots.ticket.service;

import java.util.List;

import com.mk.ots.ticket.model.BPrizeInfo;

public interface IBPrizeStockService {

	/**
	 * 查询该奖品的详细信息
	 * @param prizeId
	 * @return
	 */
	public List<BPrizeInfo> queryMyThirdpartyPrize(BPrizeInfo prizeInfo);
}
