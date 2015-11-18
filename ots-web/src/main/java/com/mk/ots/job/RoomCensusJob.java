package com.mk.ots.job;


import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.restful.input.RoomstateQuerylistReqEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Room;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Roomtype;
import com.mk.ots.room.bean.RoomCensus;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 每30分钟执行一次，房量调度
 * @author jianghe
 *
 */
public class RoomCensusJob  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RoomCensusJob.class);
	private RoomstateService roomstateService = AppUtils.getBean(RoomstateService.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private static ExecutorService pool = Executors.newFixedThreadPool(5);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("RoomCensusJob::start");
		final Calendar calendar = Calendar.getInstance();
		final String today = sdf.format(calendar.getTime());
		final String tomorrow = sdf.format(DateUtils.addDays(calendar.getTime(),1));
		try {
			//获取所有酒店id
			List<Long> ids = roomstateService.readonlyHotelIds();
			for (final Long id:ids) {
				pool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							RoomstateQuerylistReqEntity params = new RoomstateQuerylistReqEntity();
							params.setHotelid(id);
							params.setStartdateday(today);
							params.setEnddateday(tomorrow);
							List<RoomstateQuerylistRespEntity> list= roomstateService.findHotelRoomState(null,params);
							if(list!=null && list.size()>0){
								RoomstateQuerylistRespEntity result = list.get(0);

								RoomCensus roomCensus = new RoomCensus();
								roomCensus.setHotelid(result.getHotelid());
								roomCensus.setHotelname(result.getHotelname());
								roomCensus.setVisible(result.getVisible());
								roomCensus.setOnline(result.getOnline());
								//房间总数
								int roomCount = 0;
								//空闲房间数
								int freeRoomCount = 0;
								//单床空闲数
								int freeRoomCountOneBed = 0;
								//双床空闲数
								int freeRoomCountTwoBed = 0;
								//其它空闲数
								int freeRoomCountOtherBed = 0;
								for (Roomtype roomtype:result.getRoomtype()) {
									roomCount+=roomtype.getRooms().size();
									for (Room room:roomtype.getRooms()) {
										if("vc".equals(room.getRoomstatus())){
											freeRoomCount++;
											if(roomtype.getBednum()!=null){
												if(roomtype.getBednum()==1){
													freeRoomCountOneBed++;
												}else if(roomtype.getBednum()==2){
													freeRoomCountTwoBed++;
												}else{
													freeRoomCountOtherBed++;
												}
											}else{
												logger.error("RoomCensusJob::nonbed:{},{}",id,roomtype.getRoomtypeid());
											}
										}
									}
								}
								if("F".equals(roomCensus.getVisible()) || "F".equals(roomCensus.getOnline())){
									freeRoomCount=0;
									freeRoomCountOneBed=0;
									freeRoomCountTwoBed=0;
									freeRoomCountOtherBed=0;
								}
								roomCensus.setRoomcount(roomCount);
								roomCensus.setFreeroomcount(freeRoomCount);
								roomCensus.setOther1(freeRoomCountOneBed);
								roomCensus.setOther2(freeRoomCountTwoBed);
								roomCensus.setOther3(String.valueOf(freeRoomCountOtherBed));
								roomCensus.setYear(calendar.get(Calendar.YEAR)+"");
								roomCensus.setMonth((calendar.get(Calendar.MONTH)+1)+"");
								roomCensus.setDay(calendar.get(Calendar.DAY_OF_MONTH)+"");
								roomCensus.setDate(calendar.get(Calendar.HOUR_OF_DAY)+"点"+(calendar.get(Calendar.MINUTE)==30?"半":""));
								roomCensus.setCreatetime(Calendar.getInstance().getTime());
								//执行入库操作
								roomstateService.saveRoomCensus(roomCensus);
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("oomCensusJob::error::hotelid:"+id);
							logger.error("RoomCensusJob::error{}{}",id,e.getMessage());
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RoomCensusJob::error{}",e.getMessage());
		}
		logger.info("RoomCensusJob::end");
	}
}
