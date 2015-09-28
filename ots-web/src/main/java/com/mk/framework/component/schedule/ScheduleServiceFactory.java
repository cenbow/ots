/*
 * 文件名：ScheduleServiceFactory.java 版权：Copyright by www.wmccn.com 描述： 修改人：LIFE2014 修改时间：2014-9-26
 * 跟踪单号： 修改单号： 修改内容：
 */

package com.mk.framework.component.schedule;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 调度服务工厂实现类
 * 
 * @author LIFE2014
 * @version 2014-11-5
 * @see ScheduleServiceFactoryImpl
 * @since
 */
public class ScheduleServiceFactory {
	
	private static Scheduler scheduler;

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public void startup() throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.clear();
		scheduler.start();
	}

	public void destory() throws SchedulerException {
		if (scheduler.isStarted()) {
			scheduler.shutdown();
		}
	}
}
