package com.mk.ots.job;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.restful.input.RoomstateQuerylistReqEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Room;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Roomtype;
import com.mk.ots.room.bean.RoomCensus;
import com.mk.pms.order.control.PmsUtilController;

/**
 * 每天00:15执行一次，删除并备份48小时之前房量数据调度
 * @author jianghe
 *
 */
public class RoomCensusBackUpJob  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RoomCensusBackUpJob.class);
	private RoomstateService roomstateService = AppUtils.getBean(RoomstateService.class);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("RoomCensusBackUpJob::start");
		try {
			roomstateService.backUpRoomCensus();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RoomCensusBackUpJob::error{}",e.getMessage());
		}
		logger.info("RoomCensusBackUpJob::end");
	}
}
