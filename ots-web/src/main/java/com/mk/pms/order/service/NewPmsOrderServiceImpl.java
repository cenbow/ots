package com.mk.pms.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.component.eventbus.EventBusHelper;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.orm.kit.JsonKit;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.enums.PmsCostSourceEnum;
import com.mk.ots.common.enums.PmsCostTypeEnum;
import com.mk.ots.common.enums.PmsRoomOrderStatusEnum;
import com.mk.ots.common.enums.PriceTypeEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.bean.EHotel;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.dao.EHotelDAO;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.model.TRoomModel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.manager.RedisCacheName;
import com.mk.ots.mapper.RoomSaleConfigMapper;
import com.mk.ots.mapper.RoomSaleMapper;
import com.mk.ots.mapper.TRoomMapper;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.PmsOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.service.OrderLogService;
import com.mk.ots.order.service.OrderServiceImpl;
import com.mk.ots.pay.service.IPayService;
import com.mk.ots.restful.input.RoomstateQuerylistReqEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Room;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Roomtype;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.pms.bean.PmsCheckinUser;
import com.mk.pms.bean.PmsCost;
import com.mk.pms.bean.PmsLog;
import com.mk.pms.exception.PmsException;
import com.mk.pms.hotel.service.PmsCheckinUserService;
import com.mk.pms.manager.XmlLogFileManager;
import com.mk.pms.myenum.PmsResultEnum;
import com.mk.pms.order.bean.ChangeRoom;
import com.mk.pms.order.event.PmsCalCacheEvent;
import com.mk.pms.room.bean.RoomLockPo;
import com.mk.pms.room.service.PmsRoomService;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import cn.com.winhoo.mikeweb.webout.service.bean.ChangeRoomOrderBean;
import cn.com.winhoo.pms.exception.PmsErrorEnum;
import jodd.util.StringUtil;

@Service
public class NewPmsOrderServiceImpl implements NewPmsOrderService {

	private static final Logger logger = LoggerFactory.getLogger(NewPmsOrderServiceImpl.class);
	@Autowired
	EHotelDAO eHotelDAO;

	@Autowired
	HotelDAO hotelDAO;

	@Autowired
	HotelService hotelService;

	@Autowired
	RoomService roomService;
	@Autowired
	private OrderServiceImpl orderService;

	@Autowired
	private IPayService payService;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private PmsRoomService pmsRoomService;
	@Autowired
	private RoomstateService roomstateService;
	@Autowired
	private RoomSaleMapper roomSaleMapper;
	@Autowired
	private RoomSaleConfigMapper roomSaleConfigMapper;

	@Autowired
	private TRoomMapper roomMapper;

	private final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 创建/修改客单 saveCustomerNo
	 *
	 * @param hotelId
	 * @param datalist
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public JSONObject saveCustomerNo(JSONObject param) {
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::saveCustomerNo::参数::" + param);
		JSONObject data = new JSONObject();
		HashMap unlockParam = new HashMap();
		// IN【在住】 CheckIn RX【预订取消】 NotIn OK【退房】 CheckOut IX【入住取消】 CheckOut
		// PM【挂账】 Account
		try {
			Long hotelId = (Long) param.get("hotelid");
			Map<Long, Set<Long>> map = (Map<Long, Set<Long>>) param.get("set") == null ? new HashMap<Long, Set<Long>>()
					: (Map<Long, Set<Long>>) param.get("set");
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			String cityCode = this.eHotelDAO.getCityIdByHotelId(hotel.getLong("disId"));
			NewPmsOrderServiceImpl.logger.info("更新[" + hotel.getHotelName() + "]酒店的客单记录");
			List<PmsRoomOrder> returnlist = new ArrayList<PmsRoomOrder>();
			JSONArray datalist = param.getJSONArray("customerno");

			for (int i = 0; i < datalist.size(); i++) {
				JSONObject customNo = datalist.getJSONObject(i);

				PmsRoomOrder order = this.hotelDAO.findPmsRoomOrderByHotelIdAndCustomno(hotelId,
						(String) customNo.get("customeno"));
				try {
					order = this.updatePmsRoomOrder(hotel, order, customNo, hotelId);
					if (order == null) {
						continue;
					}
				} catch (MySQLIntegrityConstraintViolationException e) {
					// 对插入数据报索引异常的进行重新更新
					order = this.updatePmsRoomOrder(hotel, null, customNo, hotelId);
					NewPmsOrderServiceImpl.logger
							.info("PmsOrderServiceImpl::saveCustomerNo::updatePmsRoomOrder重复数据插入:orderid:{}" + order);
				}

				returnlist.add(order);
				// 记录身份证、卡号、民族 数据
				if ((customNo.getJSONArray("user") != null) && (customNo.getJSONArray("user").size() > 0)) {
					JSONArray users = customNo.getJSONArray("user");
					PmsCheckinUserService checkinService = AppUtils.getBean(PmsCheckinUserService.class);
					for (int j = 0; j < users.size(); j++) {
						JSONObject user = users.getJSONObject(j);
						PmsCheckinUser checkinUser = new PmsCheckinUser();
						checkinUser.set("name", user.getString("name"));
						checkinUser.set("cardtype", user.getString("idtype"));
						checkinUser.set("cardid", user.getString("idno"));
						checkinUser.set("pmsRoomOrderNo", customNo.get("customeno"));
						checkinUser.set("freqtrv", user.getString("ispermanent"));
						if (user.get("isscan") != null) {
							checkinUser.set("isscan", StringUtils.defaultIfBlank(user.getString("isscan"), null));
						}
						// 补上酒店id
						checkinUser.set("Hotelid", hotelId);

						checkinService.saveOrUpdatePmsInCheckUser(checkinUser);
						NewPmsOrderServiceImpl.logger
								.info("OTSMessage::PmsOrderServiceImpl::checkinService::saveOrUpdatePmsInCheckUser::参数::"
										+ checkinUser.getAttrs());
					}
				}
				/**
				 * 同步day的值
				 */
				logger.info("OTSMessage::PmsOrderServiceImpl:id：{}:day:{}", order.get("id"),
						customNo.getJSONArray("day"));
				logger.info("OTSMessage::PmsOrderServiceImpl:id：{}:bolean:{}", order.get("id"),
						(customNo.getJSONArray("day") != null) && (customNo.getJSONArray("day").size() > 0));
				if ((customNo.getJSONArray("day") != null) && (customNo.getJSONArray("day").size() > 0)) {
					JSONArray days = customNo.getJSONArray("day");
					long pmsorderid = order.get("id");
					JSONObject object;
					int time;
					RoomLockPo roomLockPo = null;
					String price;

					for (int j = 0; j < days.size(); j++) {
						object = days.getJSONObject(j);
						time = object.getInteger("time");
						// roomid = object.getLong("roomid");
						price = object.getString("price");
						// 查询是否存在
						roomLockPo = this.pmsRoomService.queryRoomLockByConds(pmsorderid);
						logger.info("OTSMessage::PmsOrderServiceImpl:id：{}:roomLockPo:{}", order.get("id"), roomLockPo);
						// 判断是否存在
						if (roomLockPo == null) {
							// 如果不存在就添加房态 增加房态
							roomLockPo = new RoomLockPo();
							roomLockPo.setPmsorderid(pmsorderid);
							roomLockPo.setRoomjson(customNo.getString("day"));
							logger.info("OTSMessage::PmsOrderServiceImpl:id：{}:roomLockPo:{}", order.get("id"),
									JsonKit.toJson(roomLockPo));
							this.pmsRoomService.saveRoomLock(roomLockPo);

							try {
								this.shiftRoomForPromo(order, roomLockPo != null);
							} catch (Exception ex) {
								logger.warn(String.format("failed to makeUpForPromo on hotelId:%s; customNo:%s...",
										hotelId, customNo), ex);
							}
						} else {
							try {
								// 释放原来的房态 add jianghe
								// 根据pmsroomorder找order
								OtaRoomOrder roomOrder = AppUtils.getBean(OrderServiceImpl.class)
										.findRoomOrderByPmsRoomOrderNoAndHotelId((String) customNo.get("customeno"),
												hotelId);
								if (roomOrder != null) {
									OtaOrder otaorder = new OtaOrder();
									otaorder.setId(roomOrder.getLong("otaorderid"));
									otaorder.setHotelId(hotelId);
									roomstateService.unlockRoomInOTS(otaorder);
									logger.info("huangfang:otaorderid:" + roomOrder.getLong("otaorderid") + ",hotelid:"
											+ hotelId);
								}
								// end
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("pms2.0换房失败：{},{},{}", (String) customNo.get("customeno"), hotelId,
										e.getMessage());
								logger.error("huangfang:otaorderid:" + (String) customNo.get("customeno") + ",hotelid:"
										+ hotelId);
							}

							// 修改房态
							roomLockPo.setRoomjson(customNo.getString("day"));
							this.pmsRoomService.updateRoomLock(roomLockPo);
							logger.info("OTSMessage::PmsOrderServiceImpl:id：{}:roomLockPo:{}", order.get("id"),
									JsonKit.toJson(roomLockPo));
						}
					}
				}
			}

			// 处理roomOrder中非null值
			if (returnlist.size() > 0) {
				for (PmsRoomOrder roomOrder : returnlist) {
					Set<Long> tempset = map.get(Long.parseLong(String.valueOf(roomOrder.get("HotelId"))));
					if (tempset == null) {
						tempset = new HashSet<Long>();
						map.put(Long.parseLong(String.valueOf(roomOrder.get("HotelId"))), tempset);
					}
					tempset.add(roomOrder.getLong("roomtypeid"));
				}
				// pms订单变更后需要对ots订单状态进行变更
				this.ChangeOtaOrder(returnlist, unlockParam);
			}
			data.put("success", true);
			data.put("set", map);
		} catch (Exception e) {
			e.printStackTrace();
			NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::saveCustomerNo::error:{}",
					e.getMessage());
			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			throw new PmsException(PmsErrorEnum.noknowError.getErrorCode(), e.getLocalizedMessage());
		}
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::saveCustomerNo::完成.data{}", data);
		return data;
	}

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

	private Room findVCRooms(Long hotelid, Long roomtypeid, Date begindate, Date enddate) throws Exception {
		Room vcRoom = null;

		try {
			if (hotelid != null && begindate != null && enddate != null) {
				String begindateday = defaultFormat.format(begindate);
				String enddateday = defaultFormat.format(enddate);

				vcRoom = roomstateService.findVCHotelRoom(hotelid, roomtypeid, begindateday, enddateday);
			} else {
				logger.warn("illegal parameters passed in findVCRooms...");
			}
		} catch (Exception ex) {
			throw new Exception(String.format("failed to findVCHotelRoom for hotelid:%s; begindate:%s; enddate:%s",
					hotelid, begindate, enddate), ex);
		}

		return vcRoom;
	}

	private void doShiftRoom(String hotelid, Long pmsroomtypeid, Long pmsroomid, PmsRoomOrder pmsRoomOrder)
			throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("about to doShiftRoom with pmsroomtypeid:%s; pmsroomid:%s; ", pmsroomtypeid,
					pmsroomid));
		}

		List<Map<String, Object>> promoRooms = roomSaleMapper.queryRoomPromoByType(String.valueOf(pmsroomtypeid));
		if (promoRooms != null && promoRooms.size() > 0) {
			final Map<String, Object> promoRoom = promoRooms.get(0);

			Integer roomId = (Integer) promoRoom.get("roomid");
			Integer saleRoomtypeId = (Integer) promoRoom.get("saleroomtypeid");
			Integer roomtypeId = (Integer) promoRoom.get("roomtypeid");

			if (logger.isInfoEnabled()) {
				logger.info(
						String.format("about to findroomprice with hotelid:%s; roomtypeid:%s; begintime:%s; endtime:%s",
								hotelid, pmsroomtypeid, promoRoom.get("starttime"), promoRoom.get("endtime")));
			}

			Roomtype roomtype = null;
			try {
				roomtype = findRoomSalePrice(hotelid, roomtypeId != null ? roomtypeId.longValue() : 0L,
						(Date) promoRoom.get("starttime"), (Date) promoRoom.get("endtime"));
			} catch (Exception ex) {
				logger.warn("failed to findRoomSalePrice, quit shifting room...", ex);
				return;
			}

			if (roomId == null) {
				List<TRoomModel> models = roomMapper.findList(roomtypeId != null ? roomtypeId.longValue() : 0);
				Room vcRoom = findVCRooms(Long.valueOf(hotelid), roomtypeId != null ? roomtypeId.longValue() : 0,
						(Date) promoRoom.get("starttime"), (Date) promoRoom.get("endtime"));
				TRoomModel roomModel = isRoomExisted(models, vcRoom.getRoomid());

				if (logger.isInfoEnabled()) {
					logger.info(String.format("vcroom:%s; roomModel:%s", vcRoom == null, roomModel == null));
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

	private void shiftRoomForPromo(PmsRoomOrder pmsRoomOrder, boolean isChanged) throws Exception {
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
			if (newroomtypeid != null) {
				newRooms = roomSaleConfigMapper.getRoomSaleByParamsNew(newRoomsaleConfig);
				if (newRooms != null && newRooms.size() > 0) {
					isInPromo = isInPromo(newRooms.get(0).getStartDate(), newRooms.get(0).getEndDate(),
							newRooms.get(0).getStartTime(), newRooms.get(0).getEndTime());
				}
			}

			if(StringUtils.isNotBlank(pmsRoomOrderNo))
			{
				/**
				 * checks if this order comes from ota
				 */
				Map<String, Object> orderParameters = new HashMap<String, Object>();
				orderParameters.put("pmsroomorderno", pmsRoomOrderNo);

				List<Map<String, Object>> orderResponse = roomMapper.selectOtaRoomOrder(orderParameters);
				if (orderResponse != null && orderResponse.size() > 0) {
					isNotFromOta = false;
				}				
			}
		} catch (Exception ex) {
			logger.warn("failed to invoke getRoomSaleByParamsNew or isInPromo...", ex);
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format("checking isInPromo:%s; status:%s; isNotFromOta:%s", isInPromo, status, isNotFromOta));
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
			if (logger.isInfoEnabled()) {
				logger.info("there is room change detected...");
			}
			TRoomSaleConfig oldRoomsaleConfig = new TRoomSaleConfig();
			oldRoomsaleConfig.setRoomTypeId(oldroomtypeid == null ? 0 : oldroomtypeid.intValue());
			oldRoomsaleConfig.setValid("T");
			oldRoomsaleConfig.setTag(0);

			List<TRoomSaleConfig> oldRooms = roomSaleConfigMapper.getRoomSaleByParamsNew(oldRoomsaleConfig);
			boolean isOldPromo = oldRooms != null ? oldRooms.size() > 0 : false;
			boolean isNewPromo = newRooms != null ? newRooms.size() > 0 : false;

			/**
			 * shift room from non-promo rooms to promo news, shift required
			 */
			if (!isOldPromo && isNewPromo) {
				doShiftRoom(hotelid, newroomtypeid, newroomid, pmsRoomOrder);
			}
			/**
			 * shift room from promo rooms to promo rooms, while different promo
			 * is detected, shift required
			 */
			else if (!isOldPromo && !isNewPromo && (newroomtypeid != oldroomtypeid)) {
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

	private PmsRoomOrder updatePmsRoomOrder(EHotel hotel, PmsRoomOrder newOrder, JSONObject customNo, Long hotelId)
			throws Exception {
		String pmsStates = "RE,RX,IN,IX,PM,OK";
		PmsRoomOrder order = null;
		if (newOrder != null) {
			order = newOrder;
		} else {
			order = this.hotelDAO.findPmsRoomOrderByHotelIdAndCustomno(hotelId, (String) customNo.get("customerno"));
		}

		if (order == null) {
			order = new PmsRoomOrder();
			order.set("HotelId", hotel.get("Id"));
			order.set("HotelPms", hotel.get("Pms"));
			order.set("RoomCost", customNo.get("price"));
			order.set("OtherCost", customNo.get("cost"));
		}

		if (StringUtils.isNotBlank(order.getStr("Status")) && StringUtils.isNotBlank((String) customNo.get("status"))
				&& (pmsStates.indexOf((String) customNo.get("status")) < pmsStates.indexOf(order.getStr("Status")))) {
			NewPmsOrderServiceImpl.logger.info("NewPmsOrderServiceImpl::saveCustomerNo::错误PMS状态，当前客单状态已经是{},不能再{}",
					order.getStr("Status"), customNo.get("status"));
			return null;
		}
		if (PmsRoomOrderStatusEnum.RE.getId().equals(customNo.get("status"))) {
			if (customNo.getString("arrivetime") != null) {
				order.set("BeginTime", DateUtils.getDateFromString(customNo.getString("arrivetime")));
			}
			if (customNo.getString("leavetime") != null) {
				order.set("EndTime", DateUtils.getDateFromString(customNo.getString("leavetime")));
			}
		} else if (PmsRoomOrderStatusEnum.IN.getId().equals(customNo.get("status"))) {
			if (customNo.getString("checkintime") != null) {
				order.set("CheckInTime", DateUtils.getDateFromString(customNo.getString("checkintime")));
			}
			// 续住的场景
			if ((customNo.getString("checkouttime") != null)
					&& !customNo.getString("checkouttime").equals(order.get("EndTime"))) {
				order.set("EndTime", DateUtils.getDateFromString(customNo.getString("checkouttime")));
			}
		} else if (PmsRoomOrderStatusEnum.OK.getId().equals(customNo.get("status"))
				|| PmsRoomOrderStatusEnum.PM.getId().equals(customNo.get("status"))) {
			if (customNo.getString("checkouttime") != null) {
				order.set("CheckOutTime", DateUtils.getDateFromString(customNo.getString("checkouttime")));
			}
		}
		// 为了弥补由于错过了一些状态 导致begintime、endtime为空
		if ((customNo.getString("arrivetime") != null) && (order.get("BeginTime") == null)) {
			order.set("BeginTime", DateUtils.getDateFromString(customNo.getString("arrivetime")));
		}
		if ((customNo.getString("checkintime") != null) && (order.get("BeginTime") == null)) {
			order.set("BeginTime", DateUtils.getDateFromString(customNo.getString("checkintime")));
		}
		if ((customNo.getString("leavetime") != null) && (order.get("EndTime") == null)) {
			order.set("EndTime", DateUtils.getDateFromString(customNo.getString("leavetime")));
		}
		if ((customNo.getString("excheckouttime") != null) && (order.get("EndTime") == null)) {
			order.set("EndTime", DateUtils.getDateFromString(customNo.getString("excheckouttime")));
		}

		order.set("Opuser", "OTA接口");//
		order.set("OrderType", PriceTypeEnum.getByKey((String) customNo.get("ordertype")).getId());
		order.set("OtherPay", customNo.get("payment"));// 以支付金额
		order.set("PmsOrderNo", customNo.get("orderid"));
		order.set("PmsRoomOrderNo", customNo.get("customeno"));
		order.set("Status", customNo.get("status"));
		if (PmsRoomOrderStatusEnum.OK.getId().equals(customNo.get("recstatus"))) {
			Date bg = order.getDate("BeginTime");
			Date ed = order.get("EndTime");
			Set<String> times = new HashSet();
			while (bg.before(ed)) {
				times.add(DateUtils.getStringFromDate(bg, DateUtils.FORMATSHORTDATETIME));
				bg = DateUtils.addDays(bg, 1);
			}
			Date curDate = new Date();
			if (curDate.getHours() > 12) {// 解决延住的问题
				times.add(DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME));
			}
			String[] checkInDate = times.toArray(new String[] {});
		}
		Long roomid = 0L;
		TRoom room = this.hotelDAO.findTRoomByHotelIdAndPmsno(hotelId, (String) customNo.get("roomno"));
		if (room != null) {
			if (room.getLong("Id") != order.getLong("RoomId")) {
				order.put("OldRoomId", order.get("RoomId"));// 记录被更换的旧房间信息
				order.put("OldRoomNo", order.get("RoomNo"));
				order.put("OldRoomTypeId", order.get("RoomTypeId"));
				order.put("OldRoomTypeName", order.get("RoomTypeName"));
			}
			roomid = room.getLong("id");
			order.set("RoomId", room.get("Id"));
			order.set("RoomPms", room.get("Pms"));
			order.set("RoomNo", room.get("Name"));
			order.set("RoomTypeId", room.getRoomType().get("Id"));
			order.set("RoomTypeName", room.getRoomType().get("Name"));
			order.set("RoomTypePms", room.getRoomType().get("Pms"));
		}
		if (order.get("RoomTypePms") == null) {
			// 有垃圾数据
			return null;
		}
		order.set("Visible", "T");
		order.saveOrUpdate();
		return order;
	}

	public void ChangeOtaOrder(List<PmsRoomOrder> pmsRoomOrderList, Map unlockParam) {
		NewPmsOrderServiceImpl.logger.info("OTSMessage::roomOrderList::换房::{}", pmsRoomOrderList);
		if (pmsRoomOrderList == null) {
			NewPmsOrderServiceImpl.logger.error("OTSMessage::roomOrderList:null:return");
			return;
		} else {
			NewPmsOrderServiceImpl.logger.info("OTSMessage::roomOrderList:size=" + pmsRoomOrderList.size());
		}

		List<ChangeRoomOrderBean> roomOrderList = new ArrayList<ChangeRoomOrderBean>();
		StringBuffer hotelId = new StringBuffer();
		Map<Long, Set<Long>> map = new HashMap<Long, Set<Long>>();
		long hotelid = 0;
		for (PmsRoomOrder pmsRoomOrder : pmsRoomOrderList) {
			ChangeRoomOrderBean changeRoomOrderBean = new ChangeRoomOrderBean();
			changeRoomOrderBean.setHotelId(pmsRoomOrder.getLong("HotelId"));
			hotelId.append(pmsRoomOrder.getLong("HotelId")).append(",");
			if ((pmsRoomOrder.get("OldRoomId") != null)
					&& !pmsRoomOrder.get("OldRoomId").equals(pmsRoomOrder.get("RoomId"))) {
				changeRoomOrderBean.setOldRoomId(pmsRoomOrder.getLong("OldRoomId"));
				changeRoomOrderBean.setOldRoomNo(pmsRoomOrder.getStr("OldRoomNo"));
				changeRoomOrderBean.setOldRoomTypeId(pmsRoomOrder.getLong("OldRoomTypeId"));
				changeRoomOrderBean.setOldRoomTypeName(pmsRoomOrder.getStr("OldRoomTypeName"));
			}
			changeRoomOrderBean.setNewRoomNo(pmsRoomOrder.getStr("RoomNo"));
			changeRoomOrderBean.setNewRoomTypeId(pmsRoomOrder.getLong("RoomTypeId"));
			changeRoomOrderBean.setNewRoomTypeName(pmsRoomOrder.getStr("RoomTypeName"));
			changeRoomOrderBean.setRoomId(pmsRoomOrder.getLong("RoomId"));
			changeRoomOrderBean.setBeginTime(pmsRoomOrder.getDate("BeginTime"));
			changeRoomOrderBean.setEndTime(pmsRoomOrder.getDate("EndTime"));
			changeRoomOrderBean.setPmsRoomOrderNo(pmsRoomOrder.getStr("PmsRoomOrderNo"));
			changeRoomOrderBean.setStatus(pmsRoomOrder.getStr("Status"));
			NewPmsOrderServiceImpl.logger.info("OTSMessage::roomOrderList::换房::pmsRoomOrder::{}", pmsRoomOrder);
			// 1,修改ota客单
			OtaRoomOrder roomOrder = AppUtils.getBean(OrderServiceImpl.class).findRoomOrderByPmsRoomOrderNoAndHotelId(
					changeRoomOrderBean.getPmsRoomOrderNo(), changeRoomOrderBean.getHotelId());
			Long otaorderid = (roomOrder != null) ? roomOrder.getLong("otaorderid") : -111l;
			NewPmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder-修改ota客单:orderid:{}-{}", otaorderid,
					roomOrder);
			if (roomOrder != null) {
				roomOrder.set("BeginTime", changeRoomOrderBean.getBeginTime());
				roomOrder.set("EndTime", changeRoomOrderBean.getEndTime());
				// 保存orderlog
				this.orderLogService.findOrderLog(roomOrder.getLong("otaorderid"))
						.set("checkinTime", changeRoomOrderBean.getBeginTime())
						.set("checkoutTime", changeRoomOrderBean.getEndTime()).saveOrUpdate();
				if (!roomOrder.get("Id").equals(changeRoomOrderBean.getRoomId())) {
					roomOrder.set("RoomId", changeRoomOrderBean.getRoomId().toString());
					NewPmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder-换房otaorderid::{},ota客单-{}",
							roomOrder.get("otaorderid"), roomOrder);
					ChangeRoom changeRoom = new ChangeRoom();
					changeRoom.set("changetime", new Date());
					changeRoom.set("hotelid", changeRoomOrderBean.getHotelId());
					THotel hotel = this.hotelService.readonlyTHotel(changeRoomOrderBean.getHotelId());
					if (hotel != null) {
						changeRoom.set("hotelname", hotel.getStr("HotelName"));
					}
					changeRoom.set("NewRoomId", changeRoomOrderBean.getRoomId());
					changeRoom.set("NewRoomNo", changeRoomOrderBean.getNewRoomNo());
					changeRoom.set("NewRoomTypeId", changeRoomOrderBean.getNewRoomTypeId().toString());
					changeRoom.set("NewRoomTypeName", changeRoomOrderBean.getNewRoomTypeName());
					changeRoom.set("OldRoomId", changeRoomOrderBean.getOldRoomId());
					changeRoom.set("OldRoomNo", changeRoomOrderBean.getOldRoomNo());
					changeRoom.set("OldRoomTypeId", changeRoomOrderBean.getOldRoomTypeId());
					changeRoom.set("OldRoomTypeName", changeRoomOrderBean.getOldRoomTypeName());
					String pmsRoomOrderNo = changeRoomOrderBean.getPmsRoomOrderNo();

					changeRoom.set("roomOrderId", pmsRoomOrder.get("id"));
					if ((changeRoomOrderBean.getOldRoomId() != null) && !ObjectUtils
							.equals(changeRoomOrderBean.getRoomId(), changeRoomOrderBean.getOldRoomId())) {
						NewPmsOrderServiceImpl.logger.info(
								"OTSMessage::changePmsRoomOrder::changeRoom.saveOrUpdate.otaorderid:{},换房信息:changeRoom:",
								roomOrder.get("otaorderid"), changeRoom);
						changeRoom.saveOrUpdate();
					}
				}
				String status = changeRoomOrderBean.getStatus();
				NewPmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder---status:{}", status);
				this.orderService.changeOrderStatusByPms(roomOrder.getLong("otaorderid"), pmsRoomOrder,
						pmsRoomOrder.getStr("freqtrv"));
			}

			// 2,记录 酒店ID和对应的房型IDs,提供下面计算缓存
			Set<Long> tempset = map.get(changeRoomOrderBean.getHotelId());
			if (tempset == null) {
				tempset = new HashSet<Long>();
				map.put(changeRoomOrderBean.getHotelId(), tempset);
			}
			if (changeRoomOrderBean.getRoomTypeId() != null) {
				tempset.add(changeRoomOrderBean.getRoomTypeId());
			}
			if (changeRoomOrderBean.getNewRoomTypeId() != null) {
				tempset.add(changeRoomOrderBean.getNewRoomTypeId());
			}
			if (changeRoomOrderBean.getOldRoomTypeId() != null) {
				tempset.add(changeRoomOrderBean.getOldRoomTypeId());
			}
			hotelid = changeRoomOrderBean.getHotelId();
		}
		/*
		 * NewPmsOrderServiceImpl.logger.info(
		 * "OTSMessage::cancelBookedRoomFromPMS::{}", unlockParam); if
		 * (unlockParam.size() == 3) { String cityCode = (String)
		 * unlockParam.get("cityCode"); Long roomId = (Long)
		 * unlockParam.get("roomId"); String[] checkInDate = (String[])
		 * unlockParam.get("checkInDate"); NewPmsOrderServiceImpl.logger.info(
		 * "OTSMessage::cancelBookedRoomFromPMS::checkInDate::{}", checkInDate);
		 * this.roomService.cancelBookedRoomFromPMS(cityCode, roomId,
		 * checkInDate); NewPmsOrderServiceImpl.logger.info(
		 * "OTSMessage::cancelBookedRoomFromPMS::ok"); } else {
		 * this.roomService.readonlyCalCacheByHotelidBatch(hotelid); }
		 */
		NewPmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder---end");
	}

	/**
	 * 有效订单和客单
	 *
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject synOrder(JSONObject param) {
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::synOrder::全量更新::" + param);
		JSONObject data = new JSONObject();
		// 记录下酒店哪些房型发生了变化，为后面计算缓存传參
		Map<Long, Set<Long>> map = new HashMap<Long, Set<Long>>();
		Long hotelId = this.eHotelDAO.findEHotelIdByPms(param.getString("hotelid"));
		String synLockKey = RedisCacheName.IMIKE_OTS_SYNORDER_KEY + hotelId;
		String synLockValue = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			NewPmsOrderServiceImpl.logger
					.info("synOrder::synLockKey:" + synLockKey + "-----currentDate:" + sdf.format(new Date()));
			// 加redis锁，防止重复请求
			if ((synLockValue = DistributedLockUtil.tryLock(synLockKey, 60)) == null) {
				NewPmsOrderServiceImpl.logger.info("NewPmsOrderServiceImpl::synOrder::" + hotelId + "-----正在进行中,重复请求");
				return null;
			}
			NewPmsOrderServiceImpl.logger.info("NewPmsOrderServiceImpl::synOrder::synLockValue:" + synLockValue);

			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			if (hotel == null) {
				data.put("success", false);
				data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
				data.put("errormsg", "OTS里没有这个id" + hotelId + "的酒店");
				return data;
			}
			NewPmsOrderServiceImpl.logger.info("同步[" + hotel.getHotelName() + "]酒店的所有有效清单");
			this.hotelDAO.resetAllOrder(hotelId);

			if (param.containsKey("customerno")) {
				JSONArray customList = param.getJSONArray("customerno");
				JSONObject saveCustomerNo = new JSONObject();
				saveCustomerNo.put("hotelid", hotelId);
				saveCustomerNo.put("customerno", customList);
				saveCustomerNo.put("set", map);
				JSONObject customerNoMap = this.saveCustomerNo(saveCustomerNo);
				map = (Map<Long, Set<Long>>) customerNoMap.get("set");
			}

			/*
			 * NewPmsOrderServiceImpl.logger.info(
			 * "OTSMessage::resetRoomStatusByHotelidFromPMS::synOrder::全量更新::hotelid:{}",
			 * hotelId);
			 * this.roomService.resetRoomStatusByHotelidFromPMS(hotelId);
			 */

			/*
			 * if (param.containsKey("lock")) { JSONArray lock =
			 * param.getJSONArray("lock"); for (int i = 0; i < lock.size(); i++)
			 * { JSONObject lc = lock.getJSONObject(i); TRoom room =
			 * this.hotelDAO.findTRoomByHotelIdAndPmsno(hotelId,
			 * lc.getString("roomid")); Long roomid = room.getLong("id"); Date
			 * bg = DateUtils.getDateFromString(lc.getString("begintime")); Date
			 * ed = DateUtils.getDateFromString(lc.getString("endtime"));
			 * Set<String> times = new HashSet(); while (bg.before(ed)) {
			 * times.add(DateUtils.getStringFromDate(bg,
			 * DateUtils.FORMATSHORTDATETIME)); bg = DateUtils.addDays(bg, 1); }
			 * Date curDate = new Date(); if (curDate.getHours() > 12) {//
			 * 解决延住的问题 times.add(DateUtils.getStringFromDate(new Date(),
			 * DateUtils.FORMATSHORTDATETIME)); } String[] checkInDate =
			 * times.toArray(new String[] {}); String cityCode =
			 * hotel.getCityCode();
			 * this.roomService.cancelBookedRoomFromPMS(cityCode, roomid,
			 * checkInDate); } }
			 */
			// // 同步订单客单数据，清楚缓存
			// for (Long hotelid : map.keySet()) {
			// EventBusHelper.getEventBus().post(new
			// PmsQueryAgainEvent(hotelid));
			// }
			data.put("success", true);
			// } catch (MyException e) {
			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			// throw e;
		} finally {
			// 消除锁
			if (StringUtil.isNotEmpty(synLockValue)) {
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::synOrder::完成.data{}", data);
		return data;
	}

	/**
	 * 订单结算 saveOrderCharge
	 *
	 * @param request
	 * @return
	 */
	@Override
	public JSONObject orderCharge(JSONObject param) {
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::orderCharge::" + param);
		JSONObject data = new JSONObject();
		try {
			Long hotelId = (Long) param.get("hotelid");
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			NewPmsOrderServiceImpl.logger.info("更新[" + hotel.getHotelName() + "]酒店的客单结算");

			JSONArray customerno = param.getJSONArray("customerno");
			for (int i = 0; i < customerno.size(); i++) {
				JSONObject orderCharge = customerno.getJSONObject(i);
				PmsRoomOrder order = this.hotelDAO.findPmsRoomOrderByHotelIdAndCustomno(hotelId,
						orderCharge.getString("customeno"));
				if (order == null) {
					NewPmsOrderServiceImpl.logger.warn(
							"PMS要求结算一条不存在的客单记录，酒店id：" + hotelId + "中 酒店客单号为：" + orderCharge.get("custmoerid") + "");
				} else {
					order.set("OtherCost", orderCharge.get("othercost"));
					order.set("MikePay", orderCharge.get("mikepay"));
					order.set("OtherPay", orderCharge.get("nootapay"));
					order.set("RoomCost", orderCharge.get("roomcost"));
					order.saveOrUpdate();
				}
			}

			data.put("success", true);
		} catch (Exception e) {
			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			throw new PmsException(PmsErrorEnum.noknowError.getErrorCode(), e.getLocalizedMessage());
		}
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::orderCharge::完成.data{}", data);
		return data;
	}

	/**
	 * 房费清单
	 *
	 * @param hotelId
	 * @param order
	 * @return
	 */
	@Override
	public JSONObject roomCharge(JSONObject param) {
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::roomCharge" + param);
		JSONObject data = new JSONObject();
		Long hotelId = (Long) param.get("hotelid");
		String synLockKey = RedisCacheName.IMIKE_OTS_ROOMCHARGE_KEY + hotelId;
		String synLockValue = null;
		try {
			// 加redis锁，防止重复请求
			if ((synLockValue = DistributedLockUtil.tryLock(synLockKey, 60)) == null) {
				NewPmsOrderServiceImpl.logger.info("NewPmsOrderServiceImpl::roomCharge::" + hotelId + ",重复请求");
				return data;
			}
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			NewPmsOrderServiceImpl.logger.info("更新[" + hotel.getHotelName() + "]酒店的客单清单");

			List<String> ids = new ArrayList<>();
			JSONArray orderCharge = param.getJSONArray("customerno");
			if (orderCharge != null && orderCharge.size() > 0) {
				for (int i = 0; i < orderCharge.size(); i++) {
					JSONObject pmsCostData = orderCharge.getJSONObject(i);
					ids.add((String) pmsCostData.get("id"));
				}
				// 全查出本次夜审的已有的数据
				List<PmsCost> costs = this.hotelDAO.findPmsCostByHotelIdAndCostId(hotelId, ids);
				Map<String, PmsCost> map = new HashMap<>();
				for (PmsCost pmsCost : costs) {
					map.put((String) pmsCost.get("roomcostno"), pmsCost);
				}
				List<BizModel> datas = new ArrayList<>();
				for (int i = 0; i < orderCharge.size(); i++) {
					JSONObject pmsCostData = orderCharge.getJSONObject(i);
					PmsCost cost = new PmsCost();
					if (map.containsKey(pmsCostData.get("id"))) {
						cost = map.get(pmsCostData.get("id"));
					} else {
						cost.setHotelId(hotelId);
						cost.setHotelPms(hotel.getStr("Pms"));
						cost.setRoomCostNo((String) pmsCostData.get("id"));
					}
					cost.setCustomerno((String) pmsCostData.get("customeno"));
					cost.setCosttime(DateUtils.getDateFromString(pmsCostData.getString("bizday")));
					PmsCostTypeEnum costType = PmsCostTypeEnum
							.findPmsCostTypeEnumById(pmsCostData.getString("costtype"));
					if (costType != null) {
						cost.setCostType(costType.getId());
					} else {
						cost.setCostType("");
					}
					cost.setOpuser((String) pmsCostData.get("opuser"));
					cost.setRoomCost((BigDecimal) pmsCostData.get("roomcost"));
					cost.setOtherCost((BigDecimal) pmsCostData.get("price"));
					PmsCostSourceEnum source = PmsCostSourceEnum
							.findPmsCostSourceEnumById(pmsCostData.getString("source"));
					if (source != null) {
						cost.setSource(source.getId());
					}
					datas.add(cost);
				}
				NewPmsOrderServiceImpl.logger.info("datas:{}", datas.toArray());
				Db.batchBeans(datas, 100);
			}
			data.put("success", true);

		} catch (Exception e) {
			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			throw new PmsException(PmsErrorEnum.noknowError.getErrorCode(), e.getLocalizedMessage());
		} finally {
			// 消除锁
			if (StringUtil.isNotEmpty(synLockValue)) {
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		NewPmsOrderServiceImpl.logger.info("OTSMessage::NewPmsOrderServiceImpl::roomCharge::OK.data{}", data);
		return data;
	}

	protected void saveReceiveLogSuccess(PmsLog pmslog) {
		pmslog.setReslut(PmsResultEnum.success.getId());
		XmlLogFileManager.getInstance().writeXmlLogFile(pmslog);
	}

	@Override
	public List<PmsOrder> findNeedPmsOrderSelect(Long hotelid) {
		return this.hotelDAO.findNeedPmsOrderSelect(hotelid);
	}

	@Override
	public void deletePmsOrder(List<PmsOrder> oldlist) {
		if ((oldlist == null) || (oldlist.size() == 0)) {
			return;
		}
		for (PmsOrder pmsOrder : oldlist) {
			PmsOrder old = this.hotelDAO.findPmsOrderByHotelIdAndPmsOrderNo(
					Long.parseLong(String.valueOf(pmsOrder.get("hotelid"))), String.valueOf(pmsOrder.get("pmsorderno")),
					String.valueOf(pmsOrder.get("pmsroomtypeorderno")));
			if (old != null) {
				old.set("visible", "T");
				old.set("cancel", "T");
				old.saveOrUpdate();
			}
		}
	}

	private void flushCacheByHotelidAndRoomTypeId(List<PmsOrder> returnlist) {
		// 需要刷新缓存，更新房态
		for (PmsOrder pmsOrder : returnlist) {
			NewPmsOrderServiceImpl.logger.info("savePmsRoomTypeOrder清楚缓存：hotelid:" + pmsOrder.getLong("HotelId")
					+ "roomtypeid:" + pmsOrder.getLong("RoomTypeId"));
			try {
				EventBusHelper.getEventBus()
						.post(new PmsCalCacheEvent(pmsOrder.getLong("HotelId"), pmsOrder.getLong("RoomTypeId")));
			} catch (Exception e) {
				NewPmsOrderServiceImpl.logger.error("savePmsRoomTypeOrder清楚缓存：hotelid:" + pmsOrder.getLong("HotelId")
						+ "roomtypeid:" + pmsOrder.getLong("RoomTypeId"));
			}
		}
	}

	@Override
	public List<PmsRoomOrder> findNeedPmsRoomOrderSelect(Long hotelid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject genJsonDataSaveCustomerNo(String json) {
		JSONObject data = new JSONObject();
		try {
			NewPmsOrderServiceImpl.logger.info("saveCustomerNo::参数json:{}", json);
			JSONObject param = JSON.parseObject(json);
			String hotelid = param.getString("hotelid");
			JSONArray customerno = param.getJSONArray("customerno");
			// 新pms给的是pms编码，需要查
			Long hotelId = this.eHotelDAO.findEHotelIdByPms(hotelid);
			for (int i = 0; i < customerno.size(); i++) {
				JSONObject customNo = customerno.getJSONObject(i);
				if (customNo.containsKey("roomtypeid")) {
					Long roomtypeid = this.eHotelDAO.findRoomTypeIdByPms(hotelId, (String) customNo.get("roomtypeid"));
					NewPmsOrderServiceImpl.logger.info("saveCustomerNo::roomtypeid:{}", roomtypeid);
					if (roomtypeid == null) {
						throw MyErrorEnum.customError.getMyException("找不到roomtype信息，hotelid:" + hotelId
								+ "，pmsroomtypeid:" + (String) customNo.get("roomtypeid"));
					}
					customNo.put("roomtypeid", String.valueOf(roomtypeid.intValue()));
				}
			}
			param.put("hotelid", hotelId);
			param.put("customerno", customerno);
			NewPmsOrderServiceImpl.logger.info("saveCustomerNo::newPmsOrderService.saveCustomerNo:go");
			JSONObject jsonData = this.saveCustomerNo(param);
			jsonData.remove("set");
			return jsonData;
		} catch (Exception e) {
			e.printStackTrace();
			NewPmsOrderServiceImpl.logger.info("genJsonDataSaveCustomerNo::error:{}", e.getLocalizedMessage());
			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			throw new PmsException(PmsErrorEnum.noknowError.getErrorCode(), e.getLocalizedMessage());
		}
		// return data;
	}

	@Override
	public JSONObject genJsonDataRoomCharge(String json) {
		JSONObject data = new JSONObject();
		try {
			NewPmsOrderServiceImpl.logger.info("roomCharge::参数json:{}", json);
			JSONObject param = JSON.parseObject(json);
			String hotelid = param.getString("hotelid");
			JSONArray customerno = param.getJSONArray("customerno");
			Long hotelId = this.eHotelDAO.findEHotelIdByPms(hotelid);
			param.put("hotelid", hotelId);
			param.put("customerno", customerno);
			return this.roomCharge(param);
		} catch (Exception e) {
			e.printStackTrace();
			NewPmsOrderServiceImpl.logger.info("genJsonDataSaveCustomerNo::error:{}", e.getLocalizedMessage());
			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			throw new PmsException(PmsErrorEnum.noknowError.getErrorCode(), e.getLocalizedMessage());
		}
		// return data;
	}

	@Override
	public JSONObject genJsonDataOrderCharge(String json) {
		JSONObject data = new JSONObject();
		try {
			NewPmsOrderServiceImpl.logger.info("orderCharge::参数json:{}", json);
			JSONObject param = JSON.parseObject(json);
			String hotelid = param.getString("hotelid");
			Long hotelId = this.eHotelDAO.findEHotelIdByPms(hotelid);
			param.put("hotelid", hotelId);

			return this.orderCharge(param);
		} catch (Exception e) {
			e.printStackTrace();
			NewPmsOrderServiceImpl.logger.info("genJsonDataSaveCustomerNo::error:{}", e.getLocalizedMessage());
			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			throw new PmsException(PmsErrorEnum.noknowError.getErrorCode(), e.getLocalizedMessage());
		}
		// return data;
	}

	@Override
	public JSONObject genJsonDataSynOrder(String json) {
		// JSONObject data = new JSONObject();
		// try {
		NewPmsOrderServiceImpl.logger.info("orderCharge::参数synOrder:{}", json);
		JSONObject param = JSON.parseObject(json);
		String hotelid = param.getString("hotelid");
		JSONArray customerno = param.getJSONArray("customerno");
		JSONArray lock = param.getJSONArray("lock");

		Long hotelId = this.eHotelDAO.findEHotelIdByPms(hotelid);
		for (int i = 0; i < customerno.size(); i++) {
			JSONObject customNo = customerno.getJSONObject(i);
			if (customNo.containsKey("roomtypeid")) {
				Long roomtypeid = this.eHotelDAO.findRoomTypeIdByPms(hotelId, (String) customNo.get("roomtypeid"));
				if (roomtypeid == null) {
					throw MyErrorEnum.customError.getMyException("找不到roomtype信息，hotelid:" + hotelId + "，pmsroomtypeid:"
							+ (String) customNo.get("roomtypeid"));
				}
				customNo.put("roomtypeid", String.valueOf(roomtypeid.intValue()));
				customNo.put("Pms", hotelid);
			}
		}
		param.put("hotelid", hotelid);
		param.put("customerno", customerno);
		param.put("lock", lock);
		return this.synOrder(param);
		// } catch (Exception e) {
		// e.printStackTrace();
		// NewPmsOrderServiceImpl.logger.info("genJsonDataSaveCustomerNo::error:{}",
		// e.getLocalizedMessage());
		// data.put("success", false);
		// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
		// data.put("errormsg", e.getLocalizedMessage());
		// }
		// return data;
	}

}