package com.mk.ots.pay.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PaySrcEnum;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.common.enums.PmsErrorTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.PmsError;
import com.mk.ots.order.bean.UUseTicketRecord;
import com.mk.ots.order.dao.OrderDAO;
import com.mk.ots.pay.dao.IPayDAO;
import com.mk.ots.pay.model.POrderLog;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.UTicket;


@Component
public class PayDAO extends MyBatisDaoImpl<PPay, Long> implements IPayDAO{
	
	@Autowired
	private OrderDAO orderDao;
	
	@Autowired
	private UTicketDao uTicketMapper;
	
//	@Autowired
//	private POrderLogDAO orderLogDao;

//	public PmsError findPmsErrorByOrderId(Long orderId) {
//		return PmsError.dao.findFirst("select e.* from pms_error e, p_pay p where e.otherid = p.id and p.orderid = ?", orderId);
//	}
	
	public boolean isMatchRule(){
		//1. 
		return false;
	}

	@Override
	public Long countPPayByOrderId(Long orderid, PaySrcEnum type) {
		Map param = Maps.newHashMap();
		param.put("orderid", orderid);
		param.put("type", type.getId().intValue());
		
		return this.count("countPPayByOrderId1",param);
	}

	@Override
	public PPay getPayByOrderId(Long orderId) {
		Map param = Maps.newHashMap();
		param.put("orderid", orderId);
		return this.findOne("findByOrderId",param);
	}

	@Override
	public PPay findPayById(Long payid) {
		Map param = Maps.newHashMap();
		param.put("payid", payid);
		return this.findOne("findById",param);
	}
	
//	@Override
//	public Long countOrderByType(Long orderid, PayStatusEnum payStatus) {
//		return Db.queryLong("select count(*) from b_otaorder where  id = ? and paystatus = ?", orderid, payStatus.getId());
//	}

//	@Override
//	public boolean updateOrderStatus(Long orderid,
//			OtaOrderStatusEnum orderStatus, PayStatusEnum payStatus) {
//		return OtaOrder.dao.findById(orderid).set("orderstatus", orderStatus.getId()).set("paystatus", payStatus.getId()).update();
//	}

	
//	public POrderLog getOrderLogByPayId(Long payId) {
//		return orderLogDao.selectByPayId(payId);
//	}

	@Override
	public List<UUseTicketRecord> findTicketRecords(List<Long> recordIdList) {
//		if (recordIdList.isEmpty()) {
//			return new ArrayList<UUseTicketRecord>();
//		}
//		QueryCriteria query = QueryCriteria.newInstance();
//		query.addAllClassField(UUseTicketRecord.class);
//		query.addAllClassField(UTicket.class);
//		query.addAllClassField(BPromotion.class);
//		FromClause from = FromClause.newInstance(TableCriteria
//				.newInstance(UUseTicketRecord.class));
//		from.leftJoin(TableCriteria.newInstance(UTicket.class),
//				Condition.equalTo(FieldCriteria.newInstance(
//						UUseTicketRecord.class, "tick"), FieldCriteria
//						.newInstance(UTicket.class, "id")));
//		from.leftJoin(TableCriteria.newInstance(BPromotion.class), Condition
//				.equalTo(FieldCriteria.newInstance(UUseTicketRecord.class,
//						"promption"), FieldCriteria.newInstance(
//						BPromotion.class, "id")));
//		Condition cond1 = Condition.in(
//				FieldCriteria.newInstance(UUseTicketRecord.class, "id"),
//				recordIdList);
//		AND and = AND.newInstance(cond1);
//		WhereClause where = WhereClause.newInstance(and);
//		QueryTemplate qt = QueryTemplate.newInstance(query, from, where)
//				.setResultClass(UUseTicketRecord.class);
//		List<UUseTicketRecord> list = this.queryObjects(qt);
//		return list;
		return null;
	}

//	@Override
//	public Boolean updateRoomOrderStatus(Long orderId,
//			OtaOrderStatusEnum orderStatus, PayStatusEnum payStatus) {
//		boolean flag = true;
//		List<OtaRoomOrder> list =  OtaRoomOrder.dao.find("select * from b_otaroomorder where otaorderid = ?", orderId);
//		for(OtaRoomOrder tmp : list){
//			if(!tmp.dao.set("orderstatus", orderStatus.getId()).set("paystatus", payStatus.getId()).update()){
//				flag = false;
//				break;
//			}
//		}
//		return flag;
//	}

	@Override
	public List<UTicket> findTickets(List<Long> promotionNos, Long mid) {
		if(promotionNos!=null && promotionNos.size()>0){
			return uTicketMapper.findUTicketByPromotionAndMid(promotionNos,mid); 
		}
		return Lists.newArrayList();
	}

//	@Override
//	public void delPromoPriceByOrderId(Long orderId) {
//		// TODO 考虑批量删除 影响缓存
//		List<BPromotionPrice> list = BPromotionPrice.dao.find("select * from b_promotion_price where otaorderid=?", orderId);
//		for (BPromotionPrice bPromotionPrice : list) {
//			bPromotionPrice.delete();
//		}
//	}

//	@Override
//	public List<BPromotion> findPromotions(List<Long> promotionNos, Long orderId) {
//		String sql = "select * from b_promotion p , b_promotion_price pp where p.id = pp.promotion and p.id in(?) and p.isticket = 'F' and pp.otaorderid=?";
//		return BPromotion.dao.find(sql, getIn(promotionNos), orderId);
//	}

//	@Override
//	public Long countTicketsByMember(Long mid, Long promoId) {
//		String sql = "select count(1) from u_ticket t where t.mid = ? and t.promotionid = ?";
//		return Db.queryLong(sql, mid, promoId);
//	}

//	@Override
//	public Long countOrderTicket(Long promoId, Long orderId) {
//		String sql = "select count(1) from p_pay p, b_otaorder o, u_useticket_record u where p.orderid = o.id and u.payid = p.id and o.id=? and u.promotionid = ?";
//		return Db.queryLong(sql, orderId, promoId);
//	}

//	@Override
//	public List<PmsError> getPayToPmsErrorList(PmsErrorTypeEnum errorType, Long startTime) {
//		return PmsError.dao.find("select * from pms_error where type=? and time=?", errorType.getId(), startTime);
//	}
	
//	@Override
//	public BPromotionPrice getPromoPrice(Long orderId, Long promotionId) {
//		return BPromotionPrice.dao.findFirst("select * from b_promotion_price where otaorderid=? and promotion=?",orderId, promotionId);
//	}
	
	@Override
	public void updateOrderPromos(Long orderId, Boolean isCoupon, Boolean isPromotion) {
		OtaOrder order = orderDao.findOtaOrderById(orderId);
		order.setCoupon(isCoupon?"T":"F");
		order.setPromotion(isPromotion?"T":"F");
		order.update();
	}
	
	public String getIn(List lists) {
		StringBuffer in = new StringBuffer();
		for (Object e : lists) {
			in.append("'").append(e.toString()).append("',");
		}
		if (in.length() > 0) {
			in.setLength(in.length() - 1);
		}
		return in.toString();
	}

	@Override
	public void saveOrUpdate(PPay pPay) {
		if(pPay.getId()!=null){
			this.update(pPay);
		}else{
			this.insert(pPay);
		}
	}
	
	public boolean  updatePayPaytyeAndneedreturn(Long  payid,int paytye,String needreturn ){
		logger.info("  更改 pay 状态：payid=="+payid+"  paytye="+paytye+"  needreturn="+needreturn);
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("id", payid);
		map.put("status", paytye);
		map.put("needreturn", needreturn);
		if(this.update("updatePayByCancelOrder", map) >-1){
			logger.info("  更改 pay 状态：payid=="+payid+"  paytye="+paytye+"  needreturn="+needreturn +" 执行成功！");
			return true;
		}else{
			logger.info("  更改 pay 状态：payid=="+payid+"  paytye="+paytye+"  needreturn="+needreturn +" 执行失败！");
			return false;
		}
	}
	
	
	
	
	public List<PPay>  findPayByOrderidAndPaySrc(Long orderId,PaySrcEnum type){
		Map param = Maps.newHashMap();
		param.put("orderid", orderId);
		param.put("type", type.getId().intValue());
	    return find("findByOrderIdAndPaysrc", param);
	}

	@Override
	public int updateUserIdByOrderId(Long orderId, String userId) {
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("orderid", orderId);
		param.put("userid", userId);
	    return this.update("updateUserIdByOrderId", param);
	}

	public List<PPay> findByUserId (String userId) {
		Map param = Maps.newHashMap();
		param.put("userId", userId);
		return find("findByUserId", param);
	}
	
}
