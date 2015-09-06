package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.hotel.service.RoomService;

/**
 * 评分系统批处理
 * @author LYN
 *
 */
public class LockRoomSatusInAt12PMQuartzJob extends QuartzJobBean {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		//中午12点预离未离，且状态为IN的锁定房间
//		logger.info("中午12点预离未离，且状态为IN的锁定房间 job 开始");
//		RoomService roomService  = AppUtils.getBean(RoomService.class);
//		roomService.lockRoomPmsISinAt12PM();
//		logger.debug("12点锁定预离未离且状态为IN锁定房间job 结束");
	}
}
