package com.mk.ots.pay.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PPayInfoTypeEnum;
import com.mk.ots.pay.model.PPayInfo;

public interface IPPayInfoDao extends BaseDao<PPayInfo, Long>{
	
	public PPayInfo saveOrUpdate(PPayInfo iPPayInfo);
	
	public PPayInfo findById(long id);
	
	public PPayInfo getPPayInfoByPayidAndPayOk(long payid);
	
	/**得到用优惠券的流水*/
	public PPayInfo getPPayInfoByCoupon(long payid);
	
	public PPayInfo getPPayInfoByPayid(String payid);
	
	/**
	 * 看是否有流水存在，防止重复提交
	 * @param payid
	 * @return
	 */
	public boolean findPPayInfoByPayid(String payid);
	
	public List<PPayInfo> findByPayId(long id);
	
	public void deletePayInfoByPayid(long payid);
	
	/**
	 * 支付宝退款成功后修改本地数据库数据
	 * @param payids
	 */
	public void aliPayRefundSuccess(String  payids);
	
	
	public PPayInfo  getPayOk(String  otherno);
	
	public PPayInfo getPPayInfoByRefund(long payid);
	
	
	public PPayInfo getPPayInfo(long payid,PPayInfoTypeEnum type);
	/**
	 * 判断是否是有支付流水
	 * @param payids
	 */
	public PPayInfo selectByOrderIdAndPayOk(String  orderid);
	
	public void updatePmsSendIdByPayId(Long payid,Long payinfoid);
	
	public void updatePmsSendIdById(Long id, Long pmsSendId);
	
	public void deleteById(Long id);
	
}
