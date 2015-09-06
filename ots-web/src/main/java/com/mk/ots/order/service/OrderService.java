package com.mk.ots.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cn.com.winhoo.pms.webout.service.bean.PmsOtaAddOrder;

import com.mk.ots.common.bean.PageObject;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.member.model.UMember;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.OtaRoomPrice;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.model.BOtaorder;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.ticket.model.TicketInfo;

public interface OrderService {

	public Long saveQiekePromoPrice(Long otaOrderId);

	public List<PmsOtaAddOrder> convertToPms(List<OtaRoomOrder> roomOrders, List<OtaRoomPrice> otaRoomPrices, OtaOrder order);

	public void deleteOrder(OtaOrder order);

	public void deleteRoomOrder(OtaRoomOrder roomOrder);

	public BPromotionPrice getPromotionPriceByOrderId(Long orderId, Long promoId);

	public OtaOrder updateOrderPms(OtaOrder order);

	public void modifyOrderStatusOnPay(OtaOrder order, String paytype);

	public boolean checkQiekeOfflineUsed(OtaOrder otaOrder);

	public OtaOrder findOtaOrderByIdNoCancel(Long otaOrderId);

	public OtaOrder findOtaOrderById(Long otaOrderId);

	public boolean checkQiekeOnlineUsed(OtaOrder otaOrder);

	public boolean checkQiekeWebPromoUsed(Long mid, Long hotelId);

	public boolean checkSameRoomQiekeUsed(OtaOrder otaOrder, String qiekeStartTime, Long qiekePromoId);
	
	public PageObject<OtaOrder> findMyOtaOrder(Long hotelId, List<OtaOrderStatusEnum> statusList, String begintime, String endtime, Integer start,
            Integer limit, String isscore,List<OtaOrderStatusEnum> notStatusList);
	
	public List<TicketInfo> getTicketInfos(OtaOrder order, UMember member);

	public List<TicketInfo> getTicketInfos(Long orderId);

	public OtaOrder calcOrderCost(OtaOrder order);

	public BigDecimal getTotalCostByOtaRoomOrder(OtaRoomOrder roomOrder, boolean isQieKe);

	public void changeOrderPriceType(Long orderId);

	public void cancelOrder(Long orderId);

	public void cancelOrder(OtaOrder order, OtaOrderStatusEnum status);

	public void delCancelOrder(OtaOrder order);

	public void createPmsOrder(OtaOrder order);

	public void createPmsOrderAndLockRoomBeforerPay(OtaOrder otaOrder);

	/**
	 * 修改订单为到付，创建pmsorder
	 *
	 * @param order
	 */
	public void modifyOrderTypePT(OtaOrder order);

	public boolean lockRoom(OtaOrder order);

	public boolean unLockRoom(OtaOrder order);

	public OtaRoomOrder findRoomOrderByPmsRoomOrderNoAndHotelId(String pmsRoomOrderNo, Long hotelId);

	public PmsRoomOrder findPmsRoomOrderById(String pmsRoomOrderNo, Long hotelId);

	public OtaOrder changeOrderStatusByPay(Long otaorderid, OtaOrderStatusEnum orderStatus, PayStatusEnum payStatus, OrderTypeEnum orderType);

	public void changeOrderStatusByPms(Long otaorderid, PmsRoomOrder pmsRoomOrder, String freqtrv);


	public void modifyPmsOrderStatusAfterPay(OtaOrder order);

	public Long countWeixinOrderNum(Long mid);

	public OtaRoomOrder findOtaRoomOrderByOrderId(Long otaOrderId);

	public Map<String, Object> findMaxAndMinOrderId(Map<String, Object> paramMap);

	public List<BOtaorder> findOtaOrderList(Map<String, Object> paramMap);

	/**
	 * 推送 消息
	 */
	public void pushMessage();

	public Map<String, Object> findPromotionMaxAndMinMId(Map<String, Object> paramMap);

	public List<BOtaorder> findPromotionMidList(Map<String, Object> paramMap);

	public Long findMemberOnlyOneOrderCount(BOtaorder BOtaorder);



    /**
     * 给房态组调用,月销量纪录  显示近30天内的销量数据(不包含current date)
     * @param hotelId
     * @return
     */
    public Long findMonthlySales(Long hotelId);
}
