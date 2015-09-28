package com.mk.pms.order.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.enums.OtaOrderFlagEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PmsRoomOrderStatusEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderServiceImpl;
import com.mk.pms.bean.PmsCheckinUser;

@Service
public class PmsOrderUtilService {
	private static final Logger logger = LoggerFactory.getLogger(PmsOrderUtilService.class);

	private String pmsStates = "RE,RX,IN,IX,PM,OK";
	// PMS状态映射OTS订单的状态
	private static Map<String, Integer> statusMap = new HashMap<>();
	static {
		statusMap.put("RX", OtaOrderStatusEnum.CancelByPMS.getId());
		statusMap.put("IN", OtaOrderStatusEnum.CheckIn.getId());
		statusMap.put("IX", OtaOrderStatusEnum.CancelByPMS.getId());
		statusMap.put("PM", OtaOrderStatusEnum.Account.getId());
		statusMap.put("OK", OtaOrderStatusEnum.CheckOut.getId());
	}
	// PMS订单状态变化的范围控制
	private static Map<String, String> orderstatusMap = new HashMap<>();
	static {
		orderstatusMap.put("140", "RE");
		orderstatusMap.put("180", "IN");
		orderstatusMap.put("190", "PM");
		orderstatusMap.put("200", "OK");
		orderstatusMap.put("514", "RX");
		orderstatusMap.put("520", "NS");
	}
	private static Map<String, String> statusStruts = new HashMap<>();
	static {
		statusStruts.put("RE", "RE,RX,IN");
		statusStruts.put("RX", "RX");
		statusStruts.put("IN", "IN,IX,PM,OK");
		statusStruts.put("IX", "IX");
		statusStruts.put("PM", "PM");
		statusStruts.put("OK", "OK");
	}
	private static Map<String, String> hzStruts_1 = new HashMap<>();
	static {
		hzStruts_1.put("RE", "预定,预定取消,入住");
		hzStruts_1.put("RX", "预定取消");
		hzStruts_1.put("IN", "入住,入住取消,挂退,离店");
		hzStruts_1.put("IX", "入住取消");
		hzStruts_1.put("PM", "挂退");
		hzStruts_1.put("OK", "离店");
	}
	private static Map<String, String> hzStruts_2 = new HashMap<>();
	static {
		hzStruts_2.put("RE", "预定");
		hzStruts_2.put("RX", "预定取消");
		hzStruts_2.put("IN", "入住");
		hzStruts_2.put("IX", "入住取消");
		hzStruts_2.put("PM", "挂退");
		hzStruts_2.put("OK", "离店");
	}
	@Autowired
	OrderServiceImpl orderService;
	
    @Autowired
    private OrderBusinessLogService orderBusinessLogService;

	/**
	 * 
	 * @param orderId
	 * @param status
	 * @param dateTime
	 */
	public boolean changeRoomOrderStatus(Long orderId, String status, String dateTime,String operator, String note) {
		PmsOrderUtilService.logger.info("changePmsRoomOrderStatus::orderId:{},status:{},dateTime:{}", orderId, status, dateTime);
		OtaOrder order = orderService.findOtaOrderById(orderId);
		if (order == null) {
			throw MyErrorEnum.customError.getMyException("订单不存在");
		}
		if(!StringUtils.isNotBlank(dateTime)){
			throw MyErrorEnum.customError.getMyException("请填写时间");
		}
		Date date = DateUtils.getDateFromString(dateTime);
		if (date == null) {
			throw MyErrorEnum.customError.getMyException("时间格式错误");
		}
		if (!statusMap.containsKey(status) && !orderstatusMap.containsKey(status)  ) {
			throw MyErrorEnum.customError.getMyException("状态录入错误");
		}
		if(orderstatusMap.containsKey(status)){
			status = orderstatusMap.get(status);
			if("NS".equals(status)){
				if(order.getOrderStatus() == 140){
					order.setUpdateTime(new Date());
					order.setOrderStatus(520);
					order.saveOrUpdate();
			        this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDER.getId(), "", operator + " 更改订单状态成功：" + order.getOrderStatus() + ",原因是："+note, "");
					return true;
				} else {
					throw MyErrorEnum.customError.getMyException("错误状态，当前订单状态不是140!");
				}
			}
		}
		
		List<OtaRoomOrder> roomOrders = order.getRoomOrderList();
		for (OtaRoomOrder roomOrder : roomOrders) {
			PmsRoomOrder pmsRoomOrder = orderService.findPmsRoomOrderById(roomOrder.getPmsRoomOrderNo(), roomOrder.getHotelId());
			// 
			if (pmsRoomOrder == null) {
				throw MyErrorEnum.customError.getMyException("PMS客单不存在");
			}
			if (statusStruts.get(pmsRoomOrder.getStr("Status")).indexOf(status) < 0) {
				throw MyErrorEnum.customError.getMyException("PMS客单是" + hzStruts_2.get(pmsRoomOrder.getStr("Status")) + "，只能进行" + hzStruts_1.get(pmsRoomOrder.getStr("Status")) + "状态变化");
			}
			
			PmsOrderUtilService.logger.info("changePmsRoomOrderStatus::pmsRoomOrder:{}", pmsRoomOrder);
			pmsRoomOrder.set("Status", status);	
			if (PmsRoomOrderStatusEnum.RX.getId().equals(status)) {
				
			} else if (PmsRoomOrderStatusEnum.IN.getId().equals(status)) {
				pmsRoomOrder.set("checkintime", date);
			} else if (PmsRoomOrderStatusEnum.IX.getId().equals(status)) {
				pmsRoomOrder.set("checkouttime", date);
			} else if (PmsRoomOrderStatusEnum.PM.getId().equals(status)) {
				pmsRoomOrder.set("checkouttime", date);
			} else if (PmsRoomOrderStatusEnum.OK.getId().equals(status)) {
				pmsRoomOrder.set("checkouttime", date);
			}
					
			pmsRoomOrder.saveOrUpdate();
			PmsOrderUtilService.logger.info("pmsRoomOrder保存:{}", pmsRoomOrder);

			if (StringUtils.isNotBlank(pmsRoomOrder.getStr("Status")) && StringUtils.isNotBlank(status)
					&& pmsStates.indexOf(status) < pmsStates.indexOf(pmsRoomOrder.getStr("Status"))) {
				PmsOrderUtilService.logger.info("PmsOrderServiceImpl::saveCustomerNo::错误PMS状态，当前客单状态已经是{},不能再{}", order.getStr("Status"), status);
				throw MyErrorEnum.customError.getMyException("错误PMS状态，当前客单状态已经是" + hzStruts_2.get(order.getStr("Status")) + ",不能再" + hzStruts_2.get(status));
			}

			PmsCheckinUser checkinUser = orderService.findPmsUserIncheckSelect(pmsRoomOrder.getLong("Hotelid"), pmsRoomOrder.getStr("PmsRoomOrderNo"));
			orderService.changeOrderStatusByPms(orderId, pmsRoomOrder, checkinUser != null ? String.valueOf(checkinUser.get("freqtrv")) : "0");

			if (statusMap.containsKey(status)) {
				roomOrder.set("orderstatus", statusMap.get(status));
				roomOrder.saveOrUpdate();
				order.setUpdateTime(new Date());
				order.set("orderstatus", statusMap.get(status));
			}
		}
		order.saveOrUpdate();
        this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDER.getId(), "", operator + " 更改订单状态成功：" + order.getOrderStatus() + ",原因是："+note, "");
		return true;
	}
	
	/**
	 * 
	 * @param orderId
	 * @param status
	 * @param dateTime
	 */
	public boolean changePmsRoomOrderStatus(String pmsroomorderno, Long hotelId, String status, String dateTime,String operator, String note) {
		PmsOrderUtilService.logger.info("changePmsRoomOrderStatus::pmsroomorderno:{},hotelId:{},status:{},dateTime:{}", pmsroomorderno,hotelId, status, dateTime);
		PmsRoomOrder pmsRoomOrder = orderService.findPmsRoomOrderById(pmsroomorderno, hotelId);
		if (pmsRoomOrder == null) {
			throw MyErrorEnum.customError.getMyException("PMS客单不存在");
		}

		if (!statusMap.containsKey(status) && !orderstatusMap.containsKey(status)  ) {
			throw MyErrorEnum.customError.getMyException("状态录入错误");
		}
		if(orderstatusMap.containsKey(status)){
			status = orderstatusMap.get(status);
			if("NS".equals(status)){
				throw MyErrorEnum.customError.getMyException("客单状态输入错误!");
			}
		}
		if (statusStruts.get(pmsRoomOrder.getStr("Status")).indexOf(status) < 0) {
			throw MyErrorEnum.customError.getMyException("PMS客单是" + hzStruts_2.get(pmsRoomOrder.getStr("Status")) + "，只能进行" + hzStruts_1.get(pmsRoomOrder.getStr("Status")) + "状态变化");
		}
		
		PmsOrderUtilService.logger.info("changePmsRoomOrderStatus::pmsRoomOrder:{}", pmsRoomOrder);
		pmsRoomOrder.set("Status", status);	
		
		if(StringUtils.isNotBlank(dateTime)){
			Date date = DateUtils.getDateFromString(dateTime);
			if (date == null) {
				throw MyErrorEnum.customError.getMyException("时间格式错误");
			}
			if (PmsRoomOrderStatusEnum.RX.getId().equals(status)) {
				
			} else if (PmsRoomOrderStatusEnum.IN.getId().equals(status)) {
				pmsRoomOrder.set("checkintime", date);
			} else if (PmsRoomOrderStatusEnum.IX.getId().equals(status)) {
				pmsRoomOrder.set("checkouttime", date);
			} else if (PmsRoomOrderStatusEnum.PM.getId().equals(status)) {
				pmsRoomOrder.set("checkouttime", date);
			} else if (PmsRoomOrderStatusEnum.OK.getId().equals(status)) {
				pmsRoomOrder.set("checkouttime", date);
			}
		}
		pmsRoomOrder.saveOrUpdate();
		PmsOrderUtilService.logger.info("pmsRoomOrder保存:{}", pmsRoomOrder);

		OtaRoomOrder roomOrder = orderService.findRoomOrderByPmsRoomOrderNoAndHotelId(pmsroomorderno, hotelId);
		if (roomOrder != null) {
			roomOrder.set("orderstatus", statusMap.get(status));
			roomOrder.saveOrUpdate();
			PmsOrderUtilService.logger.info("OtaRoomOrder保存:{}", roomOrder);
			
			OtaOrder order = orderService.findOtaOrderById(roomOrder.getLong("OtaOrderId"));
			order.set("orderstatus", statusMap.get(status));
			order.saveOrUpdate();
			PmsOrderUtilService.logger.info("OtaOrder保存:{}", order);
		} else {
			PmsOrderUtilService.logger.info("不存在OtaOrder数据");
		}
		return true;
	}
}
