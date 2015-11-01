
package com.mk.ots.hotel.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mk.framework.AppUtils;
import com.mk.framework.util.SerializeUtil;
import com.mk.orm.kit.JsonKit;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.*;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.mapper.*;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.model.PmsRoomOrderModel;
import com.mk.ots.restful.input.RoomstateQuerylistReqEntity;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity;
import com.mk.ots.room.bean.RoomCensus;
import com.mk.ots.roomsale.model.RoomPromoDto;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.service.RoomSaleService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.myenum.PmsRoomOrderStatusEnum;
import com.mk.pms.room.bean.RoomLockJsonBean;
import com.mk.pms.room.bean.RoomLockPo;
import com.mk.pms.room.service.PmsRoomService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * 房态服务类(git测试5)
 * 
 * @author chuaiqing.
 *
 */
@Service
public class RoomstateService {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(RoomstateService.class);

	private Gson gson = new Gson();

	/** 房态缓存key */
	public final String ROOMSTATE_CACHE_KEY = "RMST";

	/** 房型策略价缓存key */
	public final String ROOMCOST_CACHE_KEY = RoomstateService.class.getName().concat(":").concat("TIMEPRICES");

	/** 房型价格缓存key */
	public final String ROOMTYPEPRICE_CACHE_KEY = RoomstateService.class.getName().concat(":").concat("ROOMTYPEPRICE");

	/** pms锁房标识: 1 */
	public final String LOCKED_PMS = "1";
	/** ots锁房标识: 2 */
	public final String LOCKED_OTS = "2";
	/** 房间维修锁房标识: 3 */
	public final String LOCKED_REPAIR = "3";

	/** 房态标识：可用 */
	public final String ROOM_STATUS_VC = "vc";
	/** 房态标识：不可用 */
	public final String ROOM_STATUS_NVC = "nvc";

	/** 眯客策略价缓存key */
	public final String MIKE_TIMEPRICE = RoomstateService.class.getName().concat(":").concat("MIKE_TIMEPRICE");
	/** 眯客门市价缓存key */
	public final String MIKE_ROOMPRICE = RoomstateService.class.getName().concat(":").concat("MIKE_ROOMPRICE");

	@Autowired
	private OtsCacheManager cacheManager;

	@Autowired
	private THotelMapper tHotelMapper;
	@Autowired
	private RoomCensusMapper roomCensusMapper;
	@Autowired
	private THotelMapper thotelMapper;

	@Autowired
	private TRoomRepairMapper tRoomRepairMapper;

	@Autowired
	private RoomLockPoMapper roomLockPoMapper;

	@Autowired
	private HotelService hotelService;

	@Autowired
	private PmsRoomService pmsRoomService;

	@Autowired
	private TRoomMapper troomMapper;
	@Autowired
	private TRoomTypeMapper troomtypeMapper;
	@Autowired
	private TRoomRepairMapper troomRepairMapper;
	@Autowired
	private PmsRoomOrderMapper pmsRoomOrderMapper;
	@Autowired
	private TRoomtypeInfoMapper troomtypeInfoMapper;
	@Autowired
	private TPricetimeMapper tPricetimeMapper;
	@Autowired
	private TRoomTypeMapper tRoomTypeMapper;
	@Autowired
	private TFacilityMapper tFacilityMapper;

	@Autowired
	private CashBackService cashBackService;

	@Autowired
	private HotelPriceService hotelPriceService;

	@Autowired
	private RoomSaleService roomSaleService;

	/**
	 * 
	 * @param hotelid
	 * @return
	 */
	public String getCacheKey(Long hotelid) {
		return this.ROOMSTATE_CACHE_KEY + ":" + hotelid;
	}

	/**
	 * 房型策略价格缓存key
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @param roomtypeid
	 *            参数：房型id
	 * @return
	 */
	public String getCostCacheKey(Long hotelid, Long roomtypeid) {
		return this.ROOMCOST_CACHE_KEY + ":" + hotelid + "_" + roomtypeid;
	}

	public String getHotelPriceCacheKey(Long hotelid) {
		return this.ROOMTYPEPRICE_CACHE_KEY + ":" + hotelid;
	}

	public String getMikeTimepriceCacheKeyOfHotel(Long hotelid) {
		return this.MIKE_TIMEPRICE + ":" + hotelid;
	}

	public String getMikeRoompriceCacheKeyOfHotel(Long hotelid) {
		return this.MIKE_ROOMPRICE + ":" + hotelid;
	}

	/**
	 * 
	 * @param roomid
	 * @param lockDate
	 * @return
	 */
	public String getRoomLockedKey(Long roomid, String lockDate) {
		StringBuffer bfKey = new StringBuffer();
		bfKey.setLength(0);
		bfKey.append(roomid).append(Constant.CACHENAME_SEPARATOR).append(lockDate);
		return bfKey.toString();
	}

	/**
	 * 
	 * @param hotelid
	 * @return
	 */
	public Map<String, String> getLockRoomsCache(Long hotelid) {
		Map<String, String> lockRoomsCache = null;
		Jedis jedis = null;
		try {
			jedis = this.cacheManager.getNewJedis();
			byte[] temp = jedis.get(this.getCacheKey(hotelid).getBytes());
			if (temp != null) {
				lockRoomsCache = (Map<String, String>) SerializeUtil.unserialize(temp);
			}
		} catch (Exception e) {
			//
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return lockRoomsCache;
	}

	/**
	 * 重置酒店房态
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 */
	public void resetHotelRoomstate(Long hotelid) {
		this.logger.info("RoomstateService::resetHotelRoomstate method begin...");
		this.logger.info("parameter hotelid: {}", hotelid);
		Jedis jedis = null;
		try {
			jedis = this.cacheManager.getNewJedis();
			jedis.del(this.getCacheKey(hotelid).getBytes());
			this.logger.info(
					"RoomstateService::resetHotelRoomstate method sucess. hotelid: {} room state cache has deleted.",
					hotelid);
		} catch (Exception e) {
			this.logger.info("RoomstateService::resetHotelRoomstate method is error: {}.", e.getMessage());
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		this.logger.info("RoomstateService::resetHotelRoomstate method end.");
	}

	/**
	 * 查询指定酒店的房态缓存
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @param begindate
	 *            参数：开始日期（格式为yyyy-MM-dd）
	 * @param enddate
	 *            参数：结束日期（格式为yyyy-MM-dd）
	 * @return Map 返回值：缓存map，key为 roomid~日期（格式为yyyy-MM-dd）
	 * @throws Exception
	 */
	public Map<String, String> findBookedRoomsByHotelId(Long hotelid, String begindate, String enddate)
			throws Exception {

		Map<String, String> lockRoomsCache = new HashMap<String, String>();
		Jedis jedis = null;
		try {
			jedis = this.cacheManager.getNewJedis();
			byte[] temp = jedis.get(this.getCacheKey(hotelid).getBytes());
			if (temp != null) {
				// 如果已经存在酒店房态缓存，先把PMS锁的房态缓存删掉，保留OTS（C端）锁的房态缓存.
				lockRoomsCache = (Map<String, String>) SerializeUtil.unserialize(temp);
				if (lockRoomsCache != null) {
					Iterator<Entry<String, String>> iter = lockRoomsCache.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						String key = entry.getKey();
						String val = entry.getValue();
						if (!this.LOCKED_OTS.equals(val)) {
							iter.remove();
							this.logger.info("remove roomstate cache key: {}, value: {}", key, val);
						}
					}
				} else {
					lockRoomsCache = new HashMap<String, String>();
				}
			}
			// 将入参日期转换为yyyy-MM-dd hh:mm:ss格式
			begindate = DateUtils.formatDate(DateUtils.getDateFromString(begindate));
			enddate = DateUtils.formatDate(DateUtils.getDateFromString(enddate));
			Date queryBeginTime = DateUtils.getDateFromString(begindate.concat(" 00:00:00"));
			Date queryEndTime = DateUtils.getDateFromString(enddate.concat(" 23:59:59"));

			// 查询酒店房间维修数据
			List<TRoomRepairModel> troomRepairs = troomRepairMapper.findList(hotelid);

			// 查询该酒店的客单数据
			List<PmsRoomOrderModel> orders = pmsRoomOrderMapper.findByHotelId(hotelid);

			// 将酒店维修记录当做客单数据来处理锁房态
			for (TRoomRepairModel troomRepair : troomRepairs) {
				PmsRoomOrderModel troomRepairOrder = new PmsRoomOrderModel();
				troomRepairOrder.setBegintime(troomRepair.getBegintime());
				troomRepairOrder.setEndtime(troomRepair.getEndtime());
				String etime = DateUtils.formatTime(troomRepairOrder.getEndtime());
				int itime = DateUtils.strTimeToSeconds(etime);
				if ((itime > DateUtils.TWELVE_TIME_SECONDS)) {
					// 如果是维修记录，并且结束时间大于12点，锁下一天房
					troomRepairOrder.setEndtime(DateUtils.addDays(troomRepairOrder.getEndtime(), 1));
				}
				troomRepairOrder.setRoomid(troomRepair.getRoomid());
				// 房间维修当做入住处理，status='IN'
				troomRepairOrder.setStatus(PmsRoomOrderStatusEnum.IN.getId());
				// 添加到客单列表中
				orders.add(troomRepairOrder);
			}

			for (PmsRoomOrderModel order : orders) {
				if (order.getBegintime() == null) {
					continue;
				}
				// 处理入住时间为0到6点的预定数据
				String btime = DateUtils.formatTime(order.getBegintime());
				int itime = DateUtils.strTimeToSeconds(btime);
				if ((itime >= DateUtils.ZERO_TIME_SECONDS) && (itime <= DateUtils.SIX_TIME_SECONDS)) {
					// 0到6点预定的，锁前一天房
					order.setBegintime(DateUtils.addDays(order.getBegintime(), -1));
				}
				// 处理超过中午12点，应离未离(status='IN')的预定数据
				String etime = DateUtils.formatTime(order.getEndtime());
				if ("12:00:00".equals(etime) && PmsRoomOrderStatusEnum.IN.getId().equals(order.getStatus())
						&& (order.getEndtime().getTime() <= DateUtils.getDateFromString(enddate.concat(" 12:00:00"))
								.getTime())) {
					order.setEndtime(DateUtils.addDays(order.getEndtime(), 1));
				}
				// 只要查询的起始、截止日期中的任何一个日期在订单中的入住和离开时间范围之内的，该房间不可预定.
				// 经过处理的订单入住日期
				Date orderDateBegin = DateUtils.getDateFromString(DateUtils.formatDate(order.getBegintime()));
				// 经过处理的订单离开日期
				Date orderDateEnd = DateUtils.getDateFromString(DateUtils.formatDate(order.getEndtime()));

				// 查询开始日期
				Date queryDateBegin = DateUtils.getDateFromString(DateUtils.formatDate(queryBeginTime));
				// 查询截止日期
				Date queryDateEnd = DateUtils.getDateFromString(DateUtils.formatDate(queryEndTime));

				// 如果查询截止日期小于订单入住日期， 或者查询开始日期大于订单离开日期，房态可以预订，反之不可预订.
				if ((queryDateEnd.getTime() < orderDateBegin.getTime())
						|| (queryDateBegin.getTime() > orderDateEnd.getTime())) {
					// 房态可预订
					order.setRoomstate(this.ROOM_STATUS_VC);
				} else {
					// 房态不可预订
					order.setRoomstate(this.ROOM_STATUS_NVC);
				}
				if (this.ROOM_STATUS_VC.equals(order.getRoomstate())) {
					continue;
				}
				// 计算入住天数，锁每一天房态.
				int diffDays = DateUtils.selectDateDiff(DateUtils.formatDate(order.getEndtime()),
						DateUtils.formatDate(order.getBegintime()));
				for (int i = 0; i < diffDays; i++) {
					String lockDate = DateUtils.formatDate(DateUtils.addDays(order.getBegintime(), i));
					// 维修数据pmsroomorderno为空，锁房标识为3-LOCKED_REPAIR，否则锁房标识为1-LOCKED_PMS
					String lockedFlag = order.getPmsroomorderno() == null ? this.LOCKED_REPAIR : this.LOCKED_PMS;
					lockRoomsCache.put(this.getRoomLockedKey(order.getRoomid(), lockDate), lockedFlag);
					if (this.LOCKED_REPAIR.equals(lockedFlag)) {
						this.logger.info("缓存维修房态锁房信息, roomid: {}, date: {}", order.getRoomid(), lockDate);
					} else {
						this.logger.info("缓存PMS房态锁房信息, roomid: {}, date: {}", order.getRoomid(), lockDate);
					}
				}
			}
			// 放入redis缓存
			jedis.set(this.getCacheKey(hotelid).getBytes(), SerializeUtil.serialize(lockRoomsCache));
			this.logger.info("{} 酒店房态信息已缓存到redis。", hotelid);
		} catch (Exception e) {
			lockRoomsCache = null;
			this.logger.error("findRoomstatesByHotelId has error: {}", e.getMessage());
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return lockRoomsCache;
	}

	/**
	 * 查询指定酒店的房态缓存(新pms)
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @param begindate
	 *            参数：开始日期（格式为yyyy-MM-dd）
	 * @param enddate
	 *            参数：结束日期（格式为yyyy-MM-dd）
	 * @return Map 返回值：缓存map，key为 roomid~日期（格式为yyyy-MM-dd）
	 * @throws Exception
	 */
	public Map<String, String> findBookedRoomsByHotelIdNewPms(Long hotelid, String begindate, String enddate)
			throws Exception {
		Map<String, String> lockRoomsCache = new HashMap<String, String>();
		Jedis jedis = null;
		try {
			jedis = this.cacheManager.getNewJedis();
			byte[] temp = jedis.get(this.getCacheKey(hotelid).getBytes());
			if (temp != null) {
				// 如果已经存在酒店房态缓存，先把PMS锁的房态缓存删掉，保留OTS（C端）锁的房态缓存.
				lockRoomsCache = (Map<String, String>) SerializeUtil.unserialize(temp);
				if (lockRoomsCache != null) {
					Iterator<Entry<String, String>> iter = lockRoomsCache.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						String key = entry.getKey();
						String val = entry.getValue();
						if (!this.LOCKED_OTS.equals(val)) {
							iter.remove();
							this.logger.info("新房态remove roomstate cache key: {}, value: {}", key, val);
						}
					}
				} else {
					lockRoomsCache = new HashMap<String, String>();
				}
			}
			// 将入参日期转换为yyyy-MM-dd hh:mm:ss格式
			begindate = DateUtils.formatDate(DateUtils.getDateFromString(begindate));
			enddate = DateUtils.formatDate(DateUtils.getDateFromString(enddate));
			Date queryBeginTime = DateUtils.getDateFromString(begindate.concat(" 00:00:00"));
			Date queryEndTime = DateUtils.getDateFromString(enddate.concat(" 23:59:59"));

			// 查询酒店房间维修数据
			List<TRoomRepairModel> troomRepairs = this.tRoomRepairMapper.findList(hotelid);

			// 查询该酒店的客单数据(新pms)
			List<RoomLockPo> roomLockPos = this.roomLockPoMapper.findByHotelId(hotelid);

			// 将酒店维修记录当做客单数据来处理锁房态
			for (TRoomRepairModel troomRepair : troomRepairs) {
				// 处理维修时间为0到6点的预定数据
				String btime = DateUtils.formatTime(troomRepair.getBegintime());
				int itime = DateUtils.strTimeToSeconds(btime);
				if ((itime >= DateUtils.ZERO_TIME_SECONDS) && (itime <= DateUtils.SIX_TIME_SECONDS)) {
					// 0到6点预定的，锁前一天房
					troomRepair.setBegintime(DateUtils.addDays(troomRepair.getBegintime(), -1));
				}
				// 处理超过中午12点，应离未离(status='IN')的预定数据
				String etime = DateUtils.formatTime(troomRepair.getEndtime());
				int stime = DateUtils.strTimeToSeconds(etime);
				if ("12:00:00".equals(etime) && (troomRepair.getEndtime().getTime() <= DateUtils
						.getDateFromString(enddate.concat(" 12:00:00")).getTime())) {
					// 超过中午12点、应离未离处理，锁下一天房.
					troomRepair.setEndtime(DateUtils.addDays(troomRepair.getEndtime(), 1));
				} else if (stime < DateUtils.SIX_TIME_SECONDS) {
					// 小于6点 不锁当天
					troomRepair.setEndtime(DateUtils.addDays(troomRepair.getEndtime(), -1));
				}
				// 只要查询的起始、截止日期中的任何一个日期在订单中的入住和离开时间范围之内的，该房间不可预定.
				// 经过处理的订单入住日期
				Date orderDateBegin = DateUtils.getDateFromString(DateUtils.formatDate(troomRepair.getBegintime()));
				// 经过处理的订单离开日期
				Date orderDateEnd = DateUtils.getDateFromString(DateUtils.formatDate(troomRepair.getEndtime()));

				// 查询开始日期
				Date queryDateBegin = DateUtils.getDateFromString(DateUtils.formatDate(queryBeginTime));
				// 查询截止日期
				Date queryDateEnd = DateUtils.getDateFromString(DateUtils.formatDate(queryEndTime));
				// 如果查询截止日期小于订单入住日期， 或者查询开始日期大于订单离开日期，房态可以预订，反之不可预订.
				if ((queryDateEnd.getTime() < orderDateBegin.getTime())
						|| (queryDateBegin.getTime() > orderDateEnd.getTime())) {
					// 房态可预定
					continue;
				} else {
					// 房态不可预定
					// 计算锁房天数，锁每一天房态.
					int diffDays = DateUtils.selectDateDiff(DateUtils.formatDate(troomRepair.getEndtime()),
							DateUtils.formatDate(troomRepair.getBegintime()));
					for (int i = 0; i <= diffDays; i++) {
						String lockDate = DateUtils.formatDate(DateUtils.addDays(troomRepair.getBegintime(), i));
						// 维修数据pmsroomorderno为空，锁房标识为3-LOCKED_REPAIR，否则锁房标识为1-LOCKED_PMS
						lockRoomsCache.put(this.getRoomLockedKey(troomRepair.getRoomid(), lockDate),
								this.LOCKED_REPAIR);
						this.logger.info("新房态缓存维修房态锁房信息, roomid: {}, date: {}", troomRepair.getRoomid(), lockDate);
					}
				}
			}
			for (RoomLockPo roomLockPo : roomLockPos) {
				Date pmsBegintime = roomLockPo.getBeginTime();
				Date pmsEndtime = roomLockPo.getEndTime();
				String pmsEndTimeStr = DateUtils.getStringFromDate(DateUtils.addDays(pmsEndtime, -1),
						DateUtils.FORMATSHORTDATETIME);

				String pmsstatus = roomLockPo.getStatus();
				if (roomLockPo.getRoomjson() != null) {
					List<RoomLockJsonBean> list = this.gson.fromJson(roomLockPo.getRoomjson(),
							new TypeToken<ArrayList<RoomLockJsonBean>>() {
							}.getType());
					this.logger.info("新房态解析房间json{}{}", hotelid, list.toString());
					for (RoomLockJsonBean rljb : list) {
						Map map = new HashMap<String, String>();
						map.put("pms", rljb.getRoomid());
						map.put("hotelid", hotelid);
						TRoomModel tRoomModel = pmsRoomService.selectTroomByPms(map);
						if (tRoomModel == null) {
							// throw
							// MyErrorEnum.errorParm.getMyException("未找到pms为
							// "+rljb.getRoomid()+" 的房间信息");
							logger.error("findBookedRoomsByHotelIdNewPms::未找到pms为 " + rljb.getRoomid() + " 的房间信息");
							continue;
						}
						// 判断应离未离
						// 如果离店时间是前一天
						if (pmsEndTimeStr.equals(rljb.getTime()) && PmsRoomOrderStatusEnum.IN.getId().equals(pmsstatus)
								&& (pmsEndtime.getTime() <= DateUtils.getDateFromString(enddate.concat(" 12:00:00"))
										.getTime())) {
							// 锁下一天
							lockRoomsCache.put(
									getRoomLockedKey(tRoomModel.getId(), DateUtils.formatDate(DateUtils
											.getDateFromString(rljb.getTime(), DateUtils.FORMATSHORTDATETIME))),
									LOCKED_PMS);
							lockRoomsCache.put(getRoomLockedKey(tRoomModel.getId(),
									DateUtils.formatDate(DateUtils.addDays(
											DateUtils.getDateFromString(rljb.getTime(), DateUtils.FORMATSHORTDATETIME),
											1))),
									LOCKED_PMS);
						} else {
							lockRoomsCache.put(
									getRoomLockedKey(tRoomModel.getId(), DateUtils.formatDate(DateUtils
											.getDateFromString(rljb.getTime(), DateUtils.FORMATSHORTDATETIME))),
									LOCKED_PMS);
						}
						this.logger.info("新房态缓存pms房态锁房信息, roomid: {}, date: {}", rljb.getRoomid(), rljb.getTime());
					}
				}
			}

			// 放入redis缓存
			jedis.set(this.getCacheKey(hotelid).getBytes(), SerializeUtil.serialize(lockRoomsCache));
			this.logger.info("{}新房态酒店房态信息已缓存到redis。", hotelid);
		} catch (Exception e) {
			lockRoomsCache = null;
			this.logger.error("新房态findRoomstatesByHotelId has error: {}", e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return lockRoomsCache;
	}

	/**
	 * OTS（C端）锁房处理：仅对指定酒店、房间、起始、截止日期进行锁房处理. 注：OTS锁房处理，直接对redis缓存进行操作，缓存key值为2.
	 * 
	 * @param otaroomorder
	 *            参数：酒店客单订单
	 * @return Map
	 */
	public Map<String, Object> lockRoomInOTS(OtaRoomOrder otaroomorder) throws Exception {
		this.logger.info("lockRoomInOTS method begin...");
		this.logger.info("paraemter OtaRoomOrder: {}", JsonKit.toJson(otaroomorder));
		Long hotelid = otaroomorder.getHotelId();
		Long roomid = otaroomorder.getRoomId();
		Integer status = otaroomorder.get("orderstatus");
		Date begintime = otaroomorder.getDate("begintime");
		Date endtime = otaroomorder.getDate("endtime");
		// 处理入住时间为0到6点的预定数据
		String btime = DateUtils.formatTime(begintime);
		int itime = DateUtils.strTimeToSeconds(btime);
		if ((itime >= DateUtils.ZERO_TIME_SECONDS) && (itime <= DateUtils.SIX_TIME_SECONDS)) {
			// 0到6点预定的，锁前一天房
			begintime = DateUtils.addDays(begintime, -1);
		}

		// 处理超过中午12点，应离未离(status='IN')的预定数据
		String etime = DateUtils.formatTime(endtime);
		if ("12:00:00".equals(etime) && (OtaOrderStatusEnum.CheckIn.getId().intValue() == status.intValue())
				&& (endtime.getTime() <= new Date().getTime())) {
			// 超过中午12点、应离未离处理，锁下一天房
			endtime = DateUtils.addDays(endtime, 1);
		}

		String begindate = DateUtils.formatDate(begintime);
		String enddate = DateUtils.formatDate(endtime);

		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		Jedis jedis = null;
		try {
			Map<String, String> lockRoomsCache = new HashMap<String, String>();

			jedis = this.cacheManager.getNewJedis();
			String hotelCachekey = this.getCacheKey(hotelid);
			byte[] temp = jedis.get(hotelCachekey.getBytes());
			if (temp != null) {
				lockRoomsCache = (Map<String, String>) SerializeUtil.unserialize(temp);
			}

			int lockDays = DateUtils.selectDateDiff(enddate, begindate);
			Date bdate = DateUtils.getDateFromString(begindate);
			for (int i = 0; i < lockDays; i++) {
				String lockDate = DateUtils.formatDate(DateUtils.addDays(bdate, i));
				lockRoomsCache.put(this.getRoomLockedKey(roomid, lockDate), this.LOCKED_OTS);
				this.logger.info("lockRoomInOTS::hotelid:{}, roomid:{}, lockDate: {}", hotelid, roomid, lockDate);
			}

			//
			jedis.set(this.getCacheKey(hotelid).getBytes(), SerializeUtil.serialize(lockRoomsCache));
			this.logger.info("OTS lock room success.\n {}", JsonKit.toJson(lockRoomsCache));

			//
			long finishTime = new Date().getTime();
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
		} catch (Exception e) {
			this.logger.error("OTS lock room error: {}", e.getMessage());
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		this.logger.info("lockRoomInOTS method end. result is {}", JsonKit.toJson(rtnMap));
		return rtnMap;
	}

	/**
	 * OTS（C端）解锁处理：仅对指定酒店、房间、起始、截止日期进行解锁处理. 注：OTS解锁处理，直接对redis缓存进行操作，删除对应的缓存key.
	 * 
	 * @param 参数：酒店id
	 * @param 参数：酒店房间id
	 * @param 参数：酒店预定开始日期（格式为yyyy-MM-dd）
	 * @param 参数：酒店预定结束日期（格式为yyyy-MM-dd）
	 * @return Map
	 */
	public Map<String, Object> unlockRoomInOTS(OtaOrder otaorder) {
		this.logger.info("unlockRoomInOTS method begin...");
		this.logger.info("parameter OtaOrder: {}", JsonKit.toJson(otaorder));
		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		Long hotelid = otaorder.getHotelId();
		Jedis jedis = null;
		try {
			Map<String, String> lockRoomsCache = new HashMap<String, String>();
			jedis = this.cacheManager.getNewJedis();
			String hotelCachekey = this.getCacheKey(hotelid);
			byte[] temp = jedis.get(hotelCachekey.getBytes());
			if (temp == null) {
				rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
				return rtnMap;
			}
			// 反序列化得到缓存map
			lockRoomsCache = (Map<String, String>) SerializeUtil.unserialize(temp);

			List<OtaRoomOrder> otaroomorders = otaorder.getRoomOrderList();
			for (OtaRoomOrder otaroomorder : otaroomorders) {
				Long roomid = otaroomorder.getRoomId();
				Date begintime = otaroomorder.getDate("begintime");
				Date endtime = otaroomorder.getDate("endtime");
				Integer status = otaroomorder.get("orderstatus");

				// 处理入住时间为0到6点的预定数据
				String btime = DateUtils.formatTime(begintime);
				int itime = DateUtils.strTimeToSeconds(btime);
				if ((itime >= DateUtils.ZERO_TIME_SECONDS) && (itime <= DateUtils.SIX_TIME_SECONDS)) {
					// 0到6点预定的，锁前一天房
					begintime = DateUtils.addDays(begintime, -1);
				}

				// 处理超过中午12点，应离未离(status='IN')的预定数据
				String etime = DateUtils.formatTime(endtime);
				if ("12:00:00".equals(etime) && (OtaOrderStatusEnum.CheckIn.getId().intValue() == status.intValue())
						&& (endtime.getTime() <= new Date().getTime())) {
					// 超过中午12点、应离未离处理，锁下一天房
					endtime = DateUtils.addDays(endtime, 1);
				}

				String begindate = DateUtils.formatDate(begintime);
				String enddate = DateUtils.formatDate(endtime);
				int lockDays = DateUtils.selectDateDiff(enddate, begindate);
				Date bdate = DateUtils.getDateFromString(begindate);
				for (int i = 0; i < lockDays; i++) {
					String unlockDate = DateUtils.formatDate(DateUtils.addDays(bdate, i));
					// ots只能解锁ots锁房的缓存
					if (this.LOCKED_OTS.equals(lockRoomsCache.get(this.getRoomLockedKey(roomid, unlockDate)))) {
						lockRoomsCache.remove(this.getRoomLockedKey(roomid, unlockDate));
						this.logger.info("unlockRoomInOTS::hotelid:{}, roomid:{}, unlockDate: {}", hotelid, roomid,
								unlockDate);
					}
				}
			}
			//
			jedis.set(this.getCacheKey(hotelid).getBytes(), SerializeUtil.serialize(lockRoomsCache));
			this.logger.info("OTS unlock room success.\n {}", JsonKit.toJson(lockRoomsCache));

			//
			long finishTime = new Date().getTime();
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
		} catch (Exception e) {
			this.logger.error("OTS unlock room error: {}", e.getMessage());
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		this.logger.info("unlockRoomInOTS method end. result is {}", JsonKit.toJson(rtnMap));
		return rtnMap;
	}

	/**
	 * PMS（酒店端）锁房处理：仅对指定id的预定数据进行锁房处理. 注：PMS锁房处理，b_pmsroomorder客单表中已经创建预定记录.
	 * 根据id查询客单表，并对redis进行PMS锁房处理，缓存key值为1.
	 * 
	 * @param
	 * @return Map
	 */
	public Map<String, Object> lockRoomInPMS(Long hotelid, Long roomid, String begindate, String enddate) {
		this.logger.info("lockRoomInPMS method begin... hotelid: {}, roomid: {}, begindate: {}, enddate: {}", hotelid,
				roomid, begindate, enddate);
		Map<String, Object> rtnMap = Maps.newHashMap();
		Jedis jedis = null;
		try {
			Map<String, String> lockRoomsCache = this.getLockRoomsCache(hotelid);
			if (lockRoomsCache == null) {
				lockRoomsCache = Maps.newHashMap();
			}
			int diffDays = DateUtils.selectDateDiff(enddate, begindate);
			for (int i = 0; i < diffDays; i++) {
				String unlockDate = DateUtils.formatDate(DateUtils.addDays(DateUtils.getDateFromString(begindate), i));
				lockRoomsCache.put(this.getRoomLockedKey(roomid, unlockDate), this.LOCKED_PMS);
				this.logger.info("hotelid: {}, roomid: {}, date: {} locked.", hotelid, roomid, unlockDate);
			}
			jedis = this.cacheManager.getNewJedis();
			jedis.set(this.getCacheKey(hotelid).getBytes(), SerializeUtil.serialize(lockRoomsCache));
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			this.logger.info("lockRoomInPMS method success. hotelid: {}, roomid: {}, begindate: {}, enddate: {}",
					hotelid, roomid, begindate, enddate);
		} catch (Exception e) {
			this.logger.error("lockRoomInPMS method error: {}", e.getMessage());
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return rtnMap;
	}

	/**
	 * 
	 * @param hotelid
	 * @param roomid
	 * @param
	 * @return
	 */
	public Map<String, Object> unlockRoomInPMS(Long hotelid, Long roomid, String[] lockDates) {
		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		Jedis jedis = null;
		try {
			Map<String, String> lockRoomsCache = new HashMap<String, String>();
			jedis = this.cacheManager.getNewJedis();
			String hotelCachekey = this.getCacheKey(hotelid);
			byte[] temp = jedis.get(hotelCachekey.getBytes());
			if (temp == null) {
				rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
				return rtnMap;
			}
			// 反序列化得到缓存map
			lockRoomsCache = (Map<String, String>) SerializeUtil.unserialize(temp);
			// pms解锁锁房的缓存
			for (int i = 0; i < lockDates.length; i++) {
				String lockDate = lockDates[i];
				// 转日期格式为yyyy-MM-dd
				lockDate = DateUtils.formatDate(DateUtils.getDateFromString(lockDate));
				lockRoomsCache.remove(this.getRoomLockedKey(roomid, lockDate));
				this.logger.info("unlockRoomInOTS::hotelid:{}, roomid:{}, unlockDate: {}", hotelid, roomid, lockDate);
			}
			//
			jedis.set(this.getCacheKey(hotelid).getBytes(), SerializeUtil.serialize(lockRoomsCache));
			this.logger.info("OTS unlock room success.\n {}", JsonKit.toJson(lockRoomsCache));

			//
			long finishTime = new Date().getTime();
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
		} catch (Exception e) {
			this.logger.error("OTS lock room error: {}", e.getMessage());
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return rtnMap;
	}

	/**
	 * PMS（酒店端）解锁处理：仅对指定id的预定数据进行解锁处理. 注：PMS解锁处理，b_pmsroomorder客单表预定状态已经变更.
	 * 根据id查询客单表，并对redis进行PMS解锁处理，删除对应的缓存key.
	 * 
	 * @param
	 * @return Map
	 */
	public Map<String, Object> unlockRoomInPMS(OtaOrder otaorder) {
		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		Long hotelid = otaorder.getHotelId();
		Jedis jedis = null;
		try {
			Map<String, String> lockRoomsCache = new HashMap<String, String>();
			jedis = this.cacheManager.getNewJedis();
			String hotelCachekey = this.getCacheKey(hotelid);
			byte[] temp = jedis.get(hotelCachekey.getBytes());
			if (temp == null) {
				rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
				return rtnMap;
			}
			// 反序列化得到缓存map
			lockRoomsCache = (Map<String, String>) SerializeUtil.unserialize(temp);

			List<OtaRoomOrder> otaroomorders = otaorder.getRoomOrderList();
			for (OtaRoomOrder otaroomorder : otaroomorders) {
				Long roomid = otaroomorder.getRoomId();
				Date begintime = otaroomorder.getDate("begintime");
				Date endtime = otaroomorder.getDate("endtime");
				Integer status = otaroomorder.get("orderstatus");

				// 处理入住时间为0到6点的预定数据
				String btime = DateUtils.formatTime(begintime);
				int itime = DateUtils.strTimeToSeconds(btime);
				if ((itime >= DateUtils.ZERO_TIME_SECONDS) && (itime <= DateUtils.SIX_TIME_SECONDS)) {
					// 0到6点预定的，锁前一天房
					begintime = DateUtils.addDays(begintime, -1);
				}

				// 处理超过中午12点，应离未离(status='IN')的预定数据
				String etime = DateUtils.formatTime(endtime);
				if ("12:00:00".equals(etime) && (OtaOrderStatusEnum.CheckIn.getId().intValue() == status.intValue())
						&& (endtime.getTime() <= new Date().getTime())) {
					// 超过中午12点、应离未离处理，锁下一天房
					endtime = DateUtils.addDays(endtime, 1);
				}

				String begindate = DateUtils.formatDate(begintime);
				String enddate = DateUtils.formatDate(endtime);
				int lockDays = DateUtils.selectDateDiff(enddate, begindate);
				Date bdate = DateUtils.getDateFromString(begindate);
				for (int i = 0; i < lockDays; i++) {
					String unlockDate = DateUtils.formatDate(DateUtils.addDays(bdate, i));
					// pms解锁锁房的缓存
					lockRoomsCache.remove(this.getRoomLockedKey(roomid, unlockDate));
					this.logger.info("unlockRoomInOTS::hotelid:{}, roomid:{}, unlockDate: {}", hotelid, roomid,
							unlockDate);
				}
			}
			//
			jedis.set(this.getCacheKey(hotelid).getBytes(), SerializeUtil.serialize(lockRoomsCache));
			this.logger.info("OTS unlock room success.\n {}", JsonKit.toJson(lockRoomsCache));

			//
			long finishTime = new Date().getTime();
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
		} catch (Exception e) {
			this.logger.error("OTS lock room error: {}", e.getMessage());
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return rtnMap;
	}

	/**
	 * 查询酒店房态信息.
	 * 
	 * @param params
	 *            参数：controller入参实体对象.
	 * @return
	 * @throws Exception
	 */
	public List<RoomstateQuerylistRespEntity> findHotelRoomState(String roomno, RoomstateQuerylistReqEntity params)
			throws Exception {
		List<RoomstateQuerylistRespEntity> respEntityList = Lists.newArrayList();
		String callMethod = StringUtils.isNotBlank(params.getCallmethod()) ? params.getCallmethod().trim() : "";
		Integer callEntry = params.getCallentry();
		String callVersionStr = StringUtils.isNotBlank(params.getCallversion()) ? params.getCallversion().trim() : "";

		try {
			Long hotelid = params.getHotelid();
			Long roomtypeid = params.getRoomtypeid();
			Integer bednum = params.getBednum();
			// 接口的入参，格式为yyyyMMdd
			String begindate = params.getStartdateday();
			String enddate = params.getEnddateday();

			// 从redis缓存中查询已锁的房态
			Map<String, String> lockRoomsCache = new HashMap<String, String>();
			//// THotel hotel = hotelService.readonlyTHotel(hotelid);
			// 查酒店信息
			THotelModel thotelModel = thotelMapper.selectById(hotelid);
			if (thotelModel == null) {
				logger.error("hotel: {} already delete from t_hotel.", hotelid);
				return respEntityList;
			}
			String isNewPms = thotelModel.getIsnewpms();
			//// String isNewPms= hotel.getStr("isNewPms");
			if (Constant.STR_TRUE.equals(isNewPms)) {// 新pms
				// 加埋点
				Transaction t = Cat.newTransaction("RoomState", "findBookedRoomsByHotelIdNewPms-newpms-redis");
				try {
					t.setStatus(Transaction.SUCCESS);
					lockRoomsCache = this.findBookedRoomsByHotelIdNewPms(hotelid, begindate, enddate);
				} catch (Exception e) {
					t.setStatus(e);
					throw e;
				} finally {
					t.complete();
				}
			} else {
				// 加埋点
				Transaction t = Cat.newTransaction("RoomState", "findBookedRoomsByHotelId-oldpms-redis");
				try {
					t.setStatus(Transaction.SUCCESS);
					lockRoomsCache = this.findBookedRoomsByHotelId(hotelid, begindate, enddate);
				} catch (Exception e) {
					t.setStatus(e);
					throw e;
				} finally {
					t.complete();
				}
			}
			logger.info(String.format("hotelid:%s; begindate:%s; enddate:%s; roomtypeid:%s", hotelid, begindate,
					enddate, roomtypeid));
			this.logger.info(JsonKit.toJson(lockRoomsCache));
			Transaction other = Cat.newTransaction("RoomState", "roomtype-sql");
			// 酒店房态信息map
			RoomstateQuerylistRespEntity respEntity = new RoomstateQuerylistRespEntity();

			List<TRoomTypeModel> troomTypes = new ArrayList<TRoomTypeModel>();
			try {
				other.setStatus(Transaction.SUCCESS);
				respEntity.setHotelid(hotelid);
				respEntity.setHotelname(thotelModel.getHotelname());
				respEntity.setHotelrulecode(thotelModel.getRulecode()); // 20150810
																		// add
				respEntity.setOnline(thotelModel.getOnline());
				respEntity.setVisible(thotelModel.getVisible());
				// 查询t_roomtype表数据
				troomTypes = troomtypeMapper.findList(roomtypeid, hotelid, bednum);
			} catch (Exception e) {
				other.setStatus(e);
				throw e;
			} finally {
				other.complete();
			}

			// 接口返回数据:roomtype
			List<RoomstateQuerylistRespEntity.Roomtype> roomtypes = Lists.newArrayList();

			Transaction t = Cat.newTransaction("RoomState", "loopsql");

			try {
				t.setStatus(Transaction.SUCCESS);
				// 返回酒店下的所有房型返现
				Map<Long, Object> cashBackMap = cashBackService.getCashBackByHotelId(hotelid, begindate, enddate);

				if (logger.isInfoEnabled()) {
					logger.info(String.format("cashBackMap:%s; troomTypes:%s",
							cashBackMap == null ? 0 : cashBackMap.size(), troomTypes == null ? 0 : troomTypes.size()));
				}

				for (TRoomTypeModel troomType : troomTypes) {
					// 如果按照床型查询
					if (bednum != null) {
						if (troomType.getBednum() == null) {
							this.logger.error("房型:{}没有对应的t_roomtype_info数据.", troomType.getId());
							continue;
						}
					}
					// 构建 RoomstateQuerylistRespEntity.Roomtype 房型数据

					RoomstateQuerylistRespEntity.Roomtype roomtype = respEntity.new Roomtype();

					// mike3.1 特价房型

					TRoomSaleConfig tRoomSaleConfig = new TRoomSaleConfig();
					Integer roomTypeId = Integer.valueOf(troomType.getId().toString());
					tRoomSaleConfig.setRoomTypeId(roomTypeId);

					Boolean isPromo = roomSaleService.checkRoomSale(tRoomSaleConfig);
					if (logger.isInfoEnabled()) {
						logger.info(String.format("isPromo:%s; roomTypeId:%s; callVersionStr:%s", isPromo, roomTypeId,
								callVersionStr));
					}

					if (StringUtils.isNotBlank(callVersionStr)) {
						if (callEntry != null && callEntry != 3 && "3.0".compareTo(callVersionStr) < 0
								&& !"3".equals(callMethod)) {
							TRoomSaleConfig hotelRoomSaleConfig = new TRoomSaleConfig();
							Integer thotelId = hotelid != null ? hotelid.intValue() : null;
							hotelRoomSaleConfig.setHotelId(thotelId);
							hotelRoomSaleConfig.setRoomTypeId(roomTypeId);
							List<RoomPromoDto> list = roomSaleService.queryRoomPromoByHotelNew(hotelRoomSaleConfig);
							String isonpromo = "0";

							if (logger.isInfoEnabled()) {
								logger.info(String.format("isPromo:%s; callVersionStr:%s; rooms:%s", isPromo,
										callVersionStr, list == null ? 0 : list.size()));
							}

							if (isPromo != null && isPromo) { // isBack
								isonpromo = "1";
								if (list != null && list.size() > 0) {
									RoomPromoDto roomPromoDto = list.get(0);

									Integer promostaus = DateUtils.promoStatus(roomPromoDto.getStartDate(),
											roomPromoDto.getEndDate(), roomPromoDto.getStartTime(),
											roomPromoDto.getEndTime());
									roomtype.setPromostatus(promostaus);
									roomtype.setPromotype(roomPromoDto.getPromoType());
									roomtype.setPromotext(roomPromoDto.getTypeDesc());

									if (logger.isInfoEnabled()) {
										logger.info(String.format("promostatus:%s; promotext:%s; promotype:%s",
												promostaus, roomPromoDto.getTypeDesc(), roomPromoDto.getPromoType()));
									}

									String promoStartTime = roomPromoDto.getStartTime().toString();
									if (StringUtils.isNotBlank(promoStartTime)) {
										String[] tmp = promoStartTime.split(":");
										promoStartTime = tmp[0] + ":" + tmp[1];
										roomtype.setPromostarttime(promoStartTime);
									}

									Long promoduendsec = DateUtils.promoEndDueTime(roomPromoDto.getEndDate(),
											roomPromoDto.getStartTime(), roomPromoDto.getEndTime(), promostaus);
									Long promodustartsec = DateUtils.promoStartDueTime(promoduendsec,
											roomPromoDto.getStartTime(), promostaus);

									if (promostaus == Constant.PROMOING) {
										roomtype.setPromodustartsec("0");
										roomtype.setPromoduendsec(promoduendsec.toString());

									} else {
										roomtype.setPromodustartsec(promodustartsec.toString());
										roomtype.setPromoduendsec(promoduendsec.toString());
									}

								}

							}

							roomtype.setIsonpromo(isonpromo);
						}
					}

					roomtype.setRoomtypeid(troomType.getId());
					roomtype.setBednum(troomType.getBednum());
					roomtype.setRoomtypename(troomType.getName());

					// 加埋点
					String[] prices = null;
					Transaction priceTransaction = Cat.newTransaction("RoomState", "mikeprice-redis");
					try {
						if (hotelPriceService.isUseNewPrice())
							prices = hotelPriceService.getRoomtypeMikePrices(hotelid, troomType.getId(), begindate,
									enddate);
						else
							prices = this.getRoomtypeMikePrices(hotelid, troomType.getId(), begindate, enddate);
						priceTransaction.setStatus(Transaction.SUCCESS);
					} catch (Exception e) {
						priceTransaction.setStatus(e);
						throw e;
					} finally {
						priceTransaction.complete();
					}

					BigDecimal defenseZeroPrice = new BigDecimal(Constant.DEFENSE_ZERO_PRICE);

					if (prices == null || prices.length == 0) {



						if (troomType.getCost().compareTo(BigDecimal.ZERO) <= 0){
							// 眯客价
							roomtype.setRoomtypeprice(defenseZeroPrice);
							// 门市价
							roomtype.setRoomtypepmsprice(defenseZeroPrice);
						}else{
							// 眯客价
							roomtype.setRoomtypeprice(troomType.getCost());
							// 门市价
							roomtype.setRoomtypepmsprice(troomType.getCost());
						}




					} else {
						// 眯客价
						if (prices[0] != null) {
							if ("0".equals(prices[0])){
								roomtype.setRoomtypeprice(defenseZeroPrice);
							}else{
								roomtype.setRoomtypeprice(new BigDecimal(prices[0]));
							}

						} else {
							if (troomType.getCost().compareTo(BigDecimal.ZERO) <=0){
								roomtype.setRoomtypeprice(defenseZeroPrice);
							}else{
								roomtype.setRoomtypeprice(troomType.getCost());
							}
						}

						if (troomType.getCost().compareTo(BigDecimal.ZERO) <=0){
							// 门市价
							roomtype.setRoomtypepmsprice(defenseZeroPrice);
						}else{
							// 门市价
							roomtype.setRoomtypepmsprice(troomType.getCost());
						}

					}

					// 查询房型信息t_roomtype_info
					TRoomTypeInfoModel troomtypeInfoModel = troomtypeInfoMapper
							.findByRoomtypeid(roomtype.getRoomtypeid());
					String[] bedsizes = null;
					if (troomtypeInfoModel == null) {
						this.logger.info("roomtypeid: {} not exists in table t_roomtype_info.",
								roomtype.getRoomtypeid());
					}
					RoomstateQuerylistRespEntity.Bed bed = respEntity.new Bed();
					if ((troomtypeInfoModel == null) || StringUtils.isBlank(troomtypeInfoModel.getBedsize())) {
						bed.setCount(0);
					} else {
						bedsizes = troomtypeInfoModel.getBedsize().split(",");
						bed.setCount(bedsizes.length);
					}

					List<RoomstateQuerylistRespEntity.Bedtype> beds = Lists.newArrayList();
					if (bedsizes != null) {
						for (int i = 0; i < bedsizes.length; i++) {
							RoomstateQuerylistRespEntity.Bedtype bedtype = respEntity.new Bedtype();
							bedtype.setBedtypename(troomtypeInfoModel.getBedtypename() == null ? ""
									: troomtypeInfoModel.getBedtypename());
							bedtype.setBedlength(bedsizes[i].concat("米"));
							beds.add(bedtype);
						}
					}
					bed.setBeds(beds);
					// 床信息挪到房型中
					roomtype.setBed(bed);

					/** ---------------------imike2.5新增---------------------- */
					roomtype.setArea(troomType.getArea() == null ? new BigDecimal(0) : troomType.getArea());
					String bedLength = troomType.getBedlength();
					if (StringUtils.isNotBlank(bedLength)) {
						if (bedLength.contains(","))
							bedLength.replace(",", "米,");
						bedLength += "米";
					}
					roomtype.setBedlength(StringUtils.isBlank(bedLength) ? "" : bedLength);
					roomtype.setBedtypename(
							StringUtils.isBlank(troomType.getBedtypename()) ? "" : troomType.getBedtypename());

					// 房间设施 不需要加 binding hotel设施和 roomtype设施存的关联存放表不同 :
					// 酒店的关联存在： t_hotel_facility
					// 房型的关联存在: t_roomtype_facility
					List<TFacilityModel> tFacs = tFacilityMapper.findByRoomtypeId(roomtype.getRoomtypeid());
					List<RoomstateQuerylistRespEntity.Infrastructure> infrastructures = Lists.newArrayList();
					List<RoomstateQuerylistRespEntity.Valueaddedfa> valueaddedfas = Lists.newArrayList();
					for (TFacilityModel fac : tFacs) {
						int facType = fac.getFactype();
						Long facId = fac.getId();
						String facName = fac.getFacname();
						switch (facType) {
						case 1:
							RoomstateQuerylistRespEntity.Infrastructure infrastructure = new RoomstateQuerylistRespEntity().new Infrastructure();
							infrastructure.setInfrastructureid(facId);
							infrastructure.setInfrastructurename(facName);
							infrastructures.add(infrastructure);
							break;
						case 2:
							RoomstateQuerylistRespEntity.Valueaddedfa valueaddedfa = new RoomstateQuerylistRespEntity().new Valueaddedfa();
							valueaddedfa.setValueaddedfaid(facId);
							valueaddedfa.setValueaddedfaname(facName);
							valueaddedfas.add(valueaddedfa);
							break;
						case 3:
							roomtype.setBathroomtype(facName);
							break;

						}
					}
					roomtype.setInfrastructure(infrastructures); // 基础设施
					roomtype.setValueaddedfa(valueaddedfas); // 增值设施

					// mike3.0 add
					// 添加返现
					String iscashback = "F";
					BigDecimal cash = BigDecimal.ZERO;
					Map<String, Object> rtCashBack = (Map<String, Object>) cashBackMap.get(troomType.getId());
					if (rtCashBack != null && rtCashBack.containsKey("iscashback")
							&& rtCashBack.containsKey("cashbackcost")) {
						String isback = rtCashBack.get("iscashback").toString();
						iscashback = "T";
						cash = new BigDecimal(rtCashBack.get("cashbackcost").toString());
					}
					roomtype.setCashbackcost(cash);// 返现金额
					roomtype.setIscashback(iscashback);// 是否返现（T/F）
					/** ----------------------------------------------------- */

					// 房型下的房间
					List<TRoomModel> trooms = troomMapper.findList(troomType.getId());
					List<RoomstateQuerylistRespEntity.Room> rooms = Lists.newArrayList();
					List<RoomstateQuerylistRespEntity.Room> tempRooms = Lists.newArrayList();
					List<RoomstateQuerylistRespEntity.Room> vcRooms5 = Lists.newArrayList();
					// boolean isMatch = false;
					int vcRoomCount = 0;
					for (TRoomModel troom : trooms) {
						RoomstateQuerylistRespEntity.Room room = respEntity.new Room();
						room.setRoomid(troom.getId());
						room.setRoomno(troom.getName());
						room.setRoomname(roomtype.getRoomtypename());
						room.setHaswindow(troom.getIsWindow() == null ? "" : troom.getIsWindow()); // TODO
																									// t_room关联t_room_setting
						// room.setBed(bed);
						// 与redis房态缓存比较：vc可用，nvc不可用
						this.processRoomState(room, hotelid, begindate, enddate, lockRoomsCache);

						if (room.getRoomstatus().equals(ROOM_STATUS_NVC)) {
							// 将不可订房间加入临时List中
							tempRooms.add(room);
						} else {
							// 将可预订房间加入房间集合中
							// 从历史入住进
							if (StringUtils.isNotEmpty(roomno) && roomno.equals(room.getRoomno())) {
								room.setIsselected("T");
								roomtype.setIsfocus("T");
								// isMatch = true;
							}
							rooms.add(room);

							if ("T".equals(params.getIsShowAllRoom())) {
								vcRooms5.add(room);
							} else {
								// 眯客3.0 只显示<=5个可预定房间
								if (vcRoomCount < 5) {
									vcRooms5.add(room);
									vcRoomCount++;
								}
							}
						}
					}

					roomtype.setVcroomnum(rooms.size()); // 将售房间数加入房型(roomtype)
															// 集合中 20150730 add
					// 眯客3.0 added by chuaiqing at 2015-09-21 18:14:20
					Integer vcroomnum = roomtype.getVcroomnum();
					if (vcroomnum == null) {
						roomtype.setVctxt("");
					} else {
						if (vcroomnum == 0) {
							roomtype.setVctxt("满房,最近预定3小时前");
						} else {
							if (vcroomnum <= 3) {
								roomtype.setVctxt("仅剩" + vcroomnum + "间");
							} else {
								roomtype.setVctxt("");
							}
						}
					}

					// 将tempList添加至roomList之后,实现锁房集合 已预订与可预订分类排序
					// rooms.addAll(tempRooms);//不可预定房间

					/*
					 * if(roomno!=null && !isMatch){ //找第一个可预定的置为isselect for
					 * (Room room:rooms) {
					 * if(room.getRoomstatus().equals(ROOM_STATUS_VC)){
					 * room.setSelect(true); break; } } }
					 */
					// 眯客3.0 只显示<=5个可预定房间
					roomtype.setRooms(vcRooms5);
					// roomtype.setRooms(rooms);//所有可预定房间

					// 兼容老版本
					if (isPromo != null && isPromo) {
						if (callEntry != null && callEntry != 3 && "3.0".compareTo(callVersionStr) < 0
								&& !"3".equals(callMethod)) {

							roomtypes.add(roomtype);
						}
					} else {
						roomtypes.add(roomtype);
					}
				}
			} catch (Exception e) {
				logger.error("failed to find rooms...", e);
				t.setStatus(e);
				throw e;
			} finally {
				t.complete();
			}

			/** 房型显示逻辑：1、价格从低到高；2、满房的在最下面； */
			// 眯客价排序
			Object[] roomtypesArr = roomtypes.toArray();

			// 把满房状态的房间类型放入临时List中
			List<RoomstateQuerylistRespEntity.Roomtype> tempRoomTypes = Lists.newArrayList();

			// 特价房型
			List<RoomstateQuerylistRespEntity.Roomtype> promoRoomTypes = Lists.newArrayList();
			// 普通房型
			List<RoomstateQuerylistRespEntity.Roomtype> normalRoomTypes = Lists.newArrayList();

			for (int i = 0; i < roomtypesArr.length; i++) {
				if (roomtypesArr[i] instanceof RoomstateQuerylistRespEntity.Roomtype) {
					RoomstateQuerylistRespEntity.Roomtype rt = (RoomstateQuerylistRespEntity.Roomtype) roomtypesArr[i];
					if (rt.getVcroomnum() <= 0)
						tempRoomTypes.add(rt);
					else if ("1".equals(rt.getIsonpromo())) {
						promoRoomTypes.add(rt);
					} else {
						normalRoomTypes.add(rt);
					}

				}
			}

			Object[] promoRoomtypesArr = promoRoomTypes.toArray();
			Arrays.sort(promoRoomtypesArr, this.new RoomTypesComparator());

			Object[] normalRoomtypesArr = normalRoomTypes.toArray();
			Arrays.sort(normalRoomtypesArr, this.new RoomTypesComparator());

			roomtypes.clear();

			for (int i = 0; i < promoRoomtypesArr.length; i++) {
				if (promoRoomtypesArr[i] instanceof RoomstateQuerylistRespEntity.Roomtype) {
					RoomstateQuerylistRespEntity.Roomtype rt = (RoomstateQuerylistRespEntity.Roomtype) promoRoomtypesArr[i];
					roomtypes.add(rt);

				}
			}

			for (int i = 0; i < normalRoomtypesArr.length; i++) {
				if (normalRoomtypesArr[i] instanceof RoomstateQuerylistRespEntity.Roomtype) {
					RoomstateQuerylistRespEntity.Roomtype rt = (RoomstateQuerylistRespEntity.Roomtype) normalRoomtypesArr[i];
					roomtypes.add(rt);

				}
			}

			roomtypes.addAll(tempRoomTypes); // 把满房状态的房间类型排在可定房间类型的后面
			/** 房型排序 end */

			respEntity.setRoomtype(roomtypes);
			respEntityList.add(respEntity);
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return respEntityList;
	}

	/**
	 * 
	 * @param hotelId
	 * @param roomTypeId
	 * @param beginDate
	 *            格式为yyyyMMdd
	 * @param enDate
	 *            格式为yyyyMMdd
	 * @return
	 */
	public RoomstateQuerylistRespEntity.Room findVCHotelRoom(Long hotelid, Long roomtypeid, String begindate,
			String enddate) {
		// 房型下的房间
		try {
			// 从redis缓存中查询已锁的房态
			Map<String, String> lockRoomsCache = new HashMap<String, String>();
			THotel hotel = hotelService.readonlyTHotel(hotelid);
			String isNewPms = hotel.getStr("isNewPms");
			if ("T".equals(isNewPms)) {// 新pms
				// 加埋点
				Transaction t = Cat.newTransaction("VCHotelRoom", "findBookedRoomsByHotelIdNewPms-newpms-redis");
				t.setStatus(Transaction.SUCCESS);
				try {
					lockRoomsCache = this.findBookedRoomsByHotelIdNewPms(hotelid, begindate, enddate);
				} catch (Exception e) {
					t.setStatus(e);
				} finally {
					t.complete();
				}
			} else {
				// 加埋点
				Transaction t = Cat.newTransaction("VCHotelRoom", "findBookedRoomsByHotelId-oldpms-redis");
				t.setStatus(Transaction.SUCCESS);
				try {
					lockRoomsCache = this.findBookedRoomsByHotelId(hotelid, begindate, enddate);
				} catch (Exception e) {
					t.setStatus(e);
				} finally {
					t.complete();
				}
			}

			List<TRoomModel> trooms = troomMapper.findList(roomtypeid);
			for (TRoomModel troom : trooms) {
				RoomstateQuerylistRespEntity.Room room = new RoomstateQuerylistRespEntity().new Room();
				room.setRoomid(troom.getId());
				room.setRoomno(troom.getName());
				room.setRoomname(troom.getRoomTypeName());
				room.setHaswindow(troom.getIsWindow() == null ? "" : troom.getIsWindow()); // TODO
																							// t_room关联t_room_setting
				// room.setBed(bed);
				// 与redis房态缓存比较：vc可用，nvc不可用
				this.processRoomState(room, hotelid, begindate, enddate, lockRoomsCache);

				if (room.getRoomstatus().equals(ROOM_STATUS_VC)) {
					return room;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 价格排序规则
	 */
	private class RoomTypesComparator implements Comparator<Object> {
		public int compare(Object obj1, Object obj2) {
			RoomstateQuerylistRespEntity.Roomtype roomtype1 = (RoomstateQuerylistRespEntity.Roomtype) obj1;
			RoomstateQuerylistRespEntity.Roomtype roomtype2 = (RoomstateQuerylistRespEntity.Roomtype) obj2;

			if (roomtype1.getRoomtypeprice().compareTo(roomtype2.getRoomtypeprice()) > 0) {
				return 1;
			} else if (roomtype1.getRoomtypeprice().compareTo(roomtype2.getRoomtypeprice()) < 0) {
				return -1;
			} else {
				// 如果眯客价相同 则按照门市价排序
				return roomtype1.getRoomtypepmsprice().compareTo(roomtype2.getRoomtypepmsprice());
			}

		}
	}

	/**
	 * 
	 * @param room
	 * @param hotelid
	 * @param begindate
	 * @param enddate
	 */
	private void processRoomState(RoomstateQuerylistRespEntity.Room room, Long hotelid, String begindate,
			String enddate, Map<String, String> lockRoomsCache) throws Exception {
		Long roomid = room.getRoomid();
		String bdate = DateUtils.formatDate(DateUtils.getDateFromString(begindate));
		String edate = DateUtils.formatDate(DateUtils.getDateFromString(enddate));
		int lockDays = DateUtils.selectDateDiff(edate, bdate);
		if (lockRoomsCache == null) {
			room.setRoomstatus(this.ROOM_STATUS_VC);
			return;
		}
		// 先设置为vc
		room.setRoomstatus(this.ROOM_STATUS_VC);
		for (int i = 0; i < lockDays; i++) {
			String lockDate = DateUtils.formatDate(DateUtils.addDays(DateUtils.getDateFromString(begindate), i));
			String lockFlag = lockRoomsCache.get(this.getRoomLockedKey(roomid, lockDate));
			if (lockFlag != null) {
				// 只要房态锁房缓存中存在key，设置该房间不可用nvc
				this.logger.info("processRoomState::hotelid:{}, roomid:{}, Date: {}, status is nvc.", hotelid, roomid,
						lockDate);
				room.setRoomstatus(this.ROOM_STATUS_NVC);
				return;
			}
		}
	}

	/**
	 * 
	 * @param hotelid
	 * @param roomtypeid
	 * @param isinit
	 * @return
	 */
	public Map<String, String> findTimePrices(Long hotelid, Long roomtypeid, boolean isinit) {
		Jedis jedis = null;
		Map<String, String> rtnMap = Maps.newHashMap();
		try {
			jedis = this.cacheManager.getNewJedis();
			String key = this.getCostCacheKey(hotelid, roomtypeid);
			if (isinit) {
				// 如果是初始化，先重置.
				jedis.del(key);
			}
			rtnMap = jedis.hgetAll(key);
			if ((rtnMap != null) && (rtnMap.size() > 0)) {
				this.logger.info("return timeprices from redis.");
				return rtnMap;
			}

			List<TPricetimeWithPrices> pricetimeList = tPricetimeMapper.findTimePriceList(hotelid, roomtypeid);
			for (TPricetimeWithPrices pricetime : pricetimeList) {
				if (StringUtils.isEmpty(pricetime.getCron())) {
					continue;
				}
				// TODO: 酒店策略价的同步处理，如果已经设置过策略价，再做修改或删除操作，这时缓存需要做更新处理.
				// 酒店策略价格设置.
				BigDecimal cronPrice = pricetime.getPrice();
				BigDecimal cronSubprice = pricetime.getSubprice();
				BigDecimal cronSubper = pricetime.getSubper();
				if ((cronPrice == null) && (cronSubprice == null) && (cronSubper == null)) {
					continue;
				}
				if ((cronSubprice != null) && (cronSubprice.compareTo(BigDecimal.ZERO) == 1)) {
					// 设置了下浮价格
					cronPrice = cronPrice.subtract(cronSubprice);
				} else {
					if ((cronSubper != null) && (cronSubper.compareTo(BigDecimal.ZERO) == 1)) {
						// 设置了下浮百分比
						cronPrice = cronPrice
								.multiply(BigDecimal.ONE.subtract(
										cronSubper.divide(BigDecimal.valueOf(100d), 2, BigDecimal.ROUND_HALF_UP)))
								.setScale(0, BigDecimal.ROUND_HALF_UP);
					}
				}

				String[] cronExprs = pricetime.getCron().split(" ");
				if (cronExprs.length < 7) {
					continue;
				}
				String[] cronDays = cronExprs[3].split(",");
				String cronMonth = cronExprs[4];
				if (cronMonth.length() == 1) {
					cronMonth = "0" + cronMonth;
				}
				String cronYear = cronExprs[6];
				for (int i = 0; i < cronDays.length; i++) {
					String day = cronDays[i];
					if (day.length() == 1) {
						day = "0" + day;
					}
					String priceDate = cronYear + "-" + cronMonth + "-" + day;
					String value = String.valueOf(cronPrice);
					if (jedis.hexists(key, priceDate)) {
						String price = jedis.hget(key, priceDate);
						BigDecimal bigprice = BigDecimal.valueOf(Double.valueOf(price));
						if (cronPrice.compareTo(bigprice) == -1) {
							jedis.hset(key, priceDate, value);
						}
					} else {
						jedis.hset(key, priceDate, value);
					}
				}
			}

			rtnMap = jedis.hgetAll(key);
			this.logger.info("cache timeprices to redis.");
		} catch (Exception e) {
			this.logger.error("RoomstateService::readonlyFindTimePrices is error: {}", e.getMessage());
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return rtnMap;
	}

	/**
	 * 查询房型门市价.
	 * 
	 * @param roomtypeid
	 * @param isinit
	 * @return
	 */
	public BigDecimal findRoomtypePrice(Long roomtypeid, boolean isinit) {
		Jedis jedis = null;
		BigDecimal rtnPrice = BigDecimal.ONE;
		try {
			TRoomTypeWithBasePrice troomtype = tRoomTypeMapper.findPriceById(roomtypeid);
			Long hotelid = troomtype.getThotelid();

			jedis = this.cacheManager.getNewJedis();
			String key = this.getHotelPriceCacheKey(hotelid);
			String field = roomtypeid.toString();
			if (isinit) {
				// 如果是初始化，先删除.
				jedis.hdel(key, field);
			}
			boolean hasPrice = jedis.hexists(key, field);
			if (hasPrice) {
				String strPrice = jedis.hget(key, field);
				rtnPrice = BigDecimal.valueOf(Double.valueOf(strPrice));
				return rtnPrice;
			}

			// 如果缓存中没有，查库
			BigDecimal cost = troomtype.getCost();
			rtnPrice = cost;

			Long _roomtypeid = troomtype.getRoomtypeid();
			if (_roomtypeid == null) {
				// 没有设置对应的基本价格，直接返回门市价
				rtnPrice = troomtype.getCost();
				return rtnPrice;
			}

			/*
			 * BigDecimal price = troomtype.getPrice(); BigDecimal subprice =
			 * troomtype.getSubprice(); BigDecimal subper =
			 * troomtype.getSubper(); if ((price == null) && (subprice == null)
			 * && (subper == null)) { // 没有设置对应的基本价格，直接返回门市价 rtnPrice =
			 * troomtype.getCost(); return rtnPrice; }
			 * 
			 * // 没有设置基本价格 if (price == null) { // 设置了减价 if ((subprice != null)
			 * && (subprice.compareTo(BigDecimal.ZERO) == 1)) { rtnPrice =
			 * BigDecimal.valueOf(cost.doubleValue() - subprice.doubleValue());
			 * if (rtnPrice.compareTo(BigDecimal.ZERO) == -1) { rtnPrice =
			 * BigDecimal.ZERO; return rtnPrice; } }
			 * 
			 * // 设置了减幅 if ((subper != null) &&
			 * (subper.compareTo(BigDecimal.ZERO) == 1)) { double dbl =
			 * troomtype.getCost().doubleValue() * (1d - subper.doubleValue());
			 * rtnPrice = BigDecimal.valueOf(dbl).setScale(0,
			 * BigDecimal.ROUND_HALF_UP); return rtnPrice; } }
			 * 
			 * if (price.compareTo(BigDecimal.ZERO) == 1) { // 设置了基本价 rtnPrice =
			 * troomtype.getPrice(); return rtnPrice; }
			 */

			// 放入redis缓存
			jedis.hset(key, field, rtnPrice.toString());
			this.logger.info("roomtype: {} price cache to redis, cache value is {}", roomtypeid, rtnPrice);
		} catch (Exception e) {
			this.logger.error("RoomstateService::readonlyFindRoomtypePrice is error: {}", e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return rtnPrice;
	}

	/**
	 * 
	 * @param hotelid
	 * @param isinit
	 */
	public Map<String, String> findHotelPrices(Long hotelid, boolean isinit) {
		this.logger.info("RoomstateService::readonlyFindHotelPrices method begin...");
		this.logger.info("method parameters is hotelid:{}, isinit:{}", hotelid, isinit);
		Map<String, String> rtnMap = Maps.newHashMap();
		Jedis jedis = null;
		try {
			jedis = this.cacheManager.getNewJedis();
			String key = this.getHotelPriceCacheKey(hotelid);
			this.logger.info("RoomstateService::readonlyFindHotelPrices cache key is {}", key);
			if (isinit) {
				this.logger.info("isinit is {}, prepared to delete cache by key: {}", isinit);
				// 如果是初始化，重置酒店房型价格缓存
				jedis.del(key);
				this.logger.info("cache key:{} is deleted.", key);
			}
			this.logger.info("prepared to find hotel prices by connected mysql...");
			List<TRoomTypeWithBasePrice> list = tRoomTypeMapper.findHotelPrices(hotelid);
			this.logger.info("{} rows hotel prices data found.", list.size());
			for (TRoomTypeWithBasePrice troomtype : list) {
				this.logger.info("TRoomTypeWithBasePrice::troomtype is: {}", JsonKit.toJson(troomtype));
				String field = troomtype.getId().toString();
				BigDecimal cost = troomtype.getCost();
				BigDecimal rtnPrice = cost;

				Long _roomtypeid = troomtype.getRoomtypeid();
				if (_roomtypeid == null) {
					// 没有设置对应的基本价格，直接返回门市价
					rtnPrice = troomtype.getCost();
					jedis.hset(key, field, rtnPrice.toString());
					this.logger.info("roomtypeid not fund. 直接返回门市价: {}", rtnPrice);
					continue;
				}

				BigDecimal price = troomtype.getPrice();
				BigDecimal subprice = troomtype.getSubprice();
				BigDecimal subper = troomtype.getSubper();
				if ((price == null) && (subprice == null) && (subper == null)) {
					// 没有设置对应的基本价格，直接返回门市价
					if (troomtype.getCost().compareTo(BigDecimal.ZERO) <= 0){
						rtnPrice =  new BigDecimal(Constant.DEFENSE_ZERO_PRICE);
					}else {
						rtnPrice = troomtype.getCost();
					}

					jedis.hset(key, field, rtnPrice.toString());
					this.logger.info("没有设置对应的基本价格，直接返回门市价: {}", rtnPrice);
					continue;
				}

				// 没有设置基本价格
				if (price == null) {
					// 设置了减价
					if ((subprice != null) && (subprice.compareTo(BigDecimal.ZERO) == 1)) {
						rtnPrice = BigDecimal.valueOf(cost.doubleValue() - subprice.doubleValue());
						if (rtnPrice.compareTo(BigDecimal.ZERO) == -1) {
							rtnPrice  = new BigDecimal(Constant.DEFENSE_ZERO_PRICE); //BigDecimal.ZERO;
							jedis.hset(key, field, rtnPrice.toString());
							this.logger.info("基本价未设置, 设置了下浮: {}, 基本价为: {}", subprice, rtnPrice);
							continue;
						}
					}

					// 设置了减幅
					if ((subper != null) && (subper.compareTo(BigDecimal.ZERO) == 1)) {
						double dbl = troomtype.getCost().doubleValue() * (1d - subper.doubleValue());
						rtnPrice = BigDecimal.valueOf(dbl).setScale(0, BigDecimal.ROUND_HALF_UP);

						if (rtnPrice.compareTo(BigDecimal.ZERO) <= 0){
							rtnPrice = new BigDecimal(Constant.DEFENSE_ZERO_PRICE);
						}

						jedis.hset(key, field, rtnPrice.toString());
						this.logger.info("基本价未设置, 设置了下浮百分比: {}%, 基本价为: {}", subper, rtnPrice);
						continue;
					}
				} else {
					// 设置了基本价
					rtnPrice = troomtype.getPrice();
					if (rtnPrice.compareTo(BigDecimal.ZERO) <= 0){
						rtnPrice = new BigDecimal(Constant.DEFENSE_ZERO_PRICE);
					}
					jedis.hset(key, field, rtnPrice.toString());
					continue;
				}

				// 放入redis缓存
				jedis.hset(key, field, rtnPrice.toString());
				this.logger.info("roomtype: {} price cache to redis, cache value is {}", field, rtnPrice);
			}
			rtnMap = jedis.hgetAll(key);
			this.logger.info("RoomstateService::readonlyFindHotelPrices return is: {}", JsonKit.toJson(rtnMap));
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error(e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return rtnMap;
	}

	/**
	 * 获取酒店房型价格
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @param roomtypeid
	 *            参数：房型id
	 * @param statedate
	 *            参数：起始日期
	 * @param enddate
	 *            参数：截止日期
	 * @return BigDecimal 返回值
	 */
	public BigDecimal getRoomPrice(Long hotelid, Long roomtypeid, String startdate, String enddate) {
		this.logger.info("RoomstateService::getRoomPrice method begin...");
		this.logger.info("parameters is hotelid:{}, roomtypeid:{}, startdate:{}, enddate:{}", hotelid, roomtypeid,
				startdate, enddate);
		// 按照起始和截止日期计算策略价格.
		this.logger.info("从redis中取得策略价格readonlyFindTimePrices, hotelid:{}, roomtypeid:{}", hotelid, roomtypeid);
		Map<String, String> timeprices = this.findTimePrices(hotelid, roomtypeid, false);
		this.logger.info("策略价格:{}", JsonKit.toJson(timeprices));
		int days = DateUtils.selectDateDiff(enddate, startdate) + 1;
		this.logger.info("查询天数:{}", days);
		Date sdate = DateUtils.getDateFromString(startdate);
		this.logger.info("查询开始日期:{}", DateUtils.formatDate(sdate));
		BigDecimal price = null;
		for (int i = 0; i < days; i++) {
			Date date = DateUtils.addDays(sdate, i);
			String key = DateUtils.formatDate(date);
			this.logger.info("timeprice date is {}", key);
			String val = timeprices.get(key);
			this.logger.info("timeprice value is {}", val);
			if ((val == null) || val.trim().equals("") || val.trim().equalsIgnoreCase("null")) {
				continue;
			}
			if (price == null) {
				try {
					price = BigDecimal.valueOf(Double.valueOf(val));
					this.logger.info("init price is {}", price);
				} catch (Exception e) {
					System.out.println("priceValue:" + val);
					price = null;
				}
			} else {
				this.logger.info("price value is {}", val);
				if (BigDecimal.valueOf(Double.valueOf(val)).compareTo(price) == -1) {
					price = BigDecimal.valueOf(Double.valueOf(val));
					this.logger.info("有新低价了, price is {}", price);
				}
			}
		}

		// 没有策略价格，取房型价格.
		if (price == null) {
			this.logger.info("未配置策略价格.");
			price = this.findRoomtypePrice(roomtypeid, false);
			this.logger.info("房型价格, price is {}", price);



		}

		if (price.compareTo(BigDecimal.ZERO) <= 0){
			return new BigDecimal(Constant.DEFENSE_ZERO_PRICE);
		}

		return price;
	}

	/**
	 * H端审核更新酒店眯客价（酒店的最低眯客价以及对应的房型的门市价）
	 * 
	 * @param hotelid
	 *            参数: 酒店id
	 * @param roomtypeid
	 *            参数: 房型id
	 * @param forceUpdate
	 *            参数: 是否强制更新缓存
	 * @return
	 */
	public Map<String, Object> updateHotelMikepricesCache(Long hotelid, Long roomtypeId, boolean forceUpdate) {
		this.logger.info("RoomstateService::readonlyUpdateHotelMikepricesCache method begin...");
		this.logger.info("parameter is hotelid:{}, roomtypeid:{}, forceUpdate:{}", hotelid, roomtypeId, forceUpdate);
		long startTime = new Date().getTime();
		long finishTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		Jedis jedis = null;
		// 酒店眯客策略价格map
		Map<String, String> mikeTimepriceMap = null;
		// 酒店眯客门市价格map
		Map<String, String> mikeRoompriceMap = null;
		try {
			jedis = this.cacheManager.getNewJedis();
			String keyMikeTimeprice = this.getMikeTimepriceCacheKeyOfHotel(hotelid);
			this.logger.info("眯客策略价缓存key: {}", keyMikeTimeprice);
			String keyMikeRoomprice = this.getMikeRoompriceCacheKeyOfHotel(hotelid);
			this.logger.info("眯客门市价缓存key: {}", keyMikeRoomprice);

			if (jedis.exists(keyMikeRoomprice) && jedis.exists(keyMikeTimeprice) && !forceUpdate) {
				// 如果已经存在缓存key, 并且不强制更新缓存, 直接返回缓存数据, 不再查询数据库.
				mikeTimepriceMap = jedis.hgetAll(keyMikeTimeprice);
				logger.info("直接从缓存中获取眯客策略价: {}", JsonKit.toJson(keyMikeTimeprice));

				mikeRoompriceMap = jedis.hgetAll(keyMikeRoomprice);
				logger.info("直接从缓存中获取眯客门市价: {}", JsonKit.toJson(keyMikeRoomprice));

				rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
				rtnMap.put(keyMikeTimeprice, mikeTimepriceMap);
				rtnMap.put(keyMikeRoomprice, mikeRoompriceMap);
				if (AppUtils.DEBUG_MODE) {
					finishTime = new Date().getTime();
					rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
				}
				return rtnMap;
			}
			logger.info("酒店hotelid:{}价格缓存不存在, 或者强制更新缓存...", hotelid);

			logger.info("获取并打开数据库连接...");

			/*
			 * 查询酒店基本价和门市价开始----------------------------------------------------
			 * -----
			 */
			logger.info("准备从数据库中查询酒店门市价...");
			List<TRoomTypeWithBasePrice> roomtypeWithBasepriceList = troomtypeMapper.findHotelPrices(hotelid);
			this.logger.info("查询到酒店hotelid: {}设置的门市价和基本价数据 {} 条.", hotelid, roomtypeWithBasepriceList.size());
			// 删除眯客门市价缓存
			jedis.del(keyMikeRoomprice);
			this.logger.info("删除眯客门市价缓存key:{}", keyMikeRoomprice);

			for (TRoomTypeWithBasePrice troomtype : roomtypeWithBasepriceList) {
				this.logger.info("门市价和基本价设置数据:{}", JsonKit.toJson(troomtype));
				Long theroomtypeId = troomtype.getId();
				String field = theroomtypeId.toString();
				BigDecimal cost = troomtype.getCost();
				BigDecimal rtnPrice = cost;

				BigDecimal price = troomtype.getPrice();
				BigDecimal subprice = troomtype.getSubprice();
				BigDecimal subper = troomtype.getSubper();
				if ((price == null) && (subprice == null) && (subper == null)) {
					// 没有设置对应的基本价格，直接返回门市价
					jedis.hset(keyMikeRoomprice, field, rtnPrice.toString());
					this.logger.info("没有设置对应的基本价格，直接返回门市价: {}", rtnPrice);
					continue;
				}

				// 没有设置基本价格
				if (price == null) {
					// 设置了减价
					if ((subprice != null) && (subprice.compareTo(BigDecimal.ZERO) == 1)) {
						rtnPrice = BigDecimal.valueOf(cost.doubleValue() - subprice.doubleValue());
						if (rtnPrice.compareTo(BigDecimal.ZERO) == -1) {
							rtnPrice = new BigDecimal(Constant.DEFENSE_ZERO_PRICE) ;  //BigDecimal.ZERO;
							jedis.hset(keyMikeRoomprice, field, rtnPrice.toString());
							this.logger.info("基本价未设置, 设置了下浮: {}, 基本价为: {}", subprice, rtnPrice);
							continue;
						}
					}

					// 设置了减幅
					if ((subper != null) && (subper.compareTo(BigDecimal.ZERO) == 1)) {
						double dbl = troomtype.getCost().doubleValue() * (1d - subper.doubleValue());
						rtnPrice = BigDecimal.valueOf(dbl).setScale(0, BigDecimal.ROUND_HALF_UP);
						jedis.hset(keyMikeRoomprice, field, rtnPrice.toString());
						this.logger.info("基本价未设置, 设置了下浮百分比: {}%, 基本价为: {}", subper, rtnPrice);
						continue;
					}
				} else {
					// 设置了基本价
					rtnPrice = troomtype.getPrice();
					if (rtnPrice.compareTo(BigDecimal.ZERO) <= 0){
						rtnPrice = new BigDecimal(Constant.DEFENSE_ZERO_PRICE);
					}
					jedis.hset(keyMikeRoomprice, field, rtnPrice.toString());
					this.logger.info("设置了基本价为: {}", rtnPrice);
					continue;
				}

				// 放入redis缓存

				if (rtnPrice.compareTo(BigDecimal.ZERO) <= 0){
					rtnPrice = new BigDecimal(Constant.DEFENSE_ZERO_PRICE);
				}

				jedis.hset(keyMikeRoomprice, field, rtnPrice.toString());
				this.logger.info("roomtype: {} price cache to redis, cache value is {}", field, rtnPrice);
			}
			// 得到眯客门市价缓存map数据.
			if (jedis.exists(keyMikeRoomprice)) {
				mikeRoompriceMap = jedis.hgetAll(keyMikeRoomprice);
				this.logger.info("酒店门市价key: {}已缓存: {}", keyMikeRoomprice, JsonKit.toJson(mikeRoompriceMap));
			}
			/*
			 * 查询酒店基本价和门市价结束----------------------------------------------------
			 * -----
			 */

			/*
			 * 查询酒店策略价开始--------------------------------------------------------
			 * --------
			 */
			// 查询酒店策略价（通过H端酒店价格维护进行设置）
			logger.info("准备从数据库中查询酒店策略价...");
			List<TPricetimeWithPrices> timepriceList = tPricetimeMapper.findTimePriceList(hotelid, roomtypeId);
			this.logger.info("查询到酒店hotelid: {}设置的策略价格数据 {} 条.", hotelid, timepriceList.size());
			// 删除眯客策略价缓存
			jedis.del(keyMikeTimeprice);
			this.logger.info("删除眯客策略价缓存key:{}", keyMikeTimeprice);

			for (TPricetimeWithPrices pricetime : timepriceList) {
				this.logger.info("策略价设置数据:{}", JsonKit.toJson(pricetime));
				if (StringUtils.isEmpty(pricetime.getCron())) {
					continue;
				}
				Long roomtypeid = pricetime.getRoomtypeid();
				// 酒店策略价格设置.
				BigDecimal cronPrice = pricetime.getPrice();
				BigDecimal cronSubprice = pricetime.getSubprice();
				BigDecimal cronSubper = pricetime.getSubper();
				if ((cronPrice == null) && (cronSubprice == null) && (cronSubper == null)) {
					continue;
				}
				if (cronPrice == null) {
					// 未设置基础价 取基本价-->门市价
					if (mikeRoompriceMap != null && mikeRoompriceMap.containsKey(roomtypeid.toString())) {
						cronPrice = BigDecimal.valueOf(Double.valueOf(mikeRoompriceMap.get(roomtypeid.toString())));
					} else {
						continue;
					}
				}
				if ((cronSubprice != null) && (cronSubprice.compareTo(BigDecimal.ZERO) == 1)) {
					// 设置了下浮价格
					cronPrice = cronPrice.subtract(cronSubprice);
					this.logger.info("下浮价格cronSubprice: {}, 策略价cronPrice: {}", cronSubprice, cronPrice);
				} else if ((cronSubper != null) && (cronSubper.compareTo(BigDecimal.ZERO) == 1)) {
					// 设置了下浮百分比
					double dbl = cronPrice.doubleValue() * (1d - cronSubper.doubleValue());
					cronPrice = BigDecimal.valueOf(dbl).setScale(0, BigDecimal.ROUND_HALF_UP);

					this.logger.info("下浮百分比cronSubper: {}, 策略价cronPrice: {}", cronSubper, cronPrice);
				}

				String[] cronExprs = pricetime.getCron().split(" ");
				this.logger.info("策略价cron表达式: {}", pricetime.getCron());
				if (cronExprs.length < 7) {
					continue;
				}
				String[] cronDays = cronExprs[3].split(",");
				String cronMonth = cronExprs[4];
				if (cronMonth.length() == 1) {
					cronMonth = "0" + cronMonth;
				}
				String cronYear = cronExprs[6];
				for (int i = 0; i < cronDays.length; i++) {
					String day = cronDays[i];
					if (day.length() == 1) {
						day = "0" + day;
					}
					String priceDate = cronYear + "-" + cronMonth + "-" + day;
					String value = String.valueOf(cronPrice);
					// 酒店房型id和日期组合，作为缓存field.
					String timepriceField = roomtypeid.toString() + "`" + priceDate;
					if (jedis.hexists(keyMikeTimeprice, timepriceField)) {
						// 如果有重复设置的，取低价.
						String price = jedis.hget(keyMikeTimeprice, timepriceField);
						BigDecimal bigprice = BigDecimal.valueOf(Double.valueOf(price));
						if (cronPrice.compareTo(bigprice) == -1) {
							jedis.hset(keyMikeTimeprice, timepriceField, value);
						}
					} else {
						// 缓存酒店指定房型在某天的策略价
						jedis.hset(keyMikeTimeprice, timepriceField, value);
					}
					this.logger.info("缓存策略价, key:{}, field:{}, value:{}", keyMikeTimeprice, timepriceField, value);
				}
			}
			// 得到眯客策略价缓存map数据
			if (jedis.exists(keyMikeTimeprice)) {
				mikeTimepriceMap = jedis.hgetAll(keyMikeTimeprice);
				this.logger.info("酒店策略价key: {}已缓存: {}", keyMikeTimeprice, JsonKit.toJson(mikeTimepriceMap));
			}
			/*
			 * 查询酒店策略价开始--------------------------------------------------------
			 * --------
			 */

			// 处理返回值
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put(keyMikeTimeprice, mikeTimepriceMap);
			rtnMap.put(keyMikeRoomprice, mikeRoompriceMap);
			if (AppUtils.DEBUG_MODE) {
				finishTime = new Date().getTime();
				rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
			}
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return rtnMap;
	}

	/**
	 * 从酒店策略价格缓存中计算得到最低策略价.
	 * 
	 * @param mikeTimepriceMap
	 *            参数：眯客策略价格缓存map
	 * @return String 返回值
	 */
	private Map<String, String> getMinTimeprice(Map<String, String> mikeTimepriceMap, String startdateday,
			String enddateday) {
		if (mikeTimepriceMap == null || mikeTimepriceMap.size() == 0) {
			return null;
		}
		Map<String, String> minTimeprice = Maps.newHashMap();
		try {
			long startday = Long.valueOf(startdateday.replaceAll("-", ""));
			long endday = Long.valueOf(enddateday.replaceAll("-", ""));
			Iterator<Entry<String, String>> iter = mikeTimepriceMap.entrySet().iterator();
			String minkey = null;
			String minval = null;
			String keydate = "";
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String val = entry.getValue();
				String[] keyArr = key.split("`");
				if (keyArr.length > 1) {
					keydate = keyArr[1];
				}
				if (StringUtils.isBlank(keydate)) {
					continue;
				}
				long timeday = Long.valueOf(keydate.replaceAll("-", ""));
				// 策略价设置的日期不在查询区间
				if (timeday < startday || timeday > endday) {
					continue;
				}

				if (minkey == null && minval == null) {
					minkey = key;
					minval = val;
				} else {
					BigDecimal minprice = new BigDecimal(minval);
					BigDecimal price = new BigDecimal(val);
					if (price.compareTo(minprice) == -1) {
						minkey = key;
						minval = val;
					}
				}
			}
			if (minkey != null && minval != null) {
				minTimeprice.put(minkey, minval);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return minTimeprice;
	}

	/**
	 * 从酒店门市价格缓存中计算得到酒店房型门市价.
	 * 
	 * @param mikeRoompriceMap
	 *            参数：眯客门市价格缓存map
	 * @return String 返回值
	 */
	private Map<String, String> getMinRoomprice(Map<String, String> mikeRoompriceMap) {
		if (mikeRoompriceMap == null || mikeRoompriceMap.size() == 0) {
			return null;
		}
		Map<String, String> minRoomprice = Maps.newHashMap();
		try {
			Iterator<Entry<String, String>> iter = mikeRoompriceMap.entrySet().iterator();
			String minkey = null;
			String minval = null;
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String val = entry.getValue();

				if (minkey == null && minval == null) {
					minkey = key;
					minval = val;
				} else {
					BigDecimal minprice = new BigDecimal(minval);
					BigDecimal price = new BigDecimal(val);
					if (price.compareTo(minprice) == -1) {
						minkey = key;
						minval = val;
					}
				}
			}
			if (minkey != null && minval != null) {
				if ("0".equals(minval)){
					minval = Constant.DEFENSE_ZERO_PRICE.toString();
				}
				minRoomprice.put(minkey, minval);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return minRoomprice;
	}

	/**
	 * 得到酒店眯客价 返回值为字符串数组, 第1个为酒店的最低眯客价, 第2个为最低眯客价房型对应的门市价.
	 * 
	 * @param hotelid
	 * @return String[] 返回值
	 */
	public String[] getHotelMikePrices(Long hotelid, String startdateday, String enddateday) {
		String[] resultVal = new String[] { Constant.DEFENSE_ZERO_PRICE.toString(), Constant.DEFENSE_ZERO_PRICE.toString() };
		try {
			Map<String, Object> rtnMap = updateHotelMikepricesCache(hotelid, null, false);
			if (rtnMap == null || rtnMap.size() == 0) {
				return resultVal;
			}
			boolean success = Boolean.valueOf(String.valueOf(rtnMap.get(ServiceOutput.STR_MSG_SUCCESS)));
			// 酒店眯客策略价格map
			Map<String, String> mikeTimepriceMap = null;
			// 酒店眯客门市价格map
			Map<String, String> mikeRoompriceMap = null;
			if (success) {
				String keyMikeTimeprice = this.getMikeTimepriceCacheKeyOfHotel(hotelid);
				String keyMikeRoomprice = this.getMikeRoompriceCacheKeyOfHotel(hotelid);
				mikeTimepriceMap = (Map<String, String>) rtnMap.get(keyMikeTimeprice);
				mikeRoompriceMap = (Map<String, String>) rtnMap.get(keyMikeRoomprice);

				// 没有策略价格, 直接取门市价.
				if (mikeTimepriceMap == null || mikeTimepriceMap.size() == 0) {
					Map<String, String> minRoompriceMap = this.getMinRoomprice(mikeRoompriceMap);

					// 如果酒店也没有设置基本价和门市价
					if (minRoompriceMap == null || minRoompriceMap.size() == 0) {
						return resultVal;
					}
					Iterator<Entry<String, String>> iter = minRoompriceMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						// key为roomtypeid
						String key = entry.getKey();
						Long roomtypeid = Long.valueOf(key);
						BigDecimal cost = BigDecimal.ZERO;
						// 根据key查房型的门市价.
						if (roomtypeid != null) {
							cost = this.getRoomtypeCost(roomtypeid);
						}
						String val = entry.getValue();

						//
						if (!StringUtils.isBlank(val)) {
							resultVal[0] = val;
							resultVal[1] = cost.toString();
						}
						break;
					}
				} else {
					Map<String, String> minTimepriceMap = this.getMinTimeprice(mikeTimepriceMap, startdateday,
							enddateday);
					Iterator<Entry<String, String>> iter = minTimepriceMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						String val = entry.getValue();
						if (!StringUtils.isBlank(val)) {
							resultVal[0] = val;
						}

						// key的格式roomtypeid`date, 从key中解析roomtypeid
						String key = entry.getKey();
						String keyroomtypeid = key.split("`")[0];
						String strRoomprice = mikeRoompriceMap.get(keyroomtypeid);
						if (!StringUtils.isBlank(strRoomprice)) {
							// 重新查房型门市价.
							Long roomtypeid = Long.valueOf(keyroomtypeid);
							BigDecimal cost = BigDecimal.ZERO;
							if (roomtypeid != null) {
								cost = this.getRoomtypeCost(roomtypeid);
							}
							//// resultVal[1] =
							//// mikeRoompriceMap.get(keyroomtypeid);
							resultVal[1] = cost.toString();
						}
						break;
					}
					// 如果酒店设置了策略价，但是根据查询日期区间没有查到策略价，取门市价.
					if (minTimepriceMap == null || minTimepriceMap.size() == 0) {
						//
						Map<String, String> minRoompriceMap = this.getMinRoomprice(mikeRoompriceMap);
						Iterator<Entry<String, String>> iterRoomprice = minRoompriceMap.entrySet().iterator();
						while (iterRoomprice.hasNext()) {
							Map.Entry<String, String> entry = iterRoomprice.next();
							// key为roomtypeid
							String key = entry.getKey();
							Long roomtypeid = Long.valueOf(key);
							BigDecimal cost = BigDecimal.ZERO;
							// 根据key查房型的门市价.
							if (roomtypeid != null) {
								cost = this.getRoomtypeCost(roomtypeid);
							}
							String val = entry.getValue();

							//
							if (!StringUtils.isBlank(val)) {
								resultVal[0] = val;
								resultVal[1] = cost.toString();
							}
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultVal;
	}

	/**
	 * 得到酒店房型眯客价
	 * 
	 * @param hotelid
	 *            参数：酒店id
	 * @param roomtypeid
	 *            参数：房型id
	 * @param startdateday
	 *            参数：查询起始日期
	 * @param enddateday
	 *            参数：查询截止日期
	 * @return String[] 返回值
	 */
	public String[] getRoomtypeMikePrices(Long hotelid, Long roomtypeid, String startdateday, String enddateday) {
		String[] resultVal = new String[] { Constant.DEFENSE_ZERO_PRICE.toString(), Constant.DEFENSE_ZERO_PRICE.toString() };
		try {
			Map<String, Object> rtnMap = updateHotelMikepricesCache(hotelid, null, false);
			if (rtnMap == null || rtnMap.size() == 0) {
				return resultVal;
			}
			boolean success = Boolean.valueOf(String.valueOf(rtnMap.get(ServiceOutput.STR_MSG_SUCCESS)));
			// 酒店眯客策略价格map
			Map<String, String> mikeTimepriceMap = null;
			// 酒店眯客门市价格map
			Map<String, String> mikeRoompriceMap = null;
			if (success) {
				String keyMikeTimeprice = this.getMikeTimepriceCacheKeyOfHotel(hotelid);
				String keyMikeRoomprice = this.getMikeRoompriceCacheKeyOfHotel(hotelid);
				mikeTimepriceMap = (Map<String, String>) rtnMap.get(keyMikeTimeprice);
				mikeRoompriceMap = (Map<String, String>) rtnMap.get(keyMikeRoomprice);
				// 没有策略价格, 直接取门市价.
				if (mikeTimepriceMap == null || mikeTimepriceMap.size() == 0) {
					// 如果也没有门市价（这种情况从业务来讲是非正常情况）
					if (mikeRoompriceMap == null || mikeRoompriceMap.size() == 0) {
						return resultVal;
					}
					resultVal[0] = mikeRoompriceMap.get(roomtypeid.toString());
					resultVal[1] = mikeRoompriceMap.get(roomtypeid.toString());
					return resultVal;
				}

				int days = DateUtils.selectDateDiff(enddateday, startdateday) + 1;
				this.logger.info("查询天数:{}", days);
				Date sdate = DateUtils.getDateFromString(startdateday);
				this.logger.info("查询开始日期:{}", DateUtils.formatDate(sdate));
				BigDecimal price = null;
				for (int i = 0; i < days; i++) {
					Date date = DateUtils.addDays(sdate, i);
					String key = "" + roomtypeid + "`" + DateUtils.formatDate(date);
					this.logger.info("timeprice key is {}", key);
					String val = mikeTimepriceMap.get(key);
					this.logger.info("timeprice value is {}", val);
					if ((val == null) || val.trim().equals("") || val.trim().equalsIgnoreCase("null")) {
						continue;
					}
					if (price == null) {
						try {
							price = BigDecimal.valueOf(Double.valueOf(val));
							logger.info("init price is {}", price);
						} catch (Exception e) {
							logger.error("priceValue: {}", val);
							System.out.println(val);
							price = null;
						}
					} else {
						logger.info("price value is {}", val);
						if (BigDecimal.valueOf(Double.valueOf(val)).compareTo(price) == -1) {
							price = BigDecimal.valueOf(Double.valueOf(val));
							logger.info("有新低价了, price is {}", price);
						}
					}
				}

				// 没有策略价格，取房型价格.
				if (price == null) {
					logger.info("未配置策略价格.");
					String val = mikeRoompriceMap.get(roomtypeid.toString());
					if (val == null || "0".equals(val)) {
						val = Constant.DEFENSE_ZERO_PRICE.toString();
						logger.info("房型价格error,酒店需要审核:{}--{}--{}--{}", hotelid, roomtypeid, startdateday, enddateday);
						Cat.logEvent("ZeroPrice", "hotelid: " +hotelid + " roomtypeid: " + roomtypeid);
						System.out.println("房型价格error,酒店需要审核:{" + hotelid + "}--{" + roomtypeid + "}--{" + startdateday
								+ "}--{" + enddateday + "}");
					}


					price = new BigDecimal(val);
					resultVal[0] = price.toString();
					resultVal[1] = price.toString();
					logger.info("房型价格, price is {}", price);
				} else {
					resultVal[0] = price.toString();
					resultVal[1] = mikeRoompriceMap.get(roomtypeid.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultVal;
	}

	/**
	 * 取得房型门市价
	 * 
	 * @param roomtypeid
	 *            参数：房型id
	 * @return BigDecimal 返回值
	 */
	public BigDecimal getRoomtypeCost(Long roomtypeid) {
		BigDecimal cost = BigDecimal.ZERO;
		try {
			TRoomTypeModel troomtype = tRoomTypeMapper.selectByPrimaryKey(roomtypeid);
			if (troomtype == null) {
				return cost;
			}
			cost = troomtype.getCost();
			if (cost == null) {
				return BigDecimal.ZERO;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cost;
	}

	/**
	 * @return 返回所有酒店id列表
	 */
	public List<Long> readonlyHotelIds() {
		return tHotelMapper.findAllHotelIds();
	}

	/**
	 * @param record
	 *            房量统计入库
	 */
	public int saveRoomCensus(RoomCensus record) {
		return roomCensusMapper.insertSelective(record);
	}

	/**
	 * 删除并备份48小时之前房量数据调度
	 */
	public void backUpRoomCensus() {
		roomCensusMapper.backUpRoomCensus();
		roomCensusMapper.deleteRoomCensus2DaysAgo();
	}
}
