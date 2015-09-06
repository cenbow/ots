package com.mk.ots.pay.dao.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PmsSendEnum;
import com.mk.ots.pay.dao.IPOrderLogDao;
import com.mk.ots.pay.model.POrderLog;

@Service
public class POrderLogDAO extends MyBatisDaoImpl<POrderLog, Long> implements IPOrderLogDao{
	
	public POrderLog selectByPayId(Long payId){
		return POrderLog.dao.findFirst("select * from p_orderlog where payid=?", payId);
	}
	
	@Override
	public void saveOrUpdate(POrderLog pOrderLog) {
		if(pOrderLog.getId()!=null){
			this.update(pOrderLog);
		}else{
			this.insert(pOrderLog);
		}
	}
	
	@Override
	public POrderLog findPOrderLogByPay(Long payid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("payid", payid);
		return this.findOne("findPOrderLogByPay", param);
	}

	@Override
	public boolean updateByPayRefund(Long payid){
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", payid);
		if(this.update("updateByPayRefund", map)>0){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void updatePmsSendStatusById(Long id, PmsSendEnum pmssend, String sendreason){
		POrderLog pOrderLog = new POrderLog();
		pOrderLog.setId(id);
		pOrderLog.setPmssend(pmssend);
		pOrderLog.setPmssendtime(new Date());
		pOrderLog.setSendreason(sendreason);
		this.update(pOrderLog);
	}
	
	@Override
	public void updatePmsRefundStatusById(Long id, PmsSendEnum pmssend, String refundreason){
		POrderLog pOrderLog = new POrderLog();
		pOrderLog.setId(id);
		pOrderLog.setPmsrefund(pmssend);
		pOrderLog.setPmsrefundtime(new Date());
		pOrderLog.setRefundreason(refundreason);
		this.update(pOrderLog);
	}

	public boolean updatePayLogQiekeIncome(Long orderid,int status){
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderid", orderid);
		if(2==status){
			map.put("qiekeIncome", 10);
		} else {
			map.put("qiekeIncome", 20);
		}
		if(this.update("updatePayLogQiekeIncome", map)>0){
			return true;
		}else{
			return false;
		}
	}
}
