package com.mk.ots.pay.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PaySrcEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.common.enums.PmsErrorTypeEnum;
import com.mk.ots.order.bean.PmsError;
import com.mk.ots.order.bean.UUseTicketRecord;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.ticket.model.UTicket;

/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public interface IPayDAO extends BaseDao<PPay, Long>{

	public Long countPPayByOrderId(Long orderid, PaySrcEnum type);
	
	public Long countOrderByType(Long orderid , PayStatusEnum payStatus);
	
	public boolean updateOrderStatus(Long orderid,OtaOrderStatusEnum orderStatus, PayStatusEnum payStatus);

	public PPay getPayByOrderId(Long orderId);

	public List<UUseTicketRecord> findTicketRecords(List<Long> recordIdList);

	public PmsError findPmsErrorByOrderId(Long orderId);

	public PPay findPayById(Long payid);

	public Boolean updateRoomOrderStatus(Long orderId, OtaOrderStatusEnum orderStatus, PayStatusEnum payStatus);

	public void delPromoPriceByOrderId(Long orderId);

	public List<BPromotion> findPromotions(List<Long> list, Long orderId);

	public List<UTicket> findTickets(List<Long> promotionNoList, Long mid);

	public Long countTicketsByMember(Long mid, Long promoId);

	public Long countOrderTicket(Long promoId, Long orderId);

	public List<PmsError> getPayToPmsErrorList(PmsErrorTypeEnum paytopmserror, Long startTime);

	public BPromotionPrice getPromoPrice(Long orderId, Long promotionId);

	public void updateOrderPromos(Long id, Boolean isCoupon, Boolean isPromotion);
	
	public void saveOrUpdate(PPay pPay);
	
	public boolean  updatePayPaytyeAndneedreturn(Long  payid,int paytye,String needreturn);
	
	
	public List<PPay> findPayByOrderidAndPaySrc(Long orderId,PaySrcEnum type);
	
}
