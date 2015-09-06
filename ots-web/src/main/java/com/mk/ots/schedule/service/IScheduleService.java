package com.mk.ots.schedule.service;

import com.mk.framework.schedule.model.SchedulePlan;
import com.mk.framework.service.BaseService;

public interface IScheduleService extends BaseService<SchedulePlan, Long> {

	public abstract boolean runNow(Long id);
}
