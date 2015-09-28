package com.mk.framework.schedule;


import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;




/**
 * 功能简述:〈一句话描述〉 详细描述:〈功能详细描述〉
 * 
 * @author LIFE2014
 * @version 2014-10-20
 * @see DefaultJobDetail
 * @since
 */
public class DefaultJobDetail implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap param = context.getJobDetail().getJobDataMap();
        IScheduleEvent scheduleEvent = (IScheduleEvent)param.get(CronTag.SCHEDULEEVENT);
        scheduleEvent.execute();
    }
}
