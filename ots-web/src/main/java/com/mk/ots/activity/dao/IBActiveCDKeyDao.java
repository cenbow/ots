package com.mk.ots.activity.dao;

import java.util.Date;
import java.util.List;

import com.google.common.base.Optional;
import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.activity.model.BActiveCDKey;

/**
 * 兑换码
 * @author nolan
 *
 */
public interface IBActiveCDKeyDao extends BaseDao<BActiveCDKey, Long> {

	/**
	 * 修改兑换码为已使用状态
	 * @param code
	 */
	public abstract void useBActiveCDKey(String code);

	/**
	 * 查询兑换码明细
	 * @param code
	 * @return
	 */
	public abstract Optional<BActiveCDKey> getBActiveCDKey(String code);

	public abstract Long getCDKeyNum(Long activeid, Long channelid, Long promotionid);
	
	public abstract Long getNotUseCDKeyNum(Long activeid, Long channelid, Long promotionid);

	/**
	 * 批量生成码
	 * @param batchNo
	 * @param activeid
	 * @param channelid
	 * @param promotionid
	 * @param expiration
	 * @param codeList
	 */
	public abstract void batchGenCode(String batchNo, Long activeid, Long channelid, Long promotionid, Date expiration, List<String> codeList);

	/**
	 * 查询生成码
	 * @param batchNo
	 * @param activeid
	 * @param channelid
	 * @return
	 */
	public abstract List<BActiveCDKey> getCDKeys(String batchNo, Long activeid, Long channelid);
	
	/**
	 * 检测此活下的某一批次是否存在
	 * @param activeid
	 * @param batchno
	 * @return
	 */
	public abstract boolean existBatchNo(Long activeid, String batchno);
}
