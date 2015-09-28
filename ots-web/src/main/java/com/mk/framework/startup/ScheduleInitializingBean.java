package com.mk.framework.startup;

import java.util.List;
import java.util.Map;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.framework.schedule.IScheduleEvent;
import com.mk.framework.schedule.ScheduleService;
import com.mk.framework.schedule.model.SchedulePlan;

@Component
public class ScheduleInitializingBean implements InitializingBean {
	
	final Logger logger = LoggerFactory.getLogger(ScheduleInitializingBean.class);
	
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ScheduleService scheduleService = new ScheduleService(this.schedulerFactory);
		String sql = "select * from sy_schedule where active = 'T' and DATE_FORMAT(CURDATE(), '%Y%m%d') between DATE_FORMAT(startdate, '%Y%m%d') and DATE_FORMAT(enddate, '%Y%m%d') ";
 	    List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
 	    if(list!=null && list.size()>0){
 	    	for(Map<String,Object> tmp : list){
 	 	    	SchedulePlan plan = new ObjectMapper().convertValue(tmp, SchedulePlan.class);
 	 	    	IScheduleEvent script = (IScheduleEvent) Class.forName(plan.getScript()).newInstance();
 	 	    	scheduleService.addSchedule(plan.getId()+"", plan.getName(), null, null, plan.getExpression(), script);
 	 	    	logger.info("load the job: {}, {}, {}.", plan.getName(), plan.getExpression(), plan.getScript());
 			}
 	    }
	}
}
