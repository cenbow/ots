package com.mk.framework.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;


@Component
public class ScheduleService {
	
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	public ScheduleService() {
	}
	
	public Scheduler getScheduler(){
		return schedulerFactoryBean.getScheduler();
	}
	
    public ScheduleService(SchedulerFactoryBean schedulerFactoryBean) {
    	this.schedulerFactoryBean = schedulerFactoryBean;
	}
    public boolean exist(String scheduleId) throws SchedulerException {
        try {
            return getScheduler().checkExists(new JobKey(scheduleId));
        } catch (SchedulerException e) {
        	throw new SchedulerException("exist error.",e);
        }
    }

    public void addSchedule(String scheduleId, String name, String cronEvent, String[] param,
                            String cronExp, IScheduleEvent event) {
        JobDataMap dataBean = new JobDataMap();
        dataBean.put(CronTag.EVENT, cronEvent);
        dataBean.put(CronTag.PARAM, param);
        dataBean.put(CronTag.SCHEDULECODE, scheduleId);
        dataBean.put(CronTag.SCHEDULEEVENT, event);

        JobDetail taskJobImpl = newJob(DefaultJobDetail.class).withIdentity(new JobKey(scheduleId)).usingJobData(dataBean).build();
        Trigger taskTrigger = null;
        if (cronExp != null && cronExp.trim().length() > 0) {
            taskTrigger = newTrigger().withIdentity(new TriggerKey(scheduleId)).withSchedule(cronSchedule(cronExp)).build();
        } else {
            taskTrigger = newTrigger().withIdentity(new TriggerKey(scheduleId)).startNow().build();
        }
        try {
            getScheduler().scheduleJob(taskJobImpl, taskTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    
    public void addScheduleOnce(String scheduleId, String name, String cronEvent, String[] param, Date startTime, IScheduleEvent event) {
    	JobDataMap dataBean = new JobDataMap();
    	dataBean.put(CronTag.EVENT, cronEvent);
    	dataBean.put(CronTag.PARAM, param);
    	dataBean.put(CronTag.SCHEDULECODE, scheduleId);
    	dataBean.put(CronTag.SCHEDULEEVENT, event);
    	
    	JobDetail taskJobImpl = newJob(DefaultJobDetail.class).withIdentity(new JobKey(scheduleId)).usingJobData(dataBean).build();
    	Trigger taskTrigger = null;
    	if (startTime != null) {
    		taskTrigger = new SimpleTriggerImpl(scheduleId, null, startTime);
    	} else {
    		taskTrigger = newTrigger().withIdentity(new TriggerKey(scheduleId)).startNow().build();
    	}
    	try {
    		getScheduler().scheduleJob(taskJobImpl, taskTrigger);
    	} catch (SchedulerException e) {
    		e.printStackTrace();
    	}
    }
    
  

    public void updateSchedule(String scheduleId, String name, String cronEvent, String[] param,
                               String cronExp, IScheduleEvent event) throws SchedulerException {
        try {
            if (getScheduler().checkExists(new JobKey(scheduleId))) {
                JobDetail jd = getScheduler().getJobDetail(new JobKey(scheduleId));
                JobDataMap dataBean = jd.getJobDataMap();
                dataBean.put(CronTag.EVENT, cronEvent);
                dataBean.put(CronTag.PARAM, param);
                dataBean.put(CronTag.SCHEDULECODE, scheduleId);
                dataBean.put(CronTag.SCHEDULEEVENT, event);

                getScheduler().pauseTrigger(new TriggerKey(scheduleId));
                getScheduler().unscheduleJob(new TriggerKey(scheduleId));
                getScheduler().deleteJob(new JobKey(scheduleId));
                
                Trigger trg = getScheduler().getTrigger(new TriggerKey(scheduleId));
                trg = newTrigger().withIdentity(new TriggerKey(scheduleId)).withSchedule(
                    cronSchedule(cronExp)).build();
                getScheduler().scheduleJob(jd, trg);
            }
        } catch (SchedulerException e) {
        	 throw new SchedulerException("updateSchedule error.",e);
        }

    }

    public void deleteSchedule(String scheduleId) throws SchedulerException {
        try {
            if (getScheduler().checkExists(new JobKey(scheduleId))) {
                getScheduler().pauseTrigger(new TriggerKey(scheduleId));
                getScheduler().unscheduleJob(new TriggerKey(scheduleId));
                getScheduler().deleteJob(new JobKey(scheduleId));
            }
        } catch (SchedulerException e) {
            throw new SchedulerException("deleteSchedule error.", e);
        }
    }

    public void runNow(String scheduleId, String name, String cronEvent, String[] param, String cronExp, IScheduleEvent event) throws SchedulerException {
    	 if (exist(scheduleId)) {
             try {
                 getScheduler().triggerJob(new JobKey(scheduleId));
             } catch (SchedulerException e) {
                 throw new SchedulerException("runNow error.",e);
             }
         } else {
        	 JobDataMap dataBean = new JobDataMap();
             dataBean.put(CronTag.SCHEDULEEVENT, event);

             JobDetail taskJobImpl = newJob(DefaultJobDetail.class).withIdentity(new JobKey(scheduleId)).usingJobData(
                 dataBean).build();
             Trigger taskTrigger  = newTrigger().withIdentity(new TriggerKey(scheduleId)).startNow().build();
             try {
                 getScheduler().scheduleJob(taskJobImpl, taskTrigger);
             } catch (SchedulerException e) {
            	 throw new SchedulerException("runNow error.",e);
             }
         }
    }
}
