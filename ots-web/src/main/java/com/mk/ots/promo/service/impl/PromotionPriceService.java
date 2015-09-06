package com.mk.ots.promo.service.impl;

import com.google.common.collect.Lists;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.enums.PPayInfoTypeEnum;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.common.enums.TicketStatusEnum;
import com.mk.ots.member.model.UMember;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.pay.dao.IPayDAO;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.dao.IBPromotionPriceDao;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.promo.service.IPromotionPriceService;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.UTicket;
import com.mk.ots.ticket.service.parse.ITicketParse;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PromotionPriceService implements IPromotionPriceService {
	final Logger logger = LoggerFactory.getLogger(PromotionPriceService.class);
	
	@Autowired
	private IPayDAO iPayDAO;
	
	@Autowired
	private IBPromotionDao iBPromotionDao;
	
	@Autowired
	private  IBPromotionPriceDao  iBPromotionPriceDao;
	
	@Autowired
	private UTicketDao uTicketDao;
	

	@Override
	public void bindPromotionPrice(List<Long> promotionidList, UMember member, OtaOrder otaOrder){
		logger.info("订单绑定券：mid:{}, otaorder:{}, promotions:{}", member.getMid(), otaOrder.getAttrs(), promotionidList);
		if(otaOrder!=null){
			List<BPromotionPrice> alreadyBindList = iBPromotionPriceDao.findPromotionPricesByOrderId(otaOrder.getId());
			if(CollectionUtils.isEmpty(promotionidList)){
				if(CollectionUtils.isEmpty(alreadyBindList)){
					//1. 订单没有绑定券，则无动作
					
				}else{
					//2. 订单有绑定券，则需要解绑券
					for(BPromotionPrice tpbp : alreadyBindList){
						unbindOrderPromotion(tpbp.getPromotion(), member.getMid(), otaOrder.getId());
					}
				}
			}else{
				List<UTicket> tickets = iPayDAO.findTickets(promotionidList, member.getMid());
				if(CollectionUtils.isEmpty(alreadyBindList)){
					//1.无绑定券，直接绑定
					for(UTicket utk : tickets){
						bindOrderPromotion(utk, member.getMid(), otaOrder);
					}
				}else{
					//2. 订单有绑定券，则需要判断是否为当前订单券
					List<Long> alreadBindPromotionIdList = Lists.newArrayList();
					for(BPromotionPrice bp : alreadyBindList){
						alreadBindPromotionIdList.add(bp.getPromotion());
					}
					for(UTicket utk : tickets){
						if(alreadBindPromotionIdList.contains(utk.getPromotion().getId())){
							//2.1 参数券id为当前订单券，则无动作
						}else{
							//2.2 参数券id不是当前订单券，则需切换券。 逻辑处理：解绑以前的券，并绑定现有券。注：券状态要进行相应修改
							//A. 清除之前绑定券
							for(BPromotionPrice tpbp : alreadyBindList){
								unbindOrderPromotion(tpbp.getPromotion(), member.getMid(), otaOrder.getId());
							}
							//B. 绑定现有券
							bindOrderPromotion(utk, member.getMid(), otaOrder);
						}
					}
				}
			}
		}
	}
	
	@Override
	public List<BPromotionPrice> queryBPromotionPrices(Long otaorderid){
		return this.iBPromotionPriceDao.findPromotionPricesByOrderId(otaorderid);
	}
	
	/**
	 * 根据优惠券id获取用户优惠券使用记录
	 */
	public List<BPromotionPrice> queryBPromotionPricesByPromId(Long promotionid){
		return this.iBPromotionPriceDao.queryBPromotionPricesByPromId(promotionid);
	}
	
	/**
	 * 解绑单张用户优惠券(非系统券)
	 * @param promoid
	 * @param mid
	 * @param orderid
	 */
	private void unbindOrderPromotion(Long promoid, Long mid, Long orderid){
		logger.info("解绑优惠券>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Start.");
		logger.info("入参: promotionid: {}, mid: {}, orderid: {}", promoid, mid, orderid);
		
		UTicket ukt = uTicketDao.findUTicketByPromoIdAndMid(promoid, mid);
		logger.info("1. 查询用户指定券. uticket:{}", ukt);
		
		if(ukt!=null){
			iBPromotionPriceDao.deleteByOrderidAndPromotionId(promoid, orderid);  //只删除用户优惠券，不删除系统券
			logger.info("2. 删除订单绑定券信息.");
			
			updateTicketSts(ukt, TicketStatusEnum.unused);
			logger.info("3. 修改优惠券为未使用状态. uticket:{}", ukt);
		}
		logger.info("解绑优惠券>>>>>>>>>>>>>>>>>>>>>>>>>>>>>End.");
	}
	
	/**
	 * 绑定用户券(非系统券)
	 * @param promoid
	 * @param mid
	 * @param orderid
	 */
	private void bindOrderPromotion(UTicket ukt, Long mid, OtaOrder order){
		logger.info("绑定用户优惠券>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Start.");
		//1. 查询用户优惠券
		logger.info("1. 查询用户优惠券. uticket:{}", ukt);
		
		if(ukt!=null && order!=null){
			ITicketParse parse = ukt.createParseBean(order);
			if(parse.checkUsable()){ //判断优惠券是否可用
				//2. 绑定优惠券
				BPromotionPrice bPromotionPrice = new BPromotionPrice();
				bPromotionPrice.setPromotion(ukt.getPromotion().getId());
				bPromotionPrice.setOtaorderid(order.getId());
				bPromotionPrice.setPrice(parse.getOnlinePrice());
				bPromotionPrice.setOfflineprice(parse.getOfflinePrice());
				logger.info("2. 绑定优惠券. bPromotionPrice:{}", bPromotionPrice);
				iBPromotionPriceDao.saveOrUpdate(bPromotionPrice);
				
				//3. 更新用户券状态
				ukt.setOtaorderid(order.getId());
				updateTicketSts(ukt, TicketStatusEnum.used);
				logger.info("3. 更新用户券状态. uticket:{}", ukt);
			}else{
				logger.error("优惠券不可用. ");
			}
		}
		logger.info("绑定用户优惠券>>>>>>>>>>>>>>>>>>>>>>>>>>>>>End.");
	}
	
	@Override
	public void updateTicketUnusedStatus(OtaOrder order){
		logger.info("修改订单优惠券状态为未使用状态. order:{}", order);
		// add by zhangyajun 20150722  
		//加规则，如果规则是重庆规则，那么取消订单，不返回优惠券
		 logger.info("规则编号:{}",order.getRuleCode());
//		 if (order.getRuleCode()!=null) {
//			 if (RuleEnum.CHONG_QIN.getId().intValue()!=order.getRuleCode().intValue()) {
//				 extractUpdateTicketUnusedStatus(order);
//			 }
//		 }else {
			 extractUpdateTicketUnusedStatus(order);
//		 }
			 
		 
		
	}
	/**
	 * 20150722 add by zhangyajun
	 * 提取一个更新优惠券状态的独立方法
	 * @param order
	 */
	public  void extractUpdateTicketUnusedStatus(OtaOrder order){
		List<BPromotionPrice> bindList = iBPromotionPriceDao.findPromotionPricesByOrderId(order.getId());
		if(bindList!=null && bindList.size()>0){
			for(BPromotionPrice bpp: bindList){
				logger.info(">>> promotionid:{}, mid:{}", bpp.getPromotion(), order.getMid());
				UTicket ukt = uTicketDao.findUTicketByPromoIdAndMid(bpp.getPromotion(), order.getMid());
				logger.info(">>> uticket:{}", ukt);
				if(ukt!=null){
					ukt.setOtaorderid(order.getId());
					updateTicketSts(ukt, TicketStatusEnum.unused);
					logger.info("修改用户优惠券为未使用状态.mid:{}, tmppromoid:{}.", order.getMid(), bpp.getPromotion());
				}
			}
		}
	}
	/**
	 * 解析优惠券
	 * @param order
	 * @param mid
	 * @param promotionNoList 
	 * @return 
	 */
	
	private List<ITicketParse> getTicketParses(OtaOrder order, Long mid, List<Long> promotionNoList) {
		List<ITicketParse> parses = new ArrayList<ITicketParse>();
		//查询优惠券
		List<UTicket> tickets = iPayDAO.findTickets(promotionNoList, mid);
		if(tickets.isEmpty()){
			return parses;
		}
		//解析优惠券
		for (UTicket ticket : tickets) {
			ITicketParse parse = ticket.createParseBean(order);
			//此处不判断优惠券是否被使用，涉及到修改订单时的逻辑
			if(parse.checkUsableByTime()){
				parses.add(parse);
			}
			
		}
		return parses;
	}
	
	/**
	 * 解析切客优惠码
	 * @param otaOrder
	 * @param promotionNoList
	 * @param cousNo 
	 * @return 
	 */
	private List<ITicketParse> getQieKePromotionParses(OtaOrder otaOrder, List<Long> promotionNoList) {
		List<ITicketParse> parses = Lists.newArrayList();
		//查询优惠码
		List<BPromotion> promotions = this.iBPromotionDao.findByPromotionType(PromotionTypeEnum.qieke);
		logger.debug("查询切客优惠码:{}", promotions);
		
		if(promotions!=null && promotions.size()>0){
			//解析优惠券码信息
			for (BPromotion promotion : promotions) {
				ITicketParse parse = promotion.createParseBean(otaOrder);
				parse.checkUsable();
				parses.add(parse);
			}
		}
		return parses;
	}
	
	/**
	 * 使用优惠券更新优惠券状态
	 * @param ticketParses
	 * @param sts
	 */
	private void updateTicektsSts(List<ITicketParse> ticketParses, TicketStatusEnum sts,Long orderId) {
		for (ITicketParse ticketParse : ticketParses) {
			if(ticketParse.needUse()){
				ticketParse.getTicket().setOtaorderid(orderId);
				updateTicketSts(ticketParse.getTicket(), sts);
			}
		}
	}
 
	/**
	 * 更新优惠券状态
	 * @param ticket
	 * @param statu
	 */
	private void updateTicketSts(UTicket ticket, TicketStatusEnum statu) {
		logger.info("进入updateTicketSts(), ticket{}, statu:{}", ticket, statu);
		if(ticket == null){
			return;
		}
		if(TicketStatusEnum.unused.equals(statu)){
			ticket.setUsetime(null);
			ticket.setOtaorderid(null);
		}else{
			ticket.setUsetime(new Date());
		}
		ticket.setStatus(statu.getId());
		logger.info("更新优惠券状态: {}", ticket);
		uTicketDao.saveOrUpdate(ticket);
		logger.info("退出updateTicketSts(). 更新后状态：{}", ticket);
	}

	/**
	 * 更新优惠码数量
	 * @param promotionParses
	 * @param type
	 */
	private void updatePromoNums(List<ITicketParse> promotionParses,
			PPayInfoTypeEnum type) {
		for (ITicketParse promoParse : promotionParses) {
			if(promoParse.needUse()){
				updatePromoNumByType(promoParse.getPromotion(), type);
			}
		}
	}
	
	/**
	 * 更新优惠码数量
	 * @param promotion
	 * @param type
	 */
	private void updatePromoNumByType(BPromotion promotion, PPayInfoTypeEnum type) {
		if(promotion == null){
			return;
		}
		//优惠码数量-1无数量限制直接返回
		if(promotion.getNum() <0){
			return;
		}
		//Y2U优惠码退
		if(type.equals(PPayInfoTypeEnum.Y2U)){
			promotion.setNum(promotion.getNum()+1);
			iBPromotionDao.saveOrUpdate(promotion);
			return;
		}
		//Y2P优惠码入
		if(promotion.getNum() ==0){
			throw MyErrorEnum.noUsablePromotaion.getMyException();
		}
		promotion.setNum(promotion.getNum()-1);
		iBPromotionDao.saveOrUpdate(promotion);
	}
	
	@Override
	public List<BPromotion> queryAllOrderQikePromotions(Long mid, Long otaorderid){
		return iBPromotionDao.queryAllOrderQikePromotions(mid, otaorderid);
	}

	@Override
	public boolean isBindPromotion(Long otaorderid) {
		logger.info("isBindPromotion>>>>>>>>>>>>>>>>>>>>START. orderid:{}", otaorderid);
		if (otaorderid == null) {
			return false;
		}
		List<BPromotionPrice> alreadyBindList = iBPromotionPriceDao.findUserPromotionPricesByOrderId(otaorderid);
		logger.info("alreadyBindList:{}", alreadyBindList);
		boolean rtn = alreadyBindList != null && alreadyBindList.size() > 0;
		logger.info("isBindPromotion>>>>>>>>>>>>>>>>>>>>END. orderid:{}, rtnvalue: {}", otaorderid, rtn);
		return rtn;
	}
}
