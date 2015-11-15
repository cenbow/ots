package com.mk.ots.ticket.controller;

import cn.com.winhoo.mikeweb.myenum.OrderTypeEnum;
import com.dianping.cat.Cat;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.ValidateUtils;
import com.mk.ots.activity.dao.impl.BActivityDao;
import com.mk.ots.activity.model.BActiveChannel;
import com.mk.ots.activity.model.BActivity;
import com.mk.ots.activity.service.IBActiveCDKeyService;
import com.mk.ots.activity.service.IBActiveChannelService;
import com.mk.ots.activity.service.IBActivityService;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PrizeTypeEnum;
import com.mk.ots.common.enums.PromotionMethodTypeEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.manager.RedisCacheName;
import com.mk.ots.member.model.UMember;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.promo.service.IPromotionPriceService;
import com.mk.ots.ticket.model.BPrizeInfo;
import com.mk.ots.ticket.model.TicketInfo;
import com.mk.ots.ticket.service.*;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * 优惠券业务接口
 * 1. 查询订单及我的优惠券列表
 * 2. 领取优惠券
 * @author nolan.zhang
 *
 */
@Controller
@RequestMapping(value="/ticket", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketController {
	final Logger logger = LoggerFactory.getLogger(TicketController.class);
	
	@Autowired
	private ITicketService ticketService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private IPromoService iPromoService;
	
	@Autowired
	private IBActivityService ibActivityService;
	
	@Autowired
	private ITicketService  iTicketService;
	
	@Autowired
	private IMessageService iMessageService;
	
	@Autowired
	private IUActiveShareService iUActiveShareService;
	
	@Autowired
	private IUGiftRecordService iUGiftRecordService;
	
	@Autowired
	private IBActiveChannelService iBActiveChannelService;
	
	@Autowired
	private IBActiveCDKeyService iBActiveCDKeyService;
	@Autowired
	private IPromotionPriceService promotionPriceService;
	@Autowired
	private IBPrizeStockService ibPrizeStockService;
	@Autowired
	private IBPrizeService ibPrizeService;
	@Autowired
	private BActivityDao bActivityDao;
	
	/**
	 * 查询优惠券接口
	 * @param token 
	 * @param orderid 非必填
	 * @param hotelid 酒店id	非必填
	 * @param roomtypeid 房型id	非必填
	 * @param roomid 所选房间id	非必填
	 * @param ordertype 预付类型	非必填 预付 到付"
	 * @param tickettype 优惠券类型	"切客优惠券 议价优惠券 普通优惠券"
	 * @param uselimit 使用限制	1.线上  2.线下  3.空－全部
	 * @param startdateday 查询开始日期	非必填yyyyMMdd
	 * @param enddateday 查询结束日期	非必填yyyyMMdd
	 * @param begintime 开始时间	非必填，14位
	 * @param endtime 结束时间	非必填，14位
	 * @return
	 */
	@RequestMapping("/querylist")
	public ResponseEntity<Map<String,Object>> selectTicket(String token, String orderid, String hotelid, String roomtypeid,
			String roomid, String ordertype,String tickettype, String uselimit, String startdateday, String enddateday, String begintime, String endtime){
		UMember memberByToken = MyTokenUtils.getMemberByToken(token);
		//1. 参数验证
		OtaOrder otaOrder = null;
		if(!Strings.isNullOrEmpty(orderid)){
			otaOrder = orderService.findOtaOrderById(Long.parseLong(orderid));
			if(otaOrder == null){
				throw MyErrorEnum.findOrder.getMyException();
			}
		}
		if(!Strings.isNullOrEmpty(ordertype)){
			OrderTypeEnum orderTypeEnum = OrderTypeEnum.getByID(Integer.parseInt(ordertype));
			if(orderTypeEnum == null){
				throw MyErrorEnum.notExistOrderType.getMyException();
			}
		}
		
		//2.查询订单优惠券及用户优惠券
		List<TicketInfo> tickinfoList = Lists.newArrayList();
		
		if(otaOrder!=null){
			//查询某一订单已绑定券及可用券列表
			List<TicketInfo> bindList = this.ticketService.getBindOrderAndAvailableTicketInfos(otaOrder, memberByToken.getMid());
			if(bindList!=null && bindList.size()>0){
				tickinfoList.addAll(bindList);
			}
		}else{
			//查询我的优惠券列表
			List<TicketInfo> qikeList = this.ticketService.getHistoryOrderQieKeTicketInfosByMid(memberByToken.getMid(), null);
			if(qikeList!=null && qikeList.size()>0){
				tickinfoList.addAll(qikeList);
			}
			logger.info("查询用户历史订单切客券列表. qikelist:{}", qikeList.toString());
			
			List<TicketInfo> myticketList = ticketService.queryMyTicket(memberByToken.getMid(), null);
			if(myticketList!=null && myticketList.size()>0){
				tickinfoList.addAll(myticketList);
			}
			logger.info("查询我的优惠券. myticketlist:{}", myticketList.toString());
		}
		
		//3.优惠券过滤及排序处理
		int checkcount = 0;
		//3.1 过滤处理
		if(!Strings.isNullOrEmpty(uselimit)){
			List<TicketInfo> filterList = Lists.newArrayList();
			for(TicketInfo tmpti: tickinfoList){
				if(tmpti.getUselimit().equals(uselimit)){
					filterList.add(tmpti);
				}
			}
			tickinfoList = filterList;
		}
		if(!Strings.isNullOrEmpty(tickettype)){
			List<TicketInfo> filterList = Lists.newArrayList();
			for(TicketInfo tmpti: tickinfoList){
				if(tmpti.getType()==Integer.parseInt(tickettype)){
					filterList.add(tmpti);
				}
			}
			tickinfoList = filterList;
		}
		//3.2 排序处理
		sortTicketInfos(tickinfoList);
		if(tickinfoList!=null && tickinfoList.size()>0){
			for(TicketInfo tmpti: tickinfoList){
				if(!tmpti.isIsused() && tmpti.getCheck()){
					checkcount++;
				}
			}
		}
		
		//4. 组织响应数据
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("tickets", tickinfoList);
		rtnMap.put("checkcount", checkcount);
		rtnMap.put("returntype", tickinfoList!=null&&tickinfoList.size()>0 ? 1 : 2);  //待切客规则确定再具体细分查不到券的原因
 		return new ResponseEntity<Map<String,Object>>(rtnMap, HttpStatus.OK);
	}
	
	public void sortTicketInfos(List<TicketInfo> ticketInfos) {
		ticketService.sortTicketInfos(ticketInfos);
	}
	
	@RequestMapping("/get")
    public ResponseEntity<Map<String, Object>> getTicketByActiveId(String token, String activeid, String recommendphone, String hardwarecode) {
        //TODO 添加入参recommendphone,待以后实现
		
		
		//1. 参数校验
		//1.1  基本校验
		if(Strings.isNullOrEmpty(token)){
			throw MyErrorEnum.errorParm.getMyException("用户会话过期.");
		}
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不允许为空.");
		}
		//1.2 业务校验
		//1.2.1 用户校验
		UMember member = MyTokenUtils.getMemberByToken(token);
		if(member==null){
			throw MyErrorEnum.customError.getMyException("无效用户.");
		}
		//1.2.2 活动校验
		BActivity bActivity =  ibActivityService.findById(Long.parseLong(activeid));
		if(bActivity == null){
			throw MyErrorEnum.IllegalActive.getMyException();
		}
		Date current = new Date();
		if(current.before(bActivity.getBegintime())){
			throw MyErrorEnum.notStartActive.getMyException();
		}
		if(current.after(bActivity.getEndtime())){
			throw MyErrorEnum.alreadyEndActive.getMyException();
		}
		if(bActivity.getPromotionmethodtype()==null || PromotionMethodTypeEnum.AUTO.equals(bActivity.getPromotionmethodtype())){
			throw MyErrorEnum.customError.getMyException("此类活动不允许领取优惠券.");
		}
		String synLockKey = RedisCacheName.IMIKE_OTS_GETTICKET_KEY+member.getMid();
		String synLockValue=null;
		Map<String, Object> rtnMap = Maps.newHashMap();
		List<TicketInfo> tickets = Lists.newArrayList();
		try{
			//加redis锁，防止重复请求
			if((synLockValue=DistributedLockUtil.tryLock(synLockKey, 60))==null){
				logger.info("mid:"+member.getMid()+"-----领取优惠券正在进行中,重复请求");

				return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
			}
			boolean isCondition = false;
			//1.2.3 校验活动是否可以多次领取
			if(bActivity.getLimitget()==-1){
				//A. 无限次领取 
				isCondition = true;
			}else{
				//B. 限定次数领取
				int limitgetnum = bActivity.getLimitget();
				long totalGetTicket = this.iTicketService.countByMidAndActiveId(member.getMid(), bActivity.getId());  //参加此活动总共领券数量
				if(totalGetTicket >= limitgetnum){
					throw MyErrorEnum.customError.getMyException("此活动只允许领取"+limitgetnum+"次.");
				}
				
				Date starttime = LocalDateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
				Date endtime = LocalDateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate();
				long uGetDayTicketNum = this.iTicketService.countByMidAndActiveIdAndTime(member.getMid(), bActivity.getId(), starttime, endtime);  //当天领券数量
				if(uGetDayTicketNum >= 1){
					throw MyErrorEnum.customError.getMyException("今天已领取过优惠券.");
				}
				isCondition = uGetDayTicketNum==0 && totalGetTicket<limitgetnum;

				
			}

			//去掉15元依赖1元券的处理  modify by tankai 20150709 10:20 
			//isCondition = collectTicketBy15yuan(isCondition, member.getMid(), activeid);
			
			//2. 根据不同活动领取不同优惠券
			if(isCondition){
                List<Long> pidList = this.iPromoService.genTicketByActive(Long.parseLong(activeid), member.getMid(), hardwarecode);
                if(pidList!=null && pidList.size()>0){
					tickets = this.ticketService.queryMyTicketByPromotionids(member.getMid(), pidList);
				}
			}

		}finally{
			//消除锁
			if(StringUtil.isNotEmpty(synLockValue)){
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		
		rtnMap.put("success", tickets!=null && tickets.size()>0);
		rtnMap.put("tickets", tickets);
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
	}
	
	/**
	 * 领取15元券
	 * @param isCondition
	 * @param mid
	 * @param activeid
	 * @return
	 */
	private boolean collectTicketBy15yuan(boolean isCondition,Long mid,String activeid){
		if(isCondition&&String.valueOf(Constant.ACTIVE_15YUAN).equals(activeid)){
			//是否参加过10+1活动
			//写死活动
			logger.info("用户正在领取15元体验券……");
			List<TicketInfo> list = iTicketService.queryMyTicket(mid, Constant.ACTIVE_10YUAN_1);//活动写死
			if(CollectionUtils.isNotEmpty(list)){
				//用户使用10+1元住店券并办理离店之后;
				TicketInfo ticketInfo = list.get(0);
				//根据优惠券id获取订单
				List<BPromotionPrice> bPromotionPrices = promotionPriceService.queryBPromotionPricesByPromId(ticketInfo.getId());
				//查询订单是否离店
				if(CollectionUtils.isNotEmpty(bPromotionPrices)){
					for (BPromotionPrice bPromotionPrice : bPromotionPrices) {
						OtaOrder order = orderService.findOtaOrderById(bPromotionPrice.getOtaorderid());
						if(order.getOrderStatus() == OtaOrderStatusEnum.CheckOut.getId()){//离店的
							logger.info("用户使用10+1元住店券并办理离店之后");
							isCondition = true;
							break;
						}
					}
				}else{
					logger.info("用户没有使用10+1元住店，不能领取15元优惠券");
                    throw MyErrorEnum.errorParm.getMyException("您没有使用1元优惠券住店，不能领取15元体验券");
				}
			}else{
				logger.info("用户没有领取10+1优惠券，不能领取15元体验券");
				throw MyErrorEnum.errorParm.getMyException("您没有领取1元优惠券，不能领取15元体验券");
			}
			isCondition=false;
		}
		return isCondition;
	}
	
	/**
	 * 查询优惠券领取情况
	 * @return
	 */
	@RequestMapping("/querysituation")
	public ResponseEntity<Map<String, Object>> querysituation(String token, String activeid, String starttime, String endtime){
		if(Strings.isNullOrEmpty(token)){
			throw MyErrorEnum.customError.getMyException("token不允许为空.");
		}
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.customError.getMyException("活动id不允许为空.");
		}
		if(Strings.isNullOrEmpty(starttime)){
			throw MyErrorEnum.customError.getMyException("开始时间不允许为空.");
		}
		if(Strings.isNullOrEmpty(endtime)){
			throw MyErrorEnum.customError.getMyException("结束时间不允许为空.");
		}
		
		
		UMember loginMember = MyTokenUtils.getMemberByToken(token);
		if(loginMember == null){
			throw MyErrorEnum.customError.getMyException("用户不存在.");
		}
		BActivity bActivity = this.ibActivityService.findById(Long.parseLong(activeid));
		if(bActivity == null){
			throw MyErrorEnum.customError.getMyException("活动不存在.");
		}
		Date stdate = null;
		Date edtime = null;
		try {
			DateTimeFormatter format = DateTimeFormat .forPattern("yyyyMMddHHmmss");
			stdate = DateTime.parse(starttime, format).toDate();
			edtime = DateTime.parse(endtime, format).toDate();
		} catch (Exception e) {
			e.printStackTrace();
			throw MyErrorEnum.customError.getMyException("日期格式不正确.参照:20150501083000"); 
		}
		
		List<TicketInfo> myticketList = Lists.newArrayList();
		long num = iTicketService.countByMidAndActiveIdAndTime(loginMember.getMid(), bActivity.getId(), stdate, edtime);
		if(num>0){
			myticketList = ticketService.queryMyTicket(loginMember.getMid(), bActivity.getId());
		}
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("isget", num>0 ? 'T':'F');
		rtnMap.put("num", num);
		rtnMap.put("tickets", myticketList);
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
	}
	
	/**
	 * 优惠码生成
	 * @return
	 */
	@RequestMapping("/codecreate")
	public ResponseEntity<Map<String, Object>> codecreate(String activeid, String num, String channelid, String allcount, String email){
		//1. 基础参数校验
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.customError.getMyException("活动编码不允许为空.");
		}
		if(Strings.isNullOrEmpty(num)){
			throw MyErrorEnum.customError.getMyException("批次号不允许为空.");
		}
		if(Strings.isNullOrEmpty(channelid)){
			throw MyErrorEnum.customError.getMyException("渠道编码不允许为空.");
		}
		if(Strings.isNullOrEmpty(allcount)){
			throw MyErrorEnum.customError.getMyException("生成码数量不允许为空.");
		}
		if(Strings.isNullOrEmpty(email)){
			throw MyErrorEnum.customError.getMyException("Email不允许为空.");
		}
		if(!email.endsWith("@imike.com")){
			throw MyErrorEnum.customError.getMyException("Email格式不正确.格式为:xxxx@imike.com");
		}
		
		//1.1 校验email格式
		if(!ValidateUtils.isMail(email)){
			throw MyErrorEnum.customError.getMyException("Email格式不正确.");
		}
		//1.2 校验活动
		BActivity bActivity =  ibActivityService.findById(Long.parseLong(activeid));
		if(bActivity == null){
			throw MyErrorEnum.IllegalActive.getMyException();
		}
		Date current = new Date();
		if(current.before(bActivity.getBegintime())){
			throw MyErrorEnum.notStartActive.getMyException();
		}
		if(current.after(bActivity.getEndtime())){
			throw MyErrorEnum.alreadyEndActive.getMyException();
		}
		//1.3 校验某一活动下的渠道编码是否存在
		Optional<BActiveChannel> ofActiveChannel = this.iBActiveChannelService.findActiveChannel(bActivity.getId(), Long.parseLong(channelid));
		if(!ofActiveChannel.isPresent()){
			throw MyErrorEnum.customError.getMyException("此活动下不存在渠道:"+channelid);
		}
		//1.4 校验某一活动下的批次号是否存在
		if(this.iBActiveCDKeyService.existBatchNo(bActivity.getId(), num)){
			throw MyErrorEnum.customError.getMyException("批次号["+num+"]已经存在.");
		}
		
		//2. 生成优惠码
		this.iPromoService.genCouponCode(ofActiveChannel.get(), num, Long.parseLong(allcount), email);
		
		//3. 组织数据返回
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
	}
	
	/**
	 * 通过优惠码领取优惠券
	 * @return
	 */
	@RequestMapping("/getbycode")
    public ResponseEntity<Map<String, Object>> getbycode(String token, String code, String hardwarecode) {
        //1. 参数校验
		if(Strings.isNullOrEmpty(code)){
			throw MyErrorEnum.customError.getMyException("兑换码不允许为空.");
		}
		Map<String, Object> rtnMap = Maps.newHashMap();
		UMember member = MyTokenUtils.getMemberByToken(token);
		
		String synLockKey = RedisCacheName.IMIKE_OTS_GETBYCODE_KEY+member.getMid();
		String synLockValue=null;
		try{
			//加redis锁，防止重复请求
			if((synLockValue=DistributedLockUtil.tryLock(synLockKey, 60))==null){
				logger.info("mid:"+member.getMid()+"-----领取优惠券正在进行中,重复请求");

				return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
			}
			//2. 兑换券
            List<TicketInfo> ticketList = this.ticketService.exchange(member, code, hardwarecode);

            //3. 组织数据返回
			rtnMap.put("success", ticketList!=null && ticketList.size()>0);
			rtnMap.put("tickets", ticketList);
		}finally{
			//消除锁
			if(StringUtil.isNotEmpty(synLockValue)){
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
	}
	
	/**
	 * 礼品抽奖参加记录
	 * @param token
	 * @param activeid
	 * @param ostype
	 * 必填
		1-IOS
		2-ANDROID
		3-微信
		4-Other
		H5
	 * @return
	 */
	@RequestMapping("/tryluck")
	public ResponseEntity<Map<String, Object>> tryluck(String token, String activeid,String ostype){

		Cat.logEvent("tryluckrequest",activeid,ostype,token);
		//1. 参数校验
		//1.1  基本校验
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不允许为空.");
		}
		if(Strings.isNullOrEmpty(ostype)){
			throw MyErrorEnum.errorParm.getMyException("设备类型不允许为空.");
		}else {
			if(!OSTypeEnum.IOS.getId().equals(ostype)&&!OSTypeEnum.ANDROID.getId().equals(ostype)&&!OSTypeEnum.WX.getId().equals(ostype)){
				throw MyErrorEnum.errorParm.getMyException("该设备类型不允许访问.");
			}
		}
		UMember member = MyTokenUtils.getMemberByToken(token);
		if(member==null){
			throw MyErrorEnum.customError.getMyException("无效用户.");
		}
		
		//1.2.2 活动校验
		BActivity bActivity =  ibActivityService.findById(Long.parseLong(activeid));
		if(bActivity == null){
			throw MyErrorEnum.IllegalActive.getMyException();
		}
		Date current = new Date();
		if(current.before(bActivity.getBegintime())){
			throw MyErrorEnum.notStartActive.getMyException();
		}
		if(current.after(bActivity.getEndtime())){
			throw MyErrorEnum.alreadyEndActive.getMyException();
		}
		if(bActivity.getPromotionmethodtype()==null || PromotionMethodTypeEnum.AUTO.equals(bActivity.getPromotionmethodtype())){
			throw MyErrorEnum.customError.getMyException("此类活动不允许领取优惠券.");
		}
		String synLockKey = RedisCacheName.IMIKE_OTS_TRYLUCK_KEY+member.getMid();
		String synLockValue=null;
		Map<String, Object> rtnMap = Maps.newHashMap();
		List<TicketInfo> tickets = Lists.newArrayList();
		List<BPrizeInfo> prizeInfo = Lists.newArrayList();
		try{
			//加redis锁，防止重复请求
			if((synLockValue=DistributedLockUtil.tryLock(synLockKey, 60))==null){
				logger.info("mid:"+member.getMid()+"-----领取优惠券正在进行中,重复请求");

				return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
			}
			boolean isCondition = false;
			//1.2.3 校验活动是否可以多次领取
			if(bActivity.getLimitget()==-1){
				//A. 无限次领取 
				isCondition = true;
			}else{
				//B. 限定次数领取
				int limitgetnum = bActivity.getLimitget();
				long totalGetTicket = this.iTicketService.countByMidAndActiveId(member.getMid(), bActivity.getId());  //参加此活动总共领券数量
				if(totalGetTicket >= limitgetnum){
					throw MyErrorEnum.customError.getMyException("此活动只允许领取"+limitgetnum+"次.");
				}
				
				Date starttime = LocalDateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
				Date endtime = LocalDateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate();
				long uGetDayTicketNum = this.iTicketService.countByMidAndActiveIdAndTime(member.getMid(), bActivity.getId(), starttime, endtime);  //当天领券数量
				if(uGetDayTicketNum >= 1){
					throw MyErrorEnum.customError.getMyException("今天已领取过优惠券.");
				}
				isCondition = uGetDayTicketNum==0 && totalGetTicket<limitgetnum;

				
			}
			
			//2. 根据不同活动领取不同优惠券
			if(isCondition){
				//定义：id，type
                //List<BPrizeInfo> pidList = this.iPromoService.tryLuckByActive(Long.parseLong(activeid), member.getMid(), ostype);
				List<BPrizeInfo> pidList = this.iPromoService.genTicketByActive(Long.parseLong(activeid), member,ostype,null); //重庆砸金蛋
				if(pidList!=null && pidList.size()>0){
					logger.info("pidList.get(0):{}",pidList.get(0).toString());
					List<Long> pidMiKeList =new ArrayList<Long>();
					for (BPrizeInfo list : pidList) {
						pidMiKeList.add(list.getId());
					}
					// 米可优惠券
					if (PrizeTypeEnum.mike.getId().intValue()==pidList.get(0).getType()) {
						logger.info(member.getId()+"领取到的是眯客券");
						tickets = this.ticketService.queryMyTicketByPromotionids(member.getMid(), pidMiKeList);
					}else if (PrizeTypeEnum.thirdparty.getId().intValue()==pidList.get(0).getType()){
						logger.info(member.getId()+"领取到的是第三方券");
						prizeInfo = this.ibPrizeStockService.queryMyThirdpartyPrize(pidList.get(0));
					}else if (PrizeTypeEnum.material.getId().intValue()==pidList.get(0).getType()){
						logger.info(member.getId()+"领取到的是实物");
						prizeInfo = this.ibPrizeService.queryMyMaterialPrize(pidList.get(0));
					}
					 
				}
			}

		}finally{
			//消除锁
			if(StringUtil.isNotEmpty(synLockValue)){
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		boolean trueOrFalse = (tickets!=null && tickets.size()>0) || (prizeInfo!=null && prizeInfo.size()>0) ;
		if (trueOrFalse){
			Cat.logEvent("KickEggFromMike",member.getLoginname(),"SUCCESS",prizeInfo.toString());
		}else {
			Cat.logEvent("KickEggFromMike",member.getLoginname(),"FAILED",prizeInfo.toString());
		}
		rtnMap.put("success", trueOrFalse);
		rtnMap.put("tickets", tickets);
		rtnMap.put("others", prizeInfo);
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
	
	}

	@RequestMapping("/nologintryluck")
	public ResponseEntity<Map<String, Object>> nologintryluck(String activeid,String ostype,String usermark){
		logger.info("nologintryluck接口传来参数 ,activeid：{},ostype:{},usermark:{}",activeid,ostype,usermark);
		//1. 参数校验
		//1.1  基本校验
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不允许为空.");
		}
		if(Strings.isNullOrEmpty(usermark)){
			throw MyErrorEnum.errorParm.getMyException("该设备唯一标示不允许为空.");
		}

		if(!Strings.isNullOrEmpty(ostype)&&!OSTypeEnum.H.getId().equals(ostype)){
			throw MyErrorEnum.errorParm.getMyException("该设备类型不允许访问.");
		}
		//1.2.2 活动校验
		BActivity bActivity =  bActivityDao.findById(Long.parseLong(activeid));
		if(bActivity == null){
			throw MyErrorEnum.IllegalActive.getMyException();
		}
		Date current = new Date();
		if(current.before(bActivity.getBegintime())){
			throw MyErrorEnum.notStartActive.getMyException();
		}
		if(current.after(bActivity.getEndtime())){
			throw MyErrorEnum.alreadyEndActive.getMyException();
		}
		if(bActivity.getPromotionmethodtype()==null || PromotionMethodTypeEnum.AUTO.equals(bActivity.getPromotionmethodtype())){
			throw MyErrorEnum.customError.getMyException("此类活动不允许领取优惠券.");
		}
		String  synLockKey = RedisCacheName.IMIKE_OTS_TRYLUCK_KEY+usermark;

		String synLockValue=null;
		Map<String, Object> rtnMap = Maps.newHashMap();
		List<TicketInfo> tickets = Lists.newArrayList();
		List<BPrizeInfo> prizeInfo = Lists.newArrayList();
		try{
			//加redis锁，防止重复请求
			if((synLockValue=DistributedLockUtil.tryLock(synLockKey, 60))==null){
				logger.info("usermark:"+usermark+"-----领取优惠券正在进行中,重复请求");
				return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
			}
			boolean isCondition = false;
			//1.2.3 校验活动是否可以多次领取
			if(bActivity.getLimitget()==-1){
				//A. 无限次领取
				isCondition = true;
			}
			//B. 限定次数领取
				/*int limitgetnum = bActivity.getLimitget();
				long totalGetTicket = this.iTicketService.countByMidAndActiveId(member.getMid(), bActivity.getId());  //参加此活动总共领券数量
				if(totalGetTicket >= limitgetnum){
					throw MyErrorEnum.customError.getMyException("此活动只允许领取"+limitgetnum+"次.");
				}

				Date starttime = LocalDateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
				Date endtime = LocalDateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate();
				long uGetDayTicketNum = this.iTicketService.countByMidAndActiveIdAndTime(member.getMid(), bActivity.getId(), starttime, endtime);  //当天领券数量
				if(uGetDayTicketNum >= 1){
					throw MyErrorEnum.customError.getMyException("今天已领取过优惠券.");
				}
				isCondition = uGetDayTicketNum==0 && totalGetTicket<limitgetnum;

			}
			*/
			//2. 根据不同活动领取不同优惠券
			if(isCondition){
				//定义：id，type
				List<BPrizeInfo> pidList=this.iPromoService.genTicketByActive(Long.parseLong(activeid), null,ostype,usermark);
				if(pidList!=null && pidList.size()>0){
					logger.info("pidList.get(0):{}",pidList.get(0).toString());
					List<Long> pidMiKeList =new ArrayList<Long>();
					for (BPrizeInfo list : pidList) {
						pidMiKeList.add(list.getId());
					}
					// 米可优惠券
					if (PrizeTypeEnum.mike.getId().intValue()==pidList.get(0).getType()) {
						logger.info("usermark:"+usermark+"领取到的是眯客券");
						tickets = this.ticketService.queryMyTicketOnUserMark(pidList.get(0),Long.parseLong(activeid));
					}else if (PrizeTypeEnum.thirdparty.getId().intValue()==pidList.get(0).getType()){
						logger.info("usermark:"+usermark+"领取到的是第三方券");
						prizeInfo = this.ibPrizeStockService.queryMyThirdpartyPrize(pidList.get(0));
					}else if (PrizeTypeEnum.material.getId().intValue()==pidList.get(0).getType()){
						logger.info("usermark:"+usermark+"领取到的是实物");

						prizeInfo = this.ibPrizeService.queryMyMaterialPrize(pidList.get(0));
					}
				}
			}

		}finally{
			//消除锁
			if(StringUtil.isNotEmpty(synLockValue)){
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		boolean trueOrFalse = (tickets!=null && tickets.size()>0) || (prizeInfo!=null && prizeInfo.size()>0) ;

		rtnMap.put("success", trueOrFalse);
		rtnMap.put("tickets", tickets);
		rtnMap.put("others", prizeInfo);
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);

	}

	/**
	 * 活动转发记录
	 * @param token
	 * @param activeid 活动id
	 * @return
	 */
	@RequestMapping("/activeforward")
	public ResponseEntity<Map<String, Object>> activeforward(String token, String activeid){
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.customError.getMyException("活动编码不允许为空.");
		}
		Long mid = MyTokenUtils.getMidByToken(token);
		Long actid = Long.parseLong(activeid);

		this.iUActiveShareService.record(mid, actid);
		
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
	}
	
	/**
	 * 查询活动转发次数
	 * @param activeid
	 * @return
	 */
	@RequestMapping("/activeforwardcount")
	public ResponseEntity<Map<String, Object>> activeforwardcount(String activeid){
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.customError.getMyException("活动编码不允许为空.");
		}
		Long actid = Long.parseLong(activeid);
		
		long count = this.iUActiveShareService.count(actid);
		
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("forwardcount", count);
		return new ResponseEntity<Map<String, Object>>(rtnMap,HttpStatus.OK);
	}

	/**
	 * 查询用户领取优惠券次数
	 * @param token 用户token
	 * @return ResponseEntity
	 */
	@RequestMapping("/getcount")
	public ResponseEntity<Map<String, Object>> getcount(String token){
		long mid = MyTokenUtils.getMidByToken(token);
		
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("getcount", this.iTicketService.getHandGetPromotionCount(mid));
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 查询用户优惠券未领取情况
	 * @param token 用户token
	 * @return ResponseEntity
	 */
	@RequestMapping("/situation")
	public ResponseEntity<Map<String, Object>> situation(String token){
		long mid = MyTokenUtils.getMidByToken(token);
		
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("tickets", this.iTicketService.getNotActivePromotionCount(mid));
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 优惠券领取
	 * 将为领取状态的优惠券置为领取状态
	 * @param token 用户token
	 * @param promotionid 优惠券id
	 * @return ResponseEntity
	 */
	@RequestMapping("/changegetstatus")
	public ResponseEntity<Map<String, Object>> changegetstatus(String token, Long promotionid){
		if(promotionid==null){
			throw MyErrorEnum.customError.getMyException("参数不允许为空.");
		}
		
		long mid = MyTokenUtils.getMidByToken(token);
		boolean isflag = this.iTicketService.activatePromotion(mid, promotionid);
		
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("tickets", this.iTicketService.queryMyTicketByPromotionids(mid, ImmutableList.of(promotionid)));
		rtnMap.put("success", isflag);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	
	/**
	 * 查询第三方领取的优惠券
	 * @param token 用户token
	 * @param activeid 活动id
	 * @return ResponseEntity
	 */
	@RequestMapping("/queryluck")
	public ResponseEntity<Map<String, Object>> queryluck(String token, String activeid){
        logger.info("传来参数token:{},activeid:{}",token,activeid);
		//1. 参数校验
		if(Strings.isNullOrEmpty(token)){
			throw MyErrorEnum.errorParm.getMyException("用户会话过期.");
		}
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不能为空.");
		}
		//2. 业务校验
		UMember member = MyTokenUtils.getMemberByToken(token);
		if(member==null){
			throw MyErrorEnum.customError.getMyException("无效用户.");
		}
		List<BPrizeInfo>  bPrizeInfoList = this.iTicketService.queryMyHistoryPrize(member.getId(), Long.parseLong(activeid));
		logger.info("返回优惠券集合个数bPrizeInfoList.size:{}",bPrizeInfoList.size());
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("lucklist", bPrizeInfoList);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 查询第三方领未领取的优惠券
	 * @param token 用户token
	 * @param activeid 活动id
	 * @return ResponseEntity
	 */
	@RequestMapping("/querynotreceive")
	public ResponseEntity<Map<String, Object>> querynotreceive(String activeid,String usermark){
		logger.info("传来参数,activeid:{},usermark:{}",activeid,usermark);
		//1. 参数校验
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不能为空.");
		}

		List<BPrizeInfo>  bPrizeInfoList =  this.iTicketService.queryMyNotreceiveyPrize(Long.parseLong(activeid),usermark);

		logger.info("返回优惠券集合个数bPrizeInfoList.size:{}",bPrizeInfoList.size());
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("lucklist", bPrizeInfoList);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	/**
	 * 根据记录id将查询到的记录插入手机号,更新奖品记录状态
	 * @param prizerecordid
	 * @param phone 活动id
	 * @return ResponseEntity
	 */
	@RequestMapping("/prizebindingphone")
	public ResponseEntity<Map<String, Object>> prizebindingphone(String prizerecordid,String activeid,String phone,String ostype){
		logger.info("传来参数prizerecordid:{},phone:{}",prizerecordid,phone);
		//1. 参数校验
		if(Strings.isNullOrEmpty(prizerecordid)){
			throw MyErrorEnum.errorParm.getMyException("奖品记录号不能为空.");
		}

		if(Strings.isNullOrEmpty(phone)){
			throw MyErrorEnum.errorParm.getMyException("手机号不能为空.");
		}
		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不能为空.");
		}
		//判断该手机是否领取奖品
		iTicketService.checkReceivePrizeByPhone(phone,Long.parseLong(activeid),OSTypeEnum.H.getId(), DateUtils.getDate());
		//如果该手机已经是眯客用户就直接绑定，如果不是眯客用户，就下次他登录(注册)时候绑定
		iTicketService.prizeBindingUser(phone,prizerecordid);
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}

	@RequestMapping("/uuid")
	public ResponseEntity<Map<String, Object>> getuuid(){
		logger.info("git uuid");
		UUID uuid = UUID.randomUUID();
		String guuid = uuid.toString();

		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("usermark",guuid);
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}


	/**
	 * 查询领取的中奖结果
	 * @param token 用户token
	 * @param activeid 活动id
	 * @return ResponseEntity
	 */
	@RequestMapping("/queryallluck")
	public ResponseEntity<Map<String, Object>> querylucky(String token, String activeid,String usermark){
		logger.info("传来参数token:{},activeid:{}, usermark:{}",token,activeid, usermark);
		//1. 参数校验

		if(Strings.isNullOrEmpty(activeid)){
			throw MyErrorEnum.errorParm.getMyException("活动id不能为空.");
		}
		List<BPrizeInfo> prizeInfoLists = new ArrayList<>();
		//2. 业务校验
		if (StringUtils.isNoneBlank(token)){
			UMember member = MyTokenUtils.getMemberByToken(token);
			if(member==null){

			}
			List<BPrizeInfo>  bPrizeInfoList = this.iTicketService.queryMyHistoryPrize(member.getId(), Long.parseLong(activeid));
			for (BPrizeInfo bPrizeInfo: bPrizeInfoList){
				BPrizeInfo tmpPrizeinfo = new BPrizeInfo();
				tmpPrizeinfo.setName(bPrizeInfo.getName());
				tmpPrizeinfo.setCode(bPrizeInfo.getCode());
				tmpPrizeinfo.setMerchantid(bPrizeInfo.getMerchantid());
				try{
					Date tmpBeginDate = DateUtils.getDateFromString(bPrizeInfo.getBegintime());
					String tmpBgTime = DateUtils.getStringFromDate(tmpBeginDate, DateUtils.FORMAT_DATE);
					tmpPrizeinfo.setBegintime(tmpBgTime);
				}catch (Exception e){
					tmpPrizeinfo.setBegintime(bPrizeInfo.getBegintime());
				}

				try{
					Date tmpEndDate = DateUtils.getDateFromString(bPrizeInfo.getEndtime());
					String tmpEdTime = DateUtils.getStringFromDate(tmpEndDate, DateUtils.FORMAT_DATE);
					tmpPrizeinfo.setEndtime(tmpEdTime);
				}catch (Exception e){
					tmpPrizeinfo.setEndtime(bPrizeInfo.getEndtime());
				}

				try{
					Date tmpCreateDate = DateUtils.getDateFromString(bPrizeInfo.getCreatetime());
					String tmpCreateTime = DateUtils.getStringFromDate(tmpCreateDate, DateUtils.FORMAT_DATE);
					tmpPrizeinfo.setCreatetime(tmpCreateTime);
				}catch (Exception e){
					tmpPrizeinfo.setCreatetime(bPrizeInfo.getCreatetime());
				}

				tmpPrizeinfo.setId(bPrizeInfo.getId());
				tmpPrizeinfo.setPrice(bPrizeInfo.getPrice());
				tmpPrizeinfo.setPrizeRecordId(bPrizeInfo.getPrizeRecordId());
				tmpPrizeinfo.setType(bPrizeInfo.getType());
				tmpPrizeinfo.setUrl(bPrizeInfo.getUrl());

				prizeInfoLists.add(tmpPrizeinfo);
			}

		}

		if (StringUtils.isNotBlank(usermark)){
			List<BPrizeInfo>  bPrizeInfoList =  this.iTicketService.queryMyNotreceiveyPrize(Long.parseLong(activeid),usermark);
			for (BPrizeInfo bPrizeInfo: bPrizeInfoList){
				BPrizeInfo tmpPrizeinfo = new BPrizeInfo();
				tmpPrizeinfo.setName(bPrizeInfo.getName());
				tmpPrizeinfo.setCode(bPrizeInfo.getCode());
				tmpPrizeinfo.setMerchantid(bPrizeInfo.getMerchantid());
				try{
					Date tmpBeginDate = DateUtils.getDateFromString(bPrizeInfo.getBegintime());
					String tmpBgTime = DateUtils.getStringFromDate(tmpBeginDate, DateUtils.FORMAT_DATE);
					tmpPrizeinfo.setBegintime(tmpBgTime);
				}catch (Exception e){
					tmpPrizeinfo.setBegintime(bPrizeInfo.getBegintime());
				}

				try{
					Date tmpEndDate = DateUtils.getDateFromString(bPrizeInfo.getEndtime());
					String tmpEdTime = DateUtils.getStringFromDate(tmpEndDate, DateUtils.FORMAT_DATE);
					tmpPrizeinfo.setEndtime(tmpEdTime);
				}catch (Exception e){
					tmpPrizeinfo.setEndtime(bPrizeInfo.getEndtime());
				}

				try{
					Date tmpCreateDate = DateUtils.getDateFromString(bPrizeInfo.getCreatetime());
					String tmpCreateTime = DateUtils.getStringFromDate(tmpCreateDate, DateUtils.FORMAT_DATE);
					tmpPrizeinfo.setCreatetime(tmpCreateTime);
				}catch (Exception e){
					tmpPrizeinfo.setCreatetime(bPrizeInfo.getCreatetime());
				}

				tmpPrizeinfo.setId(bPrizeInfo.getId());
				tmpPrizeinfo.setPrice(bPrizeInfo.getPrice());
				tmpPrizeinfo.setPrizeRecordId(bPrizeInfo.getPrizeRecordId());
				tmpPrizeinfo.setType(bPrizeInfo.getType());
				tmpPrizeinfo.setUrl(bPrizeInfo.getUrl());

				prizeInfoLists.add(tmpPrizeinfo);
			}
		}

		logger.info("返回优惠券集合个数prizeInfoLists.size:{}",prizeInfoLists.size());
		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		rtnMap.put("lucklist", prizeInfoLists);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
}
