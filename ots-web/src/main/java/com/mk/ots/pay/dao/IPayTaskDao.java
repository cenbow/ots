package com.mk.ots.pay.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.pay.model.PPayTask;

public interface IPayTaskDao  extends BaseDao<PPayTask, Long> {
	
	public List<PPayTask> selectInitTask(PayTaskTypeEnum taskType, PayTaskStatusEnum status);
	
	public Long insertPayTask(PPayTask task);
	
	public int updatePayTask(PayTaskStatusEnum status, List<PPayTask> tasks);
	
	public boolean exist(Long orderId, PayTaskTypeEnum taskType);

}
