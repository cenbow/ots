package com.mk.ots.ticket.service.impl;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.AppUtils;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.activity.dao.IBActiveCDKeyDao;
import com.mk.ots.activity.dao.IUActiveCDKeyLogDao;
import com.mk.ots.activity.model.BActiveCDKey;
import com.mk.ots.activity.model.BActivity;
import com.mk.ots.activity.service.IBActiveChannelService;
import com.mk.ots.activity.service.IBActivityService;
import com.mk.ots.appstatus.dao.IAppStatusDao;
import com.mk.ots.common.enums.*;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.mapper.TPromotionCityMapper;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.order.bean.OrderLog;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.dao.OtaOrderDAO;
import com.mk.ots.order.dao.RoomOrderDAO;
import com.mk.ots.order.model.BOtaorder;
import com.mk.ots.order.model.FirstOrderModel;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.model.CouponParam;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.dao.IBPromotionPriceDao;
import com.mk.ots.promo.dao.IBPromotionRuleDAO;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.promo.model.BPromotionRule;
import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.promo.service.IPromotionPriceService;
import com.mk.ots.ticket.dao.BHotelStatDao;
import com.mk.ots.ticket.dao.USendUTicketDao;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.*;
import com.mk.ots.ticket.service.ITicketService;
import com.mk.ots.ticket.service.IUActiveShareService;
import com.mk.ots.ticket.service.IUPrizeRecordService;
import com.mk.ots.ticket.service.parse.ITicketParse;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 优惠券服务接口
 * @author nolan
 *
 */
@Service
public class TicketService implements ITicketService{
	final Logger logger = LoggerFactory.getLogger(TicketService.class);

	@Autowired
	private IBPromotionDao iBPromotionDao;

	@Autowired
	private RoomOrderDAO roomOrderDAO;
	
	@Autowired
	private UTicketDao ticketMapper;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private IMemberService iMemberService;
	
	@Autowired
	private IAppStatusDao iAppStatusDao;
	
	@Autowired
	private IBPromotionPriceDao iBPromotionPriceDao;

	@Autowired
	private IPromotionPriceService iPromotionPriceService;

	@Autowired
	private IBActiveCDKeyDao iBActiveCDKeyDao;
	
	@Autowired
	private IBActiveChannelService iBActiveChannelService;
	
	@Autowired
	private IPromoService iPromoService;
	
	@Autowired
	private IUActiveCDKeyLogDao iUActiveCDKeyLogDao;

    @Autowired
    private OtaOrderDAO otaOrderDAO;
    @Autowired
    private IBPromotionRuleDAO ibPromotionRuleDAO;

    @Autowired
    private BHotelStatDao bHotelStatDao;
    @Autowired
    private IUPrizeRecordService iuPrizeRecordService;
    @Autowired
    private IUActiveShareService iuActiveShareService;

    @Autowired
    private THotelMapper tHotelMapper;

	@Autowired
	private TPromotionCityMapper tPromotionCityMapper;

	@Autowired
	private USendUTicketDao uSendUTicketDao;
	@Autowired
	private IBActivityService ibActivityService;
	/**
     * 10+1活动券
     */
    public static final long ACTIVE_10YUAN_1 = Constant.ACTIVE_10YUAN_1;
    /**
     * 15元体验券
     */
    public static final long ACTIVE_15YUAN = Constant.ACTIVE_15YUAN;
    
    /**
     * 100元优惠券
     */
    public static final long ACTIVE_100YUAN = Constant.ACTIVE_100YUAN;
    /**
     *  77元七夕特享券
     */
    public static final long ACTIVE_QIXI = Constant.ACTIVE_QIXI;
    /**
     * iosAPP
     */
    public static final int  IOS_APP = OrderMethodEnum.IOS.getId();
    /**
     * 安卓APP
     */
    public static final int  ANDROID_APP = OrderMethodEnum.ANDROID.getId();
	@Override
	public List<TicketInfo> getTicketInfosByCode(String code){
		List<TicketInfo> ticketList = Lists.newArrayList();
		
		Optional<BActiveCDKey> of = iBActiveCDKeyDao.getBActiveCDKey(code);
		if(of.isPresent()){
			BPromotion bpdefine =  this.iBPromotionDao.findById(of.get().getPromotionid());
			TicketInfo promotion2tickinfo = promotion2tickinfo(bpdefine.createParseBean(null));
			ticketList.add(promotion2tickinfo);
		}
		return ticketList;
	}
	
	
	/**
	 * 查询用户可用券(可修改订单的情况)
     * @param otaOrder
     * @param mid
	 * @return
	 */
	@Override
	public List<TicketInfo> getBindOrderAndAvailableTicketInfos(OtaOrder otaOrder, Long mid) {
		logger.info("getBindOrderAndAvailableTicketInfos(查询绑定券和可用券)>>>>>>>>>>>>>>>>开始");
		logger.info("orderid: {}, mid: {}...", otaOrder, mid);
		
		List<TicketInfo> ticketList = Lists.newArrayList();
		//1. 查询订单已绑定券(议价券/切客券 或 用户普通券， 两者互斥)
		if(otaOrder != null){
			List<TicketInfo> alreadyticketList = getOrderAlreadyBindTickets(otaOrder);
			logger.info("1. 已绑定券: {}.", alreadyticketList);
			ticketList.addAll(alreadyticketList);
			
			//2. 查询我的可用优惠券(注：只有在线付可以使用我的优惠券)
			if(otaOrder.getSpreadUser() ==null){
				List<TicketInfo> myticket = queryMyAvailableTicket(mid);
				logger.info("2. 查询可用优惠券: {}", myticket);
				
				if(CollectionUtils.isNotEmpty(myticket)){
//                    List citycodes = Lists.newArrayList();
//                    citycodes.add("310000");

                    filterLimitArea(myticket, otaOrder);
//					checkBRule(myticket, otaOrder);
					//update by tankai  去掉对B规则的酒店判断
//					checkBRule(myticket, otaOrder);
					sortTicketByActivety(myticket,otaOrder,mid);
					logger.info("3.排序可用普通优惠券: {}", myticket);
					if(CollectionUtils.isNotEmpty(myticket)){//可能为空
						ticketList.addAll(myticket);
					}
				}
			} else {
				logger.info("只允许预付订单可以使用优惠券.");
			}
		}
		logger.info("查询绑定券和可用券: {}", ticketList);
		logger.info("getBindOrderAndAvailableTicketInfos(查询绑定券和可用券)>>>>>>>>>>>>>>>>结束.");
		return ticketList;
	}

//    private void filterLimitArea(List<TicketInfo> myticket, OtaOrder otaOrder, List citycodes) {
//    	Iterator<TicketInfo> it =myticket.iterator();
//    	while (it.hasNext()) {
//    		TicketInfo info = it.next();
//
//    		 if (info.getType() == PromotionTypeEnum.shoudan.getId()) {
//                 THotelModel tHotelModel = tHotelMapper.findHotelInfoById(otaOrder.getHotelId());
//                 if (!citycodes.contains(tHotelModel.getCitycode())) {
//                     it.remove();
//                 }
//             }
//		}
//    }
	/**
	 * 过滤只允许某城市使用的优惠券
	 *
	 * @param myticket
	 * @param otaOrder
	 */
	private void filterLimitArea(List<TicketInfo> myticket, OtaOrder otaOrder) {
		THotelModel tHotelModel = tHotelMapper.findHotelInfoById(otaOrder.getHotelId());
		HashMap  m = new  HashMap();
		m.put("cityCode",tHotelModel.getCitycode());
		Iterator<TicketInfo> it =myticket.iterator();
		while (it.hasNext()) {
			TicketInfo info = it.next();

			//首单优惠券 || 110活动优惠券
			if (info.getType() == PromotionTypeEnum.shoudan.getId() || info.getActivityid().longValue() == 110l) {
				if(!org.apache.commons.lang3.StringUtils.isEmpty(tHotelModel.getCitycode())){
					m.put("activityId",info.getActivityid());
					List<BPromotionCity>   promotionCityList = tPromotionCityMapper.findPromotionCityByCityCode(m);
					logger.info("method [filterLimitArea]  parme : promotionCityList: {}, mid: {}...",promotionCityList ,tHotelModel);
					if(CollectionUtils.isEmpty(promotionCityList)){
						logger.info("method [filterLimitArea]  remove  promotion :",it);
						it.remove();
					}
				}
			}
		}
	}
    

	/**
	 * 检验B规则
	 */
	public void checkBRule(List<TicketInfo> myticket,OtaOrder otaOrder){
		//符合b规则，不显示30元优惠券
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", otaOrder.getMid());
		param.put("hotelid", otaOrder.getHotelId());
		List<BOtaorder> oldOrder=otaOrderDAO.findOtaOrderListByMidAndHotelId(param);
		boolean flag=false;
		//判断该用户在该酒店是否首次订单
		if(CollectionUtils.isNotEmpty(oldOrder)){
			flag = true;
		}
		if (flag) {
			//删除优惠券
			Integer length=myticket.size();
			for (int i = 0; i < length; i++) {
				if (Long.valueOf(Constant.ACTIVE_B_30YUAN).equals(myticket.get(i).getActivityid())) {
					if(myticket.get(i)!=null&&myticket.get(i).getId()!=null){
						logger.info("从我的优惠券列表中删除重庆30元优惠券！ticketId: "+myticket.get(i).getId());
						myticket.remove(i);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 查询用户订单历史绑定券(完成订单或不可修改订单的情况下)
     * @param otaOrder
     * @return
	 */
	@Override
	public List<TicketInfo> getOrderAlreadyBindTickets(OtaOrder otaOrder) {
		logger.info("查询订单已绑定的优惠券(getOrderAlreadyBindTickets): orderid:{}.", otaOrder.getId());
		
		List<TicketInfo> rtnList = Lists.newArrayList();
		List<BPromotionPrice> bpList = this.iBPromotionPriceDao.findPromotionPricesByOrderId(otaOrder.getId());
		if(bpList!=null && bpList.size()>0){
			for(BPromotionPrice price : bpList){
				BPromotion tmpbp = this.iBPromotionDao.findById(price.getPromotion());
				logger.info("查询绑定券...：orderid:{}, promotionid:{}, info:{}.", otaOrder.getId(), tmpbp.getId(), tmpbp.toString());
				if(tmpbp!=null){
					TicketInfo promotion2tickinfo = promotion2tickinfo(tmpbp.createParseBean(otaOrder));
					promotion2tickinfo.setSelect(true);
					promotion2tickinfo.setCheck(true);
					promotion2tickinfo.setIsused(true);
					rtnList.add(promotion2tickinfo);
				}
			}
		}
		logger.info("已绑定券列表(BPromotionPrice)：{}.", rtnList);
		return rtnList;
	}
	
	@Override
	public List<TicketInfo> getHistoryOrderQieKeTicketInfosByMid(Long mid, Long otaorderid) {
		logger.info("查询历史订单使用的切客券: mid:{}, otaorderid:{}", mid, otaorderid);
		List<TicketInfo> rtnList = Lists.newArrayList();
		
		List<BPromotion> queryList = this.iPromotionPriceService.queryAllOrderQikePromotions(mid, otaorderid);
		if(queryList!=null && queryList.size()>0){
			for(BPromotion tmp : queryList){
				TicketInfo promotion2tickinfo = promotion2tickinfo(tmp.createParseBean(null));
				promotion2tickinfo.setCheck(false);
				promotion2tickinfo.setIsused(true);
				promotion2tickinfo.setStatus(TicketStatusEnum.used.getId());
				promotion2tickinfo.setStatusname(TicketStatusEnum.used.getName());
				rtnList.add(promotion2tickinfo);
			}
		}
		logger.info("查询历史订单使用的切客券结果: {}", rtnList);
		return rtnList;
	}
	
	/**
	 * 查询我的所有优惠券
	 * @param mid 用户mid
	 * @param status 是否已用
	 * @return
	 */
	@Override
	public List<TicketInfo> queryMyTicket(long mid, Boolean status){
		List<TicketInfo> rtnList = Lists.newArrayList();
		List<UTicket> uList = this.ticketMapper.findUTicketByMid(mid, status);
		for(UTicket ut : uList){
			if(ut!=null && ut.getPromotion()!=null && !ut.getPromotion().getIsinstance()){
				rtnList.add(promotion2tickinfo(ut.createParseBean(null)));
			}
		}
		return rtnList;
	}
	
	
	@Override
	public List<TicketInfo> queryMyAvailableTicket(long mid){
		List<TicketInfo> rtnList = Lists.newArrayList();
		List<UTicket> uList = this.ticketMapper.findUTicketByMid(mid, false);
		for(UTicket ut : uList){
			TicketInfo info = promotion2tickinfo(ut.createParseBean(null));
			if(!info.isIsused() && info.getCheck()){
				rtnList.add(info);
			}
		}
		return rtnList;
	}
	
	@Override
	public List<TicketInfo> queryMyTicket(long mid, long activeid) {
		List<TicketInfo> rtnList = Lists.newArrayList();
		List<UTicket> uList = this.ticketMapper.findUTicketByMidAndActiveid(mid, activeid);
		for(UTicket ut : uList){
			if(ut!=null && ut.getPromotion()!=null && !ut.getPromotion().getIsinstance()){
				rtnList.add(promotion2tickinfo(ut.createParseBean(null)));
			}
		}
		return rtnList;
	}
	
	@Override
	public List<UTicket> queryTicketByMidAndActvie(long mid, long activeid) {
		return  this.ticketMapper.findUTicketByMidAndActiveid(mid, activeid);
	}
	
	@Override
	public List<TicketInfo> queryMyTicketByPromotionids(long mid, List<Long> promotionids) {
		List<TicketInfo> rtnList = Lists.newArrayList();
		List<UTicket> uList = this.ticketMapper.findUTicketByPromotionAndMid(promotionids, mid);
		for(UTicket ut : uList){
			if(ut!=null && ut.getPromotion()!=null && !ut.getPromotion().getIsinstance()){
				rtnList.add(promotion2tickinfo(ut.createParseBean(null)));
			}
		}
		return rtnList;
	}
	
	@Override
	public long countByMidAndActiveId(Long mid, Long activeid) {
		return ticketMapper.countByMidAndActiveId(mid, activeid);
	}
	
	@Override
	public long countByMidAndActiveIdAndTime(Long mid, Long activeid, Date starttime, Date endtime) {
		return ticketMapper.countByMidAndActiveIdAndTime(mid, activeid, starttime, endtime);
	}
	
	@Override
	public boolean isBeforeMay20Member(Long mid){
		Optional<UMember> ofMember =  this.iMemberService.findMemberById(mid);
		if(ofMember.isPresent()){
			UMember um = ofMember.get();
			LocalDate ld = LocalDate.fromDateFields(um.getCreatetime());
			return ld.isBefore(LocalDate.parse("2015-05-20")) || ld.isEqual(LocalDate.parse("2015-05-20"));
		}
		return false;
	}
	
	@Override
	public boolean isHaveAppAndLogin(Long mid){
		List list = iAppStatusDao.findByMid(mid);
		return list!=null && list.size()>0;
	}
	
	@Override
    public List<TicketInfo> exchange(UMember member, String code, String hardwarecode) {
        List<TicketInfo> ticketList = Lists.newArrayList();
		//1. 检测兑换码是否有效
		Optional<BActiveCDKey> ofkey =  this.iBActiveCDKeyDao.getBActiveCDKey(code);
		if(ofkey.isPresent()){
			//2 兑换码是否有效(兑过与否)
			BActiveCDKey bActiveCDKey = ofkey.get();
			if(!bActiveCDKey.getUsed()){
				if(new Date().before(bActiveCDKey.getExpiration())){
					//判断用户是否么购买过10+1 或者15元优惠价
					if(bActiveCDKey.getActiveid()!=null){
						if (bActiveCDKey.getActiveid()==Constant.ACTIVE_10YUAN_1 || 
								bActiveCDKey.getActiveid()==Constant.ACTIVE_15YUAN ) {//5就是10+1的活动ID
							long count = ticketMapper.countByMidAndActiveId(member.getId(), bActiveCDKey.getActiveid());
							if (count>0) {
								if(bActiveCDKey.getActiveid()==Constant.ACTIVE_10YUAN_1){
									throw MyErrorEnum.customError.getMyException("很抱歉！您已经体验过“1元住酒店”活动咯~请继续关注眯客其他优惠活动吧。");
								}else{
									throw MyErrorEnum.customError.getMyException("很抱歉！您已经体验过“15元住酒店”活动咯~请继续关注眯客其他优惠活动吧。");
								}
							}
						}
					}

                    //region 首单优惠券兑换优惠码逻辑判断
                    BPromotion bp = iBPromotionDao.findById(bActiveCDKey.getPromotionid());
                    if (bp != null && PromotionTypeEnum.shoudan.equals(bp.getType())) {
                        FirstOrderModel fom = new FirstOrderModel();
                        fom.setMid(member.getMid());
                        boolean isFirstOrder = orderService.isFirstOrder(fom);
                        if (!isFirstOrder) {
                            throw MyErrorEnum.customError.getMyException("很抱歉。您已不是首单用户,谢谢参与!");
                        }
                        List<UTicket> uList = this.ticketMapper.findUTicketByPromotionType(member.getMid(), com.mk.ots.common.enums.PromotionTypeEnum.shoudan);
                        if (uList != null && uList.size() > 0) {
                            throw MyErrorEnum.customError.getMyException("很抱歉！您已经兑换过“眯客首单优惠券”咯~请继续关注眯客其他优惠活动吧。");
                        }

                        if (!Strings.isNullOrEmpty(hardwarecode) && this.iPromoService.isGetFirstOrderPromotion(hardwarecode)) {
                            throw MyErrorEnum.customError.getMyException("很抱歉！此手机已经兑换过“眯客首单优惠券”咯~请继续关注眯客其他优惠活动吧。");
                        }
                    }
                    //endregion

					boolean isPush = false;
                    List<Long> genList = this.iPromoService.genCGTicket(bActiveCDKey.getPromotionid(), member.getMid(), null, null, PromotionMethodTypeEnum.HAND, bActiveCDKey.getCode(), bActiveCDKey.getChannelid(), hardwarecode);

                    //3. 兑换券
					boolean isgen = genList!=null && genList.size()>0;
					//修改券使用记录
					if(isgen){
						this.iBActiveCDKeyDao.useBActiveCDKey(code);
						for(Long pid : genList){
							BPromotion bpdefine =  this.iBPromotionDao.findById(pid);
							TicketInfo promotion2tickinfo = promotion2tickinfo(bpdefine.createParseBean(null));
							ticketList.add(promotion2tickinfo);
						}
					}
					//TODO push 消息
                    iUActiveCDKeyLogDao.log(member.getMid(), bActiveCDKey.getActiveid(), bActiveCDKey.getChannelid(), bActiveCDKey.getPromotionid(), code, isgen, isPush, hardwarecode);
                }else{
					throw MyErrorEnum.customError.getMyException("兑换码已过期.");	
				}
			} else {
				throw MyErrorEnum.customError.getMyException("兑换码已被兑换.");
			}
		} else {
			throw MyErrorEnum.customError.getMyException("兑换码不正确.");
		}
		return ticketList;
	}
	
	/**
	 * 修改某一订单所使用的用户优惠券为可用
	 * @param orderid
	 */
	@Override
	public void updateUTicketAvailable(Long orderid){
		this.ticketMapper.updateUTicketAvailable(orderid);
	}
	
	/**
	 * 对ticketInfo排序
	 * 
	 * @param ticketInfos
	 */
	public void sortTicketInfos(List<TicketInfo> ticketInfos) {
		if (CollectionUtils.isEmpty(ticketInfos)) {
			return;
		}
		Collections.sort(ticketInfos, new Comparator<TicketInfo>() {
			@Override
			public int compare(TicketInfo param1, TicketInfo param2) {
				// 可用的优惠券优先
				if (param1.getCheck() != param2.getCheck()) {
					return param2.getCheck().compareTo(param1.getCheck());
				}
				// 优惠券结束日期较早的优先 modify by tankai 逻辑修改为 按照线上优惠额倒序排序
				//return param1.getEndtime().compareTo(param2.getEndtime());
				
				//优惠券信息，按照线上优惠额倒序排序
				return param2.getSubprice().compareTo(param1.getSubprice());
			}
		});
	}
	
	
	/**
	 * 封装活动的集合
	 * @param ticketInfos
	 * @param userTicketInfos
	 * @param userTicketInfosAll
	 * @param order
	 * @param mid
	 */
	private  void fillActiveList(List<TicketInfo> ticketInfos,List<TicketInfo> userTicketInfos,
	List<TicketInfo> userTicketInfosAll, OtaOrder order,long mid){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = ticketInfos.size()-1; i >=0; i--) {
			TicketInfo ticket = ticketInfos.get(i);
			//如果优惠券类型为首单优惠券，则判断该用户是否首单
			if (com.mk.ots.common.enums.PromotionTypeEnum.shoudan.getId().equals(ticket.getType())) {
				FirstOrderModel fom=new FirstOrderModel();
				fom.setMid(mid);
				boolean isFirstOrder =orderService.isFirstOrder(fom);
				logger.info("用户：{}的首单查询结果为：{}",mid,isFirstOrder);
				if (!isFirstOrder) {
					//不是首单，则将优惠券从列表中移除
					ticketInfos.remove(i);
					logger.info("将首单优惠券：{}从优惠券列表中移除",ticket);
					continue;
				}
			}
			
			//获取该用户对该酒店是否使用过15优惠券
			if (ticket.getCheck()) {
				if(ticket.getActivityid()==null){
					continue;
				}
				if (ticket.getActivityid()==ACTIVE_10YUAN_1) { //10+1优惠价
					if (order.getDaynumber()==1&&(order.getOrderMethod()==IOS_APP||order.getOrderMethod()==ANDROID_APP)) { //单日
						//判断15元券是否有效
						int  flag = 0;
						for (int j = 0; j < userTicketInfosAll.size(); j++) {
							if (userTicketInfosAll.get(j).getActivityid()==ACTIVE_15YUAN) {
								if (userTicketInfosAll.get(j).getCheck()) {
									flag = 1;//说明有15券而且还有效
									break;
								}
							}
						}
						if(flag==0){//15元没有 或者 无效
							List<UTicket> ticketList = this.ticketMapper.findUTicketByMidAndActiveidReturnUTicket(mid,ACTIVE_15YUAN);
								 if (CollectionUtils.isNotEmpty(ticketList)) {//说明已经用过了
									    OtaOrder otaOrder = orderService.findOtaOrderById(ticketList.get(0).getOtaorderid());
									    boolean orderflag = otaOrder!=null&&otaOrder.getHotelId()!=null&&(otaOrder.getOrderStatus() == OtaOrderStatusEnum.Confirm.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.CheckInOnline.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.CheckIn.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.Account.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.CheckOut.getId().intValue());
									    if(orderflag){
									    		 if (otaOrder.getHotelId().longValue()==order.getHotelId().longValue()) {//15元券入住的酒店和要预定的酒店ID相同
														ticketInfos.get(i).setCheck(false);
														break ; 
													}else {
														userTicketInfos.add(ticket);
														ticketInfos.remove(i);
														break;
													}
									    	
										    }else {
										    	userTicketInfos.add(ticket);
												ticketInfos.remove(i);
												break;
											}
									    
								}else {//没有15元券
									userTicketInfos.add(ticket);
									ticketInfos.remove(i);
									break;
								}
						}else if(flag==1){//说明有15券而且还有效
							userTicketInfos.add(ticket);
							ticketInfos.remove(i);
							continue;
						}
					}else { //不是单日(不能使用10+1券)
						ticketInfos.get(i).setCheck(false);
						continue ; 
					}
				}else if(ticket.getActivityid()==ACTIVE_15YUAN) { //15元券
					//判断有没有10+1券
					int  flag = 0;//没有10+1
					for (int j = 0; j < userTicketInfosAll.size(); j++) {
						if (userTicketInfosAll.get(j).getActivityid()==ACTIVE_10YUAN_1) {
							if (userTicketInfosAll.get(j).getCheck()) {
								flag = 1;//说明有10+1券而且还有效
								break;
							}
						}
					}
					
					//当天预定而且是单日预定
					boolean ticket15Bl = df.format(order.getBeginTime()).equals(df.format(new Date())) && (order.getDaynumber()==1) && (order.getOrderMethod()==IOS_APP||order.getOrderMethod()==ANDROID_APP);
				     if (ticket15Bl) {
				    	 if (flag==0) {//没有10+1
				    		 List<UTicket> ticketList = this.ticketMapper.findUTicketByMidAndActiveidReturnUTicket(mid,ACTIVE_10YUAN_1);
				    		 if (CollectionUtils.isNotEmpty(ticketList)) {//说明10+1 已经用过了
				    			 OtaOrder otaOrder = orderService.findOtaOrderById(ticketList.get(0).getOtaorderid());
				    			 boolean orderflag = otaOrder!=null&&otaOrder.getHotelId()!=null&&(otaOrder.getOrderStatus() == OtaOrderStatusEnum.Confirm.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.CheckInOnline.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.CheckIn.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.Account.getId().intValue()||otaOrder.getOrderStatus() == OtaOrderStatusEnum.CheckOut.getId().intValue());
				    			 if(orderflag){
				    					 if (otaOrder.getHotelId().longValue()==order.getHotelId().longValue()) {//10+1元券入住的酒店和要预定的酒店ID相同
				    						 ticketInfos.get(i).setCheck(false);
				    						 break ; 
				    					 }else {
										    	userTicketInfos.add(ticket);
												ticketInfos.remove(i);
												break;
											}
				    			 }else {
				    				    userTicketInfos.add(ticket);
										ticketInfos.remove(i);
										break;
								}
				    			 
				    		 }else { // 说明没有 10+1
				    			    userTicketInfos.add(ticket);
									ticketInfos.remove(i);
									break;
							}
						}else if (flag==1){
							userTicketInfos.add(ticket);
							ticketInfos.remove(i);
							continue;
							}
				     }else {
				    	 ticketInfos.get(i).setCheck(false);
					     continue ; 
					}
				}else if(ticket.getActivityid()==ACTIVE_100YUAN){
					if (ticket.getSubprice().compareTo(BigDecimal.valueOf(100L)) == 0 ) {
						if (order.getPrice().compareTo(BigDecimal.valueOf(150L)) < 0) { //房间中没有房价超过150的，设置优惠券不可用
							ticketInfos.get(i).setCheck(false);
						}
					}
				}else if(ticket.getActivityid()==ACTIVE_QIXI){
					if (ticket.getSubprice().compareTo(BigDecimal.valueOf(77L)) == 0 ) { //如果是77元七夕特价券
						if (order.getPrice().compareTo(BigDecimal.valueOf(150L)) < 0) { //房间中没有房价超过150的，设置优惠券不可用
							ticketInfos.get(i).setCheck(false);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 根据活动排序优惠券
	 * 此方法目前针对10+1活动和15元活动
	 */
	public  void sortTicketByActivety(List<TicketInfo> ticketInfos , OtaOrder order,long mid){
		if (CollectionUtils.isEmpty(ticketInfos)) {
			return;
		}
		List<TicketInfo> userTicketInfos = Lists.newArrayList();
		List<TicketInfo> userTicketInfosAll = Lists.newArrayList();
		userTicketInfosAll.addAll(ticketInfos);
		fillActiveList(ticketInfos, userTicketInfos, userTicketInfosAll, order, mid);
		
		Collections.sort(ticketInfos, new Comparator<TicketInfo>() {
			@Override
			public int compare(TicketInfo param1, TicketInfo param2) {
				// 可用的优惠券优先
				if (param1.getCheck() != param2.getCheck()) {
					return param2.getCheck().compareTo(param1.getCheck());
				}
				// 优惠券结束日期较早的优先 modify by tankai 逻辑修改为 按照线上优惠额倒序排序
				//return param1.getEndtime().compareTo(param2.getEndtime());
				
				//优惠券信息，按照线上优惠额倒序排序
				return param2.getSubprice().compareTo(param1.getSubprice());
			}
		});
		userTicketInfosAll.clear();
		if (CollectionUtils.isNotEmpty(userTicketInfos)&&userTicketInfos.size()>=2) {
			for (int j = userTicketInfos.size()-1; j>=0; j--) {
				if (userTicketInfos.get(j).getActivityid()==ACTIVE_10YUAN_1) {
					userTicketInfosAll.add(userTicketInfos.get(j));
					userTicketInfos.remove(j);
				}
			}
		}
		
		userTicketInfosAll.addAll(userTicketInfos);
		userTicketInfosAll.addAll(ticketInfos);
		ticketInfos.clear();
		ticketInfos.addAll(userTicketInfosAll);
	}

	/**
	 * 设置默认选中的优惠券
	 *
	 * @param ticketInfos
	 */
	public void defaultTicketSelect(List<TicketInfo> ticketInfos) {
		// 判断第一张是否可用
		if ((ticketInfos == null) || (ticketInfos.size() < 1)) {
			return;
		}
		TicketInfo ticketInfo = ticketInfos.get(0);
		if (ticketInfo.getCheck()) {
			ticketInfo.setSelect(true);
		}
	}
	
	/**
	 * 转换信息
	 * 将优惠券信息转换为前端识别的信息
	 * @param parse
	 * @return
	 */
	private TicketInfo promotion2tickinfo(ITicketParse parse){
		TicketInfo info = new TicketInfo();
		BPromotion bp = parse.getPromotion();
		info.setId(bp.getId());
		info.setName(bp.getName());
		info.setDescription(bp.getDescription());
		info.setSelect(false);
		info.setCheck(parse.checkUsable());
		if(parse.getTicket()!=null){
			if(parse.getTicket().getStatus()==TicketStatusEnum.unused.getId().intValue()){
				//只有unused时，才是真正的未使用，其他的状态，都认为使用了。
				info.setIsused(false);
			}else{
				info.setIsused(true);
			}
			info.setStatus(parse.getTicket().getStatus());
			
			//封装不同的状态值
			
			if(parse.getTicket().getStatus() == TicketStatusEnum.used.getId().intValue()){
				info.setStatus(TicketStatusEnum.used.getId());
				info.setStatusname(TicketStatusEnum.used.getName());
			}else if(parse.getTicket().getStatus() == TicketStatusEnum.invalid.getId().intValue()){
				info.setStatus(TicketStatusEnum.invalid.getId());
				info.setStatusname(TicketStatusEnum.invalid.getName());
			}else if(parse.getTicket().getStatus() == TicketStatusEnum.unused.getId().intValue()){
				//check是否过期
				if(!checkUsable(bp.getBegintime(), bp.getEndtime())){//过期的单据
					info.setStatus(TicketStatusEnum.overdue.getId());
					info.setStatusname(TicketStatusEnum.overdue.getName());
					//如果已经过期，置为不可用
					info.setIsused(true);
				}else{//多少天失效
					int diff = DateUtils.diffDay(new Date(), bp.getEndtime());
					info.setStatus(TicketStatusEnum.unused.getId());
					if(diff>0){
						info.setStatusname(MessageFormat.format(TicketStatusEnum.limit.getName(),diff ));
					}else{
						info.setStatusname("今天过期");
					}
				}
			}
			logger.info("优惠券id："+bp.getId()+"状态："+info.getStatusname());
			
		}
		info.setSubprice(parse.getOnlinePrice());
		info.setOfflinePrice(parse.getOfflinePrice());
		info.setOfflinesubprice(parse.getOfflinePrice());//接口要用这个字段，所以要封装
        info.setType(bp.getType().getId());
        info.setIsticket(bp.getIsticket());
		info.setBegintime(bp.getBegintime());
		info.setEndtime(bp.getEndtime());
		//add by zyj 20150702
		info.setActivityid(bp.getActivitiesid());
		if(info.getSubprice()!=null && info.getSubprice().compareTo(BigDecimal.ZERO)>0 && info.getOfflineprice()!=null && info.getOfflineprice().compareTo(BigDecimal.ZERO)>0){
			info.setUselimit(TicketUselimitEnum.ALL.getType());
		}else if(info.getSubprice()!=null && info.getSubprice().compareTo(BigDecimal.ZERO)>0){
			info.setUselimit(TicketUselimitEnum.YF.getType());
		}else if(info.getOfflineprice()!=null && info.getOfflineprice().compareTo(BigDecimal.ZERO)>0){
			info.setUselimit(TicketUselimitEnum.PT.getType());
		}
		
		return info; 
	}
	
	private boolean checkUsable(Date begintime,Date endtime) {
		Date currentDate = new Date();
		return  begintime.before(currentDate) 
				&& endtime.after(currentDate);
	}

	public  Map<String, Object> findMaxAndMinUTicketId(Map<String, Object> paramMap){
		return ticketMapper.findMaxAndMinUTicketId(paramMap);
	}
	    
    public  List<UTicket> findUTicketList(Map<String, Object> paramMap){
    	return ticketMapper.findUTicketList(paramMap);
    }
	
    @Override
    public long getHandGetPromotionCount(Long mid) {
    	return this.ticketMapper.getHandGetPromotionCount(mid);
    }
    
    @Override
    public List<TicketInfo> getNotActivePromotionCount(Long mid) {
    	List<TicketInfo> rtnList = Lists.newArrayList();
    	
    	List<UTicket> uList = this.ticketMapper.getNotActivePromotions(mid);
    	for(UTicket ut : uList){
			if(ut!=null && ut.getPromotion()!=null && !ut.getPromotion().getIsinstance()){
				rtnList.add(promotion2tickinfo(ut.createParseBean(null)));
			}
		}
		return rtnList;
    }
    
    @Override
    public boolean activatePromotion(Long mid, Long promotionid) {
    	return this.ticketMapper.activatePromotion(mid, promotionid);
    }
    
    
    /**
     * 绑定优惠券
     * @param order
     * @param member
     * @param log
     */
    public void  saveBindPromotion(OtaOrder order, UMember member, OrderLog log){
    	if(order.getActiveid()!=null
                &&order.getActiveid()!=Constant.ACTIVE_10YUAN_1
                &&order.getActiveid()!=Constant.ACTIVE_15YUAN){ //if activeid is not null bind activepromotion
            this.bindActivePromotion(order,log);
        }else{
        	this.bindUserPromotion(order, member,log);
        }
    }
    
    /**
	 * 绑定活动券
	 */
	private void bindActivePromotion(OtaOrder order, OrderLog log) {

		String first = DateUtils.getStringFromDate(order.getBeginTime(), DateUtils.FORMAT_DATE);
		String second = DateUtils.getStringFromDate(order.getCreateTime(), DateUtils.FORMAT_DATE);
		int diff = DateUtils.selectDateDiff(first, second);
		// 根据活动id获取优惠券
		List<BPromotion> proList = iPromoService.findByActiveId(order.getActiveid());
		for (BPromotion bPromotion : proList) {

			if (bPromotion.getNum() == -1) {
				// 根据优惠券获取规则
				// b_promotion_rule
				BPromotionRule bPromotionRule = ibPromotionRuleDAO.findRuleByPromotionId(bPromotion.getId());
				if (bPromotionRule != null) {
					if ("10001".equals(bPromotionRule.getRulecode())) {
						if (String.valueOf(diff).equals(bPromotionRule.getRuleformula())) {
							// 使用该优惠券
							logger.info("绑定活动券 promotionid:{}", bPromotion.getId());
							ITicketParse parse = bPromotion.createParseBean(order);

							logger.info("检测活动券是否可用:{}", parse.checkUsable());
							if (parse.checkUsable()) {
								BigDecimal onlinePrice = BigDecimal.ZERO;
								BigDecimal offlinePrice = BigDecimal.ZERO;
								if (OrderTypeEnum.YF.getId().intValue() == order.getOrderType()) {
									if (bPromotion.getOnlineprice() != null) {
										onlinePrice = bPromotion.getOnlineprice();
									}
								} else if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
									if (bPromotion.getOfflineprice() != null) {
										offlinePrice = bPromotion.getOfflineprice();
									}
								}

								BPromotionPrice bp = new BPromotionPrice();
								bp.setOfflineprice(offlinePrice);
								bp.setPrice(onlinePrice);
								bp.setPromotion(bPromotion.getId());
								bp.setOtaorderid(order.getId());
								logger.info("绑定活动券. orderid:{}, promotion:{}", order.getId(), bp);
								this.iBPromotionPriceDao.saveOrUpdate(bp);
								logger.info("绑定活动券成功. orderid:{}, promotion:{}", order.getId(), bp);

								// 优惠券绑定时，增加保存订单上优惠券使用状态，是否使用了优惠券。
								saveOrderTicketStatus(order.getId(), "coupon");
								log.set("promotion", "T").saveOrUpdate();
								break;
							}

						}
					}
				}
			}
		}
		// 根据规则id和时间差获取最终的优惠券

	}
	
	
	
	/**
	 * 绑定用户券
	 * 
     * @param order
     */
	private void bindUserPromotion(OtaOrder order, UMember member, OrderLog log) {
		// add 类型判断 10元活动 绑定优惠券过程
		// add 类型判断 15元活动
		//当规则码为空时，走默认逻辑（上海逻辑）
		if (order.getSpreadUser() == null) { // 预付且spreaduser为空
			logger.info("1.查询我的可用优惠券.........");
			List<TicketInfo> myticket = this.queryMyTicket(
					order.getMid(), false);
//			this.checkBRule(myticket, order);
			sortAndBindTicket(order, myticket, member, log);
		}

	}
    
	
    /**
     * 根据给定的优惠券进行排序与绑定
     * @param order
     * @param myticket
     * @param member
     * @param log
     */
    private void sortAndBindTicket(OtaOrder order,List<TicketInfo> myticket,UMember member,OrderLog log){
			logger.info("2.排序我的可用优惠券(将要到期的优惠券排前).........");
			// ticketService.sortTicketInfos(myticket);
           this.sortTicketByActivety(myticket,order,order.getMid());
			logger.info("3.绑定第一张优惠券.........");
			logger.info("所有可用券: {}", myticket);
			if (myticket != null && myticket.size() > 0) {
				for (TicketInfo tmpTf : myticket) {
					if (tmpTf.getIsticket() && tmpTf.getCheck()) {
						// 普通优惠券缺省绑定第一张券
						logger.info("缺省绑定第一张普通券 promotionid:{}", tmpTf.getId());
						IPromotionPriceService ip = AppUtils.getBean(IPromotionPriceService.class);
						ip.bindPromotionPrice(Lists.newArrayList(tmpTf.getId()), member, order);
						log.set("promotion", "T").saveOrUpdate();
						// 优惠券绑定时，增加保存订单上优惠券使用状态，是否使用了优惠券。
						saveOrderTicketStatus(order.getId(), "coupon");
						break;
					}
				}
			}
		}

	

    /**
     * 保存订单上的优惠券状态
     * T or F
     * 增加该方法原因：在创建和修改订单时，订单先于优惠券保存，导致订单上的优惠券状态不一致
     */
	public void saveOrderTicketStatus(Long orderId, String ticketName) {
		if (orderId != null) {
			OtaOrder newOrder = new OtaOrder(orderId, ticketName, "T");
			newOrder.saveOrUpdate();
		}
	}

    @Override
    public void saveOrUpdateHotelStat(OtaOrder otaOrder ,PmsRoomOrder pmsRoomOrder) {
        logger.info("开始插入用户{}入住酒店{}流水，otaorderid：{}", otaOrder.getMid(), otaOrder.getHotelId(), otaOrder.getId());

        // 检查参数是否合法
        if (null == otaOrder.getId() || null == otaOrder.getMid() || null == otaOrder.getHotelId()) {
            logger.info("参数无效，无法继续处理");
            return;
        }

        // 判断订单是否存在酒店流水，以及用户是否已经推送券而未激活
        try {

            BHotelStat bHotelStat = bHotelStatDao.getBHotelStatByOtaorderid(otaOrder.getId());
            if (null == bHotelStat) {
                bHotelStat = new BHotelStat();
                bHotelStat.setMid(otaOrder.getMid());
                bHotelStat.setHotelId(otaOrder.getHotelId());
                bHotelStat.setOtaOrderId(otaOrder.getId());
                bHotelStat.setRoomTypeId(pmsRoomOrder.getLong("RoomTypeId")); 
                bHotelStat.setRoomNo(pmsRoomOrder.getStr("Roomno"));
                bHotelStat.setRoomTypeName(pmsRoomOrder.getStr("RoomTypeName"));
                bHotelStat.setCreateTime(pmsRoomOrder.getDate("Endtime"));
                
                // 查询用户是否有未激活的此活动优惠券
                UTicket uTicket = ticketMapper.findUnactiveUTicket(otaOrder.getMid(),
                        Constant.LIVE_THREE_ACTIVE, TicketStatusEnum.used.getId());
                if (null == uTicket) {
                    bHotelStat.setStatisticInvalid(StatisticInvalidTypeEnum.statisticValid.getType());
                } else {
                    bHotelStat.setStatisticInvalid(StatisticInvalidTypeEnum.statisticInvalid.getType());
                }

                bHotelStatDao.saveOrUpdate(bHotelStat);
                logger.info("用户{}入住酒店{}流水插入成功，otaorderid：{}", otaOrder.getMid(), otaOrder.getHotelId(), otaOrder.getId());
            } else {
                logger.info("用户{}入住酒店{}流水已存在，otaorderid：{}", otaOrder.getMid(), otaOrder.getHotelId(), otaOrder.getId());
            }

        } catch (Exception e) {
            logger.error("用户{" + otaOrder.get("mid") + "}入住酒店{" + otaOrder.get("hotelid") + "}流水操作数据库异常，otaorderid：{" + otaOrder.getId() + "}", e);
        }

    }
    private long getTimeFlag (){
    	long timeFlag = 0;
    	Properties pps = new Properties();
        try {
            InputStream is=this.getClass().getResourceAsStream("/prize.properties"); 
             pps.load(is);
             is.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                throw MyErrorEnum.customError.getMyException("解析资源文件时出错");
            }  
          try {
        	  timeFlag = Long.parseLong(pps.getProperty("prize.prize_time_flag").trim());
             } catch (Exception e) {
                // TODO: handle exception
                 e.printStackTrace();
                throw MyErrorEnum.customError.getMyException("资源文件属性prize.prize_time_flag值不是整数");
              }
        if (timeFlag<0) {
              throw MyErrorEnum.customError.getMyException("资源文件属性prize.prize_time_flag值不是正整数");
         }
        return timeFlag;
        
    }
    public boolean checkUserLuckyDraw(long mid, long activeid,String ostype){
		 logger.info("下面判断该用户是否用抽奖的机会，传来的参数：mid:{},activeid:{},ostype:{}",mid ,activeid,ostype);
		 //获取资源文件中的属性值
		 long timeFlag = getTimeFlag();
	      String date = null; 
         if (timeFlag == 0) {date = DateUtils.getDate();}

		 //根据几个条件判断该用户在该活动中是否有抽奖机会
		 List<String> ostypes =new ArrayList<String>();
		if (OSTypeEnum.H.getId().equals(ostype)) {
			ostypes.add(OSTypeEnum.IOS.getId());
			ostypes.add(OSTypeEnum.ANDROID.getId());
			ostypes.add(OSTypeEnum.WX.getId());
			long prizeRecordCount = iuPrizeRecordService.selectCountByMidAndActiveIdAndOstypeAndTime(mid, activeid, ostypes, date);
			if (prizeRecordCount >= 1){
				logger.info("1：来自第三方不可以抽奖， 在 mike 平台参与过抽奖，prizeRecordCount：{}",prizeRecordCount);
				throw MyErrorEnum.customError.getMyException(Constant.ACTIVE_NOTE);
			}
		}else if (OSTypeEnum.WX.getId().equals(ostype)){
			ostypes.add(OSTypeEnum.H.getId());
			long prizeRecordCount = iuPrizeRecordService.selectCountByMidAndActiveIdAndOstypeAndTime(mid, activeid, ostypes, date);
			if (prizeRecordCount >= 1){
				logger.info("1：微信不可以抽奖，已经在来自第三方平台平台参与过抽奖，prizeRecordCount：{}",prizeRecordCount);
				throw MyErrorEnum.customError.getMyException(Constant.ACTIVE_NOTE);
			}
		}

		ostypes =new ArrayList<String>();
		 if (OSTypeEnum.IOS.getId().equals(ostype)||OSTypeEnum.ANDROID.getId().equals(ostype)) {
			 ostypes.add(OSTypeEnum.IOS.getId());
			 ostypes.add(OSTypeEnum.ANDROID.getId());
		}else {
			ostypes.add(ostype);
         }
		 
		long prizeRecordCount = iuPrizeRecordService.selectCountByMidAndActiveIdAndOstypeAndTime(mid, activeid, ostypes, date);
        if (prizeRecordCount == 0) {//没抽过奖，可以抽奖
			logger.info("2：可以抽奖，prizeRecordCount：{}",prizeRecordCount);
			 return true;
		}else {
			logger.info("4：不可以抽奖，prizeRecordCount：{}",prizeRecordCount);
			throw MyErrorEnum.customError.getMyException(Constant.ACTIVE_NOTE);
		}
		/*else if (prizeRecordCount == 1) {
			//判断该活动是否分享过，如果分享过，该抽奖方式(app或微信)还可以再抽奖一次，没分享过则不可以再抽奖了
		    long shareCount = iuActiveShareService.countNumByMidAndActiveIdAndTime(mid, activeid,date);
		    if (shareCount == 0) { //没有分享过，不允许抽奖
				logger.info("3：不可以抽奖，shareCount：{}",shareCount);
				throw MyErrorEnum.customError.getMyException("分享后才能再次抽奖");
			}else if (shareCount >= 1) { //分享过,可以抽奖
				logger.info("4：可以抽奖，shareCount：{}",shareCount);
				return true;
			}
		}else if (prizeRecordCount >= 2) { //app或微信单个抽奖设备方式抽奖次数已经等于或超过了最大手机次数所以不能再次抽奖了
			logger.info("4：不可以抽奖，prizeRecordCount：{}",prizeRecordCount);
			throw MyErrorEnum.customError.getMyException("今天的抽奖机会已经用光啦，请明日再来。");
		}*/

		
	 }
    
    public List<BPrizeInfo> queryMyHistoryPrize(long mid ,long activeid){
    	List<BPrizeInfo>  bPrizeInfoList =  new ArrayList<BPrizeInfo>();	
    	
    	//查询不是括眯客券
    	List<UPrizeRecord> uPrizeRecordNotMikeList = iuPrizeRecordService.queryMyHistoryIsOrNotMiKePrize(mid, activeid, PrizeTypeEnum.mike.getId().intValue(), false);
        if (CollectionUtils.isNotEmpty(uPrizeRecordNotMikeList)) {
            logger.info("获取优惠券集合个数uPrizeRecordNotMikeList.size:{}",uPrizeRecordNotMikeList.size());
    		int i = 0;
			for (UPrizeRecord ur:uPrizeRecordNotMikeList) {
				logger.info("第"+(++i)+"个优惠券内容：{}",ur);
				//if (PrizeTypeEnum.mike.getId().intValue()!=ur.getPrizetype().longValue()) {
					BPrizeInfo bPrizeInfo = new BPrizeInfo();
					if (ur.getCreatetime()!=null) {
						bPrizeInfo.setCreatetime(DateUtils.formatDateTime(ur.getCreatetime()));
					}else{
						bPrizeInfo.setCreatetime("");
					}
					bPrizeInfo.setName(ur.getbPrize().getName());
					bPrizeInfo.setType(ur.getbPrize().getType());
					bPrizeInfo.setPrice(ur.getbPrize().getPrice());
					bPrizeInfo.setCode(ur.getPrizeinfo());
					if (ur.getbPrize().getBegintime()!=null) {
						bPrizeInfo.setBegintime(DateUtils.formatDateTime(ur.getbPrize().getBegintime()));
					}else{
						bPrizeInfo.setBegintime("");
					}
					if (ur.getbPrize().getEndtime()!=null) {
						bPrizeInfo.setEndtime(DateUtils.formatDateTime(ur.getbPrize().getEndtime()));
					}else{
						bPrizeInfo.setEndtime("");
					}
					
					bPrizeInfoList.add(bPrizeInfo);
				//}
			}
		}
    	
    	//查询是括眯客券
    	List<UPrizeRecord> uPrizeRecordMikeList = iuPrizeRecordService.queryMyHistoryIsOrNotMiKePrize(mid,activeid,PrizeTypeEnum.mike.getId().intValue(),true);
    	if (CollectionUtils.isNotEmpty(uPrizeRecordMikeList)) {
    		logger.info("获取优惠券集合个数uPrizeRecordMikeList.size:{}",uPrizeRecordMikeList.size());
    		int i = 0;
			for (UPrizeRecord mike:uPrizeRecordMikeList) {
				logger.info("第"+(++i)+"个优惠券内容：{}",mike);
				//if (PrizeTypeEnum.mike.getId().intValue()!=ur.getPrizetype().longValue()) {
					BPrizeInfo bPrizeInfo = new BPrizeInfo();
					if (mike.getCreatetime()!=null) {
						bPrizeInfo.setCreatetime(DateUtils.formatDateTime(mike.getCreatetime()));
					}else{
						bPrizeInfo.setCreatetime("");
					}
					bPrizeInfo.setName(mike.getbPromotion().getName());
					bPrizeInfo.setType(mike.getPrizetype().intValue());
					bPrizeInfo.setPrice(mike.getbPromotion().getOnlineprice().longValue());
					bPrizeInfo.setCode("");
					if (mike.getbPromotion().getBegintime()!=null) {
						bPrizeInfo.setBegintime(DateUtils.formatDateTime(mike.getbPromotion().getBegintime()));
					}else{
						bPrizeInfo.setBegintime("");
					}
					if (mike.getbPromotion().getEndtime()!=null) {
						bPrizeInfo.setEndtime(DateUtils.formatDateTime(mike.getbPromotion().getEndtime()));
					}else{
						bPrizeInfo.setEndtime("");
					}
					
					bPrizeInfoList.add(bPrizeInfo);
				//}
			}
		}
    	return bPrizeInfoList;
    }
    @Override
    public List<Long> getCountValid(int statisticInvalid, int liveHotelNum, int batDataNum) {
        return bHotelStatDao.getCountValid(statisticInvalid, liveHotelNum, batDataNum);
    }

    @Override
    public void updateInvalidByMid(Long mid) {
        bHotelStatDao.updateInvalidByMid(mid);
    }


	@Override
	public List<USendUticket> getNeedSendCountValid(int statisticInvalid, int batDataNum) {
		return uSendUTicketDao.getNeedSendCountValid(statisticInvalid, batDataNum);
	}


	@Override
	public void updateSendTicketInvalidByMid(Long mid) {
		uSendUTicketDao.updateSendTicketInvalidByMid(mid);
	}
	
	/**
	 * 根据订单查询订单上使用的优惠券、酒店补贴、议价券等信息
	 * @param order
	 * @return
	 */
	public CouponParam queryCouponParam(OtaOrder order){
		CouponParam couponParam = new CouponParam();
		BigDecimal totalPrice = caculateAllCost(order);
		couponParam.setUserCost(totalPrice);
		
		if(order==null||order.getId()==null){
			logger.info("订单入参为空，返回");
			return couponParam;
		}
		if(order.getTotalPrice()==null){
			logger.info("订单总金额TotalPrice为空,orderid="+order.getId());
			return couponParam;
		}
		logger.info("根据订单查询订单上使用的优惠券、酒店补贴、议价券等信息,orderid="+order.getId()+"，订单总金额："+couponParam.getUserCost());
		//查询切客券
		
		//查询议价券
		
		//查询普通优惠券
		List<BPromotion> alreadyBindList = iBPromotionDao.queryBPromotionByOrderId(order.getId());
		if(CollectionUtils.isNotEmpty(alreadyBindList)){
			logger.info("订单绑定优惠券list："+alreadyBindList.size()+",orderid="+order.getId());
			for (BPromotion bPromotion : alreadyBindList) {
				if(bPromotion!=null){
					BigDecimal price = null;
					if (order.getOrderType() == OrderTypeEnum.PT.getId()){
						price = bPromotion.getOfflineprice();
						logger.info("到付订单,orderid="+order.getId());
		        	} else if(order.getOrderType() == OrderTypeEnum.YF.getId()) {
		        		price = bPromotion.getOnlineprice();
		        		logger.info("预付订单,orderid="+order.getId());
		        	}
					
					
					if(!PayTools.isPositive(price)){
						continue;
					}
				
					if(PromotionTypeEnum.yijia.getId().equals(bPromotion.getType())){
						logger.info("议价券，只记录酒店补贴,orderid="+order.getId()+"，酒店补贴金额："+price);
						couponParam.setHotelCost(price);
					}else{
						logger.info("非议价券，记录ota补贴,orderid="+order.getId()+"，ota补贴金额："+price);
						couponParam.setCoupon(price);
						if(bPromotion.getIsota()!=null && bPromotion.getIsota()){
							if (totalPrice.subtract(price).compareTo(BigDecimal.ZERO) > 0) {
								if(bPromotion.getOtapre()!=null){
									couponParam.setUserCost(bPromotion.getOtapre().multiply(price));
								}else{
									couponParam.setUserCost(price);
								}
				            }else{
				            	couponParam.setUserCost(BigDecimal.ZERO);
				            }
							logger.info("ota实际补贴,orderid="+order.getId()+"，金额："+couponParam.getUserCost());
						}
					} 
				}
			}
			
		}
		logger.info("orderid="+order.getId()+",couponParam="+couponParam.toString()+"，订单总金额："+totalPrice);
		return couponParam;
	}
	 /**
     * 计算总花费
     *
     * @param order
     * @param allcost
     * @return
     */
    private BigDecimal caculateAllCost(OtaOrder order) {
        BigDecimal allcost = BigDecimal.ZERO;
        List<OtaRoomOrder> roomOrdrs = order.getRoomOrderList();
        if(CollectionUtils.isNotEmpty(roomOrdrs)){
        	for (OtaRoomOrder otaRoomOrder : roomOrdrs) {
                allcost = allcost.add(otaRoomOrder.getTotalPrice());
            }
        }else{
        	throw MyErrorEnum.customError.getMyException("用户订单没有找到房间");
        }
        
        return allcost;
    }

	public ITicketParse createParseBean(OtaOrder otaOrder,BPromotion bPromotion) {
		try {
			UTicketDao uTicketDao = AppUtils.getBean(UTicketDao.class);
			Object ob = Class.forName(bPromotion.getClassname()).newInstance();
			ITicketParse parse = (ITicketParse) ob;
			if(PromotionTypeEnum.qieke.equals(bPromotion.getType().getId()) || PromotionTypeEnum.yijia.equals(bPromotion.getType().getId())){
				parse.init(null, bPromotion);
			} else {
				parse.init(uTicketDao.findByPromotionId(bPromotion.getId()), bPromotion);
			}
			parse.parse(otaOrder);
			return parse;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *  解析切客券与议价券
	 *
	 * @param otaOrder
	 * @param promotionNoList
	 * @param cousNo
	 * @return
	 */
	public List<ITicketParse> getPromotionParses(OtaOrder otaOrder) {
		// 查询切客券与议价券
		List<BPromotion> promotions = Lists.newArrayList();
		List<ITicketParse> parses = new ArrayList<>();
		if (otaOrder != null) {
			promotions = this.iBPromotionDao.queryYiJiaAndQiKePromotionByOrderId(otaOrder.getId());
			// 解析优惠券码信息
			for (BPromotion promotion : promotions) {
				ITicketParse parse = this.createParseBean(otaOrder,promotion);
				parse.checkUsable();
				parses.add(parse);
			}
		}
		return parses;
	}

	@Override
	public boolean checkReceivePrizeByPhone(String phone, Long activeid,
											String ostype, String date) {
		// TODO Auto-generated method stub
		boolean checkflag = iuPrizeRecordService.checkReceivePrizeByPhone(phone,activeid,ostype,date);
		if (checkflag) {
			throw MyErrorEnum.customError.getMyException("该手机号已经领取过奖品，不能再领取");
		}
		return false;
	}
	@Override
	public void prizeBindingUser(String phone, String prizerecordid) {
		// TODO Auto-generated method stub
		//获取该奖品记录
		UPrizeRecord  recordPrize =  iuPrizeRecordService.findUPrizeRecordById(Long.parseLong(prizerecordid));
		if (recordPrize==null) {
			throw MyErrorEnum.customError.getMyException("奖品流水记录id["+prizerecordid+"]无效");
		}
		if (String.valueOf(ReceiveStateEnum.binding.getId().intValue()).equals(recordPrize.getReceivestate())) {
			throw MyErrorEnum.customError.getMyException("该奖品已经领取");
		}
		//判断该手机号是不是眯客用户
		Optional<UMember> umember = iMemberService.findMemberByPhone(phone);
		if (umember.isPresent()) { //有该用户
			UMember member = umember.get();
			//给该用户绑定奖品
			if (PrizeTypeEnum.mike.getId().intValue() == recordPrize.getPrizetype().longValue()) {
				BActivity bActivity =  ibActivityService.findById(recordPrize.getActiveid());
				if(bActivity == null){
					throw MyErrorEnum.IllegalActive.getMyException();
				}
				List<BPromotion> bPromotions = iPromoService.findByActiveidAndPrizeId(recordPrize.getActiveid(), recordPrize.getPrizeid());
				if (CollectionUtils.isEmpty(bPromotions)) {
					throw MyErrorEnum.errorParm.getMyException("不能找到该奖品对应的优惠券!");
				}else {
					if (bPromotions.size()> 1) {
						throw MyErrorEnum.errorParm.getMyException("该活动下有相同名字的优惠券!");
					}
				}
				List<Long> proId = iPromoService.genCGTicket(bPromotions.get(0).getId(), member.getMid(), bActivity.getBegintime(), bActivity.getEndtime(), bActivity.getPromotionmethodtype(), null, null,"");
				recordPrize.setPrizeinfo(String.valueOf(proId.get(0)));
			}
			recordPrize.setMid(member.getMid());
			recordPrize.setPhone(member.getLoginname());
			recordPrize.setReceivestate(String.valueOf(ReceiveStateEnum.binding.getId()));
			recordPrize.setCreatetime(new Date());
			this.updatePrizeRecordByRecordId(recordPrize);
		}else { //不是眯客用户
			iuPrizeRecordService.updatePhoneByRecordId(Long.parseLong(prizerecordid),phone,ReceiveStateEnum.Geted.getId());
		}

	}
	public void updatePrizeRecordByRecordId(UPrizeRecord prizeRecord) {
		// TODO Auto-generated method stub
		iuPrizeRecordService.updatePrizeRecordByRecordId(prizeRecord);
	}
	@Override
	public List<BPrizeInfo> queryMyNotreceiveyPrize(Long activeid, String usermark) {
		// TODO Auto-generated method stub
		/*通过usermark和手机查询登录用户未领取的奖品，以usermark为主，手机为辅，如果usermark查询到了，就不用phone查询了*/
		List<BPrizeInfo>  bPrizeInfoList =  new ArrayList<BPrizeInfo>();
		//通过手机查询奖品信息
		List<UPrizeRecord> notReceiveyPrize = null;
		if (!Strings.isNullOrEmpty(usermark)) {
			notReceiveyPrize =iuPrizeRecordService.queryMyHistoryPrizeByUserMark(usermark,activeid,ReceiveStateEnum.Unget.getId(),DateUtils.getDate());
		}
		if (CollectionUtils.isNotEmpty(notReceiveyPrize)) {
			logger.info("获取优惠券集合个数notReceiveyPrize.size:{}", notReceiveyPrize.size());
			int i = 0;
			for (UPrizeRecord ur:notReceiveyPrize) {
				logger.info("第"+(++i)+"个优惠券内容：{}",ur);
				BPrizeInfo bPrizeInfo = new BPrizeInfo();
				bPrizeInfo.setId(ur.getId());//记录id
				if (ur.getCreatetime()!=null) {
					bPrizeInfo.setCreatetime(DateUtils.formatDateTime(ur.getCreatetime()));
				}else{
					bPrizeInfo.setCreatetime("");
				}
				bPrizeInfo.setName(ur.getbPrize().getName());
				bPrizeInfo.setType(ur.getbPrize().getType());
				bPrizeInfo.setPrice(ur.getbPrize().getPrice());
				bPrizeInfo.setCode(ur.getPrizeinfo());
				if (ur.getbPrize().getBegintime()!=null) {
					bPrizeInfo.setBegintime(DateUtils.formatDateTime(ur.getbPrize().getBegintime()));
				}else{
					bPrizeInfo.setBegintime("");
				}
				if (ur.getbPrize().getEndtime()!=null) {
					bPrizeInfo.setEndtime(DateUtils.formatDateTime(ur.getbPrize().getEndtime()));
				}else{
					bPrizeInfo.setEndtime("");
				}
				bPrizeInfoList.add(bPrizeInfo);
			}
		}
		return bPrizeInfoList;

	}
	@Override
	public List<TicketInfo> queryMyTicketOnUserMark(BPrizeInfo prizeInfo,
													long activeid) {
		// TODO Auto-generated method stub
		List<TicketInfo> ticketInfos = new ArrayList<TicketInfo>();
		TicketInfo ticketInfo = new TicketInfo();
		List<BPromotion> bPromotionList = iPromoService.findByActiveidAndPrizeRecordId(activeid, prizeInfo.getPrizeRecordId());
		if (CollectionUtils.isEmpty(bPromotionList)) {
			return ticketInfos;
		}
		BPromotion bPromotion = bPromotionList.get(0);
		ticketInfo.setId(prizeInfo.getPrizeRecordId());
		ticketInfo.setName(bPromotion.getName());
		ticketInfo.setBegintime(bPromotion.getBegintime());
		ticketInfo.setEndtime(bPromotion.getEndtime());
		ticketInfo.setOfflinePrice(bPromotion.getOfflineprice());
		ticketInfo.setSubprice(bPromotion.getOnlineprice());
		ticketInfo.setCheck(true);
		ticketInfo.setIsticket(true);
		ticketInfo.setType(PromotionTypeEnum.immReduce.getId());
		if(ticketInfo.getSubprice()!=null && ticketInfo.getSubprice().compareTo(BigDecimal.ZERO)>0 && ticketInfo.getOfflineprice()!=null && ticketInfo.getOfflineprice().compareTo(BigDecimal.ZERO)>0){
			ticketInfo.setUselimit(TicketUselimitEnum.ALL.getType());
		}else if(ticketInfo.getSubprice()!=null && ticketInfo.getSubprice().compareTo(BigDecimal.ZERO)>0){
			ticketInfo.setUselimit(TicketUselimitEnum.YF.getType());
		}else if(ticketInfo.getOfflineprice()!=null && ticketInfo.getOfflineprice().compareTo(BigDecimal.ZERO)>0){
			ticketInfo.setUselimit(TicketUselimitEnum.PT.getType());
		}
		ticketInfo.setCreatetime(DateUtils.getDateFromString(prizeInfo.getCreatetime()));
		ticketInfos.add(ticketInfo);
		return ticketInfos;
	}
	@Override
	public void loginbindinggit(UMember member, Long activeid) {
		// TODO Auto-generated method stub
		//如果要绑定，将第三方平台用户通过手机领取奖品都转换为登录后的该手机的用户
		List<UPrizeRecord>  recordPrizeList =  this.findEffectivePrizeByPhone(member.getLoginname(),activeid,OSTypeEnum.H.getId(),DateUtils.getDate(),ReceiveStateEnum.Geted.getId());
		if (CollectionUtils.isNotEmpty(recordPrizeList)) {
			//眯客券
			List<UPrizeRecord> mikeRecord  = new ArrayList<UPrizeRecord>();
			//不是眯客券
			List<UPrizeRecord> notMikeRecord  = new ArrayList<UPrizeRecord>();
			List<Long> prizeIdList = new ArrayList<Long>();
			for (UPrizeRecord uPrizeRecord : recordPrizeList) {
				if (PrizeTypeEnum.mike.getId().intValue()==uPrizeRecord.getPrizetype().longValue()) {
					prizeIdList.add(uPrizeRecord.getPrizeid());
					mikeRecord.add(uPrizeRecord);
				}else {
					notMikeRecord.add(uPrizeRecord);
				}
			}
			if (CollectionUtils.isNotEmpty(prizeIdList)) {
				BActivity bActivity =  ibActivityService.findById(activeid);
				if(bActivity == null){
					throw MyErrorEnum.IllegalActive.getMyException();
				}
				List<BPromotion> bPromotions = iPromoService.findByActiveidAndPrizeIdList(activeid, prizeIdList);
				//获取对应的眯客券
				for (int i = 0; i < bPromotions.size(); i++) {
					BPromotion bp = bPromotions.get(i);
					List<Long> proId = iPromoService.genCGTicket(bp.getId(), member.getMid(), bActivity.getBegintime(), bActivity.getEndtime(), bActivity.getPromotionmethodtype(), null, null,"");
					UPrizeRecord  mike = mikeRecord.get(i);
					//更新奖品记录表
					mike.setMid(member.getMid());
					mike.setPrizeinfo(String.valueOf(proId.get(0)));
					mike.setPhone(member.getLoginname());
					mike.setReceivestate(String.valueOf(ReceiveStateEnum.binding.getId()));
					mike.setCreatetime(new Date());
					this.updatePrizeRecordByRecordId(mike);
				}
			}
			if (CollectionUtils.isNotEmpty(notMikeRecord)){
				//更新第三方奖品
				for (UPrizeRecord notmike : notMikeRecord) {
					notmike.setMid(member.getMid());
					notmike.setReceivestate(String.valueOf(ReceiveStateEnum.binding.getId()));
					notmike.setPhone(member.getLoginname());
					notmike.setCreatetime(new Date());
					this.updatePrizeRecordByRecordId(notmike);
				}
			}
		}
	}
	@Override
	public List<UPrizeRecord>findEffectivePrizeByPhone(String phone,Long activeid,String ostype,String date,Integer geted){
		// TODO Auto-generated method stub
		return iuPrizeRecordService.findEffectivePrizeByPhone(phone,activeid,ostype,DateUtils.getDate(),geted);
	}

}
