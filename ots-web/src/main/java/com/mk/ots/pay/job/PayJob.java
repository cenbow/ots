package com.mk.ots.pay.job;

import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;

public interface PayJob {
	
	public void doJob(PayTaskTypeEnum taskType, PayTaskStatusEnum currentStatus, PayTaskStatusEnum afterStatus);

}
