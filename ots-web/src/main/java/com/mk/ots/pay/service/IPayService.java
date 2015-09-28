package com.mk.ots.pay.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.mk.ots.common.enums.ManualLuzhuRstEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PPayInfoTypeEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.common.enums.RuleEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.pay.model.PMSCancelParam;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.pay.model.PPayStatusErrorOrder;
import com.mk.ots.pay.model.PPayTask;
import com.mk.ots.pay.model.PmsError;

public interface IPayService {

	/**
	 * 取消订单,去银行退款，流水保存，更改 otaOrder 状态，pay 状态，退款情况 等操作
	 * 
	 * @param order
	 *            OtaOrder
	 * @return
	 */
	public PPay cancelPay(OtaOrder order);

	/**
	 * 根据订单获取最后一个（即有效的）支付信息
	 * 
	 * @param orderId
	 * @return
	 */
	public PPay findPayByOrderId(Long orderId);

	/**
	 * 查询订单号下是否有pms失败日志
	 * 
	 * @param orderId
	 * @return
	 */
	public PmsError findPmsErrorByOrderId(Long orderId);

	public PPay findPayById(Long payid);

	/**
	 * 计算有效订单数量
	 * 
	 * @param id
	 * @param confirm
	 * @return
	 */
	public Long countOrderByOrderSts(Long orderId, OtaOrderStatusEnum sts);

//	/**
//	 * 获取优惠券数量
//	 * 
//	 * @param mid
//	 * @param id
//	 * @return
//	 */
//	public Long countTicketsByMember(Long mid, Long promoId);

//	/**
//	 * 获取订单对应的优惠券数量
//	 * 
//	 * @param promoId
//	 * @param orderId
//	 * @return
//	 */
//	public Long countOrderTicket(Long promoId, Long orderId);


	/**
	 * 支付完成后 AddPay
	 * 
	 * @param order
	 *            otaOrder
	 */
	public boolean pmsAddpayOk(OtaOrder order, PayStatusEnum payStatus, PPayInfoTypeEnum type);

	/**
	 * 判断订单是否是等待状态
	 * 
	 * @param orderid
	 */
	public Boolean payIsWaitPay(String orderid);

	/**
	 * 支付取消订单后，回调时把数据修改
	 * 
	 * @param
	 */
	//
	public boolean alipayCancelRes(String str);

	public boolean cancelPmsPay(Long orderId, PPayInfoTypeEnum type);

	/** 创建支付信息，如果需要支付的价格为0，则直接调用此方法 */
	public OtaOrder createPayPrice0(OtaOrder order);

	public Map<String, Object> createPay(HttpServletRequest request, long longorderId, String promotionno, String couponno, String paytype, String onlinepaytype);

	public Boolean payresponse(Long longorderid, String payid, String price, PPayInfoOtherTypeEnum payinfotype);

	/**
	 * 手动下发乐住币
	 * @param orderid
	 *            订单编码
	 * @param lezhu
	 *            乐住币金额
	 * @param operatorid
	 * @return
	 */
	public ManualLuzhuRstEnum pmspay(Long orderid, BigDecimal lezhu,  String operateName);

	public String selectCustomerpay(Long orderid);
	
	/**
	 * @param orderId
	 *  异常情况下支付取消——cancelpaybyerror
	 */
	public String  cancelpaybyerror(Long orderId);
	
	public PMSCancelParam getPMSCancelParam(OtaOrder order, PPayInfoTypeEnum type);
	
	public boolean reSendLeZhu(OtaOrder order, long payid,Long pmsSendId, BigDecimal price, String memberName);

	public boolean superRefund(Long orderid);

	public ManualLuzhuRstEnum addpay(Long orderid, BigDecimal price, String operateName);

	public void pushMsg(Long orderId, String paytype);

	/**
	 * 查询订单付款情况
	 */
	public String serviceFindPay(String orderid);

	/**
	 * 【HMS】查询订单付款情况
	 */
	public String hmsFindPay(Long orderid);

	
	/**
	 * 给p_orderlog 加切客收益
	 */
	public boolean addQiekeIncome(OtaOrder order, RuleEnum rule);

	/**
	 * 入住时间减创建时间大于15分钟，取消优惠券补贴
	 */
	public boolean checkinCancelCoupon(OtaOrder order);

	/**
	 * 离店时间（和入住比）小于4小时订单 调用，，p_orderlog 补贴取消
	 */
	public boolean leaveTimeLess(Long orderid );
	
	public boolean insertPayCallbackLog(Long orderId, String callbackFrom, String payResult, String payNo, BigDecimal price, String errorCode, String errorMsg);

	public Boolean sendMsg(OtaOrder order);
	
	public List<PPayTask> selectInitTask(PayTaskTypeEnum taskType, PayTaskStatusEnum status);
	
	public Long insertPayTask(PPayTask task);
	
	public int updatePayTask(PayTaskStatusEnum status, List<PPayTask> tasks);
	
	public boolean modifyPayStatus(Long orderid,Integer payStatus,String opertorName);
	
	/**
	 *根据orderid得到用户实际掏的钱
	 * @param orderid
	 */
	public BigDecimal payMoney(Long  orderid);
	public void checkPayStatusWhenIn(OtaOrder order);
	
	public Long insertPayStatusError(PPayStatusErrorOrder order);
	
	public boolean deletePayStatusError(Long orderId);
	
	//批量退款
	public String refundBatchPay(String orderid);
}
