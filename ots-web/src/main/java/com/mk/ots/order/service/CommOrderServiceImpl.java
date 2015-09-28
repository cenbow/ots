//package com.mk.ots.order.service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.mk.framework.exception.MyErrorEnum;
//import com.mk.ots.annotation.HessianService;
//import com.mk.ots.order.bean.OtaOrder;
//import com.mk.ots.pay.model.PPay;
//import com.mk.ots.pay.service.PayService;
//
//@Service
//@HessianService(value = "/commOrder", implmentInterface = CommOrderService.class)
//public class CommOrderServiceImpl implements CommOrderService {
//
//	@Autowired
//	OrderServiceImpl orderService;
//	@Autowired
//	PayService payService;
//	
//	@Override
//	public Map cancel(Map param) {
//		// 订单id
//		String orderid = String.valueOf(param.get("orderid"));
//		// 客服人员名称
//		String customerservicename = String.valueOf(param.get("customerservicename"));
//		if (StringUtils.isBlank(orderid)) {
//			throw MyErrorEnum.errorParm.getMyException("订单号不存在");
//		}
//		Long orderidTemp = null;
//		try {
//			orderidTemp = Long.parseLong(orderid);
//		} catch (NumberFormatException e1) {
//			throw MyErrorEnum.errorParm.getMyException("数字");
//		}
//		OtaOrder order = this.orderService.findOtaOrderById(orderidTemp);
//		// 这里直接取消订单
//		this.orderService.cancelOrder(order);
//		PPay ppay = this.payService.findPayByOrderId(orderidTemp);
//		boolean showRoom = true;// 显示客单
//		boolean showInUser = true;// 显示入住人----显示客单时才能显示入住人
//		// 订单转换为json
//		HashMap data = new HashMap();
//		// OrderUtil.getOrderToJson(jsonObj, ppay, order, showRoom, showInUser);
//		data.put("success", true);
//		return data;
//	}
//
//	@Override
//	public Map modify(Map param) {
//		HashMap data = new HashMap();
//		// OrderUtil.getOrderToJson(jsonObj, ppay, order, showRoom, showInUser);
//		data.put("success", true);
//		data.put("message", "正在开发。");
//		return data;
//	}
//
//}
