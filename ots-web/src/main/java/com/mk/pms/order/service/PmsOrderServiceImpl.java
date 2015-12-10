package com.mk.pms.order.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.google.gson.Gson;
import com.mk.framework.AppUtils;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.component.eventbus.EventBusHelper;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.CommonUtils;
import com.mk.framework.util.UrlUtils;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.annotation.HessianService;
import com.mk.ots.common.enums.PmsRoomOrderStatusEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.comp.SynOrderConf;
import com.mk.ots.hotel.bean.EHotel;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.bean.TRoomRepair;
import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.dao.EHotelDAO;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.manager.HotelPMSManager;
import com.mk.ots.manager.OtsCacheManager;
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
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.pay.service.IPayService;
import com.mk.ots.utils.MD5Util;
import com.mk.pms.bean.PmsCheckinUser;
import com.mk.pms.bean.PmsCost;
import com.mk.pms.bean.PmsLog;
import com.mk.pms.exception.PmsException;
import com.mk.pms.hotel.service.PmsCheckinUserService;
import com.mk.pms.manager.XmlLogFileManager;
import com.mk.pms.myenum.PmsResultEnum;
import com.mk.pms.order.bean.ChangeRoom;
import com.mk.pms.order.bean.SynedCustomerBean;
import com.mk.pms.order.event.PmsCalCacheEvent;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import cn.com.winhoo.mikeweb.webout.service.bean.ChangeRoomOrderBean;
import cn.com.winhoo.pms.exception.PmsErrorEnum;
import cn.com.winhoo.pms.webout.service.bean.CustomerResult;
import cn.com.winhoo.pms.webout.service.bean.OrderResult;
import cn.com.winhoo.pms.webout.service.bean.ReturnObject;
import cn.com.winhoo.pms.webout.service.bean.RoomTypeOrderResult;
import jodd.util.StringUtil;
import redis.clients.jedis.Jedis;

@Service
@HessianService(value = "/pmsorder", implmentInterface = PmsOrderService.class)
public class PmsOrderServiceImpl implements PmsOrderService {

	private static final Logger logger = LoggerFactory.getLogger(PmsOrderServiceImpl.class);
	@Autowired
	EHotelDAO eHotelDAO;

	@Autowired
	HotelDAO hotelDAO;

	@Autowired
	HotelService hotelService;

	@Autowired
	RoomService roomService;
	@Autowired
	RoomstateService roomstateService;
	@Autowired
	private OrderServiceImpl orderService;
	@Autowired
	private NewPmsOrderService newPmsOrderService;
	@Autowired
	private RoomSaleConfigMapper roomSaleConfigMapper;
	@Autowired
	private RoomSaleMapper roomSaleMapper;
	@Autowired
	private TRoomMapper roomMapper;
	@Autowired
	private PmsShiftService pmsShiftService;
	
	@Autowired
	private IPayService payService;
	@Autowired
	private OrderLogService orderLogService;

	@Autowired
	OtsCacheManager cacheManager;
	@Autowired
	SynOrderConf synOrderConf;

	private Gson gson = new Gson();

	private final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 检验缓存是否存在改订单
	 *
	 * @param customNo
	 * @param hotelId
	 * @return
	 */
	private boolean checkCacheOrder(Map customNo, Long hotelId) {
		Jedis jedis = this.cacheManager.getNewJedis();
		// 是否加缓存开关
		try {
			if (jedis.exists("switch_customerNo")) {
				return true;
			}
			String orderString = this.genOrderString(customNo, hotelId);
			String keyStr = MD5Util.md5Hex(orderString);
			PmsOrderServiceImpl.logger.info("加密后的redis原始串：" + keyStr);
			keyStr = "saveCustomerNo_" + hotelId + "_" + customNo.get("customerno") + "_" + keyStr;
			if (jedis.exists(keyStr)) {
				PmsOrderServiceImpl.logger.info("同步pms客单，校验缓存存在相同客单，hotelId=" + hotelId + ",orderString=" + orderString
						+ ",redisKeyStr=" + keyStr);
				return false;
			}
		} catch (Exception e) {
		} finally {
			jedis.close();
		}
		return true;
	}

	private void setCacheOrder(Map customNo, Long hotelId) {
		Jedis jedis = this.cacheManager.getNewJedis();
		// 是否加缓存开关
		try {
			if (jedis.exists("switch_customerNo")) {
				return;
			}
			String orderString = this.genOrderString(customNo, hotelId);
			String keyStr = MD5Util.md5Hex(orderString);
			PmsOrderServiceImpl.logger.info("加密后的redis原始串：" + keyStr);
			keyStr = "saveCustomerNo_" + hotelId + "_" + customNo.get("customerno") + "_" + keyStr;
			// save cache
			PmsOrderServiceImpl.logger.info(
					"setCacheOrder:::hotelId=" + hotelId + ",orderString=" + orderString + ",redisKeyStr=" + keyStr);
			jedis.set(keyStr, "", "NX", "EX", 60 * 60 * 24 * 7);
		} catch (Exception e) {
		} finally {
			jedis.close();
		}
	}

	private void delCacheOrder(Long hotelId) {
		Jedis jedis = this.cacheManager.getNewJedis();
		// 是否加缓存开关
		try {
			if (jedis.exists("switch_customerNo")) {
				return;
			}
			String keyStr = "saveCustomerNo_" + hotelId + "*";
			this.cacheManager.del(keyStr);
		} catch (Exception e) {
		} finally {
			PmsOrderServiceImpl.logger.info("删除saveCustomerNo_" + hotelId + "*");
			jedis.close();
		}
	}

	/**
	 * 生成redis串
	 *
	 * @param customNo
	 * @param hotelId
	 * @return
	 */
	private String genOrderString(Map customNo, Long hotelId) {
		String[] orderKey = { "customerno", "optype", "price", "cost", "recstatus", "arrivetime", "excheckouttime",
				"priceType", "roominguser", "payment", "orderid", "roomno", "name", "cardid", "ethnic", "cardtype",
				"fromaddress", "address" };
		StringBuilder sf = new StringBuilder();
		sf.append("hotelId").append("=").append(hotelId).append("&");

		for (String key : orderKey) {
			sf.append(key).append("=").append(customNo.get(key)).append("&");
		}
		PmsOrderServiceImpl.logger.info("生成的redis原始串：" + sf.toString());
		return sf.toString();
	}

	public static void main(String[] args) {
		String[] orderKey = { "customerno", "optype", "price", "cost", "recstatus", "arrivetime", "excheckouttime",
				"priceType", "roominguser", "payment", "orderid", "roomno", "name", "cardid", "ethnic", "cardtype",
				"fromaddress", "address" };
		PmsOrderServiceImpl.logger.info("orderKeyArray=" + orderKey.toString());
	}

	private void saveHotelRoomStatusTest(String uuid, Long hotelId, Map customNo, PmsRoomOrder order, int flag) {
		try {
			long id = this.synOrderConf.getHotelId();
			id = (id == 0 ? 521 : id);
			PmsOrderServiceImpl.logger.info("测试hotelid = " + id);
			if (hotelId == id) {
				PmsOrderServiceImpl.logger.info("记录酒店房间状态信息,hotelid = " + hotelId + ",flag = " + flag);
				String customerno = (String) customNo.get("customerno");
				String recstatus = (String) customNo.get("recstatus");
				String pmsstatus = (order != null ? (String) order.get("status") : "");

				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = sdf.format(date);

				if (flag == 0) {
					Db.update("insert into hotel_roomstatus_test (uuid,hotelid,customerno,status1,createtime) values('"
							+ uuid + "'," + hotelId + ",'" + customerno + "','" + recstatus + "','" + time + "')");
				} else if (flag == 1) {
					long count = Db.queryLong("select count(1) from hotel_roomstatus_test where hotelid = " + hotelId
							+ " and uuid = '" + uuid + "' and customerno = '" + customerno + "'");
					if (count > 0) {
						Db.update("update  hotel_roomstatus_test set status2 = '" + pmsstatus + "' where uuid = '"
								+ uuid + "' and customerno = '" + customerno + "'");
					} else {
						Db.update(
								"insert into hotel_roomstatus_test (uuid,hotelid,customerno,status2,createtime) values('"
										+ uuid + "'," + hotelId + ",'" + customerno + "','" + pmsstatus + "','" + time
										+ "')");
					}

				} else {
					// 回调pms
					List<String> pmsOrderIds = new ArrayList<>();
					pmsOrderIds.add(customerno);
					ReturnObject<List<CustomerResult>> ro = HotelPMSManager.getInstance().getService()
							.selectCustomerno(hotelId, pmsOrderIds);
					if ((ro != null) && (ro.getIsError() == false)) {
						PmsOrderServiceImpl.logger.info(
								"synPmsOrder::call pms ::selectCustomerno::ok::saveResult,返回size{}",
								ro.getValue().size());
						try {
							for (CustomerResult customerResult : ro.getValue()) {
								long count = Db.queryLong("select count(1) from hotel_roomstatus_test where hotelid = "
										+ hotelId + " and uuid = '" + uuid + "' and customerno = '" + customerno + "'");
								if (count > 0) {
									Db.update("update  hotel_roomstatus_test set status3 = '"
											+ customerResult.getRecstatus() + "' where uuid = '" + uuid
											+ "' and customerno = '" + customerno + "'");
								} else {
									Db.update(
											"insert into hotel_roomstatus_test (uuid,hotelid,customerno,status3,createtime) values('"
													+ uuid + "'," + hotelId + ",'" + customerno + "','"
													+ customerResult.getRecstatus() + "','" + time + "')");
								}
							}
						} catch (Exception e) {
							PmsOrderServiceImpl.logger.info("同步pms异常test:{}", e.getMessage());
						}
					} else {
						PmsOrderServiceImpl.logger.info(
								"同步pms异常test::call pms ::selectCustomerno::err::{},ro.getIsError() = {}",
								ro.getErrorMessage(), ro.getIsError());
					}
				}
			}
		} catch (Exception e) {
			PmsOrderServiceImpl.logger.error("记录酒店房间状态信息异常,hotelid = " + hotelId, e);
		} finally {
			PmsOrderServiceImpl.logger.info("记录酒店房间状态完毕,hotelid = " + hotelId + ",flag = " + flag);
		}
	}

	/**
	 * 创建/修改客单 saveCustomerNo
	 *
	 * @param hotelId
	 * @param datalist
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Map saveCustomerNo(Map param) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::saveCustomerNo::参数::" + param);
		HashMap data = new HashMap();
		HashMap unlockParam = new HashMap();
		// IN【在住】 CheckIn RX【预订取消】 NotIn OK【退房】 CheckOut IX【入住取消】 CheckOut
		// PM【挂账】 Account
		String pmsStates = "RE,RX,IN,IX,PM,OK";
		List<Map> datalist = (List<Map>) param.get("customerno");
		Long hotelId = (Long) param.get("hotelid");
		try {
			Map<Long, Set<Long>> map = (Map<Long, Set<Long>>) param.get("set") == null ? new HashMap<Long, Set<Long>>()
					: (Map<Long, Set<Long>>) param.get("set");
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			String cityCode = this.eHotelDAO.getCityIdByHotelId(hotel.getLong("disId"));
			PmsOrderServiceImpl.logger.info("更新[" + hotel.getHotelName() + "]酒店的客单记录");
			List<PmsRoomOrder> returnlist = new ArrayList<PmsRoomOrder>();
			StringBuffer sb = new StringBuffer();
			String uuid = UUID.randomUUID().toString();
			for (Map customNo : datalist) {
				if (!this.checkCacheOrder(customNo, hotelId)) {
					continue;
				}
				this.saveHotelRoomStatusTest(uuid, hotelId, customNo, null, 0);
				if (customNo.containsKey("optype") && customNo.get("optype").equals("delete")) {
					// hotelDAO.deletePmsRoomOrderByHotelIdAndCustomno(hotelId,customNo.getCustomerno());
					PmsRoomOrder order = this.hotelDAO.findPmsRoomOrderByHotelIdAndCustomno(hotelId,
							(String) customNo.get("customerno"));
					if (order == null) {
						PmsOrderServiceImpl.logger.warn("PMS要求取消一条不存在的客单记录，酒店id：" + hotelId + "中 酒店预客单号为："
								+ (String) customNo.get("customerno"));
					} else {
						PmsOrderServiceImpl.logger.info("PmsOrderServiceImpl::saveCustomerNo::order.delete()");
						order.delete();
						returnlist.add(order);
						// 记录已处理过的客单,放到redis队列中
						sb.append((String) customNo.get("customerno")).append(",");
						// 该记录处理完之后加redis锁
						this.setCacheOrder(customNo, hotelId);
					}

					this.saveHotelRoomStatusTest(uuid, hotelId, customNo, order, 1);
				} else {
					PmsRoomOrder order = this.hotelDAO.findPmsRoomOrderByHotelIdAndCustomno(hotelId,
							(String) customNo.get("customerno"));
					try {
						order = this.updatePmsRoomOrder(hotel, order, customNo, hotelId);
						PmsOrderServiceImpl.logger
								.info("PmsOrderServiceImpl::saveCustomerNo::updatePmsRoomOrder重复数据插入:order:{}", order);
						if (order == null) {
							continue;
						}
					} catch (MySQLIntegrityConstraintViolationException e) {
						// 对插入数据报索引异常的进行重新更新
						order = this.updatePmsRoomOrder(hotel, null, customNo, hotelId);
						PmsOrderServiceImpl.logger
								.info("PmsOrderServiceImpl::saveCustomerNo::updatePmsRoomOrder重复数据插入:order:{}", order);
					}
					returnlist.add(order);
					// 记录已处理过的客单,放到redis队列中
					sb.append((String) customNo.get("customerno")).append(",");
					// 该记录处理完之后加redis锁
					this.setCacheOrder(customNo, hotelId);
					// 记录身份证、卡号、民族 数据
					if (!"RE".equals(customNo.get("recstatus"))
							&& StringUtils.isNotBlank((String) customNo.get("cardid"))
							&& StringUtils.isNotBlank((String) customNo.get("cardtype"))
							&& StringUtils.isNotBlank((String) customNo.get("name"))) {
						PmsCheckinUserService checkinService = AppUtils.getBean(PmsCheckinUserService.class);
						PmsCheckinUser checkinUser = new PmsCheckinUser();
						checkinUser.set("Hotelid", hotelId);
						checkinUser.set("Name", customNo.get("name"));
						checkinUser.set("PmsRoomOrderNo", customNo.get("customerno"));
						checkinUser.set("Cardid", customNo.get("cardid"));
						checkinUser.set("Ethnic", customNo.get("ethnic"));
						checkinUser.set("CardType", customNo.get("cardtype"));
						checkinUser.set("FromAddress", customNo.get("fromaddress"));
						checkinUser.set("Address", customNo.get("address"));
						checkinUser.set("freqtrv", customNo.get("freqtrv"));// 是否常住人
						checkinUser.set("Updatetime", new Date());
						checkinUser.set("Createtime", new Date());
						// 入住人同步psb（1【上传】）
						checkinUser.set("checkinsyncpsb", customNo.get("checkinsyncpsb"));

						PmsOrderServiceImpl.logger
								.info("OTSMessage::PmsOrderServiceImpl::checkinService::saveOrUpdatePmsInCheckUser::参数::"
										+ checkinUser.getAttrs());
						checkinService.saveOrUpdatePmsInCheckUser(checkinUser);
						if (!Strings.isNullOrEmpty((String) customNo.get("freqtrv"))) {
							order.put("freqtrv", customNo.get("freqtrv"));
						}
					}

					this.saveHotelRoomStatusTest(uuid, hotelId, customNo, order, 1);

					try {
						pmsShiftService.shiftRoomForPromo(order, true);
					} catch (Exception ex) {
						logger.warn(String.format("failed to makeUpForPromo on hotelId:%s; customNo:%s...", hotelId,
								customNo), ex);
					}
				}

				this.saveHotelRoomStatusTest(uuid, hotelId, customNo, null, 2);
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
			if (sb.length() != 0) {
				sb.setLength(sb.length() - 1);
				SynedCustomerBean synedCustomerBean = new SynedCustomerBean();
				synedCustomerBean.setHotelId(hotelId);
				synedCustomerBean.setCustomernos(sb.toString());
				data.put("synedCustomerBean", synedCustomerBean);
			}

			// 若订单已经办理入住，需要取消 保留时间的消息发送的动作 修改ordertasks表中 status字段为不发送
			// CustomNo customNo = (CustomNo)datalist.get(0).get("customerno");
			// new
			// OrderServiceImpl().pushOutCheckInTimeMsgNo(Long.parseLong(customNo.getOrderid()));

			data.put("success", true);
			data.put("set", map);
		} catch (Exception e) {
			// 出错删除该酒店下的同步锁
			this.delCacheOrder(hotelId);
			PmsOrderServiceImpl.logger.info("test:test" + e.getMessage(), e);

			// data.put("success", false);
			// data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			// data.put("errormsg", e.getLocalizedMessage());
			throw new PmsException(PmsErrorEnum.noknowError.getErrorCode(), e.getLocalizedMessage());
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::saveCustomerNo::完成");
		return data;
	}

	private PmsRoomOrder updatePmsRoomOrder(EHotel hotel, PmsRoomOrder newOrder, Map customNo, Long hotelId)
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

		if (StringUtils.isNotBlank(order.getStr("Status")) && StringUtils.isNotBlank((String) customNo.get("recstatus"))
				&& (pmsStates.indexOf((String) customNo.get("recstatus")) < pmsStates
						.indexOf(order.getStr("Status")))) {
			PmsOrderServiceImpl.logger.info("PmsOrderServiceImpl::saveCustomerNo::错误PMS状态，当前客单状态已经是{},不能再{}",
					order.getStr("Status"), customNo.get("recstatus"));
			return null;
		}
		PmsOrderServiceImpl.logger.info("PmsOrderServiceImpl::saveCustomerNo::设置时间::customNo{}", customNo);
		if (PmsRoomOrderStatusEnum.RE.getId().equals(customNo.get("recstatus"))) {
			if (customNo.get("arrivetime") != null) {
				order.set("BeginTime", customNo.get("arrivetime"));
			}
			if (customNo.get("excheckouttime") != null) {
				order.set("EndTime", customNo.get("excheckouttime"));
			}
		} else if (PmsRoomOrderStatusEnum.IN.getId().equals(customNo.get("recstatus"))) {
			if (customNo.get("arrivetime") != null) {
				order.set("CheckInTime", customNo.get("arrivetime"));
			}
			// 续住的场景
			if ((customNo.get("excheckouttime") != null)
					&& !customNo.get("excheckouttime").equals(order.get("EndTime"))) {
				order.set("EndTime", customNo.get("excheckouttime"));
			}
			// pms1.0追加入住类型（pms，psb）
			if (customNo.get("checkintype") != null) {
				order.set("checkintype", "PMS".equals((String) customNo.get("checkintype")) ? "1" : "2");
			}
		} else if (PmsRoomOrderStatusEnum.OK.getId().equals(customNo.get("recstatus"))
				|| PmsRoomOrderStatusEnum.PM.getId().equals(customNo.get("recstatus"))) {
			if (customNo.get("excheckouttime") != null) {
				order.set("CheckOutTime", customNo.get("excheckouttime"));
			}
			// 在离店的时候判断如果未有入住时间，加上入住时间
			if ((order.get("CheckInTime") == null) && (customNo.get("arrivetime") != null)) {
				order.set("CheckInTime", customNo.get("arrivetime"));
			}
			// pms1.0追加离店类型（pms，psb）
			if (customNo.get("checkouttype") != null) {
				order.set("checkouttype", "PMS".equals((String) customNo.get("checkouttype")) ? "1" : "2");
			}
		}
		// pms1.0追加psb同步方式
		if (customNo.get("psbsynctype") != null) {
			order.set("psbsynctype", customNo.get("psbsynctype"));
		}
		// 为了弥补由于错过了一些状态 导致begintime、endtime为空
		if ((customNo.get("arrivetime") != null) && (order.get("BeginTime") == null)) {
			order.set("BeginTime", customNo.get("arrivetime"));
		}
		if ((customNo.get("excheckouttime") != null) && (order.get("EndTime") == null)) {
			order.set("EndTime", customNo.get("excheckouttime"));
		}

		order.set("Opuser", customNo.get("roominguser"));//
		order.set("OrderType", customNo.get("priceType"));
		order.set("OtherPay", customNo.get("payment"));// 以支付金额
		order.set("PmsOrderNo", customNo.get("orderid"));
		order.set("PmsRoomOrderNo", customNo.get("customerno"));
		order.set("Status", customNo.get("recstatus"));
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

		TRoom room = this.hotelDAO.findTRoomByHotelIdAndPmsno(hotelId, (String) customNo.get("roomno"));
		if (room != null) {
			if (room.getLong("Id") != order.getLong("RoomId")) {
				order.put("OldRoomId", order.get("RoomId"));// 记录被更换的旧房间信息
				order.put("OldRoomNo", order.get("RoomNo"));
				order.put("OldRoomTypeId", order.get("RoomTypeId"));
				order.put("OldRoomTypeName", order.get("RoomTypeName"));
			}
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
		PmsOrderServiceImpl.logger.info("OTSMessage::roomOrderList::换房::{}", pmsRoomOrderList);
		if (pmsRoomOrderList == null) {
			PmsOrderServiceImpl.logger.error("OTSMessage::roomOrderList:null:return");
			return;
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
			PmsOrderServiceImpl.logger.info("OTSMessage::roomOrderList::换房::pmsRoomOrder::{}", pmsRoomOrder);
			// 1,修改ota客单
			OtaRoomOrder roomOrder = AppUtils.getBean(OrderServiceImpl.class).findRoomOrderByPmsRoomOrderNoAndHotelId(
					changeRoomOrderBean.getPmsRoomOrderNo(), changeRoomOrderBean.getHotelId());
			Long otaorderid = (roomOrder != null) ? roomOrder.getLong("otaorderid") : -111l;
			PmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder-修改ota客单:orderid:{}-{}", otaorderid,
					roomOrder);
			if (roomOrder != null) {
				roomOrder.set("BeginTime", changeRoomOrderBean.getBeginTime());
				roomOrder.set("EndTime", changeRoomOrderBean.getEndTime());
				// 保存orderlog
				this.orderLogService.findOrderLog(roomOrder.getLong("otaorderid"))
						.set("checkinTime", changeRoomOrderBean.getBeginTime())
						.set("checkoutTime", changeRoomOrderBean.getEndTime()).saveOrUpdate();
				if (!roomOrder.get("RoomId").equals(changeRoomOrderBean.getRoomId())) {
					roomOrder.set("RoomId", changeRoomOrderBean.getRoomId().toString());
					PmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder-换房otaorderid::{},ota客单-{}",
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
					changeRoom.set("roomOrderId", pmsRoomOrder.get("id"));

					PmsOrderServiceImpl.logger.info("changePmsRoomOrder::roomid:{},oldroomid:{}",
							changeRoomOrderBean.getRoomId(), changeRoomOrderBean.getOldRoomId());
					if (changeRoomOrderBean.getOldRoomId() != null
							&& !changeRoomOrderBean.getOldRoomId().equals(changeRoomOrderBean.getRoomId())) {
						PmsOrderServiceImpl.logger.info("changePmsRoomOrder::otaorderid:{},换房信息:changeRoom:{}",
								roomOrder.get("otaorderid"), changeRoom);
						changeRoom.saveOrUpdate();
						// 释放房态
						OtaOrder order = orderService.findOtaOrderById(otaorderid);
						roomstateService.unlockRoomInOTS(order);
					}
				}
				String status = changeRoomOrderBean.getStatus();
				PmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder---status:{}", status);
				this.orderService.changeOrderStatusByPms(roomOrder.getLong("otaorderid"), pmsRoomOrder,
						pmsRoomOrder.getStr("freqtrv"));
			}

			// if(roomOrder.getInt("orderstatus") < 500){
			// String status= changeRoomOrderBean.getStatus();
			// if(status.equals(PmsRoomOrderStatusEnum.IN.toString())){
			// roomOrder.set("OrderStatus", OtaOrderStatusEnum.CheckIn);
			// }else if(status.equals(PmsRoomOrderStatusEnum.RX.toString())){
			// roomOrder.set("OrderStatus",OtaOrderStatusEnum.NotIn);
			// }else if(status.equals(PmsRoomOrderStatusEnum.OK.toString())){
			// roomOrder.set("OrderStatus",OtaOrderStatusEnum.CheckOut);
			// }else if(status.equals(PmsRoomOrderStatusEnum.IX.toString())){
			// roomOrder.set("OrderStatus",OtaOrderStatusEnum.CheckOut);
			// }else if(status.equals(PmsRoomOrderStatusEnum.PM.toString())){
			// roomOrder.set("OrderStatus",OtaOrderStatusEnum.Account);
			// }
			// }
			// roomOrder.saveOrUpdate();

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
		PmsOrderServiceImpl.logger.info("OTSMessage::cancelBookedRoomFromPMS::{}", unlockParam);
		if (unlockParam.size() == 3) {
			String cityCode = (String) unlockParam.get("cityCode");
			Long roomId = (Long) unlockParam.get("roomId");
			String[] checkInDate = (String[]) unlockParam.get("checkInDate");
			PmsOrderServiceImpl.logger.info("OTSMessage::cancelBookedRoomFromPMS::checkInDate::{}", checkInDate);

			// // added by chuaiqing at 2015-06-14 14:56:40
			// //roomService.cancelBookedRoomFromPMS(cityCode, roomId,
			// checkInDate);
			this.roomstateService.unlockRoomInPMS(hotelid, roomId, checkInDate);
			// // added end.

			// logger.info("退房刷整个酒店房态++++++++{},刷新时间:{}", hotelid,
			// DateUtils.getDatetime());
			// roomService.readonlyCalCacheByHotelidBatch(hotelid);
			PmsOrderServiceImpl.logger.info("OTSMessage::cancelBookedRoomFromPMS::ok");
		} else {
			// logger.info("OTSMessage::changePmsRoomOrder::" + map);
			// Iterator<Map.Entry<Long, Set<Long>>>
			// iter=map.entrySet().iterator();
			// while(iter.hasNext()){
			// Entry<Long, Set<Long>> entry = iter.next();
			// for (Long id : entry.getValue()) {
			// logger.info("changePmsRoom清楚缓存：hotelid:"+entry.getKey()+"roomtypeid:"+id);
			// try {
			// EventBusHelper.getEventBus().post(new
			// PmsCalCacheEvent(entry.getKey(), id));
			// } catch (Exception e) {
			// logger.error("PmsCalCacheEvent清楚缓存：hotelid:"+entry.getKey()+"
			// roomTypeID:"+id,
			// e);
			// }
			// }
			// }

			// // added by chuaiqing at 2015-06-17 16:47:20
			// //roomService.readonlyCalCacheByHotelidBatch(hotelid);
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::changePmsRoomOrder---end");
	}

	/**
	 * 有效订单和客单
	 *
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map synOrder(Map param) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::synOrder::全量更新::" + param);
		HashMap data = new HashMap();
		// Set<Long> hotelidsSet=new HashSet<Long>();
		// 记录下酒店哪些房型发生了变化，为后面计算缓存传參
		Map<Long, Set<Long>> map = new HashMap<Long, Set<Long>>();
		Long hotelId = (Long) param.get("hotelid");
		String synLockKey = RedisCacheName.IMIKE_OTS_SYNORDER_KEY + hotelId;
		String synLockValue = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			PmsOrderServiceImpl.logger
					.info("synOrder::synLockKey:" + synLockKey + "-----currentDate:" + sdf.format(new Date()));
			// 加redis锁，防止重复请求
			if ((synLockValue = DistributedLockUtil.tryLock(synLockKey, 60)) == null) {
				PmsOrderServiceImpl.logger.info("PmsOrderServiceImpl::synOrder::" + hotelId + "-----正在进行中,重复请求");
				return data;
			}
			PmsOrderServiceImpl.logger.info("PmsOrderServiceImpl::synOrder::synLockValue:" + synLockValue);

			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			PmsOrderServiceImpl.logger.info("同步[" + hotel.getHotelName() + "]酒店的所有有效清单");

			if (param.containsKey("roomTypeOderList")) {
				List<PmsOrder> changeOrder = new ArrayList<PmsOrder>();
				{
					List<Map> roomTypeOderList = (List<Map>) param.get("roomTypeOderList");
					List<PmsOrder> tempreturnlist = this.savePmsRoomTypeOrder(hotelId, roomTypeOderList);
					changeOrder.addAll(tempreturnlist);

					if (changeOrder != null) {
						for (PmsOrder pmsOrder : changeOrder) {
							Set<Long> tempset = map.get(pmsOrder.getLong("Hotelid"));
							if (tempset == null) {
								tempset = new HashSet<Long>();
								map.put(pmsOrder.getLong("Hotelid"), tempset);
							}
							tempset.add(pmsOrder.getLong("RoomTypeId"));
						}
					}
				}
			}

			if (param.containsKey("customList")) {
				// orderService saveCustomNo 相同
				List<Map> customList = (List<Map>) param.get("customList");
				Map saveCustomerNo = new HashMap();
				saveCustomerNo.put("hotelid", hotelId);
				saveCustomerNo.put("customerno", customList);
				saveCustomerNo.put("set", map);
				Map customerNoMap = this.saveCustomerNo(saveCustomerNo);
				map = (Map<Long, Set<Long>>) customerNoMap.get("set");
				if (customerNoMap.get("synedCustomerBean") != null) {
					// 该酒店已同步的客单,放到redis队列中
					SynedCustomerBean synedCustomerBean = (SynedCustomerBean) customerNoMap.get("synedCustomerBean");
					String jsonStr = this.gson.toJson(synedCustomerBean);
					PmsOrderServiceImpl.logger.info("已同步客单放到redis中{}{}", hotelId, jsonStr);
					this.cacheManager.lpush("synedCustomersQuene", jsonStr);
				}
			} else {
				// 如果没传客单，反查剩余全部
				SynedCustomerBean synedCustomerBean = new SynedCustomerBean();
				synedCustomerBean.setHotelId(hotelId);
				synedCustomerBean.setCustomernos("");
				String jsonStr = this.gson.toJson(synedCustomerBean);
				PmsOrderServiceImpl.logger.info("已同步客单放到redis中{}{}", hotelId, jsonStr);
				this.cacheManager.lpush("synedCustomersQuene", jsonStr);
			}
			// 是否有房间进行维修
			if (param.containsKey("repairList")) {
				List<TRoomRepair> changeRepair = new ArrayList<TRoomRepair>();
				// 先删除不存在列表中的维修单，之后调用原方法即可
				List<TRoomRepair> templist = this.hotelDAO.findAllTRoomRepairByHotelId(hotelId);
				List<Map> repairList = (List<Map>) param.get("repairList");
				for (TRoomRepair tRoomRepair : templist) {
					boolean find = false;
					for (Map repair : repairList) {
						if (tRoomRepair.getStr("RepairId").equals(repair.get("id"))) {
							find = true;
							break;
						}
					}
					if (find == false) {
						tRoomRepair.delete();
						changeRepair.add(tRoomRepair);
					}
				}
				List<TRoomRepair> tempreturnlist = this.saveRepairs(hotelId, repairList);
				changeRepair.addAll(tempreturnlist);
				if (changeRepair != null) {
					for (TRoomRepair tRoomRepair : changeRepair) {
						Set<Long> tempset = map.get(tRoomRepair.getLong("Hotelid"));
						if (tempset == null) {
							tempset = new HashSet<Long>();
							map.put(tRoomRepair.getLong("Hotelid"), tempset);
						}
						tempset.add(tRoomRepair.getLong("RoomTypeid"));
					}
				}
			} else {
				PmsOrderServiceImpl.logger.info("删除酒店所有修房记录:deleteAllRoomRepairs::hotelid:{}", hotelId);
				this.roomService.deleteAllRoomRepairs(hotelId);
			}

			// 根据酒店id刷新房态

			PmsOrderServiceImpl.logger.info("OTSMessage::resetRoomStatusByHotelidFromPMS::synOrder::全量更新::hotelid:{}",
					hotelId);

			// // added by chuaiqing at 2015-06-14 14:40:30
			// //roomService.resetRoomStatusByHotelidFromPMS(hotelId);
			this.roomstateService.resetHotelRoomstate(hotelId);
			// // added end.

			// 调用重现计算缓存
			// for (Long _hotelId : map.keySet()) {
			// Set<Long> roomTypeIds = map.get(_hotelId);
			// for (Long roomTypeId : roomTypeIds) {
			// 全亮量新房态
			// roomService.resetRoomStateFromPMS(_hotelId,roomTypeId);
			// }
			// }
			// 同步订单客单数据，清楚缓存
			// for (Long hotelid : map.keySet()) {
			// EventBusHelper.getEventBus().post(new
			// PmsQueryAgainEvent(hotelid));
			// }
			data.put("success", true);
		} catch (Exception e) {
			data.put("success", false);
			data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			data.put("errormsg", e.getLocalizedMessage());
		} finally {
			// 消除锁
			if (StringUtil.isNotEmpty(synLockValue)) {
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::synOrder::完成");
		return data;
	}

	/**
	 * 保存维修记录
	 *
	 * @param hotelId
	 * @param datalist
	 * @return
	 */
	private List<TRoomRepair> saveRepairs(Long hotelId, List<Map> datalist) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::saveRepairs::参数::" + datalist);
		PmsOrderServiceImpl.logger.info("更新[" + hotelId + "]酒店的维修记录");
		List<TRoomRepair> returnlist = new ArrayList<TRoomRepair>();
		for (Map repair : datalist) {
			if (repair.containsKey("optype") && repair.get("optype").equals("delete")) {
				// hotelDAO.deleteTRoomRepairByHotelIdAndRepairId(hotelId,repair.getId());
				TRoomRepair old = this.hotelDAO.findTRoomRepairByHotelIdAndRepairId(hotelId, (String) repair.get("id"));
				if (old == null) {
					PmsOrderServiceImpl.logger
							.warn("PMS要求取消一条不存在的维修单记录，酒店id：" + hotelId + "中 维修单号为：" + (String) repair.get("id"));
				} else {
					old.delete();
					returnlist.add(old);
				}
			} else {
				TRoomRepair old = this.hotelDAO.findTRoomRepairByHotelIdAndRepairId(hotelId, (String) repair.get("id"));
				if (old == null) {
					old = new TRoomRepair();
					old.set("Hotelid", hotelId);
				}
				old.set("BeginTime", repair.get("begintime"));
				old.set("EndTime", repair.get("endtime"));
				old.set("RepairId", repair.get("id"));
				TRoom room = this.hotelDAO.findTRoomByHotelIdAndPmsno(hotelId, (String) repair.get("roomid"));
				if (room != null) {
					old.set("Roomid", room.getLong("id"));
					old.set("roomtypeid", room.get("roomtypeid"));
				}
				returnlist.add(old);
				old.saveOrUpdate();
			}
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::saveRepairs::完成");
		return returnlist;
	}

	/**
	 * 订单结算 saveOrderCharge
	 *
	 * @param request
	 * @return
	 */
	@Override
	public Map orderCharge(Map param) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::orderCharge::" + param);
		HashMap data = new HashMap();
		try {
			Long hotelId = (Long) param.get("hotleid");
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			PmsOrderServiceImpl.logger.info("更新[" + hotel.getHotelName() + "]酒店的客单结算");

			List<Map> customerno = (List<Map>) param.get("customerno");
			for (Map orderCharge : customerno) {
				PmsRoomOrder order = this.hotelDAO.findPmsRoomOrderByHotelIdAndCustomno(hotelId,
						(String) orderCharge.get("custmoerid"));
				if (order == null) {
					PmsOrderServiceImpl.logger.warn(
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
			data.put("success", false);
			data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			data.put("errormsg", e.getLocalizedMessage());
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::orderCharge::完成");
		return data;
	}

	/**
	 * 同步酒店订单 savePmsRoomTypeOrder
	 *
	 * @param request
	 * @return
	 */
	@Override
	public List<PmsOrder> savePmsRoomTypeOrder(Long hotelId, List<Map> dataList) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::savePmsRoomTypeOrder");
		EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
		List<PmsOrder> returnlist = new ArrayList<PmsOrder>();
		PmsOrderServiceImpl.logger.info(
				"OTSMessage::PmsOrderServiceImpl::savePmsRoomTypeOrder::更新[" + hotel.getHotelName() + "]酒店的预订单记录");

		for (Map roomTypeOrder : dataList) {
			TRoomType roomType = this.eHotelDAO.findTRoomTypeByPmsno(hotelId, (String) roomTypeOrder.get("roomtypeid"));
			if (roomTypeOrder.containsKey("optype") && roomTypeOrder.get("optype").equals("delete")) {
				PmsOrder old = this.hotelDAO.findPmsOrderByHotelIdAndPmsOrderNo(hotelId,
						(String) roomTypeOrder.get("orderid"), (String) roomTypeOrder.get("batchno"));
				if (old == null) {
					PmsOrderServiceImpl.logger.warn("PMS要求取消一条不存在的预订单记录，酒店id：" + hotelId + "中 酒店预订单号为："
							+ roomTypeOrder.get("orderid") + "的批次号" + roomTypeOrder.get("batchno") + "");
				} else {
					old.set("Cancel", "T");
					old.set("Visible", "T");
					old.update();
					returnlist.add(old);
				}
			} else {
				PmsOrder old = this.hotelDAO.findPmsOrderByHotelIdAndPmsOrderNo(hotelId,
						(String) roomTypeOrder.get("orderid"), (String) roomTypeOrder.get("batchno"));
				if (old == null) {
					old = new PmsOrder();
					old.set("HotelId", hotelId);
					old.set("HotelPms", hotel.get("Pms"));
					old.set("PmsRoomTypeOrderNo", roomTypeOrder.get("batchno"));
					old.set("CreateTime", new Date());
					old.set("PmsOrderNo", roomTypeOrder.get("orderid"));
				}
				old.set("BeginTime", roomTypeOrder.get("arrivaltime"));
				old.set("EndTime", roomTypeOrder.get("excheckouttime"));
				old.set("CheckInNum", roomTypeOrder.get("checkincnt"));
				old.set("Opuser", roomTypeOrder.get("opuser"));
				old.set("OrderNum", roomTypeOrder.get("bookingcnt"));
				old.set("PlanNum", roomTypeOrder.get("roomingcnt"));
				old.set("Cancel", false);
				old.set("RoomTypeId", roomType.get("Id"));
				old.set("RoomTypePms", roomType.get("Pms"));
				old.set("Visible", "T");
				old.set("UpdateTime", new Date());
				old.saveOrUpdate();
				returnlist.add(old);
			}
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::savePmsRoomTypeOrder::OK");
		return returnlist;
	}

	/**
	 * 房费清单
	 *
	 * @param hotelId
	 * @param order
	 * @return
	 */
	@Override
	public Map roomCharge(Map param) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::roomCharge" + param);
		HashMap data = new HashMap();
		Long hotelId = (Long) param.get("hotleid");
		String synLockKey = RedisCacheName.IMIKE_OTS_ROOMCHARGE_KEY + hotelId;
		String synLockValue = null;
		try {
			// 加redis锁，防止重复请求
			if ((synLockValue = DistributedLockUtil.tryLock(synLockKey, 60)) == null) {
				PmsOrderServiceImpl.logger.info("PmsOrderServiceImpl::roomCharge::" + hotelId + ",重复请求");
				return data;
			}
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			PmsOrderServiceImpl.logger.info("更新[" + hotel.getHotelName() + "]酒店的客单清单");

			List<Map> orderCharge = (List<Map>) param.get("customerno");
			List<String> ids = new ArrayList<>();
			for (int i = 0; i < orderCharge.size(); i++) {
				Map pmsCostData = orderCharge.get(i);
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
				Map pmsCostData = orderCharge.get(i);
				PmsCost cost = new PmsCost();
				if (map.containsKey(pmsCostData.get("id"))) {
					cost = map.get(pmsCostData.get("id"));
				} else {
					cost.setHotelId(hotelId);
					cost.setHotelPms(hotel.getStr("Pms"));
					cost.setRoomCostNo((String) pmsCostData.get("id"));
				}
				cost.setCustomerno((String) pmsCostData.get("customerno"));
				cost.setCosttime((Date) pmsCostData.get("bizday"));
				cost.setCostType((String) pmsCostData.get("costtype"));
				cost.setOpuser((String) pmsCostData.get("opuser"));
				cost.setRoomCost((BigDecimal) pmsCostData.get("roomcost"));
				cost.setOtherCost((BigDecimal) pmsCostData.get("price"));
				cost.setSource((String) pmsCostData.get("costsource"));
				datas.add(cost);
			}
			PmsOrderServiceImpl.logger.info("datas:{}", datas.toArray());
			Db.batchBeans(datas, 100);
			data.put("success", true);
		} catch (Exception e) {
			data.put("success", false);
			data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			data.put("errormsg", e.getLocalizedMessage());
		} finally {
			// 消除锁
			if (StringUtil.isNotEmpty(synLockValue)) {
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::roomCharge::OK");
		return data;
	}

	protected void saveReceiveLogSuccess(PmsLog pmslog) {
		pmslog.setReslut(PmsResultEnum.success.getId());
		// Services.getIPmsService().saveOrUpdatePmsLog(pmslog);
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

	@Override
	public List<PmsRoomOrder> findNeedPmsRoomOrderSelect(Long hotelid, String roomNos) {
		return this.hotelDAO.findNeedPmsRoomOrderSelect(hotelid, roomNos);
	}

	@Override
	public List<PmsRoomOrder> findUnSynedPmsRoomOrder(Long hotelid, String customerNos) {
		return this.hotelDAO.findUnSynedPmsRoomOrder(hotelid, customerNos);
	}

	@Override
	public List<PmsRoomOrder> findNeedPmsRoomOrderSelectBefore(Long hotelid, String roomNos) {
		return this.hotelDAO.findNeedPmsRoomOrderSelectBefore(hotelid, roomNos);
	}

	/**
	 * 修改房间类型， SOAP 调用的接口
	 */
	@Override
	public Map savePmsRoomTypeOrder(Map param) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::savePmsRoomTypeOrder::" + param);
		HashMap data = new HashMap();
		Long hotelId = (Long) param.get("hotelid");
		try {
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			Map<String, TRoomType> roomTypePms = new HashMap<String, TRoomType>();
			PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::call::savePmsRoomTypeOrder::更新["
					+ hotel.getHotelName() + "]酒店的预订单记录::hotelId::" + hotelId);
			List<Map> dataList = (List<Map>) param.get("datalist");
			PmsOrderServiceImpl.logger
					.info("OTSMessage::PmsOrderServiceImpl::call::savePmsRoomTypeOrder::dataList::" + dataList);
			List<PmsOrder> returnlist = this.savePmsRoomTypeOrder(hotelId, dataList);
			PmsOrderServiceImpl.logger
					.info("OTSMessage::PmsOrderServiceImpl::call::flushCacheByHotelidAndRoomTypeId::" + returnlist);
			this.flushCacheByHotelidAndRoomTypeId(returnlist);
			PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::savePmsRoomTypeOrder::ok");
			data.put("success", true);
		} catch (Exception e) {
			data.put("success", false);
			data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			data.put("errormsg", e.getLocalizedMessage());
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::savePmsRoomTypeOrder::OK");

		return data;
	}

	private void flushCacheByHotelidAndRoomTypeId(List<PmsOrder> returnlist) {
		// 需要刷新缓存，更新房态
		for (PmsOrder pmsOrder : returnlist) {
			PmsOrderServiceImpl.logger.info("savePmsRoomTypeOrder清楚缓存：hotelid:" + pmsOrder.getLong("HotelId")
					+ "roomtypeid:" + pmsOrder.getLong("RoomTypeId"));
			try {
				EventBusHelper.getEventBus()
						.post(new PmsCalCacheEvent(pmsOrder.getLong("HotelId"), pmsOrder.getLong("RoomTypeId")));
			} catch (Exception e) {
				PmsOrderServiceImpl.logger.error("savePmsRoomTypeOrder清楚缓存：hotelid:" + pmsOrder.getLong("HotelId")
						+ "roomtypeid:" + pmsOrder.getLong("RoomTypeId"));
			}
		}
	}

	@Override
	public Map cancelOrder(Map param) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::cancelOrder::param::" + param);
		Long hotelId = (Long) param.get("hotelid");
		HashMap data = new HashMap();
		try {
			EHotel hotel = this.eHotelDAO.findEHotelByid(hotelId);
			PmsOrderServiceImpl.logger.debug(
					"OTSMessage::PmsOrderServiceImpl::call::cancelOrder::取消[" + hotel.getHotelName() + "]酒店的预订单记录");
			List<PmsOrder> returnlist = new ArrayList<PmsOrder>();
			List<Map> datalist = (List<Map>) param.get("datalist");
			for (Map cancelBean : datalist) {
				List<PmsOrder> list = this.hotelDAO.findPmsOrderByHotelIdAndPmsOrderNo(hotelId,
						Long.parseLong((String) cancelBean.get("orderidpms")));
				returnlist.addAll(list);
				for (PmsOrder pmsOrder : list) {
					pmsOrder.set("cancel", "T");
					pmsOrder.saveOrUpdate();
					PmsOrderServiceImpl.logger
							.info("OTSMessage::PmsOrderServiceImpl::cancelOrder::pmsOrder.saveOrUpdate()");
				}
				this.flushCacheByHotelidAndRoomTypeId(returnlist);
			}
			data.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("success", false);
			data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			data.put("errormsg", e.getLocalizedMessage());
		}
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::cancelOrder::OK");
		return data;
	}

	@Override
	public Map saveRepairs(Map param) {
		PmsOrderServiceImpl.logger.info("OTSMessage::PmsOrderServiceImpl::saveRepairs::param::" + param);
		Long hotelId = (Long) param.get("hotelid");
		HashMap data = new HashMap();
		try {
			List<TRoomRepair> returnlist = new ArrayList<TRoomRepair>();
			List<Map> datalist = (List<Map>) param.get("datalist");
			for (Map repair : datalist) {
				Boolean delete = (Boolean) repair.get("delete");
				if (repair.containsKey("delete") && delete) {
					TRoomRepair old = this.hotelDAO.findTRoomRepairByHotelIdAndRepairId(hotelId,
							(String) repair.get("id"));
					if (old == null) {
						PmsOrderServiceImpl.logger
								.info("PMS要求取消一条不存在的维修单记录，酒店id：" + hotelId + "中 维修单号为：" + repair.get("id"));
					} else {
						TRoomRepair roomRepair = TRoomRepair.dao
								.findById(Long.parseLong(String.valueOf(old.get("id"))));
						// 房子修好了，则删除ots里的记录
						roomRepair.delete();
						returnlist.add(old);
					}
				} else {
					TRoomRepair old = this.hotelDAO.findTRoomRepairByHotelIdAndRepairId(hotelId,
							(String) repair.get("id"));
					if (old == null) {
						old = new TRoomRepair();
						old.set("hotelid", hotelId);
					}
					old.set("begintime", repair.get("begintime"));
					old.set("endtime", repair.get("endtime"));
					old.set("RepairId", repair.get("id"));
					TRoom room = this.hotelDAO.findTRoomByHotelIdAndPmsno(hotelId, (String) repair.get("roomid"));
					if (room != null) {
						old.set("roomid", room.get("id"));
						old.set("roomtypeid", room.get("roomtypeid"));
					}
					returnlist.add(old);
					old.saveOrUpdate();
				}
			}
			for (TRoomRepair tRoomRepair : returnlist) {
				EventBusHelper.getEventBus()
						.post(new PmsCalCacheEvent(tRoomRepair.getLong("hotelid"), tRoomRepair.getLong("roomtypeid")));
			}
			data.put("success", true);
		} catch (Exception e) {
			data.put("success", false);
			data.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			data.put("errormsg", e.getLocalizedMessage());
		}
		return data;
	}

	/**
	 * @param hotelid
	 *            全量更新，同步信息 今天的
	 */
	@Override
	public void synPmsOrder(Long hotelid, String roomNos) {
		PmsOrderServiceImpl.logger.info("OTSMessage::resetRoomStatusByHotelidFromPMS::synPmsOrder::全量更新::同步消息:{}",
				hotelid);

		if (hotelid == null) {
			PmsOrderServiceImpl.logger.error("synPmsOrder::没有hotel信息，返回了。hotelid::" + hotelid);
		}
		PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
		List<PmsRoomOrder> oldRoomOrderlist = pmsOrderService.findNeedPmsRoomOrderSelect(hotelid, roomNos);
		if (oldRoomOrderlist.size() > 0) {
			List<String> pmsCustomNos = new ArrayList<>();
			for (PmsRoomOrder temp : oldRoomOrderlist) {
				if (pmsCustomNos.contains(temp.get("PmsRoomOrderNo")) == false) {
					pmsCustomNos.add(String.valueOf(temp.get("PmsRoomOrderNo")));
				}
			}
			THotel hotel = this.hotelService.readonlyTHotel(hotelid);
			// pms2.0
			if ("T".equals(hotel.getStr("isNewPms"))) {
				JSONObject pmsCustomNosJson = new JSONObject();
				pmsCustomNosJson.put("hotelid", hotel.getStr("pms"));
				StringBuffer ids = new StringBuffer();
				for (String id : pmsCustomNos) {
					ids.append(id).append(",");
				}
				ids.setLength(ids.length() - 1);
				pmsCustomNosJson.put("customerid", ids.toString());

				String result = null;
				Transaction t = Cat.newTransaction("PmsHttpsPost", UrlUtils.getUrl("newpms.url") + "/selectcustomerno");
				try {
					PmsOrderServiceImpl.logger.info("Pms2.0同步今天0点之后数据传参,hotelid:{},参数:{}", hotelid,
							pmsCustomNos.toString());
					result = this.doPostJson(UrlUtils.getUrl("newpms.url") + "/selectcustomerno",
							pmsCustomNosJson.toJSONString());
					PmsOrderServiceImpl.logger.info("Pms2.0同步今天0点之后数据结果{}{}", hotelid, result);
					Cat.logEvent("Pms/selectcustomerno", CommonUtils.toStr(hotelid), Event.SUCCESS,
							pmsCustomNosJson.toJSONString());
					t.setStatus(Transaction.SUCCESS);
				} catch (Exception e) {
					t.setStatus(e);
					this.logger.error("Pms/selectcustomerno error.", e);
					throw MyErrorEnum.errorParm.getMyException(e.getMessage());
				} finally {
					t.complete();
				}

				JSONObject returnObject = JSON.parseObject(result);
				if (returnObject.getBooleanValue("success")) {
					JSONArray customerno = returnObject.getJSONArray("customerno");
					JSONObject param = new JSONObject();
					param.put("hotelid", hotelid);
					param.put("customerno", customerno);
					Map map = this.newPmsOrderService.saveCustomerNo(param);
				} else {
					PmsOrderServiceImpl.logger.error("Pms2.0同步今天0点之后数据::err::{}", returnObject.getString("errormsg"));
					throw MyErrorEnum.customError.getMyException(
							"NEWPMSselectCustomerno:" + returnObject.getString("errormsg") + "；如需帮助请联系客服人员");
				}
			} else {
				// pms1.0
				ReturnObject<List<CustomerResult>> ro = HotelPMSManager.getInstance().getService()
						.selectCustomerno(hotelid, pmsCustomNos);
				PmsOrderServiceImpl.logger.info(
						"synPmsOrder::call pms ::selectCustomerno::ok::saveResult,同步今天0点之后数据传参{}{}", hotelid,
						pmsCustomNos.toString());
				if ((ro != null) && (ro.getIsError() == false)) {
					PmsOrderServiceImpl.logger.info("synPmsOrder::call pms ::selectCustomerno::ok::saveResult,返回size{}",
							ro.getValue().size());
					if ((pmsCustomNos.size() > 1) && (ro.getValue().size() <= 1)) {// 判断是否升级ota
						PmsOrderServiceImpl.logger.error("ota为旧版本:{}", hotelid);
					}
					try {
						String theResult = this.saveResult(hotelid, ro.getValue());
						PmsOrderServiceImpl.logger.info(
								"synPmsOrder::call pms ::selectCustomerno::ok::saveResult,同步今天0点之后数据{}{}", hotelid,
								theResult);
					} catch (Exception e) {
						PmsOrderServiceImpl.logger.error("同步今天0点之后数据错误:{}", e.getMessage());
					}
				} else {
					PmsOrderServiceImpl.logger.error("synPmsOrder::call pms ::selectCustomerno::err::{}",
							ro.getErrorMessage());
				}
			}
		}
	}

	/**
	 * @param hotelid
	 *            全量更新，同步信息 今天之前的
	 */
	@Override
	public String synPmsOrderBefore(Long hotelid, String roomNos) {
		PmsOrderServiceImpl.logger.info("OTSMessage::resetRoomStatusByHotelidFromPMS::synPmsOrder::全量更新::同步消息:{}",
				hotelid);

		if (hotelid == null) {
			PmsOrderServiceImpl.logger.info("synPmsOrder::没有hotel信息，返回了。hotelid::" + hotelid);
			return "无数据";
		}
		PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
		/*
		 * List<PmsOrder> oldlist =
		 * pmsOrderService.findNeedPmsOrderSelect(hotelid);
		 * if(oldlist.size()>0){ List<String> pmsOrderIds=new ArrayList<>(); for
		 * (PmsOrder temp : oldlist) {
		 * if(pmsOrderIds.contains(temp.get("PmsOrderNo"))==false){
		 * pmsOrderIds.add(String.valueOf(temp.get("PmsOrderNo"))); } }
		 * logger.info ("synPmsOrder::call pms selectOrder::pmsOrderIds::"
		 * +pmsOrderIds); ReturnObject<List<OrderResult>> returnobject =
		 * HotelPMSManager.getInstance().getService().selectOrder(hotelid,
		 * pmsOrderIds); logger.info(
		 * "synPmsOrder::call pms selectOrder::pmsOrderIds::ok"); //
		 * PmsOutService pmsOurService=new PmsOutService(); //
		 * pmsOurService.setTimeout(60*3); // ReturnObject<List<OrderResult>>
		 * returnobject=pmsOurService.selectOrder(hotelid, pmsOrderIds);
		 * if(returnobject!=null && returnobject.getIsError()==false){
		 * if(returnobject.getValue()!=null){ for (OrderResult or :
		 * returnobject.getValue()) { saveResult(hotelid, or,oldlist); } } }
		 * else { logger.info(
		 * "synPmsOrder::call pms selectOrder::哎呦 pms selectOrder 异常::err::" +
		 * returnobject.getErrorMessage()); } } if(oldlist.size()>0){
		 * logger.info("synPmsOrder::deletePmsOrder");
		 * pmsOrderService.deletePmsOrder(oldlist);
		 * logger.info("synPmsOrder::deletePmsOrder::ok"); }
		 */
		List<PmsRoomOrder> oldRoomOrderlist = pmsOrderService.findNeedPmsRoomOrderSelectBefore(hotelid, roomNos);
		String result = "";
		if (oldRoomOrderlist.size() > 0) {
			List<String> pmsCustomNos = new ArrayList<>();
			for (PmsRoomOrder temp : oldRoomOrderlist) {
				if (pmsCustomNos.contains(temp.get("PmsRoomOrderNo")) == false) {
					pmsCustomNos.add(String.valueOf(temp.get("PmsRoomOrderNo")));
				}
			}
			// PmsOutService pmsOurService=new PmsOutService();
			// pmsOurService.setTimeout(60*3);
			PmsOrderServiceImpl.logger.info("synPmsOrder::call pms ::selectCustomerno::{}", pmsCustomNos);
			for (String k : pmsCustomNos) {
				List<String> list = new ArrayList<String>();
				list.add(k);
				ReturnObject<List<CustomerResult>> ro = HotelPMSManager.getInstance().getService()
						.selectCustomerno(hotelid, list);
				/*
				 * if(pmsCustomNos.size()>1){ pmsCustomNos.remove(0); }
				 * ReturnObject<List<CustomerResult>> ro =
				 * HotelPMSManager.getInstance
				 * ().getService().selectCustomerno(hotelid, pmsCustomNos);
				 */
				PmsOrderServiceImpl.logger.info(
						"synPmsOrder::call pms ::selectCustomerno::ok::saveResult,同步今天之前数据传参{}{}", hotelid,
						pmsCustomNos.toString());
				if ((ro != null) && (ro.getIsError() == false)) {
					PmsOrderServiceImpl.logger.info("synPmsOrder::call pms ::selectCustomerno::ok::saveResult,返回size{}",
							ro.getValue().size());
					String theResult = this.saveResult(hotelid, ro.getValue());
					result += ("~~~" + theResult);
					PmsOrderServiceImpl.logger.info(
							"synPmsOrder::call pms ::selectCustomerno::ok::saveResult,同步今天之前数据{}{}", hotelid,
							theResult);
				} else {
					PmsOrderServiceImpl.logger.info("synPmsOrder::call pms ::selectCustomerno::err::{}",
							ro.getErrorMessage());
				}
			}
		}
		return result;
	}

	@Override
	public String saveResult(Long hotelId, List<CustomerResult> result) {
		if ((result == null) || (result.size() == 0)) {
			return "无数据";
		}
		List<Map> datalist = new ArrayList<Map>();
		List<String> resultList = new ArrayList<String>();
		for (CustomerResult customerResult : result) {
			StringBuffer sb = new StringBuffer();
			Map bean = new HashMap();
			sb.append("roomtypepms:").append(customerResult.getRoomTypePms()).append(",");
			sb.append("roompms:").append(customerResult.getRoompms()).append(",");
			sb.append("delete:").append("false").append(",");
			sb.append("customerno:").append(customerResult.getCustomerpms()).append(",");
			sb.append("recstatus:").append(customerResult.getRecstatus());
			resultList.add(sb.toString());
			bean.put("roomtypepms", customerResult.getRoomTypePms());
			bean.put("roompms", customerResult.getRoompms());
			bean.put("delete", false);
			bean.put("customerno", customerResult.getCustomerpms());
			// PmsRoomOrderStatusEnum
			// recstatus=PmsRoomOrderStatusEnum.findPmsRoomOrderStatusEnumById(customerResult.getRecstatus());
			bean.put("recstatus", customerResult.getRecstatus());
			bean.put("rectype", "");
			if (customerResult.getPrice() != null) {
				bean.put("price", customerResult.getPrice());
			}
			if (customerResult.getPayment() != null) {
				bean.put("payment", customerResult.getPayment());
			}
			if (customerResult.getCost() != null) {
				bean.put("cost", customerResult.getCost());
			}
			if (customerResult.getBalance() != null) {
				bean.put("balance", customerResult.getBalance());
			}
			bean.put("arrivetime", customerResult.getArrivaltime());
			bean.put("excheckouttime", customerResult.getExcheckouttime());
			bean.put("Roominguser", customerResult.getRoominguser());
			String priceType = customerResult.getPaytype();
			if (!"H".equalsIgnoreCase(priceType)) {
				bean.put("priceType", 2);
			} else {
				bean.put("priceType", 1);
			}
			// pms、psb同步数据字段更新
			// pms1.0追加入住类型（pms，psb）
			if (customerResult.getCheckintype() != null) {
				bean.put("checkintype", "PMS".equals((String) customerResult.getCheckintype()) ? "1" : "2");
			}
			if (customerResult.getCheckouttype() != null) {
				bean.put("checkouttype", "PMS".equals((String) customerResult.getCheckouttype()) ? "1" : "2");
			}
			bean.put("checkinsyncpsb", customerResult.getCheckinsyncpsb());
			bean.put("psbsynctype", customerResult.getPsbsynctype());

			datalist.add(bean);
		}
		Map tempMap = new HashMap();
		tempMap.put("hotelid", hotelId);
		tempMap.put("customerno", datalist);
		// PmsOrderService pmsOrderService =
		// AppUtils.getBean(PmsOrderService.class);
		Map map = this.saveCustomerNo(tempMap);
		HotelService hotelService = AppUtils.getBean(HotelService.class);
		/*
		 * if (map.containsKey("set")) { Map<Long, Set<Long>> map_ = (Map<Long,
		 * Set<Long>>) map.get("set"); // 计算缓存 for (Object _hotelId :
		 * map_.keySet()) { Set<Long> roomTypeIds = map_.get(_hotelId); for
		 * (Long roomTypeId : roomTypeIds) {
		 * this.roomService.calCacheByHotelIdAndRoomType((Long) _hotelId,
		 * roomTypeId); } } }
		 */

		return resultList.toString();
	}

	private void saveResult(Long hotelId, OrderResult or, List<PmsOrder> oldlist) {
		if (or == null) {
			return;
		}
		if (or.getRoomTypeOrderList() != null) {
			List<Map> dataList = new ArrayList<Map>();
			for (RoomTypeOrderResult result : or.getRoomTypeOrderList()) {
				Map rto = new HashMap();
				rto.put("orderid", or.getPmsorderid());
				rto.put("optype", "delete");
				rto.put("roomtypepms", result.getRoomtypePms());
				rto.put("bookingcnt", result.getBookingcnt());
				rto.put("roomingcnt", result.getRoomingcnt());
				rto.put("checkincnt", result.getCheckincnt());
				rto.put("price", result.getPrice());
				rto.put("begintime", result.getArrivaldate());
				rto.put("endtime", result.getExcheckoutdate());
				rto.put("opuser", result.getOpuser());
				rto.put("batchno", result.getBatchno());
				dataList.add(rto);
				this.removeOldList(oldlist, rto);
			}
			PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
			List<PmsOrder> sendlist = pmsOrderService.savePmsRoomTypeOrder(hotelId, dataList);

			/*
			 * for (PmsOrder pmsOrder : sendlist) { HotelService hotelService =
			 * AppUtils.getBean(HotelService.class);
			 * this.roomService.calCacheByHotelIdAndRoomType(pmsOrder.getLong(
			 * "Hotelid"), pmsOrder.getLong("roomtypepms")); }
			 */
			// MikeWebHessianTask task=new
			// MikeWebHessianTask(MikeWebHessianCmdEnum.changePmsOrder,
			// sendlist);
			// SysConfig.getInstance().getThreadPool().submit(task);
		}

		if (or.getCustomerList() != null) {
			List<Map> datalist = new ArrayList<Map>();
			for (CustomerResult result : or.getCustomerList()) {
				Map bean = new HashMap();
				bean.put("roomtypepms", result.getRoomTypePms());
				bean.put("roompms", result.getRoompms());
				bean.put("delete", false);
				bean.put("orderid", or.getPmsorderid());
				bean.put("customerno", result.getCustomerpms());
				// PmsRoomOrderStatusEnum
				// recstatus=PmsRoomOrderStatusEnum.findPmsRoomOrderStatusEnumById(result.getRecstatus());
				bean.put("recstatus", result.getRecstatus());
				bean.put("rectype", "");
				bean.put("roompms", result.getRoompms());
				if (result.getPrice() != null) {
					bean.put("price", result.getPrice());
				}
				if (result.getPayment() != null) {
					bean.put("payment", result.getPayment());
				}
				if (result.getCost() != null) {
					bean.put("cost", result.getCost());
				}
				if (result.getBalance() != null) {
					bean.put("balance", result.getBalance());
				}
				bean.put("arrivetime", result.getArrivaltime());
				bean.put("excheckouttime", result.getExcheckouttime());
				bean.put("roominguser", result.getRoominguser());
				String priceType = result.getPaytype();
				if (!"H".equalsIgnoreCase(priceType)) {
					bean.put("priceType", 2);
				} else {
					bean.put("priceType", 1);
				}
				datalist.add(bean);
			}
			Map tempMap = new HashMap();
			tempMap.put("hotelid", hotelId);
			tempMap.put("customerno", datalist);
			PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
			Map map = pmsOrderService.saveCustomerNo(tempMap);
			// HotelService hotelService = AppUtils.getBean(HotelService.class);
			// 计算缓存
			/*
			 * for (Object _hotelId : map.keySet()) { Set<Long> roomTypeIds =
			 * (Set<Long>) map.get(_hotelId); for (Long roomTypeId :
			 * roomTypeIds) {
			 * this.roomService.calCacheByHotelIdAndRoomType((Long) _hotelId,
			 * roomTypeId); } }
			 */
		}
	}

	private void removeOldList(List<PmsOrder> oldlist, Map rto) {
		if ((oldlist == null) || (oldlist.size() == 0)) {
			return;
		}
		Iterator<PmsOrder> iter = oldlist.iterator();
		while (iter.hasNext()) {
			PmsOrder old = iter.next();
			if (StringUtils.equals(String.valueOf(old.get("PmsOrderNo")), String.valueOf(rto.get("orderid")))
					&& StringUtils.equals(String.valueOf(old.get("PmsRoomTypeOrderNo")),
							String.valueOf(rto.get("batchno")))) {
				iter.remove();
			}
		}
	}

	@Override
	public void batchUpdateCustomerNo(Long hotelId) {
		try {
			String sql = "update b_pmsroomorder a inner join (select id,roomtypeid,name,pms from t_room where roomtypeid in (select id from t_roomtype where thotelid=?)) b on a.roompms=b.pms and a.roomno=b.name set a.roomtypeid=b.roomtypeid,a.roomid=b.id where a.hotelid=?";
			Db.update(sql, hotelId, hotelId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String doPostJson(String url, String json) {
		JSONObject back = new JSONObject();
		try {
			return PayTools.dopostjson(url, json);
		} catch (Exception e) {
			PmsOrderServiceImpl.logger.info("doPostJson参数:{},{},异常:{}", url, json, e.getLocalizedMessage());
			e.printStackTrace();
			back.put("success", false);
			back.put("errorcode", "-1");
			back.put("errormsg", e.getLocalizedMessage());
		}
		return back.toJSONString();
	}
}
