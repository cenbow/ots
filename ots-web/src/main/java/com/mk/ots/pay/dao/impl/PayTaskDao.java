package com.mk.ots.pay.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.pay.dao.IPayTaskDao;
import com.mk.ots.pay.model.PPayTask;

@Component
public class PayTaskDao extends MyBatisDaoImpl<PPayTask, Long> implements IPayTaskDao  {

	@Override
	public List<PPayTask> selectInitTask(PayTaskTypeEnum taskType, PayTaskStatusEnum status) {
		
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("taskType", taskType.getId());
		param.put("status", status.getCode());
		
		return super.find("selectInitTask", param);
		
	}

	@Override
	public Long insertPayTask(PPayTask task) {
		
		return super.insert(task);
		
	}

	@Override
	public int updatePayTask(PayTaskStatusEnum status, List<PPayTask> tasks) {
		
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("status", status.getCode());
		param.put("tasks", tasks);
		
		return update("updatePayTask", param);
		
	}
	
	public boolean exist(Long orderId, PayTaskTypeEnum taskType) {

		Map<String, Object> param = new HashMap<String, Object>();

		param.put("orderId", orderId);
		param.put("taskType", taskType.getId());

		return find("exist", param).size() == 0 ? false : true;
	}

}
