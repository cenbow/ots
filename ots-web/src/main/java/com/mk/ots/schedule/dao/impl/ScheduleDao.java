package com.mk.ots.schedule.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.framework.schedule.model.SchedulePlan;
import com.mk.ots.schedule.dao.IScheduleDao;

@Component
public class ScheduleDao extends MyBatisDaoImpl<SchedulePlan, Long> implements IScheduleDao{
}
