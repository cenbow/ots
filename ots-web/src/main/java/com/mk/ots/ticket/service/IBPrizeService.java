package com.mk.ots.ticket.service;

import java.util.List;

import com.mk.ots.ticket.model.BPrizeInfo;

public interface IBPrizeService {


	/**
	 * 查询实物对象
	 * @param mid
	 * @param activeId
	 * @param prizeId
	 * @return
	 */
	public List<BPrizeInfo> queryMyMaterialPrize(BPrizeInfo prizeInfo);
}
