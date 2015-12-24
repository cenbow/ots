package com.mk.pms.order.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.bean.EHotel;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.dao.EHotelDAO;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.manager.RedisCacheName;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.PmsOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.service.OrderLogService;
import com.mk.ots.order.service.OrderServiceImpl;
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
	private OrderLogService orderLogService;
	@Autowired
	private PmsRoomService pmsRoomService;
	@Autowired
	private RoomstateService roomstateService;
	@Autowired
	private PmsShiftService pmsShiftService;

	/**
	 * 创建/修改客单 saveCustomerNo
	 *
	 * @param hotelId
	 * @param datalist
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS) 	
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
								pmsShiftService.shiftRoomForPromo(order, param.getString("type"), roomLockPo != null);
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

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS) 
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
				boolean isChanged = false;
				if (!roomOrder.get("Id").equals(changeRoomOrderBean.getRoomId())) {
					isChanged = true;
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

			try {
				pmsShiftService.shiftRoomForPromo(pmsRoomOrder, "1", true);
			} catch (Exception ex) {
				logger.error("failed to shiftRoomForPromo in ChangeOtaOrder...", ex);
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