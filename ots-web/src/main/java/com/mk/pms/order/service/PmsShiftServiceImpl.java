package com.mk.pms.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.TRoomModel;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.mapper.RoomSaleConfigMapper;
import com.mk.ots.mapper.RoomSaleMapper;
import com.mk.ots.mapper.TRoomMapper;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.restful.input.RoomstateQuerylistReqEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Room;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Roomtype;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.pms.room.bean.RoomRepairPo;

@Service
@Transactional(readOnly = false, propagation = Propagation.SUPPORTS) 
public class PmsShiftServiceImpl implements PmsShiftService {
	private static final Logger logger = LoggerFactory.getLogger(PmsShiftServiceImpl.class);
	@Autowired
	private RoomstateService roomstateService;
	@Autowired
	private RoomSaleMapper roomSaleMapper;
	@Autowired
	private RoomSaleConfigMapper roomSaleConfigMapper;
	@Autowired
	private TRoomMapper roomMapper;
	@Autowired
	private PmsShiftHelperService pmsShiftHelperService;

	private final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyyMMdd");

	private boolean isInPromo(Date begindate, Date enddate, Time startTime, Time endTime) {
		boolean isInPromo = false;

		Integer promostaus = DateUtils.promoStatus(begindate, enddate, startTime, endTime);
		if (promostaus != null && Constant.PROMOING == promostaus) {
			isInPromo = true;
		}

		return isInPromo;
	}

	private TRoomModel isRoomExisted(List<TRoomModel> rooms, Long roomid) {
		TRoomModel roomModel = null;

		for (int i = 0; rooms != null && i < rooms.size(); i++) {
			TRoomModel troomModel = rooms.get(i);

			Long roomModelId = troomModel.getId();
			if (roomModelId != null && roomModelId == roomid) {
				roomModel = troomModel;
				break;
			}
		}

		return roomModel;
	}

	private List<Room> findVCRooms(Long hotelid, Long roomtypeid, Date begindate, Date enddate) throws Exception {
		List<Room> vcRooms = null;

		try {
			if (hotelid != null && begindate != null && enddate != null) {
				String startdateday = DateUtils.formatDateTime(begindate, DateUtils.FORMATSHORTDATETIME);
				String enddateday = DateUtils.formatDateTime(enddate, DateUtils.FORMATSHORTDATETIME);

				if (logger.isInfoEnabled()) {
					logger.info(String.format(
							"about to findVCRooms for hotelid:%s; roomtypeid:%s; startdateday:%s; enddateday:%s",
							hotelid, roomtypeid, startdateday, enddateday));
				}

				RoomstateQuerylistReqEntity reqEntity = new RoomstateQuerylistReqEntity();
				reqEntity.setCallversion("3.2");
				reqEntity.setCallentry(2);
				reqEntity.setHotelid(hotelid);
				reqEntity.setRoomtypeid(roomtypeid);
				reqEntity.setStartdateday(DateUtils.formatDateTime(begindate, DateUtils.FORMATSHORTDATETIME));
				reqEntity.setEnddateday(DateUtils.formatDateTime(enddate, DateUtils.FORMATSHORTDATETIME));
				reqEntity.setIsShowAllRoom("T");

				List<RoomstateQuerylistRespEntity> response = roomstateService.findHotelRoomState("", reqEntity);
				if (response != null && response.size() > 0 && response.get(0).getRoomtype() != null
						&& response.get(0).getRoomtype().size() > 0) {
					List<Room> rooms = response.get(0).getRoomtype().get(0).getRooms();
					vcRooms = rooms;
				} else {
					logger.warn(String.format("no available rooms found for hotelid:%s; roomtypeid:%s", hotelid,
							roomtypeid));
				}
			} else {
				logger.warn("illegal parameters passed in findVCRooms...");
			}
		} catch (Exception ex) {
			throw new Exception(String.format("failed to findVCHotelRoom for hotelid:%s; begindate:%s; enddate:%s",
					hotelid, begindate, enddate), ex);
		}

		return vcRooms;
	}

	private void doShiftRoom(String hotelid, Long pmsroomtypeid, Long pmsroomid, PmsRoomOrder pmsRoomOrder)
			throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("about to doShiftRoom with pmsroomtypeid:%s; pmsroomid:%s; ", pmsroomtypeid,
					pmsroomid));
		}

		if (pmsroomid == null) {
			logger.warn("will not shift room since pmsroomid is empty");
			return;
		}

		List<Map<String, Object>> promoRooms = roomSaleMapper.queryRoomPromoByType(String.valueOf(pmsroomtypeid));
		if (promoRooms != null && promoRooms.size() > 0) {
			final Map<String, Object> promoRoom = promoRooms.get(0);

			Integer roomId = (Integer) promoRoom.get("roomid");
			Integer saleRoomtypeId = (Integer) promoRoom.get("saleroomtypeid");
			Integer roomtypeId = (Integer) promoRoom.get("roomtypeid");

			if (logger.isInfoEnabled()) {
				logger.info(String.format(
						"about to findroomprice with hotelid:%s; pmsroomtypeid:%s; roomtypeid:%s begintime:%s; endtime:%s",
						hotelid, pmsroomtypeid, roomtypeId, pmsRoomOrder.get("BeginTime"),
						pmsRoomOrder.get("EndTime")));
			}

			Roomtype roomtype = null;
			try {
				roomtype = findRoomSalePrice(hotelid, roomtypeId != null ? roomtypeId.longValue() : 0L,
						(Date) pmsRoomOrder.get("BeginTime"), (Date) pmsRoomOrder.get("EndTime"));
			} catch (Exception ex) {
				logger.warn("failed to findRoomSalePrice, quit shifting room...", ex);
				return;
			}

			if (roomId == null) {
				List<TRoomModel> models = roomMapper.findList(roomtypeId != null ? roomtypeId.longValue() : 0);
				List<Room> vcRooms = findVCRooms(Long.valueOf(hotelid), roomtypeId != null ? roomtypeId.longValue() : 0,
						(Date) pmsRoomOrder.get("BeginTime"), (Date) pmsRoomOrder.get("EndTime"));
				Room vcRoom = null;
				if (vcRooms != null && vcRooms.size() > 0) {
					vcRoom = vcRooms.get(vcRooms.size() - 1);
				} else {
					return;
				}

				TRoomModel roomModel = isRoomExisted(models, vcRoom.getRoomid());

				if (logger.isInfoEnabled()) {
					logger.info(String.format("vcroom:%s; roomModel:%s; roomstatus:%s; vcroomid:%s", vcRoom == null,
							roomModel == null, vcRoom != null ? vcRoom.getRoomstatus() : "",
							vcRoom != null ? vcRoom.getRoomid() : ""));
				}

				/**
				 * promo room has been ordered by non-promo pms, supplementary
				 * room is required
				 */
				if (vcRoom != null && roomModel != null && StringUtils.isNotBlank(hotelid)
						&& vcRoom.getRoomid() != null) {
					if (logger.isInfoEnabled()) {
						logger.info(String.format("room is available for a shift...roomid:%s; roomtypeid:%s",
								vcRoom.getRoomid(), roomtypeId));
					}

					/**
					 * double check isFromOta
					 */
					if (StringUtils.isNotBlank(pmsRoomOrder.getStr("PmsRoomOrderNo"))
							&& !pmsShiftHelperService.isOtaOrder(pmsRoomOrder.getStr("PmsRoomOrderNo"))) {
						logger.info("double checked, it is from ota, quit...");
						return;
					}

					try {
						/**
						 * update vacant non-promo room to promo room
						 */
						Map<String, Object> updateParameters = new HashMap<>();
						updateParameters.put("roomid", vcRoom.getRoomid());
						updateParameters.put("roomtypeid", saleRoomtypeId);

						roomMapper.updateRoomtypeByRoom(updateParameters);

						updateParameters.put("roomno", vcRoom.getRoomno());
						updateParameters.put("baseprice", roomtype != null ? roomtype.getRoomtypeprice() : 0);
						updateParameters.put("roomid", vcRoom.getRoomid());
						updateParameters.put("pms", roomModel.getPms());

						if (logger.isInfoEnabled()) {
							logger.info(String.format(
									"about to saveroomsale with parameters, roomno:%s; baseprice:%s; roomid:%s",
									updateParameters.get("roomno"), updateParameters.get("baseprice"),
									updateParameters.get("roomid")));
						}

						this.saveRoomSale(promoRoom, updateParameters);

						/**
						 * update current promo room to non-promo room
						 */
						updateParameters.put("roomid", pmsroomid);
						updateParameters.put("roomtypeid", roomtypeId);
						roomMapper.updateRoomtypeByRoom(updateParameters);

						if (logger.isInfoEnabled()) {
							logger.info(String.format("updateRoomtypeByRoom succeed for hotelid:%s; roomtypeid:%s",
									hotelid, saleRoomtypeId));
						}
					} catch (Exception ex) {
						throw new Exception(String.format("failed to move room for roomid:%s; roomtypeid:%s",
								vcRoom.getRoomid(), saleRoomtypeId), ex);
					}
				} else {
					logger.warn(String.format(
							"tried to make up room for promo roomtypeid using normal roomtypeid:%s, however no available room found...",
							roomtypeId));
				}
			}
		}
	}

	public void shiftRoomForPromo(RoomRepairPo roomRepairPo) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("enter shiftRoomForPromo repair-mode");
		}

		PmsRoomOrder pmsRoomOrder = new PmsRoomOrder();
		pmsRoomOrder.set("RoomTypeId", roomRepairPo.getRoomtypeid());
		pmsRoomOrder.set("RoomId", roomRepairPo.getRoomid());
		pmsRoomOrder.set("HotelId", roomRepairPo.getHotelid());
		pmsRoomOrder.set("Status", "RE");
		pmsRoomOrder.set("BeginTime", roomRepairPo.getBegintime());
		pmsRoomOrder.set("EndTime", roomRepairPo.getEndtime());

		shiftRoomForPromo(pmsRoomOrder, false);
	}

	public void shiftRoomForPromo(PmsRoomOrder pmsRoomOrder, boolean isChanged) throws Exception {
		Long oldroomtypeid = pmsRoomOrder.getLong("OldRoomTypeId");
		Long newroomtypeid = pmsRoomOrder.getLong("RoomTypeId");
		Long newroomid = pmsRoomOrder.getLong("RoomId");
		String status = pmsRoomOrder.getStr("Status");
		String hotelid = String.valueOf(pmsRoomOrder.get("HotelId"));
		String pmsRoomOrderNo = pmsRoomOrder.getStr("PmsRoomOrderNo");

		boolean isProceed = false;
		boolean isInPromo = false;
		boolean isNotFromOta = true;

		if (logger.isInfoEnabled()) {
			logger.info(String.format(
					"about to shiftRoomForPromo... hotelid:%s; status:%s; oldroomtypeid:%s; newroomtypeid:%s; newroomid:%s; pmsRoomOrderNo:%s",
					hotelid, status, oldroomtypeid, newroomtypeid, newroomid, pmsRoomOrderNo));
		}

		TRoomSaleConfig newRoomsaleConfig = new TRoomSaleConfig();
		newRoomsaleConfig.setRoomTypeId(newroomtypeid == null ? 0 : newroomtypeid.intValue());
		newRoomsaleConfig.setValid("T");
		newRoomsaleConfig.setTag(0);
		List<TRoomSaleConfig> newRooms = null;

		try {
			if (newroomid != null) {
				Map<String, Object> roomParameters = new HashMap<String, Object>();
				roomParameters.put("roomid", newroomid);

				List<Map<String, Object>> promoList = roomSaleMapper.checkPromoByRoom(roomParameters);

				if (logger.isInfoEnabled()) {
					logger.info(String.format("check if newroomid:%s is promoted with promolist %s returned", newroomid,
							promoList != null ? promoList.size() : 0));
				}

				if (promoList != null && promoList.size() > 0) {
					newroomtypeid = (Long) promoList.get(0).get("roomtypeid");
				} else {
					logger.warn("no promo can be found by roomid...");
					newroomtypeid = null;
				}
			}
			if (newroomtypeid != null) {
				newRooms = roomSaleConfigMapper.getRoomSaleByParamsNew(newRoomsaleConfig);
				if (newRooms != null && newRooms.size() > 0) {
					isInPromo = isInPromo(newRooms.get(0).getStartDate(), newRooms.get(0).getEndDate(),
							newRooms.get(0).getStartTime(), newRooms.get(0).getEndTime());
				}
			}

			isNotFromOta = pmsShiftHelperService.isOtaOrder(pmsRoomOrderNo);
		} catch (Exception ex) {
			logger.warn("failed to invoke getRoomSaleByParamsNew or isInPromo...", ex);
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format(
					"checking isInPromo:%s; status:%s; isNotFromOta:%s; hotelid:%s; roomtypeid:%s; roomid:%s",
					isInPromo, status, isNotFromOta, hotelid, newroomtypeid, newroomid));
		}

		/**
		 * process this room shift only during promo period
		 */
		if (isNotFromOta && isInPromo && ("RE".equalsIgnoreCase(status) || "IN".equalsIgnoreCase(status))) {
			isProceed = true;
		}

		if (!isProceed) {
			return;
		}

		/**
		 * there is room change going on, checks out for promo if a room shift
		 * is necessary
		 */
		if (isChanged) {
			TRoomSaleConfig oldRoomsaleConfig = new TRoomSaleConfig();
			oldRoomsaleConfig.setRoomTypeId(oldroomtypeid == null ? 0 : oldroomtypeid.intValue());
			oldRoomsaleConfig.setValid("T");
			oldRoomsaleConfig.setTag(0);

			List<TRoomSaleConfig> oldRooms = roomSaleConfigMapper.getRoomSaleByParamsNew(oldRoomsaleConfig);
			boolean isOldPromo = oldRooms != null ? oldRooms.size() > 0 : false;
			boolean isNewPromo = newRooms != null ? newRooms.size() > 0 : false;

			if (logger.isInfoEnabled()) {
				logger.info(String.format(
						"there is room change detected...oldpromo:%s;oldroomtype:%s; newpromo:%s; newrootype:%s",
						isOldPromo, oldroomtypeid, isNewPromo, newroomtypeid));
			}

			/**
			 * shift room from non-promo rooms to promo news, shift required
			 */
			if (isNewPromo) {
				doShiftRoom(hotelid, newroomtypeid, newroomid, pmsRoomOrder);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("no room change detected...");
			}
			doShiftRoom(hotelid, newroomtypeid, newroomid, pmsRoomOrder);
		}
	}

	private Roomtype findRoomSalePrice(String hotelId, Long roomTypeId, Date startdate, Date enddate) throws Exception {
		RoomstateQuerylistReqEntity parameters = new RoomstateQuerylistReqEntity();
		parameters.setIsShowAllRoom("T");
		parameters.setHotelid(Long.valueOf(hotelId));
		parameters.setRoomtypeid(roomTypeId);
		parameters.setCallentry(2);
		parameters.setCallversion("3.2.5");

		String begindateday = defaultFormat.format(startdate);
		String enddateday = defaultFormat.format(enddate);

		parameters.setStartdateday(begindateday);
		parameters.setEnddateday(enddateday);

		List<RoomstateQuerylistRespEntity> responses = roomstateService.findHotelRoomState("", parameters);
		Roomtype roomtype = null;
		if (responses != null && responses.size() > 0 && responses.get(0) != null) {
			roomtype = responses.get(0).getRoomtype().get(0);
		}

		return roomtype;
	}

	private BigDecimal calaValue(BigDecimal baseValue, BigDecimal value, Integer valueTypeEnum) {
		if (logger.isInfoEnabled()) {
			logger.info("value received, basevalue:%s; value:%s; valuetypeenum:%s", baseValue, value, valueTypeEnum);
		}

		if (null == baseValue || null == valueTypeEnum) {
			return baseValue;
		}

		if (null == value) {
			return baseValue;
		}

		logger.info("============sales online job >> calaValue");
		if (1 == valueTypeEnum) {
			logger.info("============sales online job >> calaValue：TYPE_TO：" + value);
			return value;
		} else if (2 == valueTypeEnum) {
			BigDecimal result = baseValue.subtract(value);
			if (result.compareTo(BigDecimal.ZERO) > 0) {
				logger.info("============sales online job >> calaValue：TYPE_ADD：" + value);
				return result;
			} else {
				logger.info("============sales online job >> calaValue：TYPE_ADD：" + BigDecimal.ZERO);
				return BigDecimal.ZERO;
			}
		} else if (3 == valueTypeEnum) {
			logger.info("============sales online job >> calaValue：TYPE_OFF：" + value);
			return baseValue.multiply(value).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		} else {
			logger.info("============sales online job >> calaValue：else：" + baseValue);
			return baseValue;
		}
	}

	private Date[] getStartEndDate(Date runTime, Date startTime, Date endTime) throws Exception {
		if (null == runTime || null == startTime || null == endTime) {
			return new Date[] { new Date(), new Date() };
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			String strMidTime = dateFormat.format(runTime);
			Date midTime = datetimeFormat.parse(strMidTime + " 3:00:00");

			Date startDate = null;
			Date endDate = null;
			// 若当前时间晚于中午12点,住房时间为今日到明日。若早于12点，住房时间为昨日到今日
			if (runTime.before(midTime)) {
				endDate = runTime;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.add(Calendar.DATE, -1);
				startDate = calendar.getTime();
			} else {
				startDate = runTime;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				calendar.add(Calendar.DATE, 1);
				endDate = calendar.getTime();
			}

			// 若开始时间早于endTime，当日时间
			if (startTime.before(endTime)) {
				String strStartDate = dateFormat.format(runTime) + " " + timeFormat.format(startTime);
				startDate = datetimeFormat.parse(strStartDate);

				String strEndDate = dateFormat.format(runTime) + " " + timeFormat.format(endTime);
				endDate = datetimeFormat.parse(strEndDate);
			} else {
				String strStartDate = dateFormat.format(startDate) + " " + timeFormat.format(startTime);
				startDate = datetimeFormat.parse(strStartDate);

				String strEndDate = dateFormat.format(endDate) + " " + timeFormat.format(endTime);
				endDate = datetimeFormat.parse(strEndDate);
			}

			return new Date[] { startDate, endDate };
		} catch (Exception ex) {
			throw new Exception("failed to parse stsrt/end date", ex);
		}
	}

	private void saveRoomSale(Map<String, Object> roomConfig, Map<String, Object> saveRoomParameters) throws Exception {
		BigDecimal basePrice = (BigDecimal) saveRoomParameters.get("baseprice");
		BigDecimal saleValueOrg = (BigDecimal) roomConfig.get("salevalue");
		Integer saleType = (Integer) roomConfig.get("saletype");
		BigDecimal settleValueOrg = (BigDecimal) roomConfig.get("settlevalue");
		Integer settleType = (Integer) roomConfig.get("settletype");
		Date starttime = (Date) roomConfig.get("starttime");
		Date endtime = (Date) roomConfig.get("endtime");

		BigDecimal saleValue = calaValue(basePrice, saleValueOrg, saleType);
		BigDecimal settleValue = calaValue(basePrice, settleValueOrg, settleType);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date[] startEndDate = getStartEndDate(new Date(), starttime, endtime);
		Date startDate = startEndDate[0];
		Date endDate = startEndDate[1];

		if (logger.isInfoEnabled()) {
			logger.info(
					String.format("about to saveRoomSale with saleValue:%s; settleValue:%s; startdate:%s; enddate:%s",
							saleValue, settleValue, startDate, endDate));
		}

		Map<String, Object> isExistedParameter = new HashMap<>();
		isExistedParameter.put("oldroomtypeid", roomConfig.get("roomtypeid"));
		isExistedParameter.put("roomtypeid", roomConfig.get("saleroomtypeid"));
		isExistedParameter.put("roomid", saveRoomParameters.get("roomid"));
		isExistedParameter.put("hotelid", roomConfig.get("hotelid"));
		isExistedParameter.put("configid", roomConfig.get("configid"));

		List<Map<String, Object>> isRoomSaleExisted = roomSaleMapper.isRoomSaleExisted(isExistedParameter);
		if (isRoomSaleExisted != null && isRoomSaleExisted.size() > 0) {
			logger.warn(String.format("room sale already existed. oldroomtypeid:%s; roomtypeid:%s",
					isExistedParameter.get("oldroomtypeid"), isExistedParameter.get("roomtypeid"),
					isExistedParameter.get("roomid"), isExistedParameter.get("hotelid")));
			return;
		}

		// log roomSale
		Map<String, Object> saveRoomSaleParameter = new HashMap<String, Object>();
		saveRoomSaleParameter.put("oldroomtypeid", roomConfig.get("roomtypeid"));
		saveRoomSaleParameter.put("roomtypeid", roomConfig.get("saleroomtypeid"));

		saveRoomSaleParameter.put("roomno", saveRoomParameters.get("roomno"));
		saveRoomSaleParameter.put("roomid", saveRoomParameters.get("roomid"));
		saveRoomSaleParameter.put("pms", saveRoomParameters.get("pms"));

		saveRoomSaleParameter.put("createdate", dateFormat.format(new Date()));
		saveRoomSaleParameter.put("saleprice", saleValue);
		saveRoomSaleParameter.put("costprice", basePrice);
		saveRoomSaleParameter.put("starttime", startDate);
		saveRoomSaleParameter.put("endtime", endDate);

		saveRoomSaleParameter.put("configid", roomConfig.get("configid"));
		saveRoomSaleParameter.put("isback", "F");
		saveRoomSaleParameter.put("salename", roomConfig.get("salename"));
		saveRoomSaleParameter.put("saletype", roomConfig.get("saletype"));
		saveRoomSaleParameter.put("hotelid", roomConfig.get("hotelid"));
		saveRoomSaleParameter.put("settlevalue", settleValue);

		this.roomSaleMapper.saveRoomSale(saveRoomSaleParameter);
	}
}
