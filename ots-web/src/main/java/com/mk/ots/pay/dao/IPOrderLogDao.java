package com.mk.ots.pay.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PmsSendEnum;
import com.mk.ots.pay.model.POrderLog;

public interface IPOrderLogDao extends BaseDao<POrderLog, Long> {
	
	public void saveOrUpdate(POrderLog pOrderLog);
	
	public POrderLog findPOrderLogByPay(Long payId) ;
	
	public boolean updateByPayRefund(Long payId);

	public abstract void updatePmsSendStatusById(Long id, PmsSendEnum pmssend, String reason);

	public abstract void updatePmsRefundStatusById(Long id, PmsSendEnum pmssend, String reason);
	
}
