package com.mk.ots.order.hessian;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jodd.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.annotation.HessianService;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaOrderFlagEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.manager.RedisCacheName;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderServiceImpl;
import com.mk.ots.order.service.OrderUtil;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.pay.service.PayService;
import com.mk.pms.bean.PmsCost;
import com.mk.pms.order.service.PmsCostService;

@Service
@HessianService(value = "/order", implmentInterface = OrderHessianService.class)
public class OrderHessianServiceImpl implements OrderHessianService {
	private static Logger logger = LoggerFactory.getLogger(OrderHessianServiceImpl.class);
	@Autowired
	OrderServiceImpl orderService;
	@Autowired
	PayService payService;
	@Autowired
	OrderUtil orderUtil;
	@Autowired
	PmsCostService pmsCostService;
	@Autowired
	private OrderBusinessLogService orderBusinessLogService;
	/**
	 * 客服人员直接取消订单
	 */
	@Override
	public Map cancel(Map param) {
		logger.info("OrderHessianServiceImpl::cancel::客服取消订单::参数::{}", param);
		// 订单id
		String orderid = String.valueOf(param.get("orderid"));
		String memo = String.valueOf(param.get("memo"));
		// 客服人员名称
		String customerservicename = String.valueOf(param.get("customerservicename"));
		HashMap data = new HashMap();
		String synLockKey = RedisCacheName.IMIKE_OTS_CANCELORDER_KEY + orderid;
		String synLockValue = null;
		try {
			if (StringUtils.isBlank(orderid)) {
				throw MyErrorEnum.errorParm.getMyException("订单号不存在");
			}
			//加redis锁，防止重复请求
			if((synLockValue=DistributedLockUtil.tryLock(synLockKey, 60))==null){
				logger.info("OrderHessianServiceImpl::cancel::{},重复操作", orderid);
				return data;
			}
			Long orderidTemp = null;
			try {
				orderidTemp = Long.parseLong(orderid);
			} catch (NumberFormatException e1) {
				throw MyErrorEnum.errorParm.getMyException("数字");
			}
			OtaOrder order = this.orderService.findOtaOrderById(orderidTemp);
			if (order == null) {
				throw MyErrorEnum.errorParm.getMyException("订单号不存在");
			}
			// 入住的订单不能取消.
			if ((order.getLong("orderstatus") > OtaOrderStatusEnum.CheckInOnline.getId().longValue())
					&& (order.getLong("orderstatus") < OtaOrderStatusEnum.CancelByU_WaitRefund.getId().longValue())) {
				throw MyErrorEnum.delOrderErrorByOrderIn.getMyException();
			}
			// 订单状态120、140并且没有pmscost就可以取消，否则提示异常
			PmsCost cost = pmsCostService.findPmsCost(orderidTemp);
			if (cost != null && order.getLong("orderstatus") < OtaOrderStatusEnum.CheckInOnline.getId().longValue()) {
				throw MyErrorEnum.customError.getMyException("订单已经夜审，不能取消");
			}
			// 不能重复取消订单
			if (order.getLong("orderstatus") >= OtaOrderStatusEnum.CancelByU_WaitRefund.getId().longValue()) {
				throw MyErrorEnum.customError.getMyException("不能重复取消订单");
			}
			//备注
			order.setNote(memo);
			// 这里直接取消订单
			this.orderService.cancelOrder(order);
			orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_CANCELORDER.getId(), "客服编号" + customerservicename, "", "");
			logger.info("OrderHessianServiceImpl::cancel::ok");
			data.put("message", "取消订单成功");
			data.put("success", true);
		} catch (Exception e) {
			logger.info("OrderHessianServiceImpl::cancel::error:{}", e.getLocalizedMessage(), e);
			data.put("success", false);
			data.put("message", e.getLocalizedMessage());
		} finally {
			//消除锁
			if(StringUtil.isNotEmpty(synLockValue)){
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		return data;
	}

	/**
	 * 修改订单的hessian调用接口
	 */
	@Override
	public Map modify(Map param) {
		logger.info("OrderHessianServiceImpl::modify::客服修改订单::参数::{}", param);
		HashMap data = new HashMap();
		// 客服人员名称
		String customerservicename = String.valueOf(param.get("customerservicename"));
		String orderid = String.valueOf(param.get("orderid"));
		if (!StringUtils.isNumeric(orderid)) {
			data.put("message", "订单号不是数字");
		}
		OtaOrder order = orderService.findOtaOrderById(Long.parseLong(orderid));
		if (order == null) {
			data.put("message", "订单不存在");
		}
		if (data.size() == 0) {
			String contacts = String.valueOf(param.get("contacts"));
			String contactsphone = String.valueOf(param.get("contactsphone"));
			String contactsemail = String.valueOf(param.get("contactsemail"));
			String contactsweixin = String.valueOf(param.get("contactsweixin"));
			if (StringUtils.isNotBlank(contacts)) {
				order.setContacts(contacts);
			}
			if (StringUtils.isNotBlank(contactsphone)) {
				order.setContactsPhone(contactsphone);
			}
			if (StringUtils.isNotBlank(contactsemail)) {
				order.setContactsEmail(contactsemail);
			}
			if (StringUtils.isNotBlank(contactsweixin)) {
				order.setContactsWeiXin(contactsweixin);
			}

			order.setUpdateTime(new Date());
			order.saveOrUpdate();
			orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDER.getId(), "客服:"+customerservicename, "", "");
			data.put("success", true);
			data.put("message", "修改订单成功");
		} else {
			data.put("success", false);
		}
		logger.info("OrderHessianServiceImpl::modify::客服修改订单::返回::{}", data);
		return data;
	}

	/**
	 * 客服取消第三方支付后修改订单状态为取消订单
	 */
	@Override
	public Map modifyOrderStatusAfterCancelPay(Map param) {
		logger.info("OrderHessianServiceImpl::modifyOrderStatusAfterCancelPay::客服修改订单状态::{}", param);
		HashMap data = new HashMap();
		// 客服人员名称
		String customerservicename = String.valueOf(param.get("customerservicename"));
		String orderid = String.valueOf(param.get("orderid"));
		if (!StringUtils.isNumeric(orderid)) {
			data.put("message", "订单号不是数字");
		}
		OtaOrder order = orderService.findOtaOrderById(Long.parseLong(orderid));
		if (order == null) {
			data.put("message", "订单不存在");
		}
		if (data.size() == 0) {
			if (order.getOrderStatus() == OtaOrderStatusEnum.CancelByU_WaitRefund.getId()) {
				order.setOrderStatus(OtaOrderStatusEnum.CancelByU_Refunded.getId());
				order.setUpdateTime(new Date());
				order.saveOrUpdate();
				orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDERSTATUSAFTERCANCELPAY.getId(), "客服:"+customerservicename, "", "");
				data.put("success", true);
				data.put("message", "修改订单成功");
			} else {
				data.put("success", false);
				data.put("message", "订单非等待退款状态，操作失败");
			}
		} else {
			data.put("success", false);
		}
		logger.info("OrderHessianServiceImpl::modifyOrderStatusAfterCancelPay::客服修改订单状态:返回:{}", data);
		return data;
	}

	@Override
	public Map cancelOrderSuper(Map param) {
		// 订单id
		String orderid = String.valueOf(param.get("orderid"));
		HashMap data = new HashMap();
		try {
			logger.info("OrderHessianServiceImpl::cancelOrderSuper::param::{}", param);
			if (StringUtils.isBlank(orderid)) {
				throw MyErrorEnum.customError.getMyException("订单号不存在");
			}
			Long orderidTemp = null;
			try {
				orderidTemp = Long.parseLong(orderid);
			} catch (NumberFormatException e1) {
				throw MyErrorEnum.customError.getMyException("订单号非数字");
			}
			OtaOrder order = this.orderService.findOtaOrderById(orderidTemp);
			if (order == null) {
				throw MyErrorEnum.customError.getMyException("订单号不存在");
			}
			if (order.getOrderType() != OrderTypeEnum.YF.getId()) {
				throw MyErrorEnum.customError.getMyException("到付订单无需取消支付");
			}
			PPay pay = payService.findPayByOrderId(orderidTemp);
			if (pay == null ||  order.getPayStatus() != PayStatusEnum.alreadyPay.getId()) {
				throw MyErrorEnum.customError.getMyException("订单未支付，无需退款");
			}
			if (order.getPayStatus() == PayStatusEnum.refundPay.getId()) {
				throw MyErrorEnum.customError.getMyException("订单已退款，不能重复取消");
			}
			if (order.getLong("orderstatus") >= OtaOrderStatusEnum.CancelByU_Refunded.getId().longValue()) {
				throw MyErrorEnum.customError.getMyException("不能重复取消订单");
			}
			// 订单为预付的情况、未退款、未取消已退款；且已经支付
			orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_CANCELORDER.getId(), "客服取消订单", "", "");
			this.orderService.cancelOrderSuper(order);
			logger.info("OrderHessianServiceImpl::cancelOrderSuper::ok");
			data.put("message", "取消订单成功");
			data.put("success", true);
		} catch (Exception e) {
			logger.info("OrderHessianServiceImpl::cancelOrderSuper::error:{}", e.getLocalizedMessage(), e);
			data.put("success", false);
			data.put("message", e.getLocalizedMessage());
		}
		return data;
	}
}
