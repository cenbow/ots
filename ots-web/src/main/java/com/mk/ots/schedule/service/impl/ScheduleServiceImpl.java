package com.mk.ots.schedule.service.impl;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.framework.schedule.IScheduleEvent;
import com.mk.framework.schedule.ScheduleService;
import com.mk.framework.schedule.model.SchedulePlan;
import com.mk.framework.service.impl.BaseServiceImpl;
import com.mk.ots.schedule.dao.IScheduleDao;
import com.mk.ots.schedule.service.IScheduleService;
import com.mk.ots.ticket.controller.TicketController;

@Service
public class ScheduleServiceImpl extends BaseServiceImpl<SchedulePlan, Long> implements IScheduleService {
	
	final Logger logger = LoggerFactory.getLogger(TicketController.class);
	
	@Autowired
	private IScheduleDao iScheduleDao;
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Override
	public BaseDao<SchedulePlan, Long> getDao() {
		return iScheduleDao;
	}

	@Override
	public Long insert(SchedulePlan entity) {
		Long id = super.insert(entity);
		try {
			Class<IScheduleEvent> cls = (Class<IScheduleEvent>) Class.forName(entity.getScript());
			if(entity.getActive()){
				scheduleService.addSchedule(String.valueOf(id), entity.getName(), null, null, entity.getExpression(), cls.newInstance());
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.error("添加调度计划任务时，往调度容器中注册失败. info:{}", entity.toString());
		}
		return id;
	}
	
	@Override
	public int delete(Long id) {
		SchedulePlan entity = super.findById(id);
		super.delete(id);
		try {
			if (entity.getActive()) {
                scheduleService.deleteSchedule(entity.getId().toString());
            }
		} catch (SchedulerException e) {
			logger.error("删除调度计划任务时发生错误. id:{}", id);
		}
		return id.intValue();
	}
	
	@Override
	public int update(SchedulePlan plan) {
		super.update(plan);
        try {
        	plan = super.findById(plan.getId());
			if (plan.getActive()) {
				Class<IScheduleEvent> cls = (Class<IScheduleEvent>) Class.forName(plan.getScript());
			    // 判断容器中是否存在此调度
			    if (scheduleService.exist(plan.getId().toString())) {
			        scheduleService.updateSchedule(plan.getId().toString(), plan.getName(), null, null, plan.getExpression(), cls.newInstance());
			    } else {
			        // B.注册新调度
			    	scheduleService.addSchedule(plan.getId().toString(), plan.getName(), null, null, plan.getExpression(), cls.newInstance());
			    }
			} else {// 非启动状态
			        // 判断容器中是否存在此调度，存在则删除.
			    if (scheduleService.exist(plan.getId().toString())) {
			        scheduleService.deleteSchedule(plan.getId().toString());
			    }
			}
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | SchedulerException e) {
			logger.error("更新调度计划任务时发生错误. info:{}", plan.toString());
		}
        return plan.getId().intValue();
	}
	
	@Override
	public boolean runNow(Long id){
		SchedulePlan plan = super.findById(id);
		try {
			Class<IScheduleEvent> cls = (Class<IScheduleEvent>) Class.forName(plan.getScript());
			scheduleService.runNow(plan.getId().toString(), plan.getName(), null, null, plan.getExpression(), cls.newInstance());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | SchedulerException e) {
			logger.error("立即执行调度计划任务时发生错误. info:{}", plan.toString());
			return false;
		}
		return true;
	}
	 
}
