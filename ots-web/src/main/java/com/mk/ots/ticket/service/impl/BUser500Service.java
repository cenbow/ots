//package com.mk.ots.ticket.service.impl;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.google.common.base.Optional;
//import com.mk.ots.member.dao.IMemberDao;
//import com.mk.ots.member.model.UMember;
//import com.mk.ots.message.model.MessageType;
//import com.mk.ots.message.service.IMessageService;
//import com.mk.ots.promo.service.IPromoService;
//import com.mk.ots.ticket.dao.BUser500Dao;
//import com.mk.ots.ticket.model.BUser500;
//import com.mk.ots.ticket.service.IBUser500Service;
//
//@Service
//public class BUser500Service implements IBUser500Service{
//	final Logger logger = LoggerFactory.getLogger(BUser500Service.class);
//	
//	@Autowired
//	private BUser500Dao bUser500Dao; 
//	@Autowired
//	private IMemberDao iMemberDao;
//	@Autowired
//	private IPromoService iPromoService;
//	@Autowired
//	private IMessageService iMessageService;
//	
//	@Override
//	public void batchGen(){
//		//1. 获取所有用户列表
//		List<BUser500> userPhoneList = bUser500Dao.findStatusNullList();
//		
//		//2. 获取要发的券id
//		long promotionid = 7l;
//		
//		//3. 循环用户发放券并通知信息
//		for(BUser500 tmp : userPhoneList){
//			Optional<UMember> opu = iMemberDao.findMemberByLoginName(tmp.getPhone(), null);
//			if(opu.isPresent()){
//				//1. 发放券
//				List<Long> genList = iPromoService.genCGTicket(promotionid, opu.get().getMid());
//				boolean flag = genList!=null && genList.size()>0;
//				
//				//2.  推送消息
//				if(flag){
//					bUser500Dao.updateStatusTByPhone(tmp.getPhone());
//					iMessageService.pushMsg(tmp.getPhone(), "用户礼包", "biubiu~30元酒店优惠券已放入您的账户，点击查看。", MessageType.USER.getId());
//				}
//			}else{
//				logger.error("phone:{} is not exist.", tmp.getPhone());
//			}
//		}
//		
//	}
//}
