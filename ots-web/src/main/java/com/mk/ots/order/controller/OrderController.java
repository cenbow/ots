package com.mk.ots.order.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.mk.framework.util.CommonUtils;
import com.mk.ots.system.model.UToken;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.UrlUtils;
import com.mk.ots.common.bean.PageObject;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.OrderMethodEnum;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.utils.DESUtils;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomTypeService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.order.bean.OtaCheckInUser;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.OtaRoomPrice;
import com.mk.ots.order.model.OtaOrderMac;
import com.mk.ots.order.service.OrderServiceImpl;
import com.mk.ots.order.service.OrderUtil;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.pay.service.IPayService;
import com.mk.ots.pay.service.IPriceService;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Room;
import com.mk.ots.utils.PayLockKeyUtil;
import com.mk.ots.web.ServiceOutput;

@RestController
@RequestMapping("/order")
public class OrderController {

	private static Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderServiceImpl orderService;
	@Autowired
	private IPayService payService;
	@Autowired
	private IPriceService priceService;
	@Autowired
	private RoomTypeService roomTypeService;
	@Autowired
	private OrderUtil orderUtil;
	@Autowired
	private HotelService hotelService;
	@Autowired
	private RoomstateService roomstateService;
	
	// 全部、进行中、已完成
	private ImmutableMap<String, String> statetypeMap = ImmutableMap.of("all", 	"110,120,140,160,180,190,200,510,514,512,513,520", 
																		"doing","110,120,140,160,180,510", 
																		"done",	"200,190");

	/**
	 * 创建订单
	 * 
	 * @param request
	 * @returnd
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> createOrder(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::createOrder::准备下单");
		String hotelId = request.getParameter("hotelid");
		JSONObject jsonObj = null;
		try {
		    logger.info("创建订单开始 , hotelid = "+hotelId);
			// 提取orderbean
			OtaOrder order = this.extractOrderBean(request, false);
			// 订单转换为json
			jsonObj = new JSONObject();
			// 创建订单
			this.orderService.doCreateOrder(order, jsonObj);
			
			logger.info("创建订单成功,返回数据 : "+jsonObj.toJSONString());
			Cat.logEvent("/order/create", CommonUtils.toStr(order.getHotelId()), Event.SUCCESS, jsonObj.toJSONString());
		} catch (Exception e) {
			OrderController.logger.error("创建订单失败,hotelid = " + hotelId, e);
			Cat.logError("order create error", e);
			throw e;
		}

		OrderController.logger.info("OTSMessage::OrderController::createOrder::orderService.putOrderJobIntoManager");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	
	/**
	 * 查询入住人
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/selectcheckinuser", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> selecteCheckinUser(HttpServletRequest request){
		return new ResponseEntity<JSONObject>(this.orderService.getCheckInUserByMid(), HttpStatus.OK);
	}
	
	/**
	 * 创建订单
	 * 
	 * @param request
	 * @returnd
	 */
	@RequestMapping(value = "/createByRoomType", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> createOrderByRoomType(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::createOrderByRoomType::准备下单");
		
		String hotelId = request.getParameter("hotelid");
		JSONObject jsonObj = null;
		try {
			logger.info("创建订单开始 , hotelid = "+hotelId);
			// 提取orderbean
			OtaOrder order = this.extractOrderBean(request, true);
			// 订单转换为json
			jsonObj = new JSONObject();
			// 创建订单
			this.orderService.doCreateOrder(order, jsonObj);
			jsonObj.put("success", true);
			logger.info("创建订单成功,返回数据 : " + jsonObj.toJSONString());
			Cat.logEvent("/order/create", CommonUtils.toStr(order.getHotelId()), Event.SUCCESS, jsonObj.toJSONString());
		} catch (Exception e) {
			OrderController.logger.error("创建订单失败,hotelid = " + hotelId, e);
			Cat.logError("order createByRoomType error", e);
			throw e;
		}
		
		OrderController.logger.info("createOrderByRoomType::ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	/**
	 * 修改订单
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> modifyOrder(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::modifyOrder::begin");
		// 提取orderbean
		if (this.orderUtil.checkNotNulls(request, new String[] { "orderid" })) {
			throw MyErrorEnum.errorParm.getMyException("必填项为空");
		}

		// redis锁
		String orderId = request.getParameter("orderid");
		if (StringUtils.isBlank(orderId)) {
			throw MyErrorEnum.errorParm.getMyException("订单号不存在");
		}

		OrderController.logger.info("修改订单开始,orderid = " + orderId);
		String lockValue = DistributedLockUtil.tryLock("orderTasksLock_" + orderId, 40);
		if (lockValue == null) {
			OrderController.logger.info("订单：" + orderId + "正在执行订单任务，无法修改");
			throw MyErrorEnum.customError.getMyException("订单：" + orderId + "正在执行订单任务，无法修改");
		}
		String checkLockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKey4PayCallBack(orderId), 40);
		if (checkLockValue == null) {
			OrderController.logger.info("订单：" + orderId + "正在执行修改订单任务，无法修改");
			throw MyErrorEnum.customError.getMyException("订单：" + orderId + "正在执行修改订单任务，无法修改");
		}

		JSONObject jsonObj = new JSONObject();
		try {
			this.orderService.doModifyOrder(request, jsonObj, false);
		} catch (Exception e) {
			Cat.logError("/order/modify error", e);
			throw e;
		} finally{
			// 释放 redis锁
			OrderController.logger.info("释放分布锁, orderId= " + orderId);
			DistributedLockUtil.releaseLock("orderTasksLock_" + orderId, lockValue);
			DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4PayCallBack(orderId), checkLockValue);
		}
		Cat.logEvent("/order/modify", CommonUtils.toStr(orderId), Event.SUCCESS, jsonObj.toJSONString());
		OrderController.logger.info("OTSMessage::OrderController::modifyOrder::ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	
	/**
	 * 修改订单
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modifyByRoomType", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> modifyOrderByRoomType(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::modifyOrderByRoomType::begin");
		// 提取orderbean
		if (this.orderUtil.checkNotNulls(request, new String[] { "orderid" })) {
			throw MyErrorEnum.errorParm.getMyException("必填项为空");
		}
		
		// redis锁
		String orderId = request.getParameter("orderid");
		if (StringUtils.isBlank(orderId)) {
			throw MyErrorEnum.errorParm.getMyException("订单号不存在");
		}
		
		OrderController.logger.info("修改订单开始,orderid = " + orderId);
		String lockValue = DistributedLockUtil.tryLock("orderTasksLock_" + orderId, 40);
		if (lockValue == null) {
			OrderController.logger.info("订单：" + orderId + "正在执行订单任务，无法修改");
			throw MyErrorEnum.customError.getMyException("订单：" + orderId + "正在执行订单任务，无法修改");
		}
		
		JSONObject jsonObj = new JSONObject();
		try {
			this.orderService.doModifyOrder(request, jsonObj, true);
		} catch (Exception e) {
			Cat.logError("/order/modifyByRoomType error", e);
			throw e;
		} finally{
			// 释放 redis锁
			OrderController.logger.info("释放分布锁, orderId= " + orderId);
			DistributedLockUtil.releaseLock("orderTasksLock_" + orderId, lockValue);
		}
		Cat.logEvent("/order/modifyByRoomType", CommonUtils.toStr(orderId), Event.SUCCESS, jsonObj.toJSONString());
		OrderController.logger.info("OTSMessage::OrderController::modifyOrderByRoomType::ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	/**
	 * 取消订单
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> cancelOrder(HttpServletRequest request) {
		String orderid = request.getParameter("orderid");
		String type = request.getParameter("type");// 用户回退取消订单
		type = StringUtils.isBlank(type) ? "1" : type;

		OrderController.logger.info("OTSMessage::取消订单cancelOrder--start{}", orderid);
		if (StringUtils.isBlank(orderid)) {
			throw MyErrorEnum.errorParm.getMyException("订单号不存在");
		}

		String lockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKey4Pay(orderid), 40);
		if (lockValue == null) {
			OrderController.logger.info("订单：" + orderid + "正在支付中，无法取消.");
			throw MyErrorEnum.customError.getMyException("订单：" + orderid + "正在支付中，无法取消");
		}
		// 订单转换为json
		JSONObject jsonObj = new JSONObject();
		try {
			this.orderService.doCancelOrder(orderid, type, jsonObj);
		} catch (Exception e) {
			OrderController.logger.error("取消订单失败 , orderid = " + orderid, e);
			throw MyErrorEnum.customError.getMyException("取消订单失败");
		} finally {
			OrderController.logger.info("释放分布锁");
			DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4Pay(orderid), lockValue);
		}

		OrderController.logger.info("OTSMessage::取消订单cancelOrder--ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	/**
	 * 对于C端隐藏订单
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> delOrderC(HttpServletRequest request) {
		// 判断订单号是否存在
		String orderid = request.getParameter("orderid");
		OrderController.logger.info("OTSMessage::对C端隐藏订单disable--start{}", orderid);
		if (StringUtils.isBlank(orderid)) {
			throw MyErrorEnum.errorParm.getMyException("订单号不存在");
		}
		Long orderId = null;
		try {
			orderId = Long.parseLong(orderid);
		} catch (NumberFormatException e) {
			throw MyErrorEnum.errorParm.getMyException("订单号非数字");
		}

		// 查询订单
		OtaOrder order = this.orderService.findOtaOrderById(orderId);
		if (order == null) {
			throw MyErrorEnum.errorParm.getMyException("没有找到订单");
		}

		// 隐藏订单
		try {
			this.orderService.delCancelOrder(order);
		} catch (Exception e) {
			throw MyErrorEnum.delOrder.getMyException("删除失败");
		}

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("success", true);
		// 结束
		OrderController.logger.info("OTSMessage::对C端隐藏订单disable--ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	/**
	 * 查询我的订单
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/querylist", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> queryOrder(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::querylist::begin");
		String begintime = request.getParameter("startdateday");// yyyyMMdd
		String endtime = request.getParameter("enddateday");// yyyyMMdd
		String isscore = request.getParameter("isscore");// isscore
		String OrderId = request.getParameter("orderid");// 非必填
		String page = request.getParameter("page");
		String limitTemp = request.getParameter("limit");
		String hotelIdTemp = request.getParameter("hotelid");
		String orderstatus = request.getParameter("ordertype");
		String statetype = request.getParameter("statetype");// 

		OrderController.logger.info("OrderController::queryOrder::提取传递的参数::" + this.orderUtil.getRequestParamStrings(request).toString());

		String begin = null;
		String end = null;
		Long hotelId = null;
		Integer start = 0;
		Integer limit = 10;
		try {
			if (StringUtils.isNotBlank(begintime)) {
				begin = DateUtils.getDatetime(DateUtils.getDateFromString(begintime));
			}
			if (StringUtils.isNotBlank(endtime)) {
				end = DateUtils.getDatetime(DateUtils.getDateFromString(endtime));
			}
		} catch (Exception e) {
			throw MyErrorEnum.errorParm.getMyException("日期格式不正确,yyyyMMdd");
		}
		if (StringUtils.isNotBlank(hotelIdTemp)) {
			try {
				hotelId = Long.parseLong(hotelIdTemp);
			} catch (NumberFormatException e) {
				throw MyErrorEnum.errorParm.getMyException("hotelid非数字");
			}
		}
		if (StringUtils.isNotBlank(limitTemp) && StringUtils.isNotBlank(page)) {
			limit = Integer.parseInt(limitTemp);
			start = (Integer.parseInt(page) - 1) * limit;
		}
		// 按类型查订单【全部、进行中、已完成】
		if (StringUtils.isNotBlank(statetype) && statetypeMap.containsKey(statetype)) {
			orderstatus = (String) statetypeMap.get(statetype);
		}
		List<OtaOrderStatusEnum> statusList = new ArrayList<OtaOrderStatusEnum>();
		if (!StringUtils.isBlank(orderstatus)) {
			String[] oss = orderstatus.split(",");
			for (String os : oss) {
				if (!StringUtils.isBlank(os) && !StringUtils.isNumeric(os)) {
					throw MyErrorEnum.errorParm.getMyException(os + ":订单状态不为数字");
				} else {
					statusList.add(OtaOrderStatusEnum.getByID(Integer.parseInt(os)));
				}
			}
		}

		PageObject<OtaOrder> pageObject = new PageObject<>(null, 0l);
		if (!StringUtils.isBlank(OrderId)) {
			OtaOrder order = this.orderService.findOtaOrderById(Long.parseLong(OrderId));
			if (order == null) {
				throw MyErrorEnum.findOrder.getMyException("没有找到订单");
			}
			pageObject = new PageObject<>(Lists.newArrayList(order), 1l);
		} else {
			// 不查询 已经取消的订单 (513,511)
			List<OtaOrderStatusEnum> notstatusList = new ArrayList<OtaOrderStatusEnum>();
			notstatusList.add(OtaOrderStatusEnum.CancelBySystem);
			// notstatusList.add(OtaOrderStatusEnum.CancelByU_NoRefund);
			pageObject = this.orderService.findMyOtaOrder(hotelId, statusList, begin, end, start, limit, isscore, notstatusList);
		}
		boolean showRoom = false;// 显示客单
		boolean showInUser = true;// 显示入住人----显示客单时才能显示入住人
		// 订单转换为json. 只显示订单, 不显示客单/入住人
		JSONArray orders = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		for (OtaOrder order : pageObject.getList()) {
			JSONObject jsonObj1 = new JSONObject();
			PPay ppay = this.payService.findPayByOrderId(order.getId());
			List<OtaRoomPrice> otaRoomPrices = this.priceService.findOtaRoomPriceByOrder(order);
			order.put("otaRoomPrices", otaRoomPrices);
			order.put("act", "query");
			//封装order json信息
			this.orderUtil.getOrderToJson(jsonObj1, ppay, order, showRoom, showInUser);
			orders.add(jsonObj1);
		}
		jsonObj.put("success", true);
		jsonObj.put("order", orders);
		jsonObj.put("count", pageObject.getCount());
		OrderController.logger.info("OTSMessage::OrderController::querylist::end::" + pageObject.getCount() + "::jsonObj::OK");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	/**
	 * 酒店议价接口
	 * 
	 * @return
	 */
	@RequestMapping("/changeprice")
	public ResponseEntity<Map<String, Object>> changeOtaYiJiaPrice(String otaorderid, String changeprice) {
		OrderController.logger.info("changeOtaYiJiaPrice(otaorderid:{}, changeprice:{})...", otaorderid, changeprice);
		if (Strings.isNullOrEmpty(otaorderid)) {
			throw MyErrorEnum.customError.getMyException("订单id不允许为空.");
		}
		if (Strings.isNullOrEmpty(changeprice)) {
			throw MyErrorEnum.customError.getMyException("修改价格不允许为空.");
		}
		OtaOrder otaOrder = this.orderService.findOtaOrderById(Long.parseLong(otaorderid));
		if ((otaOrder.getSpreadUser() == null) && (otaOrder.getSpreadUser().longValue() == 0l)) {
			OrderController.logger.info("非切客订单不允许议价. otaorderid:{}", otaOrder.getId());
			throw MyErrorEnum.customError.getMyException("非切客订单不允许议价.");
		}

		if (otaOrder.getOrderStatus() != 120) {
			OrderController.logger.info("议价时,订单状态(orderstatus)为:{}", otaOrder.getOrderStatus());
			throw MyErrorEnum.customError.getMyException("此订单状态不允许议价.");
		}

		Map<String, Object> rtnMap = this.orderService.doChageOtaYiJiaPrice(changeprice, otaOrder);

		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 提取订单bean
	 * 
	 * @param request
	 * @return
	 */
	private OtaOrder extractOrderBean(HttpServletRequest request, boolean createByRoomType) {
		String hotelId = request.getParameter("hotelid");
		String roomTypeId = request.getParameter("roomtypeid");
		String priceType = request.getParameter("pricetype");
		String startdateday = request.getParameter("startdateday");
		String enddateday = request.getParameter("enddateday");
		String roomId = request.getParameter("roomid");
		String orderType = request.getParameter("ordertype");// 1:预付, 2:到店支付
		String hideOrder = request.getParameter("hideorder");
		String breakfastNum = request.getParameter("breakfastnum");// 早餐数
		String contacts = request.getParameter("contacts");
		String contactsPhone = request.getParameter("contactsphone");
		String contactsEmail = request.getParameter("contactsemail");
		String contactsWeixin = request.getParameter("contactsweixin");
		String note = request.getParameter("note");
		String orderMethod = request.getParameter("ordermethod");// 订单方式，默认：3
																	// app提交
		String promotionNo = request.getParameter("promotion");// 促销代码
		String spreadUser = request.getParameter("spreaduser");
		String couponNo = request.getParameter("couponno");// 优惠券代码
		String activeid = request.getParameter("activeid");
		String quickUserId = request.getParameter("quickuserid");// 常住人主键ID，非必填，可多个，多个使用过
																	// 英文逗号分隔
		String checkInUser = request.getParameter("checkinuser");// 非必填，除去常住人之外的入住人信息，格式为json

		// 新添加坐标字段
		String userLongitude = request.getParameter("userlongitude");
		String userLatitude = request.getParameter("userlatitude");

		String  showBlackType =  request.getParameter("showblacktype");// 非必填，除去常住人之外的入住人信息，格式为json


		/*************** 移动设备信息 ************/
		// 系统号
		String sysno = request.getParameter("sysno");
		// 用户注册应用信息
		String uuid = request.getParameter("uuid");
		if (StringUtils.isBlank(uuid)) {
			uuid = request.getParameter("hardwarecode");
		}
		// 手机唯一识别码imei
		String deviceimei = request.getParameter("deviceimei");
		// sim卡串号
		String simsn = request.getParameter("simsn");
		// wifi的mac地址
		String wifimacaddr = request.getParameter("wifimacaddr");
		// 蓝牙的mac地址
		String blmacaddr = request.getParameter("blmacaddr");
		/************* 移动设备信息 ***************/
		StringBuilder notNulls = new StringBuilder("hotelid,roomtypeid,startdateday,enddateday,pricetype,ordertype"); 
		if (!createByRoomType) {
			notNulls.append(",roomid");
		}
		if (this.orderUtil.checkNotNulls(request, notNulls.toString().split(","))) {
			throw MyErrorEnum.errorParm.getMyException("必填项为空");
		}
		if (createByRoomType) {
			// 根据房型查一个房间
			Room room = roomstateService.findVCHotelRoom(Long.parseLong(hotelId), Long.parseLong(roomTypeId), startdateday, enddateday);
			if (room == null) {
				throw MyErrorEnum.customError.getMyException("很抱歉，此房型没有房间可以预定了");
			}
			roomId = room.getRoomid().toString();
		}
		StringBuffer str = this.orderUtil.getRequestParamStrings(request);
		OrderController.logger.info("开始创建订单::提取传递的参数::" + str.toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date checkBegintime = null;
		Date checkEndtime = null;
		try {
			if ((startdateday != null) && (enddateday != null)) {
				checkBegintime = sdf.parse(startdateday.substring(0, 8));
				checkEndtime = sdf.parse(enddateday.substring(0, 8));
				// TODO 
				if(DateUtils.addHours(DateUtils.getDateFromString(startdateday.substring(0, 8)), 24).compareTo(DateUtils.addHours(new Date(), -6)) < 0){ 
					// 大于6点并且开始时间比当前日期小的开始时间为当前日期
					checkBegintime = DateUtils.addHours(DateUtils.getDateFromString(startdateday.substring(0, 8)), 24);
					OrderController.logger.info("开始创建订单::extractOrderBean::传的开始时间为昨天::{},{}", startdateday.substring(0, 8), DateUtils.getStringFromDate(checkBegintime, DateUtils.FORMATSHORTDATETIME));
					// 如果开始时间不小于结束时间，结束时间＋1
					if(!checkBegintime.before(checkEndtime)){
						checkEndtime = DateUtils.addHours(DateUtils.getDateFromString(enddateday.substring(0, 8)), 24);
					}
				}
			}
		} catch (ParseException e) {
			throw MyErrorEnum.errorParm.getMyException("时间格式错误---startdateday:" + startdateday + " enddateday: " + enddateday);
		}
		OtaOrder order = new OtaOrder();
		try {
			if (StringUtils.isNotBlank(hotelId)) {
				order.setHotelId(Long.parseLong(hotelId));
			}
			if (StringUtils.isNotBlank(spreadUser)) {
				order.setSpreadUser(Long.parseLong(spreadUser));
			}
			if (StringUtils.isNotBlank(priceType)) {
				order.setPriceType(Integer.parseInt(priceType));
			}
			if (StringUtils.isNotBlank(orderMethod)) {
				order.set("ordermethod", Integer.parseInt(orderMethod));
			} else {
				order.set("ordermethod", OrderMethodEnum.WECHAT.getId());
			}
			if (StringUtils.isNotBlank(breakfastNum)) {
				order.set("breakfastnum", Integer.parseInt(breakfastNum));
			} else {
				order.set("breakfastnum", 0);
			}
			if (StringUtils.isNotBlank(orderType)) {
				order.set("ordertype", Integer.parseInt(orderType));
			} else {
				order.set("ordertype", OrderTypeEnum.YF.getId());
			}
			if (StringUtils.isNotBlank(activeid)) {
				order.setActiveid(Long.parseLong(activeid));
			}
			if (StringUtils.isNotBlank(userLongitude)) {
				order.set("userlongitude", userLongitude);
			}
			if (StringUtils.isNotBlank(userLatitude)) {
				order.set("userlatitude", userLatitude);
			}
			/*************** 移动设备信息 ************/
			OtaOrderMac otaOrderMac = new OtaOrderMac();
			// 系统号
			if (StringUtils.isNotBlank(sysno)) {
				otaOrderMac.setSysno(sysno);
			}
			// 用户注册应用信息
			if (StringUtils.isNotBlank(uuid)) {
				otaOrderMac.setUuid(uuid);
			}
			// 手机唯一识别码imei
			if (StringUtils.isNotBlank(deviceimei)) {
				otaOrderMac.setDeviceimei(DESUtils.decryptDES(deviceimei));
			}
			// sim卡串号
			if (StringUtils.isNotBlank(simsn)) {
				otaOrderMac.setSimsn(DESUtils.decryptDES(simsn));
			}
			// wifi的mac地址
			if (StringUtils.isNotBlank(wifimacaddr)) {
				otaOrderMac.setWifimacaddr(DESUtils.decryptDES(wifimacaddr));
			}
			// 蓝牙的mac地址
			if (StringUtils.isNotBlank(blmacaddr)) {
				otaOrderMac.setBlmacaddr(DESUtils.decryptDES(blmacaddr));
			}

			order.setOtaOrderMac(otaOrderMac);
			/*************** 移动设备信息 ************/
		} catch (NumberFormatException e1) {
			throw MyErrorEnum.errorParm.getMyException("数字");
		}
		order.setBeginTime(checkBegintime);
		order.setEndTime(checkEndtime);
		if (StringUtils.isNotBlank(hideOrder)) {
			order.setHiddenOrder(hideOrder.toUpperCase());
		}
		// 促销代码 和是否使用优惠
		if (StringUtils.isNotBlank(promotionNo)) {
			order.put("promotionno", promotionNo);
			order.set("promotion", "T");
		}
		if (StringUtils.isNotBlank(couponNo)) {
			order.put("couponno", couponNo);
			order.set("coupon", "T");
		}

		if (StringUtils.isNotBlank(showBlackType)) {
			order.setShowBlackType(showBlackType);
		}
		String token = request.getParameter("token");
		order.setToken(token);
		UToken uToken = MyTokenUtils.getToken(token);
		if (uToken!=null && uToken.getOstype() != null) {
			order.set("ostype", MyTokenUtils.getToken(token).getOstype());

		} else {
			order.set("ostype", OSTypeEnum.OTHER.getId());
		}

		order.setMid(uToken.getMid());

		List<OtaRoomOrder> roomOrderList = new ArrayList<>();
		{// Begin 客单信息
			OtaRoomOrder roomOrder = new OtaRoomOrder();
			roomOrder.set("hotelid", order.getHotelId());
			roomOrder.set("roomtypeid", Long.parseLong(roomTypeId));
			if (StringUtils.isNotBlank(roomTypeId) && (this.roomTypeService.getTRoomType(Long.parseLong(roomTypeId)) == null)) {
				OrderController.logger.info("OrderController::createOrder::extractOrderBean::哎呀roomTypeId无效！::{}", roomTypeId);
				throw MyErrorEnum.errorParm.getMyException("哎呀roomTypeId无效！");
			}
			// 时租 还是日租
			roomOrder.set("pricetype", order.getPriceType());
			roomOrder.set("begintime", checkBegintime);
			roomOrder.set("endtime", checkEndtime);
			roomOrder.set("roomid", roomId);
			// 预付 到付 担保
			roomOrder.set("ordertype", order.getOrderType());
			roomOrder.set("breakfastNum", order.getBreakfastNum());
			// 联系人信息
			roomOrder.set("contacts", StringUtils.isNotBlank(contacts) ? contacts : "");
			roomOrder.set("contactsphone", StringUtils.isNotBlank(contactsPhone) ? contactsPhone : "");
			roomOrder.set("contactsemail", StringUtils.isNotBlank(contactsEmail) ? contactsEmail : "");
			roomOrder.set("contactsweixin", StringUtils.isNotBlank(contactsWeixin) ? contactsWeixin : "");
			roomOrder.set("note", StringUtils.isNotBlank(note) ? note : "");
			roomOrder.set("ordermethod", order.getOrderMethod());
			List<OtaCheckInUser> inUsers = this.orderService.getInUsersByJson(null,checkInUser);
			roomOrder.put("UserList", inUsers);
			roomOrderList.add(roomOrder);
			order.put("roomorderlist", roomOrderList);
		}// End 客单信息
		return order;
	}
	
	/**
	 * 订单数量统计
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/selectcount", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> selectCountByOrderStatus(HttpServletRequest request) {
		JSONObject result = null;
		JSONObject jsonObj = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			// 接受token参数
			String token = request.getParameter("token");
			if (StringUtils.isBlank(token)) {
				throw MyErrorEnum.errorParm.getMyException("查询订单数量参数token不能为空！");
			}
			String status = request.getParameter("status");
			if (StringUtils.isBlank(status)) {
				throw MyErrorEnum.errorParm.getMyException("查询订单参数不能为空！");
			}
			JSONArray jsonArray = JSON.parseArray(status);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String sqnum = jsonObject.getString("sqnum");
				if (StringUtils.isBlank(sqnum)) {
					throw MyErrorEnum.errorParm.getMyException("sqnum参数不能为空！");
				}

				String orderstatus = jsonObject.getString("orderstatus");
				String statetype = jsonObject.getString("statetype");
				if (StringUtils.isBlank(statetype) && StringUtils.isBlank(orderstatus)) {
					throw MyErrorEnum.errorParm.getMyException("参数:订单类型和取值范围不能同时为空！");
				}
				
				if (StringUtils.isNotBlank(statetype) && statetypeMap.containsKey(statetype)) {
					orderstatus = (String) statetypeMap.get(statetype);
				}
				if (StringUtils.isBlank(orderstatus)) {
					throw MyErrorEnum.errorParm.getMyException("orderstatus参数不能为空！");
				}
				String[] orderstatus2 = orderstatus.split(",");
				List<String> orderstatus3 = Arrays.asList(orderstatus2);
				result = this.orderService.selectCountByOrderStatus(sqnum, orderstatus3, token);

				array.add(result);
			}
			jsonObj.put("success", true);
			jsonObj.put("statuscount", array);
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			jsonObj.put("success", false);
			jsonObj.put("errcode", "-1");
			jsonObj.put("errmsg", "根据订单状态 查询订单数量失败！");
		}

		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	/**
	 * CRS修改订单房间信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/crsmodify", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> crsModifyOrder(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::crsModifyOrder::begin");
		// 提取orderbean
		if (this.orderUtil.checkNotNulls(request, new String[] { "orderid", "token" })) {
			throw MyErrorEnum.errorParm.getMyException("必填项为空");
		}

		// redis锁
		String orderId = request.getParameter("orderid");
		if (StringUtils.isBlank(orderId)) {
			throw MyErrorEnum.errorParm.getMyException("订单号不存在");
		}
		String lockValue = DistributedLockUtil.tryLock("orderTasksLock_" + orderId, 40);
		if (lockValue == null) {
			OrderController.logger.info("订单：" + orderId + "正在执行订单任务，无法修改");
			throw MyErrorEnum.customError.getMyException("订单：" + orderId + "正在执行订单任务，无法修改");
		}

		JSONObject jsonObj = new JSONObject();
		try {
			this.orderService.doUpdateOrder(request, jsonObj);
		} catch (Exception e) {
			throw e;
		} finally{
			// 释放 redis锁
			OrderController.logger.info("释放分布锁, orderId= " + orderId);
			DistributedLockUtil.releaseLock("orderTasksLock_" + orderId, lockValue);
		}

		OrderController.logger.info("OTSMessage::OrderController::crsModifyOrder::ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}

	/**
	 * @param hotelId
	 *            酒店客单查询，通知pms调用selectsyncorder
	 */
	@RequestMapping(value = "/sendSynRoomOrderMsg", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sendSynRoomOrderMsg(String hotelId) {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		OrderController.logger.info("OrderController::sendSynRoomOrderMsg::params{}{}  begin", hotelId, uuid);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(hotelId)) {
				throw MyErrorEnum.errorParm.getMyException("参数hotelId为空!");
			}
			THotel hotel = this.hotelService.readonlyTHotel(Long.parseLong(hotelId));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("hotelid", hotel.getStr("pms"));
			jsonObject.put("uuid", uuid);
			jsonObject.put("function", "selectsyncorder");
			jsonObject.put("timestamp", DateUtils.formatDateTime(Calendar.getInstance().getTime()));
			String theresult = null;
			Transaction t = Cat.newTransaction("PmsHttpsPost", UrlUtils.getUrl("newpms.url") + "/selectsyncorder");
			try {
				OrderController.logger.info("OrderController::sendSynRoomOrderMsg::参数：{}", jsonObject.toJSONString());
				theresult = PayTools.dopostjson(UrlUtils.getUrl("newpms.url") + "/selectsyncorder", jsonObject.toJSONString());
				OrderController.logger.info("OrderController::sendSynRoomOrderMsg::返回：{}", theresult);
				Cat.logEvent("Pms/selectsyncorder", hotelId, Event.SUCCESS, jsonObject.toJSONString());
				t.setStatus(Transaction.SUCCESS);
			} catch (Exception e) {
				t.setStatus(e);
				this.logger.error("PMS/selectsyncorder error.", e);
				throw MyErrorEnum.errorParm.getMyException(e.getMessage());
			}finally {
				t.complete();
			}

			JSONObject returnObject = JSON.parseObject(theresult);
			if (returnObject.getBooleanValue("success")) {
				result.put(ServiceOutput.STR_MSG_SUCCESS, true);
			} else {
				OrderController.logger.info("OrderController::sendSynRoomOrderMsg::error{}", returnObject.getString("errormsg"));
				result.put(ServiceOutput.STR_MSG_SUCCESS, false);
				result.put(ServiceOutput.STR_MSG_ERRCODE, returnObject.getString("errorcode"));
				result.put(ServiceOutput.STR_MSG_ERRMSG, returnObject.getString("errormsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			OrderController.logger.info("OrderController::sendSynRoomOrderMsg  end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	/**
	 * @param orderid
	 *            酒店订单重新计算规则
	 */
	@RequestMapping(value = "/reModifyHotelRule", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> reModifyHotelRule(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::reModifyHotelRule::begin");

		JSONObject jsonObj = new JSONObject();
		this.orderService.modifyHotelRule(request, jsonObj);

		OrderController.logger.info("OTSMessage::OrderController::reModifyHotelRule::ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	

	/**
	 * @param orderid
	 * 酒店订单重新计算优惠券规则
	 */
	@RequestMapping(value = "/reModifyHotelPromotion", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> reModifyHotelPromotion(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::reModifyHotelRule::begin");

		JSONObject jsonObj = new JSONObject();
		this.orderService.modifyHotelPromotion(request, jsonObj);

		OrderController.logger.info("OTSMessage::OrderController::reModifyHotelRule::ok");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	
	/**
	 * @param request
	 * c端修改入住人
	 */
	@RequestMapping(value = "/modifycheckinuser", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> modifyCheckinuser(HttpServletRequest request) {
		OrderController.logger.info("OTSMessage::OrderController::modifyCheckinuser::begin");
		JSONObject jsonObj = new JSONObject();
		try {
			this.orderService.modifyCheckinuser(request, jsonObj);
			jsonObj.put("success", true);
		} catch (Exception e) {
			jsonObj.put("success", false);
			jsonObj.put("errcode", "-1");
			jsonObj.put("errmsg", "修改订单入住人失败！");
		}
		OrderController.logger.info("OTSMessage::OrderController::modifyCheckinuser::end");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	/**
	 * 订单状态跟踪
	 * @param request
	 */
	@RequestMapping(value = "/selectorderstatus", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> selectOrderStatus(HttpServletRequest request) {
		OrderController.logger.info("OrderController::selectOrderStatus::begin");
		JSONObject jsonObj = new JSONObject();
		try {
			this.orderService.selectOrderStatus(request, jsonObj);
			jsonObj.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("异常了：{}", e.getLocalizedMessage());
			jsonObj.put("errcode", "-1");
			jsonObj.put("errmsg", "查询失败！");
			jsonObj.put("success", false);
		}
		OrderController.logger.info("OrderController::selectOrderStatus::end");
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
}
