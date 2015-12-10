package com.mk.pms.room.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.model.EHotelModel;
import com.mk.ots.hotel.model.TRoomModel;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.order.event.PmsShiftService;
import com.mk.pms.room.bean.RoomRepairJsonBean;
import com.mk.pms.room.bean.RoomRepairLockJsonBean;
import com.mk.pms.room.bean.RoomRepairPo;
import com.mk.pms.room.service.PmsRoomService;

/**
 * @author jianghe p2o 2.0调ots controller层
 */
@RestController
@RequestMapping("/pms")
public class PmsRoomController {

	private Logger logger = LoggerFactory.getLogger(PmsRoomController.class);

	private Gson gson = new Gson();

	@Autowired
	private PmsRoomService pmsRoomService;
	@Autowired
	private RoomService roomService;

	public static final String METHOD_ADD = "add";
	public static final String METHOD_DEL = "delete";
	public static final String METHOD_UPDATE = "update";

	@Autowired
	private PmsShiftService pmsShiftService;

	/**
	 * (只给PMS的锁房)，将这个数据放到t_repair表
	 */
	@RequestMapping("/lock")
	public ResponseEntity<Map<String, Object>> lock(String json) {
		logger.info("ots::PmsRoomController::lock::params{}  begin", json);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(json)) {
				throw MyErrorEnum.errorParm.getMyException("参数错误!没有传json数据");
			}
			// 解析json
			RoomRepairJsonBean roomRepairJsonBean = gson.fromJson(json, RoomRepairJsonBean.class);
			String hotelId = roomRepairJsonBean.getHotelid();
			if (StringUtils.isEmpty(hotelId) || roomRepairJsonBean.getLock().size() == 0) {
				throw MyErrorEnum.errorParm.getMyException("参数错误!没有传hotelId");
			}
			// List<TRoomRepairModel> list = new ArrayList<TRoomRepairModel>();
			for (RoomRepairLockJsonBean rrlJsonBean : roomRepairJsonBean.getLock()) {
				RoomRepairPo roomRepairPo = new RoomRepairPo();
				// 去ehotel查询
				EHotelModel eHotelModel = pmsRoomService.selectEhotelByPms(hotelId);
				if (eHotelModel == null) {
					throw MyErrorEnum.errorParm.getMyException("未找到pms为 " + hotelId + " 的酒店信息");
				}
				Long theHotelId = eHotelModel.getId();
				roomRepairPo.setHotelid(theHotelId);
				roomRepairPo.setRepairid(rrlJsonBean.getId());
				Map map = new HashMap<String, String>();
				map.put("pms", rrlJsonBean.getRoomid());
				map.put("hotelid", theHotelId);
				TRoomModel tRoomModel = pmsRoomService.selectTroomByPms(map);
				if (tRoomModel == null) {
					// throw MyErrorEnum.errorParm.getMyException("未找到pms为
					// "+rrlJsonBean.getRoomid()+" 的房间信息");
					logger.error("lock::未找到pms为 " + rrlJsonBean.getRoomid() + " 的房间信息");
					continue;
				}
				Long theRoomId = tRoomModel.getId();
				roomRepairPo.setRoomid(theRoomId);
				// 通过roomid查询roomtypeid
				TRoom tRoom = roomService.findTRoomByRoomId(roomRepairPo.getRoomid());
				roomRepairPo.setRoomtypeid(tRoom.getLong("roomtypeid"));
				String beginTime = rrlJsonBean.getBegintime() == null ? "" : rrlJsonBean.getBegintime();
				if (!isValidDate(beginTime)) {
					throw MyErrorEnum.errorParm.getMyException("beginTime时间格式错误");
				}
				String endTime = rrlJsonBean.getEndtime() == null ? "" : rrlJsonBean.getEndtime();
				if (!isValidDate(endTime)) {
					throw MyErrorEnum.errorParm.getMyException("endTime时间格式错误");
				}
				roomRepairPo.setBegintime(DateUtils.getDateFromString(beginTime, DateUtils.FORMATDATETIME));
				roomRepairPo.setEndtime(DateUtils.getDateFromString(endTime, DateUtils.FORMATDATETIME));
				// list.add(tRoomRepairModel);
				if (StringUtils.equalsIgnoreCase(rrlJsonBean.getAction(), PmsRoomController.METHOD_ADD)) {
					// 增
					int i = pmsRoomService.saveRoomRepair(roomRepairPo);
					if (i == 0) {
						logger.info("ots::PmsRoomController::lock::add 新增0条记录");
					}

					if (logger.isInfoEnabled()) {
						logger.info("will do room shift in PmsRoomController...");
					}

					try {
						pmsShiftService.shiftRoomForPromo(roomRepairPo);
					} catch (Exception ex) {
						logger.warn("failed to shiftRoomForPromo in PmsRoomController", ex);
					}
				} else if (StringUtils.equalsIgnoreCase(rrlJsonBean.getAction(), PmsRoomController.METHOD_DEL)) {
					// 删
					int i = pmsRoomService.deleteRoomRepairByConds(roomRepairPo.getHotelid(),
							roomRepairPo.getRepairid());
					if (i == 0) {
						logger.info("ots::PmsRoomController::lock::delete 删除0条记录");
					}
				} else if (StringUtils.equalsIgnoreCase(rrlJsonBean.getAction(), PmsRoomController.METHOD_UPDATE)) {
					// 改
					int i = pmsRoomService.updateRoomRepair(roomRepairPo);
					if (i == 0) {
						logger.info("ots::PmsRoomController::lock::update 修改0条记录");
					}
				}
			}
			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("ots::PmsRoomController::lock  error {} {}", json, e.getMessage());
			throw e;
		} finally {
			logger.info("ots::PmsRoomController::lock  end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	public static boolean isValidDate(String str) {
		boolean convertSuccess = true;
		SimpleDateFormat format = new SimpleDateFormat(DateUtils.FORMATDATETIME);
		try {
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}
		return convertSuccess;
	}

}
