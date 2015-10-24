package com.mk.ots.order.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.http.HTTPException;

import com.mk.framework.util.*;
import com.mk.ots.common.enums.*;
import com.mk.ots.roomsale.model.TRoomSale;
import com.mk.ots.roomsale.service.RoomSaleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import cn.com.winhoo.mikeweb.myenum.PmsRoomOrderStatusEnum;
import cn.com.winhoo.pms.webout.service.bean.CancelOrder;
import cn.com.winhoo.pms.webout.service.bean.PmsCheckinPerson;
import cn.com.winhoo.pms.webout.service.bean.PmsOtaAddOrder;
import cn.com.winhoo.pms.webout.service.bean.PmsRCost;
import cn.com.winhoo.pms.webout.service.bean.PmsUpdateOrder;
import cn.com.winhoo.pms.webout.service.bean.ReturnObject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mk.care.kafka.common.CopywriterTypeEnum;
import com.mk.care.kafka.model.Message;
import com.mk.framework.AppUtils;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.orm.kit.JsonKit;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.bean.PageObject;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateTools;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.NumUtils;
import com.mk.ots.common.utils.SysConfig;
import com.mk.ots.hotel.bean.HQrCode;
import com.mk.ots.hotel.bean.RoomTypePriceBean;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.bean.TRoomTypeInfo;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.dao.RoomDAO;
import com.mk.ots.hotel.dao.RoomTypeDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.CashBackService;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.hotel.service.RoomTypeInfoService;
import com.mk.ots.hotel.service.RoomTypeService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.kafka.message.OtsCareProducer;
import com.mk.ots.manager.HotelPMSManager;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.mapper.OtaOrderMacMapper;
import com.mk.ots.mapper.OtaOrderTastsMapper;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.service.impl.MessageService;
import com.mk.ots.order.bean.BHotelPromotionPrice;
import com.mk.ots.order.bean.OrderLog;
import com.mk.ots.order.bean.OtaCheckInUser;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.OtaRoomPrice;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.bean.PushMessage;
import com.mk.ots.order.bean.UUseTicketRecord;
import com.mk.ots.order.common.PropertyConfigurer;
import com.mk.ots.order.dao.CheckInUserDAO;
import com.mk.ots.order.dao.OrderDAO;
import com.mk.ots.order.dao.OtaOrderDAO;
import com.mk.ots.order.dao.PmsRoomOrderDao;
import com.mk.ots.order.dao.RoomOrderDAO;
import com.mk.ots.order.model.BOrderBusinessLog;
import com.mk.ots.order.model.BOtaorder;
import com.mk.ots.order.model.FirstOrderModel;
import com.mk.ots.order.model.OtaOrderMac;
import com.mk.ots.order.model.OtaOrderTasts;
import com.mk.ots.pay.dao.IPOrderLogDao;
import com.mk.ots.pay.dao.impl.POrderLogDAO;
import com.mk.ots.pay.model.PMSCancelParam;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.pay.service.IPayService;
import com.mk.ots.pay.service.IPriceService;
import com.mk.ots.price.dao.BasePriceDAO;
import com.mk.ots.price.dao.PriceDAO;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.dao.IBPromotionPriceDao;
import com.mk.ots.promo.dao.IBPromotionRuleDAO;
import com.mk.ots.promo.dao.impl.BPromotionPriceDAO;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.promo.service.IPromotionPriceService;
import com.mk.ots.promo.service.impl.PromoService;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity.Room;
import com.mk.ots.score.model.THotelScore;
import com.mk.ots.score.service.ScoreService;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.TicketInfo;
import com.mk.ots.ticket.model.UTicket;
import com.mk.ots.ticket.service.ITicketService;
import com.mk.ots.ticket.service.parse.ITicketParse;
import com.mk.ots.utils.MD5Util;
import com.mk.ots.wallet.service.IWalletCashflowService;
import com.mk.ots.wallet.service.IWalletService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.bean.PmsCheckinUser;
import com.mk.pms.myenum.PmsErrorEnum;
import com.mk.sever.ServerChannel;

@Service
public class OrderServiceImpl implements OrderService {

    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDAO orderDAO = null;
    @Autowired
    private IPayService payService;
    @Autowired
    private IMemberService memberService;
    @Autowired
    private IPriceService priceService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomstateService roomstateService;
    @Autowired
    private CheckInUserDAO checkInUserDAO;
    @Autowired
    private RoomOrderDAO roomOrderDAO;
    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private RoomTypeDAO roomTypeDAO;
    @Autowired
    private PriceDAO priceDAO;
    @Autowired
    private BasePriceDAO basePriceDAO;
    @Autowired
    private IBPromotionDao promoDAO;
    @Autowired
    private IBPromotionPriceDao ibPromotionPriceDao;
    @Autowired
    private IPromotionPriceService iPromotionPriceService;
    @Autowired
    private RoomTypeService roomTypeService;
    @Autowired
    private RoomTypeInfoService roomTypeInfoService;
    @Resource
    private ITicketService ticketService;
    @Autowired
    private PromoService iPromoService;
    @Autowired
    private BPromotionPriceDAO iBPromotionPriceDao;
    @Autowired
    private IPromotionPriceService promoService;

    @Autowired
    private IBPromotionDao iBPromotionDao;
    @Autowired
    private PriceDAO priceDao;

    @Autowired
    private UTicketDao uTicketDao;
    @Autowired
    private OrderUtil orderUtil;
    @Autowired
    private OrderBusinessLogService orderBusinessLogService;

    @Autowired
    private IPOrderLogDao iPOrderLogDao;
    @Autowired
    private OrderLogService orderLogService;
    @Autowired
    private IBPromotionRuleDAO ibPromotionRuleDAO;
    @Autowired
    private OtaOrderDAO otaOrderDAO;
    @Autowired
    private HotelDAO hotelDAO;
    @Autowired
    private OtaOrderMacMapper otaOrderMacMapper;
    @Autowired
    private PmsRoomOrderDao pmsRoomOrderDao;
    @Autowired
    private OtaOrderTastsMapper otaOrderTastsMapper;
    @Autowired
    private OtaOrderTastsMapper orderTastsMapper;
    @Autowired
    private MessageService iMessageService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private POrderLogDAO orderLogDao;
    @Autowired
    private IWalletCashflowService walletCashflowService;
    @Autowired
    private CashBackService cashBackService;
    @Autowired
    private IWalletService walletService;
    private Gson gson = new Gson();
	@Autowired
	private HotelPriceService hotelPriceService ;
	@Autowired
    private OtsCareProducer careProducer;
    @Autowired
    private RoomSaleService roomSaleService;

    static final long TIME_FOR_FIVEMIN = 5 * 60 * 1000L;
    private static final long TIME_FOR_FIFTEEN = Long.parseLong(PropertyConfigurer.getProperty("transferCheckinUsernameTime"));
    private static final int time_for_fifteen=(int) (TIME_FOR_FIFTEEN/1000/60);
    
    @Override
    public OtaOrder updateOrderPms(OtaOrder order) {
        for (OtaRoomOrder roomOrder : order.getRoomOrderList()) {
            orderDAO.updateOtaRoomOrderToPMS(roomOrder);
        }
        return order;
    }

    @Override
    public void modifyOrderStatusOnPay(OtaOrder order, String paytype) {
        if (order.getOrderStatus() >= OtaOrderStatusEnum.Account.getId()) {
            orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_START_PAY.getId(), "", "异常:"+OtaOrderStatusEnum.getByID(order.getOrderStatus()).getName(), "");
        }
        if (paytype.trim().equals("1")) {
            order.setPayStatus(PayStatusEnum.waitPay.getId());
//          order.setOrderType(OrderTypeEnum.YF.getId());
          orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_START_PAY.getId(), "", "选择支付类型：" + OrderTypeEnum.YF.getName(), "");
      } else {
          order.setPayStatus(PayStatusEnum.doNotPay.getId());
//          order.setOrderType(OrderTypeEnum.PT.getId());
          orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_START_PAY.getId(), "", "选择支付类型：" + OrderTypeEnum.PT.getName(), "");
      }
      order.saveOrUpdate();
  }

  /**
   * @param hotel
   * @param tempRoom
   */
  private void checkInTime(THotel hotel, OtaRoomOrder tempRoom) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      String beginTime = sdf.format(tempRoom.getDate("begintime"));
      String endTime = sdf.format(tempRoom.getDate("endtime"));
      endTime = endTime.substring(0, 8) + hotel.get("defaultLeaveTime");// 离店时间
      Calendar calNow = Calendar.getInstance();
      String nowtime = sdf.format(calNow.getTime());
      boolean now = nowtime.substring(0, 8).equals(beginTime.substring(0, 8));
      if ((beginTime.substring(0, 8).compareTo(sdf.format(new Date()).substring(0, 8)) < 0) || (beginTime.compareTo(endTime) > 0)) {
          throw MyErrorEnum.saveOrderInTimeByTime.getMyException("请正确选择入住时间---begintime:" + beginTime + " endtime: " + endTime);
      }
      // 当入住时间为本天时的逻辑判断
      if (now) {
          if (nowtime.compareTo(beginTime) > 0) {
              throw MyErrorEnum.saveOrderInTimeByTime.getMyException();
          }
          // 入住时间不能大于保留时间 预付除外
          if (tempRoom.get("ordertype") == OrderTypeEnum.PT.getId()) {
              if (beginTime.compareTo(nowtime.substring(0, 8) + hotel.get("retentiontime")) > 0) {
                  throw MyErrorEnum.saveOrderInTimeByYF.getMyException();
              }
          }
      }

      try {
          tempRoom.set("endtime", sdf.parse(endTime));
      } catch (ParseException e) {
          throw MyErrorEnum.systemBusy.getMyException("结束时间错误!配置时间错误?");
      }
  }

  /**
   * @param newOrder
   * @return
   */
  public OtaOrder saveOrder(OtaOrder newOrder) {
  	Transaction t = Cat.newTransaction("Order.doCreateOrder", "saveOrder");
      try {
      // 新表单存储 并且计算价格 先保存-1 没有获得价格 保存后在设置为0
      BigDecimal temp = new BigDecimal(-1);
      BigDecimal total = temp;
      newOrder.setTotalPrice(total);
      newOrder.set("Price", temp);
      newOrder.set("daynumber", 0L);
      // 这里需要先保存订单 以保证下面OtaRoomOrder能获取到OtaOrder的ID
      OtaOrder temporder = newOrder.saveOrUpdate();
      // 需要关联客单 第一个客单

      OtaRoomOrder linkRoomOrder = null;
      for (OtaRoomOrder otaRoomOrder : newOrder.getRoomOrderList()) {
          if (total.equals(new BigDecimal(-1))) {
              total = new BigDecimal(0);
          }
          otaRoomOrder.set("OtaOrderId", newOrder.getId());
          if (linkRoomOrder != null) {
              otaRoomOrder.set("LinkRoomOrderId", linkRoomOrder.getLong("id"));
          }
          // 保存订单
          otaRoomOrder = saveRoomOrder(newOrder, otaRoomOrder, newOrder.get("spreadUser") != null);
          if (linkRoomOrder == null) {
              linkRoomOrder = otaRoomOrder;
              otaRoomOrder.set("LinkRoomOrderId", linkRoomOrder.getLong("id"));
              otaRoomOrder.update();
          }
          if (otaRoomOrder.getBigDecimal("totalprice") != null) {
              total = total.add(otaRoomOrder.getBigDecimal("totalprice"));// 计算总价格
          }
      }
      // 客单价格计算完毕再次保存订单 以保存价格
      newOrder.setPrice(newOrder.getRoomOrderList().get(0).getBigDecimal("price"));
      newOrder.setDaynumber(newOrder.getRoomOrderList().get(0).getLong("orderday").longValue());
      newOrder.setTotalPrice(total);
      if (total.equals(temp) || temporder.getPrice().equals(temp)) {
          throw MyErrorEnum.saveOrderCost.getMyException("获取价格错误");
      }
      newOrder.saveOrUpdate();
      t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
          t.setStatus(e);
          throw e;
      }finally {
          t.complete();
       }
      return newOrder;
  }

  @Override
  public void cancelOrder(Long orderId, String type) {
      logger.info("OTSMessage::取消订单cancelOrder:orderId:" + orderId);
      OtaOrder order = this.findOtaOrderById(orderId);
      if (order == null) {
          logger.info("OTSMessage::取消订单order为null");
          throw MyErrorEnum.errorParm.getMyException("订单不存在");
      }
      if (order.getLong("orderstatus") >= OtaOrderStatusEnum.CancelByU_WaitRefund.getId().longValue()) {
          // 订单已经取消
          return;
      }

      // 只能取消自己的订单
      if (MyTokenUtils.getMidByToken("").longValue() != order.getLong("mid").longValue()) {
          logger.info("OTSMessage::取消订单token出错。");
          throw MyErrorEnum.findOrder.getMyException();
      }
      if (order.getOrderType() == OrderTypeEnum.YF.getId()) {
            if (order.getPayStatus() < PayStatusEnum.alreadyPay.getId()) {
              // 暂时不需要处理
              logger.info("OTSMessage::取消订单支付状态等待支付。");
          } else {
              Calendar orderT = Calendar.getInstance();
              orderT.setTime(order.getBeginTime());
              String scancelTime = SysConfig.getInstance().getSysValueByKey(Constant.cancelTime);
              Integer cancelTime = 12 * 60;
              if (StringUtils.isNotBlank(scancelTime)) {
                  cancelTime = Integer.parseInt(scancelTime);
              }
              orderT.add(Calendar.MINUTE, -1 * cancelTime);
              Calendar nowT = Calendar.getInstance();
              if (orderT.compareTo(nowT) < 0) {
                  int h = cancelTime / 60;
                  int m = cancelTime % 60;
                  if (h > 0) {
                      if (m > 0) {
                          throw MyErrorEnum.cancelTime.getMyException("预付订单在入住前" + h + "小时" + m + "分钟内无法取消订单,如有特殊情况请联系客服.");
                      } else {
                          throw MyErrorEnum.cancelTime.getMyException("预付订单在入住前" + h + "小时内无法取消订单,如有特殊情况请联系客服.");
                      }
                  } else {
                      throw MyErrorEnum.cancelTime.getMyException("预付订单在入住前" + m + "分钟内无法取消订单,如有特殊情况请联系客服.");
                  }

              }
              logger.info("OTSMessage::orderServiceImpl282行。");
          }
      }
      // 判断能否取消表单 1:所有客单都没入住可以取消. 2:只要有一个入住后不能取消.
//    for (OtaRoomOrder roomorder : order.getRoomOrderList()) {
//        if ((roomorder.getLong("orderstatus") > OtaOrderStatusEnum.CheckInOnline.getId().longValue())
//                && (roomorder.getLong("orderstatus") < OtaOrderStatusEnum.CancelByU_WaitRefund.getId().longValue())) {
//            throw MyErrorEnum.delOrderErrorByOrderIn.getMyException();
//        }
//    }
      if ((order.getLong("orderstatus") > OtaOrderStatusEnum.CheckInOnline.getId().longValue())
              && (order.getLong("orderstatus") < OtaOrderStatusEnum.CancelByU_WaitRefund.getId().longValue())) {
          // 只能取消未入住的订单.
          throw MyErrorEnum.delOrderErrorByOrderIn.getMyException();
      }
      order.put("cancelType", type);
      this.cancelOrder(order);
      /***************发送取消订单消息推送*****************/
      if (order.getOrderType() == OrderTypeEnum.PT.getId()) {
          OtaOrderTasts  otaOrderTasts= this.getMessageToC(order, new Date(), true,CopywriterTypeEnum.pay_df_refund);
          int result = this.otaOrderTastsMapper.insertSelective(otaOrderTasts);
          if (result == 0) {
              logger.info("保存报文失败，报文信息:{}，订单Id:{}",
                      JsonKit.toJson(otaOrderTasts), orderId);
          } else {
              logger.info("保存报文成功，orderTasts id：{}，订单Id:{}",
                      otaOrderTasts.getId(), orderId);
          }
          logger.info("OrderPushMsgService:: 到付取消订单消息推送  otaOrderId = "
                  + orderId);
      }
      /***************发送取消订单消息推送*****************/
  }

    private BigDecimal getWalletYE(OtaOrder order) {
        this.walletCashflowService.unLockCashFlow(order.getMid(), order.getId());
        return this.walletService.queryBalance(order.getMid());
    }

    /**
     * 设置订单的可用钱包
     *
     * @param jsonObj
     * @param order
     */
    public void setAvailableMoney(JSONObject jsonObj, OtaOrder order) {
        String useWallet = order.getStr("isuselewallet");
        this.logger.info("订单号：{},钱包的使用情况：{}", order.getId(), useWallet);
        if(order.getPayStatus()>PayStatusEnum.waitPay.getId()){
        	this.logger.info("订单号：{},支付状态不对，不修改：{}", order.getId(), order.getPayStatus());
        	return;
        }
        if ("T".equals(useWallet)) {
            // 在线支付金额
            BigDecimal onlinepay = jsonObj.getBigDecimal("onlinepay");

            // 拿钱包余额
            BigDecimal ye = this.getWalletYE(order);

            if (ye.compareTo(onlinepay) < 0) {
                // 支付金额大于余额
                // 回写余额
                order.setAvailableMoney(ye);

                // 用户支付金额
                jsonObj.put("onlinepay", onlinepay.subtract(ye));

                // 钱包支付价格
                this.walletCashflowService.pay(order.getMid(), order.getId(), ye);
                this.logger.info("通知钱包用了，{}元，订单号：{}", ye, order.getId());
            } else {
                // 支付金额小于余额

                // 回写支付金额
                order.setAvailableMoney(onlinepay);
                // 用户支付金额
                jsonObj.put("onlinepay", 0);

                // 钱包支付价格
                this.walletCashflowService.pay(order.getMid(), order.getId(), onlinepay);
                this.logger.info("通知钱包用了，{}元，订单号：{}", onlinepay, order.getId());
            }
            order.update();

        } else if ("F".equals(useWallet)) {
        	unLockCashFlow(order);
			
        }
    }
    
    public void unLockCashFlow(OtaOrder order){
    	this.logger.info("返现金额解冻，订单号{}",order.getId());
		this.walletCashflowService.unLockCashFlow(order.getMid(), order.getId());
		order.setAvailableMoney(new BigDecimal(0));
		order.update();
    }

    /**
     * 取消订单
     *
     * @param order
     */
    public void cancelOrder(OtaOrder order) {
        logger.info("OTSMessage::取消订单:start{}", order);
        
        /*******直减订单处理******/
        if(order.getClearingType()==ClearingTypeEnum.priceDrop.getId()&&order.getPayStatus()>PayStatusEnum.paying.getId()){
        	this.logger.info("已经付款的直减订单，不能取消,订单号：{}",order.getId());
        	//已经付款就不能取消
        	throw MyErrorEnum.customError.getMyException("已经付款的直减订单，不能取消");
        }else{
        	this.logger.info("直减订单，取消冻结房间,订单号：{}",order.getId());
        	//取消冻结
        }
        /*******直减订单处理******/
        
        
        boolean haveCreatePms = false;
        for (OtaRoomOrder roomOrder : order.getRoomOrderList()) {
            if (StringUtils.isNotBlank(roomOrder.getPmsRoomOrderNo())) {
                haveCreatePms = true;
            }
        }
        logger.info("OTSMessage::取消订单:haveCreatePms::{}", haveCreatePms);
        // 已经支付、或着为到付，并创建pms里order的情况
        if (haveCreatePms) {
            logger.info("OTSMessage::取消订单::取消PMS支付::{}", order.getId());

            // 取消pms订单
            cancelPmsOrder(order);

            order.update();
        } else if (order.getPayStatus() < PayStatusEnum.alreadyPay.getId()) {
          logger.info("OTSMessage::取消订单:等待支付系统调度取消订单。" + order.getId());
          order.setOrderStatus(OtaOrderStatusEnum.CancelByU_NoRefund.getId());
			// 是否设置系统取消
			if ("2".equals(order.getStr("cancelType"))) {
				logger.info("用户回退取消订单:cancelType:{},orderid:{}", order.getStr("cancelType"), order.getId());
				order.setOrderStatus(OtaOrderStatusEnum.CancelBySystem.getId());
			}
			order.update();

            // 修改已使用券状态
            iPromotionPriceService.updateTicketStatus(order);
            logger.info("修改订单{}的券为可用状态,执行成功.", order.getId());
            orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELBYUSERWAITPAY.getId(), "", "", "");
        } else {
			logger.info("OTSMessage::取消订单::被用户取消。{}", order.getId());
			order.setOrderStatus(OtaOrderStatusEnum.CancelByU_NoRefund.getId());
			order.update();

			// 修改已使用券状态
			iPromotionPriceService.updateTicketStatus(order);
			logger.info("修改订单{}的券为可用状态,执行成功.", order.getId());
			orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELBYUSER.getId(), "", "", "");
		}

		// 更新otaroomorder
		OtaRoomOrder roomOrderTemp = this.roomOrderDAO.findOtadRoomOrderByOtaOrderId(order.getId());
		roomOrderTemp.set("OrderStatus", order.getOrderStatus());
		roomOrderTemp.update();

		// ots解房
		unLockRoom(order);

		// 取消订单成功后 取消push消息
		pushMsgNo(order);
		/******* 取消钱包 ***********/
		cancelAvailableMoney(order);
		/******* 取消钱包 ***********/
		logger.info("OTSMessage::cancelOrder---end{}", order.getId());
	}

  public void cancelPmsOrder(OtaOrder order) {
      THotel hotel = this.hotelService.readonlyTHotel(order.getLong("hotelId"));
      String isNewPms = hotel.getStr("isNewPms");
      if (!"T".equals(isNewPms)) {
          if (order.getPayStatus() == PayStatusEnum.alreadyPay.getId()) {
              // 调用取消支付
              boolean result = payService.cancelPmsPay(order.getId(), PPayInfoTypeEnum.Y2U);
          }
          // 取消都要通知PMS 需要PMS,对订单是否是到付、订单状态是否支付进行判断
          logger.info("OTSMessage::取消订单::取消PMS订单::构建CancelOrder" + order.getId());
          CancelOrder cancelOrder = new CancelOrder();
          cancelOrder.setOrderId(order.getId());
          List<Long> customerIds = new ArrayList<>();
          for (OtaRoomOrder roomOrder : order.getRoomOrderList()) {
              customerIds.add(roomOrder.getLong("id"));
          }
          cancelOrder.setCustomerIds(customerIds);
          logger.info("OTSMessage::取消订单:调用PMS取消订单" + order.getId());
          ReturnObject<Object> returnObject = HotelPMSManager.getInstance().getService().cancelOrder(order.getHotelId(), cancelOrder);
          if (HotelPMSManager.getInstance().returnError(returnObject)) {
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELORDERPMSERROR.getId(), "", "", returnObject.getErrorMessage());
              logger.info("OTSMessage::取消订单:PMS取消订单出错。{} ,{} ,{}", order.getId(), returnObject.getErrorCode(), returnObject.getErrorMessage());
              throw MyErrorEnum.customError.getMyException("PMS取消订单出错:" + returnObject.getErrorMessage() + "；如需帮助请联系客服人员");
          } else {
              logger.info("OTSMessage::取消订单:PMS取消订单成功。" + order.getId());
              // pms取消订单完成，执行ots支付取消 调用取消支付
              cancelOrderPay(order);
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELORDERPMSOKCANCELORDERPAY.getId(), "", "", "");
          }
      } else if ("T".equals(isNewPms)) {
          // 取消都要通知PMS 需要PMS,对订单是否是到付、订单状态是否支付进行判断
          logger.info("OTSMessage::取消订单::取消NEWPMS订单::构建CancelOrder" + order.getId());
          JSONObject cancelOrder = new JSONObject();
          cancelOrder.put("hotelid", order.getHotelPms());
          cancelOrder.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
          cancelOrder.put("function", "cancelorder");
          cancelOrder.put("timestamp ", DateUtils.formatDateTime(new Date()));
          cancelOrder.put("otaorderid", order.getId());
          if (order.getPayStatus() == PayStatusEnum.alreadyPay.getId()) {
              // 新pms只需调用支付获取需要取消乐住币的金额和id，调用取消pms订单的时候一并下发
              PMSCancelParam result = payService.getPMSCancelParam(order, PPayInfoTypeEnum.Y2U);
              if(result.getPayId() != null){
                  cancelOrder.put("payid", result.getPayId());
                  cancelOrder.put("cancelcost", result.getPrice());
                  logger.info("取消订单需要回收乐住币的payid = {} , price = {} , orderid = {}",result.getPayId(),result.getPrice(),order.getId());
              }
          }
          cancelOrder.put("memo", "");
          logger.info("OTSMessage::取消订单:NEWPMS取消订单，参数：{}", cancelOrder.toJSONString());
          JSONObject returnObject = JSONObject.parseObject(doPostJson(UrlUtils.getUrl("newpms.url") + "/cancelorder", cancelOrder.toJSONString()));
          logger.info("OTSMessage::取消订单:NEWPMS取消订单，返回：{}", returnObject.toJSONString());
          if (returnObject.getBooleanValue("success")) {
              logger.info("OTSMessage::取消订单:NEWPMS取消订单成功。{}", order.getId());
              cancelOrderPay(order);
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELORDERPMSOKCANCELORDERPAY.getId(), "", "", "");
          } else {
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELORDERPMSERROR.getId(), "", "", returnObject.getString("errmsg"));
              logger.info("OTSMessage::取消订单:NEWPMS取消订单出错。{},参数:{},{},{}", order.getId(), cancelOrder.toJSONString(),
                      returnObject.getString("errorcode"), returnObject.getString("errmsg"));
              throw MyErrorEnum.customError.getMyException("NEWPMS取消订单出错:" + returnObject.getString("errmsg") + "；如需帮助请联系客服人员");
          }
      }
  }

  /**
   * 取消订单超级
   * 
   * @param order
   */
  public void cancelOrderSuper(OtaOrder order) {
      // 收回优惠劵
      ticketService.updateUTicketAvailable(order.getId());
      // 取消支付【只取消OTS帐、银行的帐】
      PPay pay = payService.cancelPay(order);

      order.setOrderStatus(OtaOrderStatusEnum.CancelByU_Refunded.getId());
      order.saveOrUpdate();
      //取消订单成功后  取消push消息
      pushMsgNo(order);
  }

  /**
   * 取消订单的第三方支付
   * 
   * @param order
   */
  public void cancelOrderPay(OtaOrder order) {
      returnTicketOrderPay(order);
      // 取消支付订单支付状态
      PPay pay = payService.cancelPay(order);
      logger.info("OTSMessage::CancelOrderPayListener::取消订单::payService.cancelOrder::OK::" + order.getId());
      logger.info("OTSMessage::CancelOrderPayListener::取消订单:PMS取消后改变ots状态。::" + order.getId());
      if ((pay == null) || pay.getNeedreturn().equals(NeedReturnEnum.ok)) {
          cancelOrder(order, OtaOrderStatusEnum.CancelByU_NoRefund);
      } else if (pay.getNeedreturn().equals(NeedReturnEnum.need)) {
          cancelOrder(order, OtaOrderStatusEnum.CancelByU_WaitRefund);
      } else if (pay.getNeedreturn().equals(NeedReturnEnum.no) || pay.getNeedreturn().equals(NeedReturnEnum.finish)) {
          cancelOrder(order, OtaOrderStatusEnum.CancelByU_Refunded);
      }
  }
  /**
   * 退还支付后的优惠券
   */
    private void returnTicketOrderPay(OtaOrder order) {
        logger.info("OTSMessage::cancelOrderPay:取消订单,退还优惠券。" + order.getId());
        iPromotionPriceService.updateTicketStatus(order);
        logger.info("修改订单{}的券为可用状态,执行成功.", order.getId());
    }

  /**
   * 删除订单
   */
  @Override
  public void delCancelOrder(OtaOrder order) {
      int orderStatus = order.getOrderStatus();
      if ((orderStatus >= OtaOrderStatusEnum.CancelByU_WaitRefund.getId()) 
          && (orderStatus <= OtaOrderStatusEnum.CancelByPMS.getId()) || 
          orderStatus == OtaOrderStatusEnum.CheckOut.getId() 
          || orderStatus == OtaOrderStatusEnum.Account.getId()) {
          order.setCanshow("F");
          logger.info("OTSMessage::delCancelOrder--设置canshow为F");
          // 最后保存订单
          order.saveOrUpdate();
      } else {
          logger.info("OTSMessage::delCancelOrder失败 订单状态不对，当前状态为:{}", order.getOrderStatus());
          throw MyErrorEnum.delOrder.getMyException("删除失败 订单没有在取消状态");
      }
  }

  @Override
  public void cancelOrder(OtaOrder order, OtaOrderStatusEnum status) {
      order.setOrderStatus(status.getId());
      this.roomOrderDAO.updateCancelByOrderID(order.getId(), status);
  }

  @Override
  public void deleteOrder(OtaOrder order) {
      for (OtaRoomOrder roomOrder : order.getRoomOrderList()) {
          this.deleteRoomOrder(roomOrder);
      }
      order.delete();
  }

  @Override
  public void deleteRoomOrder(OtaRoomOrder roomOrder) {
      this.checkInUserDAO.delectOtaCheckInUserByRoomOrderId(roomOrder.getLong("id"));
      roomOrder.delete();
  }

  public JSONObject convertToNewPms(OtaOrder order, List<OtaRoomOrder> roomOrders, List<OtaRoomPrice> otaRoomPrices) {
      logger.info("OTSMessage::createPmsOrder--进入convertToPms:{}", otaRoomPrices);
      THotel hotel = this.hotelService.readonlyTHotel(order.getHotelId());
      JSONObject addOrder = new JSONObject();
      addOrder.put("hotelid", hotel.get("pms"));
      addOrder.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
      addOrder.put("function", "addorder");
      addOrder.put("timestamp ", DateUtils.formatDateTime(new Date()));
      addOrder.put("otaorderid", order.getId());
      if(PromoTypeEnum.TJ.getCode().equals(order.getPromoType())){
          addOrder.put("ispromo", "T");
      }else {
          addOrder.put("ispromo", "F");
      }

      if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
          addOrder.put("ordertype", 1);
      } else {
          addOrder.put("ordertype", 2);
      }
      addOrder.put("contact", order.getContacts());
      addOrder.put("phone", order.getContactsPhone());
      if (this.iPromotionPriceService.isBindPromotion(order.getId())) {
      	addOrder.put("memo", "眯客优惠订单");
      } else {
      	addOrder.put("memo", order.getNote());
      }
      // 客单数据
      JSONArray customerno = new JSONArray();
      for (OtaRoomOrder roomOrder : roomOrders) {
          // 订单对应客单
          JSONObject customernoItem = new JSONObject();
          customernoItem.put("customerid", roomOrder.getId());
          customernoItem.put("roomid", roomOrder.getStr("roompms"));
          customernoItem.put("arrivetime", DateUtils.getStringFromDate(roomOrder.getDate("Begintime"), "yyyyMMddHHmmss"));
          customernoItem.put("leavetime", DateUtils.getStringFromDate(roomOrder.getDate("Endtime"), "yyyyMMddHHmmss"));
          // 单日房价list
          JSONArray costArray = new JSONArray();
          if (otaRoomPrices != null) {
              Collections.sort(otaRoomPrices, new Comparator<OtaRoomPrice>() {
                  @Override
                  public int compare(OtaRoomPrice param1, OtaRoomPrice param2) {
                      return param1.getStr("actiondate").compareTo(param2.getStr("actiondate"));
                  }
              });

              for (int i = 0; i < otaRoomPrices.size(); i++) {// 不用多传一天
                  OtaRoomPrice otaroomprice = otaRoomPrices.get(i);
                  if (otaroomprice.get("otaroomorderid").equals(roomOrder.getLong("id"))) {
                      JSONObject costItem = new JSONObject();

                      costItem.put("time", otaroomprice.getStr("actiondate"));
                      costItem.put("cost", otaroomprice.get("price"));
                      costArray.add(costItem);
                  }
                  if (costArray.size() == order.getDaynumber()) {
                      break;
                  }
              }
          }

          customernoItem.put("cost", costArray);
          List<OtaCheckInUser> otaCheckInUsers = checkInUserDAO.findOtaCheckInUsers(order.getId());
          // OtaCheckInUser otaCheckInUser =
          // checkInUserDAO.findOtaCheckInUser(34385L);
          if (otaCheckInUsers.size() > 0) {
              JSONArray users = new JSONArray();
              for (OtaCheckInUser otaCheckInUser : otaCheckInUsers) {
                  //name:’’ //入住人姓名   idtype:’’ //证件类型    idno:’’ //证件号 phone ：//电话 ispermanent :  //是否常住人 （1 常住人 2 非常住人）
                  JSONObject checkInUser = new JSONObject();
                  checkInUser.put("name", otaCheckInUser.getName());
//                if (!Strings.isNullOrEmpty(otaCheckInUser.getStr("Sex"))) {
//                    checkInUser.put("Sex", SexEnum.getByName(otaCheckInUser.getStr("Sex")).getId());
//                } else {
//                    checkInUser.put("Sex", "");
//                }
//                if (null != otaCheckInUser.getDate("birthday")) {
//                    checkInUser.put("Birthday", DateUtils.getStringFromDate(otaCheckInUser.getDate("birthday"), DateUtils.FORMATSHORTDATETIME));
//                } else {
//                    checkInUser.put("Birthday", "");
//                }
//                if (!Strings.isNullOrEmpty(otaCheckInUser.getStr("CardType"))) {
//                    checkInUser.put("idtype", CardTypeEnum.getByName(otaCheckInUser.getStr("CardType")).getId());
//                } else {
//                    checkInUser.put("idtype", "");
//                }
//                checkInUser.put("idno", otaCheckInUser.get("Cardid", ""));
//                checkInUser.put("Nation", "");// otaCheckInUser.get("Ethnic",""));
//                checkInUser.put("ADDRESS", otaCheckInUser.get("Address", ""));
                  checkInUser.put("phone", otaCheckInUser.get("Phone", ""));
                  users.add(checkInUser);
              }
              customernoItem.put("user", users);
          }

          customerno.add(customernoItem);
      }

      addOrder.put("customerno", customerno);
      return addOrder;
  }

  @Override
  public List<PmsOtaAddOrder> convertToPms(List<OtaRoomOrder> roomOrders, List<OtaRoomPrice> otaRoomPrices,OtaOrder order) {
      logger.info("OTSMessage::createPmsOrder--进入convertToPms:{}", otaRoomPrices);
      List<PmsOtaAddOrder> addOrders = new ArrayList<PmsOtaAddOrder>();
      for (OtaRoomOrder roomOrder : roomOrders) {
          PmsOtaAddOrder addOrder = new PmsOtaAddOrder();
          addOrder.setOtaOrderId(roomOrder.getLong("hotelid"));
          addOrder.setCustomNo(roomOrder.getLong("id"));
          logger.info("OTSMessage::convertToPms--进入convertToPms:{},{}", roomOrder.getLong("hotelid"), roomOrder.getLong("id"));
          // 关联客单 被关联客单 关联自身
          addOrder.setLinkOrderId(roomOrder.getLinkRoomOrder() == null ? 0 : roomOrder.getLinkRoomOrder().getLong("id"));
          addOrder.setContact(roomOrder.getStr("contacts"));
          addOrder.setPhone(roomOrder.getStr("contactsphone"));
          addOrder.setPromotion(roomOrder.getStr("promotionno"));
          addOrder.setOrdertype(OrderTypeEnum.getByID(roomOrder.getInt("ordertype")).getKey());
          addOrder.setPricetype(PriceTypeEnum.getByID(roomOrder.getInt("pricetype")).getKey());
          if (this.iPromotionPriceService.isBindPromotion(order.getId())) {
          	addOrder.setMemo("眯客优惠订单");
			} else {
				addOrder.setMemo(roomOrder.getStr("note"));
			}
          addOrder.setRoompms(roomOrder.getStr("roompms"));
          addOrder.setArrivaltime(roomOrder.getDate("begintime"));
          addOrder.setExcheckouttime(roomOrder.getDate("endtime"));
          addOrder.setBreakfastcnt(roomOrder.getInt("breakfastnum"));
          logger.info("OTSMessage::convertToPms--setrCostList--addOrder:{}", addOrder);
          List<OtaRoomPrice> otaRoomPrices_room = new ArrayList<>();
          if (otaRoomPrices != null) {
              for (OtaRoomPrice otaRoomPrice : otaRoomPrices) {
                  if (otaRoomPrice.get("otaroomorderid").equals(roomOrder.get("id"))) {
                      otaRoomPrices_room.add(otaRoomPrice);
                  }
              }
          }
          logger.info("OTSMessage::convertToPms--setrCostList--roomorder:{},{}", roomOrder, otaRoomPrices_room);
          addOrder.setrCostList(this.getPmsRCost(roomOrder, otaRoomPrices_room));// 获取日租时价格表

          if (StringUtils.isNotBlank(roomOrder.getStr("contacts"))) {
              PmsCheckinPerson pmsCheckinPerson=this.getPmsCheckinPerson(roomOrder);
              addOrder.setCheckin(pmsCheckinPerson);
          }
          logger.info("convertToPms,订单id：{},订单详细：{}",addOrder.getOtaOrderId(),JsonKit.toJson(addOrder));
          addOrders.add(addOrder);
      }
      return addOrders;
  }

  private PmsCheckinPerson getPmsCheckinPerson(OtaRoomOrder roomOrder) {
      PmsCheckinPerson pmsCheckInPerson = new PmsCheckinPerson();
      List<OtaCheckInUser> otaCheckInUserList = checkInUserDAO.findOtaCheckInUserList(roomOrder.getLong("id"));

      if (StringUtils.isNotBlank(roomOrder.getStr("contacts"))) {
          pmsCheckInPerson.setCpname(roomOrder.getStr("contacts"));
      }
      if (otaCheckInUserList != null && otaCheckInUserList.size() > 0) {
          String sname = "";
          for (OtaCheckInUser otaCheckInUser : otaCheckInUserList) {
              if (StringUtils.isNotBlank(otaCheckInUser.getStr("Name"))) {
                  sname += otaCheckInUser.getStr("Name") + ";";
              }
              if (StringUtils.isNotBlank(otaCheckInUser.getStr("Phone"))) {
                    pmsCheckInPerson.setCpphone(otaCheckInUser.getStr("Phone"));
              }
          }
          if (StringUtils.isNotBlank(sname)) {
              pmsCheckInPerson.setCpname(sname.substring(0, sname.length() - 1));
          }
      }
      return pmsCheckInPerson;
  }

  private List<PmsRCost> getPmsRCost(OtaRoomOrder roomOrder, List<OtaRoomPrice> otaRoomPrices) {
      List<PmsRCost> rCostList = new ArrayList<PmsRCost>();
      // 使用优惠券后的的优惠价格
      for (OtaRoomPrice roomPrice : otaRoomPrices) {
          PmsRCost pmsRCost = new PmsRCost();
          pmsRCost.setPrice(roomPrice.getBigDecimal("pmsprice"));
          pmsRCost.setTime(roomPrice.getDate("createtime"));
          rCostList.add(pmsRCost);
      }
      return rCostList;
  }

  public void saveQiekePromoPrice(Long promotionId, Long otaOrderId, boolean onlineUsed, boolean offlineUsed) {
      // 校验优惠券线上线下是否超过使用次数
      if (offlineUsed && onlineUsed) {
          return;
      }
      BHotelPromotionPrice hPromoPrice = this.orderDAO.findHPromoPriceByOrderId(otaOrderId);
      // 校验订单对应的酒店是否有配置议价优惠券
      if (null != hPromoPrice) {
          BigDecimal onlinePrice = onlineUsed ? BigDecimal.ZERO : hPromoPrice.getBigDecimal("onlineprice");
          BigDecimal offlinePrice = offlineUsed ? BigDecimal.ZERO : hPromoPrice.getBigDecimal("offlineprice");
          this.savePromotionPrice(promotionId, onlinePrice, offlinePrice, otaOrderId);
          return;
      }
      // 酒店无配置使用默认切客优惠券配置
      BPromotion promotion = this.orderDAO.findPromotion(promotionId);
      // ITicketParse parse = promotion.createParseBean(null);
      // parse.checkUsable();
      // BigDecimal onlinePrice = onlineUsed ? BigDecimal.ZERO :
      // parse.getOnlinePrice();
      // BigDecimal offlinePrice = offlineUsed ? BigDecimal.ZERO :
      // parse.getOfflinePrice();
      // savePromotionPrice(promotionId, onlinePrice, offlinePrice,
      // otaOrderId);
  }

  public BPromotionPrice savePromotionPrice(Long promotionId, BigDecimal price, BigDecimal offlineprice, Long otaOrderId) {
      BPromotionPrice promoPrice = this.getPromotionPriceByOrderId(otaOrderId, promotionId);
      if (promoPrice == null) {
          promoPrice = new BPromotionPrice();
          BPromotion promotion = new BPromotion();
          promotion.set("id", promotionId);
          promoPrice.set("promotion", promotion);
          promoPrice.set("offlineprice", offlineprice);
          promoPrice.set("price", price);
          OtaOrder order = new OtaOrder();
          order.setId(otaOrderId);
          promoPrice.set("order", order);
          promoPrice.update();
          return promoPrice;
      }
      promoPrice.set("offlineprice", offlineprice);
      promoPrice.set("price", price);
      promoPrice.update();
      return promoPrice;
  }


	public OtaRoomOrder saveRoomOrder(OtaOrder order, OtaRoomOrder newRoomOrder, boolean isQieKe) {
		Transaction t = Cat.newTransaction("Order.doCreateOrder", "saveRoomOrder");
		try {
			// 这里需要先保存 以保证下面 方法 能获取到OtaRoomOrder的ID
			// 总价格只保存入住的价格 离店那天不计算
			if ((newRoomOrder == null) || (newRoomOrder.getDate("begintime") == null) || (newRoomOrder.getDate("endtime") == null)) {
				throw MyErrorEnum.errorParm.getMyException("客单必填项不完整");
			}
			Date createtime = (Date) order.getDate("createtime").clone();
			Date begintime = (Date) order.getDate("begintime").clone();
			Date endtime = (Date) order.getDate("endtime").clone();
			this.logger.info("saveRoomOrder::begintime：{}，endtime：{}",begintime, endtime);
			if (DateUtils.getDiffHoure(DateUtils.getDatetime(createtime), DateUtils.getDatetime(begintime)) <= 2 
			    || DateUtils.getStringFromDate(createtime, "yyyyMMdd").equals(DateUtils.getStringFromDate(begintime, "yyyyMMdd"))) {
				this.logger.info("减2小时了");
			    begintime = DateUtils.addHours(createtime, -2);
			}
            /****************************************动态提取房价*******************************************/	
            // 调用房态接口动态取得房价(目前如果list为空，则按照以前取数据库值计算房价)
			if (hotelPriceService.isUseNewPrice()) {
				this.logger.info("isUseNewPrice:使用动态取得房价");
				List<RoomTypePriceBean> roomPriceList = hotelPriceService.getRoomtypePrices(newRoomOrder.getLong("hotelid"), newRoomOrder.getLong("roomtypeid"),
						DateUtils.getStringFromDate(begintime, DateUtils.FORMATSHORTDATETIME), DateUtils.getStringFromDate(endtime, DateUtils.FORMATSHORTDATETIME));
				if (roomPriceList != null && roomPriceList.size() > 0) {
					newRoomOrder.set("totalprice", this.getTotalCostByList(roomPriceList, isQieKe));
					newRoomOrder.put("orderday", roomPriceList.size());
					newRoomOrder.set("price", isQieKe ? roomPriceList.get(0).getPrice() : roomPriceList.get(0).getMikeprice()); // a规则切客取门市,其他取Mike价
					newRoomOrder.saveOrUpdate();
					saveOrUpdateOtaCheckInUser(newRoomOrder, (List<OtaCheckInUser>) newRoomOrder.get("UserList")); // 保存订单入住人
					// 修改价钱:对象里会多保存离店那天的价格
					this.priceService.saveOtaRoomPriceByOtaRoomOrder(newRoomOrder, roomPriceList, isQieKe);
				}
			} else {
				Bean roomType = hotelDAO.findRoomTypeByRoomtypeid("" + newRoomOrder.getLong("roomtypeid"));
				List<Date> dateList = DateTools.getBeginDateList(begintime, endtime);
				Map<String, BigDecimal> map = roomService.getCost(newRoomOrder.getLong("otaorderid"), roomType, newRoomOrder.getLong("hotelid"), newRoomOrder.getLong("roomtypeid"),
						newRoomOrder.getLong("mid"), dateList, isQieKe);
				if ((map == null) || (map.size() == 0)) {
					throw MyErrorEnum.saveOrderCost.getMyException("价格查询错误");
				}
				Map<String, BigDecimal> mapPart = this.getsubLastCostByMap(map);
				newRoomOrder.set("totalprice", this.getTotalCostByMap(mapPart));
				newRoomOrder.put("orderday", mapPart.size());
				newRoomOrder.set("price", this.getFirstCostByMap(map));
				newRoomOrder.saveOrUpdate();
				saveOrUpdateOtaCheckInUser(newRoomOrder, (List<OtaCheckInUser>) newRoomOrder.get("UserList")); // 保存订单入住人
				// 修改价钱:对象里会多保存离店那天的价格
				this.priceService.saveOtaRoomPriceByOtaRoomOrder(newRoomOrder, map);
			}
            /****************************************动态提取房价*******************************************/	
			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			t.setStatus(e);
			throw e;
		} finally {
			t.complete();
		}
		return newRoomOrder;
	}
	

  public BigDecimal getTotalCostByMap(Map<String, BigDecimal> map) {
      BigDecimal total = new BigDecimal(0);
      if ((map != null) && (map.size() > 0)) {
          for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
              BigDecimal price = entry.getValue();
              if (price != null) {
                  total = total.add(price);
              } else {
                  // 只要有一个客单价格为空 报错
                  throw MyErrorEnum.saveOrderCost.getMyException("价格获取错误");
              }
          }
          return total;
      }
      
      return null;
  }

  public BigDecimal getFirstCostByMap(Map<String, BigDecimal> map) {
      List<Entry<String, BigDecimal>> list = new ArrayList<>();
      if ((map != null) && (map.size() > 0)) {
          for (Entry<String, BigDecimal> entry : map.entrySet()) {
              list.add(entry);
          }
          Collections.sort(list, new Comparator<Entry<String, BigDecimal>>() {
              @Override
              public int compare(Entry<String, BigDecimal> arg0, Entry<String, BigDecimal> arg1) {
                  return arg0.getKey().compareTo(arg1.getKey());
              }
          });
          return list.get(0).getValue();
      }
      return null;
  }

    public BigDecimal getTotalCostByList(List<RoomTypePriceBean> roomPriceList, boolean flag) {
        BigDecimal total = new BigDecimal(0);
        if ((roomPriceList != null) && (roomPriceList.size() > 0)) {
        	for (int i = 0; i < roomPriceList.size(); i++) {
        		RoomTypePriceBean rtpBean = roomPriceList.get(i);
        		BigDecimal price = new BigDecimal(0);
        		if(flag){
        			price = rtpBean.getPrice();// a规则切客 取门市价
        		} else {
        			price = rtpBean.getMikeprice(); // a规则非切客 b规则
        		}
                if (price != null) {
                    total = total.add(price);
                } else {
                    // 只要有一个客单价格为空 报错
                    throw MyErrorEnum.saveOrderCost.getMyException("价格获取错误");
                }
            }
            return total;
        }
        return null;
    }

    public Map<String, BigDecimal> getsubLastCostByMap(Map<String, BigDecimal> map) {
        List<Entry<String, BigDecimal>> list = new ArrayList<>();
        if ((map != null) && (map.size() > 0)) {
            for (Entry<String, BigDecimal> entry : map.entrySet()) {
                list.add(entry);
            }
            Collections.sort(list, new Comparator<Entry<String, BigDecimal>>() {
                @Override
                public int compare(Entry<String, BigDecimal> arg0, Entry<String, BigDecimal> arg1) {
                    return arg0.getKey().compareTo(arg1.getKey());
                }
            });
            // 减去最后一天
            // list.remove(list.size() - 1);
            Map<String, BigDecimal> newMap = new HashMap<String, BigDecimal>();
            for (Entry<String, BigDecimal> entry : list) {
                newMap.put(entry.getKey(), entry.getValue());
            }
            return newMap;
        }
        return null;
    }

  public boolean checkDate(Date check, Date begin, Date end) {
      Calendar ca = Calendar.getInstance();
      ca.setTime(check);
      ca.set(Calendar.HOUR, 0);
      ca.set(Calendar.MINUTE, 0);
      ca.set(Calendar.SECOND, 1);
      ca.set(Calendar.MILLISECOND, 1);
      check = ca.getTime();
      ca.setTime(begin);
      ca.set(Calendar.HOUR, 0);
      ca.set(Calendar.MINUTE, 0);
      ca.set(Calendar.SECOND, 0);
      ca.set(Calendar.MILLISECOND, 0);
      begin = ca.getTime();

      ca.setTime(end);
      ca.set(Calendar.HOUR, 23);
      ca.set(Calendar.MINUTE, 0);
      ca.set(Calendar.SECOND, 0);
      ca.set(Calendar.MILLISECOND, 0);
      end = ca.getTime();
      if ((begin != null) && check.before(begin)) {
          return false;
      }
      if ((end != null) && check.after(end)) {
          return false;
      }
      return true;
  }

  // 返回 Map<日期yyyyMMdd, 价格>
  /*
   * public Map<String, BigDecimal> getCost(Long hotelId, Long roomTypeId,
   * Long mId, Date beginTime, Date endTime, boolean isQieKe) {
   * 
   * // THotel hotel=HotelCacheManager.getInstance().getTHotel(hotelId);
   * TRoomType roomType = this.roomTypeDAO.findTRoomType(roomTypeId);
   * 
   * // 价格规则 数据【平台价】 List<TPrice> priceList =
   * this.priceDAO.findPrice(roomTypeId);// 房型 时间策略 // 价格 TBasePrice basePrice
   * = this.basePriceDAO.findBasePrice(roomTypeId); Map<String, BigDecimal>
   * costMap = new HashMap<String, BigDecimal>();
   * 
   * // // 1.房型基本价格【门市价】 BigDecimal $cost = roomType.get("cost");
   * 
   * // // 2.base price价格 if (basePrice != null) { if (basePrice.get("price")
   * != null) { $cost = basePrice.get("price"); } else if
   * (basePrice.getBigDecimal("subper") != null) {// 上下浮动固定价格 // $cost =
   * $cost.add(basePrice.getBigDecimal("subper")); $cost = $cost.multiply(new
   * BigDecimal(1).subtract(basePrice.getBigDecimal("subper"))); } else if
   * (basePrice.getBigDecimal("subprice") != null) {// 上下浮动百分比 // $cost =
   * $cost.multiply(basePrice.getBigDecimal("subprice").add(new
   * BigDecimal(1))); $cost =
   * $cost.subtract(basePrice.getBigDecimal("subprice")); } }
   * 
   * // // 3.价格策略算出的价格 SimpleDateFormat dateformat = new
   * SimpleDateFormat("yyyyMMdd"); CronExpression cronExpre = null; TPriceTime
   * tPriceTime = null; String subCronExpreStr = null; String cronExpreStr =
   * null; String addCronExpreStr = null; String[] subCronExpreArr = null;
   * String[] cronExpreArr = null; String[] addCronExpreArr = null; TPrice
   * tempPrice = null; Date pBegDate = null; Date pEndDate = null; boolean
   * cronFlag = true;// true：包含此价格策略 false：去除此价格策略 boolean breakfalg = false;
   * // true :跳过下方 // for (Date date : dateList) { while
   * (beginTime.compareTo(endTime) < 0 || costMap.size() <= 1) { tempPrice =
   * null; cronFlag = true; // Date date = DateTools.getBeginDate(beginTime,
   * 0); for (TPrice tPrice : priceList) { breakfalg = false; tPriceTime =
   * tPrice.getTPriceTime(); pBegDate = tPriceTime.get("BeginTime"); if
   * ((pBegDate != null) && beginTime.before(pBegDate)) { continue; } pEndDate
   * = tPriceTime.get("EndTime"); if ((pEndDate != null) &&
   * beginTime.after(pEndDate)) { continue; } addCronExpreStr =
   * tPriceTime.getStr("AddCron"); subCronExpreStr =
   * tPriceTime.getStr("SubCron"); cronExpreStr = tPriceTime.get("Cron"); try
   * { if (StringUtils.isNotBlank(subCronExpreStr)) { subCronExpreArr =
   * subCronExpreStr.split("\\|"); for (int i = 0; i < subCronExpreArr.length;
   * i++) { cronExpre = new CronExpression(subCronExpreArr[i]); if
   * (cronExpre.isSatisfiedBy(beginTime)) { // tempPrice = tPrice; // cronFlag
   * = false; breakfalg = true; } } } if ((breakfalg == false) &&
   * StringUtils.isNotBlank(cronExpreStr)) { cronExpreArr =
   * cronExpreStr.split("\\|"); for (int i = 0; i < cronExpreArr.length; i++)
   * { cronExpre = new CronExpression(cronExpreArr[i]); if
   * (cronExpre.isSatisfiedBy(beginTime)) { tempPrice = tPrice; cronFlag =
   * true; breakfalg = true; break; } } } if ((breakfalg == false) &&
   * StringUtils.isNotBlank(addCronExpreStr)) { addCronExpreArr =
   * addCronExpreStr.split("\\|"); for (int i = 0; i < addCronExpreArr.length;
   * i++) { cronExpre = new CronExpression(addCronExpreArr[i]); if
   * (cronExpre.isSatisfiedBy(beginTime)) { tempPrice = tPrice; cronFlag =
   * true; break; } } } } catch (ParseException e) { throw new
   * RuntimeException(e); } } BigDecimal cost = $cost; if (cronFlag &&
   * !isQieKe) {// 非切客情况、包含此价格策略 logger.info("非切客走的平台价"); if (tempPrice !=
   * null) { if (tempPrice.get("price") != null) { cost =
   * tempPrice.get("price"); } else if (tempPrice.get("subper") != null) {//
   * 上下浮动固定价格 $cost = $cost.multiply(new
   * BigDecimal(1).subtract(tempPrice.getBigDecimal("subper"))); } else if
   * (tempPrice.get("subprice") != null) {// 上下浮动百分比 $cost =
   * $cost.subtract(tempPrice.getBigDecimal("subprice")); } } }
   * 
   * cost = cost.setScale(0, BigDecimal.ROUND_UP);
   * costMap.put(dateformat.format(beginTime), cost); beginTime =
   * DateUtils.addDays(beginTime, 1); // beginTime = DateUtils.getDateAdded(1,
   * beginTime); } // beginTime -= DateTools.DAY_MILLSECONDS; beginTime =
   * DateUtils.addDays(beginTime, -1); String dBeg =
   * dateformat.format(beginTime); String dEnd = dateformat.format(endTime);
   * if ((DateUtils.getHour(DateUtils.getDatetime(beginTime)) >=
   * DateTools.BEGIN_HOUR) && dBeg.equals(dEnd)) { costMap.remove(dEnd); } //
   * 如果价格都没有 if ((costMap == null) || (costMap.size() == 0)) {
   * MyErrorEnum.saveOrderCost.getMyException("价格获取错误"); } return costMap; }
   */
  @Override
  public Long saveQiekePromoPrice(Long otaOrderId) {
      logger.info("OTSMessage::saveQiekePromoPrice:otaOrderId:" + otaOrderId + "---start");
      // 获取订单信息
      OtaOrder otaOrder = this.findOtaOrderByIdNoCancel(otaOrderId);
      Long qiekePromoId = this.getQiekePromoId();
      logger.info("OTSMessage::getQiekePromoId:qiekePromoId:" + qiekePromoId);
      // 校验同一酒店同一房间是否使用过
      String qiekeStartTime = OrderUtil.getQiekeStartTime();
      boolean sameRoomQieke = this.checkSameRoomQiekeUsed(otaOrder, qiekeStartTime, qiekePromoId);
      if (sameRoomQieke) {
          logger.info("OTSMessage::checkSameRoomQiekeUsed:sameRoomQieke:" + sameRoomQieke);
          return null;
      }
      // 校验web渠道使用切客优惠码
      if (OSTypeEnum.H.getId().equals(otaOrder.getOstype())) {
          boolean webPromoUsed = this.checkQiekeWebPromoUsed(otaOrder.getMid(), otaOrder.getHotelId());
          logger.info("OTSMessage::checkQiekeWebPromoUsed:webPromoUsed:" + webPromoUsed);
          if (webPromoUsed) {
              return null;
          }
          // 保存切客优惠码价格信息
          this.saveQiekePromoPrice(qiekePromoId, otaOrderId, false, false);
          return qiekePromoId;
      }
      // 校验线上渠道使用切客优惠码
      boolean onlineUsed = this.checkQiekeOnlineUsed(otaOrder);
      // 校验线下渠道使用切客优惠码
      boolean offlineUsed = this.checkQiekeOfflineUsed(otaOrder);
      // 保存切客优惠码价格信息
      this.saveQiekePromoPrice(qiekePromoId, otaOrderId, onlineUsed, offlineUsed);
      logger.info("OTSMessage::saveQiekePromoPrice:qiekePromoId" + qiekePromoId + "---end");
      return qiekePromoId;
  }

  @Override
  public BPromotionPrice getPromotionPriceByOrderId(Long orderId, Long promoId) {
      return ibPromotionPriceDao.findPromotionPricesByOrderIdAndPromoId(orderId, promoId);
  }

  @Override
  public boolean checkQiekeOfflineUsed(OtaOrder otaOrder) {
      Long count = this.countQiekeSts(otaOrder, OrderTypeEnum.PT);
      int limitCount = Integer.parseInt(SysConfig.getInstance().getSysValueByKey(Constant.qiekeOfflineLimit).trim());
      if (count >= limitCount) {
          return true;
      }
      return false;
  }

  @Override
  public boolean checkQiekeOnlineUsed(OtaOrder otaOrder) {
      Long count = this.countQiekeSts(otaOrder, OrderTypeEnum.YF);
      // Logger.getLogger(getClass()).error("预付切客数量："+count);
      int limitCount = Integer.parseInt(SysConfig.getInstance().getSysValueByKey(Constant.qiekeOnlineLimit).trim());
      if (count >= limitCount) {
          return true;
      }
      return false;
  }

  @Override
  public boolean checkSameRoomQiekeUsed(OtaOrder otaOrder, String qiekeStartTime, Long qiekePromoId) {
      // 获取pms客单列表
      List<PmsRoomOrder> pmsRoomOrderList = this.orderDAO.getOrderIdListBySameRoom(otaOrder, qiekeStartTime);
      OrderServiceImpl.logger.info("===pmsRoomOrderList.size()===" + pmsRoomOrderList.size());
      if (pmsRoomOrderList.size() == 0) {
          return false;
      }
      // 获取客单对应的订单号列表
      List<Long> orderIdList = new ArrayList<>();
      OrderServiceImpl.logger.info("===orderIdList===" + orderIdList);
      for (PmsRoomOrder pmsRoomOrder : pmsRoomOrderList) {
          Long orderId = this.orderDAO.getOrderIdListByPmsOrder(pmsRoomOrder);
          if (null != orderId) {
              orderIdList.add(orderId);
          }
      }
      // 无ota订单号直接返回
      if (orderIdList.size() == 0) {
          return false;
      }
      // 查询订单对应的切客优惠券记录
      List<UUseTicketRecord> ticketRecords = this.orderDAO.getTicketRecordsByOrderId(orderIdList, qiekePromoId);
      OrderServiceImpl.logger.info("===ticketRecords.size()===" + ticketRecords.size());
      if (ticketRecords.size() == 0) {
          return false;
      }
      // 校验优惠券记录是否是已使用
      return this.checkTicketUsed(ticketRecords);
  }

  private Long getQiekePromoId() {
      return new Long(SysConfig.getInstance().getSysValueByKey(Constant.qieketicketno).trim());
  }

  /**
   * 校验是否有取消订单的优惠券流水 排除取消订单的优惠券使用记录 取消订单同一个payId有两条记录
   *
   * @param ticketRecords
   * @return
   */
  private boolean checkTicketUsed(List<UUseTicketRecord> ticketRecords) {
      OrderServiceImpl.logger.info("===checkTicketUsed===");
      List<Long> payIdList = new ArrayList<>();
      int flag = 0;
      for (UUseTicketRecord record : ticketRecords) {
          if (payIdList.contains(record.getLong("pay"))) {
              flag--;
              continue;
          }
          payIdList.add(record.getLong("pay"));
          flag++;
      }
      if (flag > 0) {
          return true;
      }
      return false;
  }

  /**
   * 获取orderType渠道用户历史订单使用切客优惠码的数量
   *
   * @param otaOrder
   * @param orderType
   * @return
   */
  private Long countQiekeSts(OtaOrder otaOrder, OrderTypeEnum orderType) {
      return this.orderDAO.countQiekeBySts(otaOrder, orderType);
  }

  @Override
  public boolean checkQiekeWebPromoUsed(Long mid, Long hotelId) {
      long count = this.orderDAO.checkQiekeWebPromoUsed(mid, hotelId);
      int limitCount = Integer.parseInt(SysConfig.getInstance().getSysValueByKey(Constant.qiekeHtmlLimit).trim());
      if (count >= limitCount) {
          return true;
      }
      return false;
  }

  @Override
  public OtaOrder findOtaOrderByIdNoCancel(Long otaOrderId) {
      OtaOrder order = this.orderDAO.findOtaOrderById(otaOrderId);
      List<OtaOrderStatusEnum> statusList = new ArrayList<>();
      for (OtaOrderStatusEnum otaOrderStatusEnum : OtaOrderStatusEnum.values()) {
          if ((otaOrderStatusEnum.getId().intValue() < OtaOrderStatusEnum.CancelByU_WaitRefund.getId())
                  || (otaOrderStatusEnum.getId().intValue() >= OtaOrderStatusEnum.NotIn.getId())) {
              statusList.add(otaOrderStatusEnum);
          }
      }

      if (order != null) {
          List<OtaRoomOrder> roomOrderList = this.roomOrderDAO.findOtaRoomOrderByOrderId(otaOrderId, null);
          List<Long> roomOrderIdList = new ArrayList<>();// 所有客单Id
          for (OtaRoomOrder otaRoomOrder : roomOrderList) {
              roomOrderIdList.add(otaRoomOrder.getLong("id"));
          }
          // TODO 暂时没有入住人
          // List<OtaCheckInUser> allCheckUser =
          // checkInUserDAO.findUserByRoomOrder(roomOrderIdList);
          // List<OtaCheckInUser> userList = null;
          // for (OtaRoomOrder otaRoomOrder : roomOrderList) {
          // userList = new ArrayList<>();//每个客单的checkInUser
          // for (OtaCheckInUser otaCheckInUser : allCheckUser) {
          // if(otaRoomOrder.getId().equals(otaCheckInUser.getRoomOrder().getId())){
          // userList.add(otaCheckInUser);
          // }
          // }
          // otaRoomOrder.setUserList(userList);//客单中所有入住人信息
          // }
          order.put("roomorderlist", roomOrderList);// 订单中所有客单
      }
      return order;
  }

  @Override
  public OtaOrder findOtaOrderById(Long otaOrderId) {
      return this.orderDAO.findOtaOrderById(otaOrderId);
    }

    /**
     * 0，无需领取
     * 1，还未领取
     * 2，已经领取
     *
     * @param otaOrderId
     */
    @Override
    public void receiveCashBack(Long otaOrderId) {
        OtaOrder order = new OtaOrder().dao.findById(otaOrderId);
        order.setIsReceiveCashBack(ReceiveCashBackEnum.ReceiveedCashBack.getId());
        order.update();
    }

    public OtaOrder findOtaOrderByScoreId(Long scoreId) {
        THotelScore hotelScore = scoreService.findScoreByScoreid(scoreId);
        if (hotelScore != null) {
            return this.orderDAO.findOtaOrderById(hotelScore.getOrderid());
        }
        return null;
    }

  @Override
  public PageObject<OtaOrder> findMyOtaOrder(Long hotelId, List<OtaOrderStatusEnum> statusList, String begintime, String endtime, Integer start,
          Integer limit, String isscore, List<OtaOrderStatusEnum> notStatusList) {
      Boolean canshow = true;// 能否过滤被删除隐藏的订单 true:没有被删除隐藏的 False:显示被删除隐藏的
      Long Mid = MyTokenUtils.getMidByToken("");
      List<OtaOrder> orders = this.orderDAO.findMyOtaOrderByMid(Mid, hotelId, statusList, begintime, endtime, start, limit, canshow, isscore,
              notStatusList);
      List<OtaOrder> returnOrders = new ArrayList<>();
      for (OtaOrder otaOrder : orders) {
          otaOrder = this.findOtaOrderById(otaOrder.getId());
          THotel hotel = this.hotelService.readonlyTHotel(otaOrder.getHotelId());
          otaOrder.put("defaultleavetime", hotel.get("defaultleavetime", "120000"));
          otaOrder.put("hoteladdress", hotel.get("detailaddr"));
          otaOrder.put("retentiontime", hotel.get("retentiontime", "180000"));
          returnOrders.add(otaOrder);
      }
      Long count = this.orderDAO.countMyOtaOrderByMid(Mid, hotelId, statusList, begintime, endtime, start, null, canshow, isscore, notStatusList);
      PageObject<OtaOrder> pageOrders = new PageObject<>(returnOrders, count);
      return pageOrders;
  }

  @Override
  public List<TicketInfo> getTicketInfos(Long orderId) {
      OtaOrder order = this.findOtaOrderById(orderId);
      // MemberMapper 未实现。
      Optional<UMember> findMemberById = this.memberService.findMemberById(Cast.to(order.get("mid"), 0l));
      if (findMemberById.isPresent()) {
          UMember member = findMemberById.get();
          return this.getTicketInfos(order, member);
      }
      return null;
  }

  @Override
  public List<TicketInfo> getTicketInfos(OtaOrder order, UMember member) {
      // 获取计算后的订单和客单价格
      order = this.calcOrderCost(order);
      // 获取切客及议价的优惠码信息
      if (order.getSpreadUser() != null) {
          return this.getOtherTicketInfos(order);
      }
      // 获取用户对应的立减优惠券列表
      List<UTicket> tickets = this.orderDAO.findTicketsByMemberId(Cast.to(member.getMid(), 0l));
      if (tickets == null) {
          return new ArrayList<TicketInfo>();
      }
      // 创建tickInfos
      List<TicketInfo> ticketInfos = this.createTicketInfos(order, tickets);
      // tickInfos排序
      ticketService.sortTicketInfos(ticketInfos);
      // ticketService.sortTicketByActivety(ticketInfos, order,
      // member.getMid());
      // 设置默认选中的优惠券
      ticketService.defaultTicketSelect(ticketInfos);
      return ticketInfos;
  }

  // /**
  // * 设置默认选中的优惠券
  // *
  // * @param ticketInfos
  // */
  // private void defaultTicketSelect(List<TicketInfo> ticketInfos) {
  // // 判断第一张是否可用
  // if ((ticketInfos == null) || (ticketInfos.size() < 1)) {
  // return;
  // }
  // TicketInfo ticketInfo = ticketInfos.get(0);
  // if (ticketInfo.getCheck()) {
  // ticketInfo.setSelect(true);
  // }
  // }

  // /**
  // * 对ticketInfo排序
  // *
  // * @param ticketInfos
  // */
  // private void sortTicketInfos(List<TicketInfo> ticketInfos) {
  // if (ticketInfos == null) {
  // return;
  // }
  // Collections.sort(ticketInfos, new Comparator<TicketInfo>() {
  // @Override
  // public int compare(TicketInfo param1, TicketInfo param2) {
  // // 可用的优惠券优先
  // if (param1.getCheck() != param2.getCheck()) {
  // return param2.getCheck().compareTo(param1.getCheck());
  // }
  // // 优惠券结束日期较早的优先
  // return param1.getEndtime().compareTo(param2.getEndtime());
  // }
  // });
  // }

  /**
   * 封装ticketInfo集合
   *
   * @param order
   * @param tickets
   */
  private List<TicketInfo> createTicketInfos(OtaOrder order, List<UTicket> tickets) {
      ArrayList<TicketInfo> ticketInfos = new ArrayList<TicketInfo>();
      for (UTicket ticket : tickets) {
          if (!PromotionTypeEnum.immReduce.toString().equals(ticket.getPromotion().get("type"))) {
              continue;
          }
          TicketInfo info = new TicketInfo();
          ITicketParse ticketParse = null;
          try {
              // ticketParse = ticket.createParseBean(order);
              ticketParse.checkUsable();
              info.setCheck(true);
          } catch (Exception e) {
              info.setCheck(false);
          }
          info.setId(ticket.getPromotion().getLong("id"));
          info.setName(ticket.getPromotion().getStr("name"));
          info.setSubprice(ticketParse.allSubSidy());
          info.setOfflineprice(BigDecimal.ZERO);
          info.setSelect(false);
          info.setType(ticket.getPromotion().getInt("type"));
          info.setIsticket(true);
          info.setEndtime(ticket.getPromotion().getDate("endtime"));
          ticketInfos.add(info);
      }
      return ticketInfos;
  }

  /**
   * 获取切客和议价的优惠码信息
   *
   * @param order
   */
  private List<TicketInfo> getOtherTicketInfos(OtaOrder order) {
      ArrayList<TicketInfo> ticketInfos = new ArrayList<TicketInfo>();
      List<BPromotionPrice> promotionPrices = this.orderDAO.findPromotionPricesByOrderId(order.getId());
      if (promotionPrices == null) {
          return ticketInfos;
      }
      for (BPromotionPrice promotionPrice : promotionPrices) {
          TicketInfo info = new TicketInfo();
          info.setId(promotionPrice.getPromotionDAO().getLong("id"));
          info.setName(promotionPrice.getPromotionDAO().getStr("name"));
          info.setSelect(true);
          info.setCheck(true);
          info.setSubprice(promotionPrice.getBigDecimal("price"));
          info.setOfflineprice(promotionPrice.getBigDecimal("OfflinePrice"));
          info.setType(promotionPrice.getPromotionDAO().getInt("type"));
          info.setIsticket(false);
          ticketInfos.add(info);
      }
      return ticketInfos;
  }

  @Override
  public OtaOrder calcOrderCost(OtaOrder order) {
      // 只计算了订单总价 和每个客单总价
      BigDecimal temp = new BigDecimal(-1);
      BigDecimal total = temp;
      order.setTotalPrice(total);
      for (OtaRoomOrder otaRoomOrder : order.getRoomOrderList()) {
          if (total.equals(new BigDecimal(-1))) {
              total = new BigDecimal(0);
          }
          BigDecimal roomTotal = temp;
          otaRoomOrder.set("totalprice", roomTotal);
          // 存正确的价格 如果没有获取到价格是temp报错
          otaRoomOrder.set("totalprice", this.getTotalCostByOtaRoomOrder(order, otaRoomOrder, order.getSpreadUser() != null));
          if (otaRoomOrder.getBigDecimal("totalprice").equals(temp)) {
              throw MyErrorEnum.saveOrderCost.getMyException("获取价格错误");
          }
          total = total.add(otaRoomOrder.getBigDecimal("totalprice"));// 计算总价格
      }
      order.setTotalPrice(total);
      // if(order.getTotalPrice().equals(temp)){
      // throw MyErrorEnum.saveOrderCost.getMyException("获取价格错误");
      // }
      return order;
  }

	@Override
	public BigDecimal getTotalCostByOtaRoomOrder(OtaOrder order, OtaRoomOrder roomOrder, boolean isQieKe) {
		BigDecimal total = new BigDecimal(0);
		if ((roomOrder == null) || (roomOrder.getDate("begintime") == null) || (roomOrder.getDate("endtime") == null)) {
			throw MyErrorEnum.errorParm.getMyException("客单必填项不完整");
		}
		// 获取开始 结束之间的时间列表 返回的不添加最后的离店时间
		Date createtime = (Date) order.getDate("createtime").clone();
		Date begintime = (Date) order.getDate("begintime").clone();
		Date endtime = (Date) order.getDate("endtime").clone();
		this.logger.info("begintime：{}，endtime：{}",begintime, endtime);
		if (DateUtils.getDiffHoure(DateUtils.getDatetime(createtime), DateUtils.getDatetime(begintime)) <= 2 
		    || DateUtils.getStringFromDate(createtime, "yyyyMMdd").equals(DateUtils.getStringFromDate(begintime, "yyyyMMdd"))) {
			this.logger.info("减2小时了");
		    begintime = DateUtils.addHours(createtime, -2);
		}
		Bean roomType = hotelDAO.findRoomTypeByRoomtypeid("" + roomOrder.getLong("roomtypeid"));
		List<Date> dateList = DateTools.getBeginDateList(begintime, endtime);
		Map<String, BigDecimal> map = roomService.getCost(roomOrder.getLong("otaorderid"), roomType, roomOrder.getLong("hotelid"), roomOrder.getLong("roomtypeid"),
				roomOrder.getLong("mid"), dateList, isQieKe);
		if ((map != null) && (map.size() > 0)) {
			for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
				BigDecimal price = entry.getValue();
				if (price != null) {
					total = total.add(price);
				} else {
					// 只要有一个客单价格为空 报错
					throw MyErrorEnum.saveOrderCost.getMyException("价格获取错误");
				}
			}
			return total;
		}
		return null;
	}

  @Override
  public void changeOrderPriceType(Long orderId) {
      this.orderDAO.changeOrderPriceType(orderId);
      this.roomOrderDAO.changeRoomOrderPriceType(orderId);
  }

  @Override
  public void createPmsOrder(OtaOrder order) {
      logger.info("OTSMessage::createPmsOrder---start");
      logger.info("OTSMessage::createPmsOrder--开始的时候 {}",order);
      THotel hotel = this.hotelService.readonlyTHotel(order.getLong("hotelId"));
       logger.info("OTSMessage::createPmsOrder---hotelid:"+order.getLong("hotelId"));
      String isNewPms = hotel.getStr("isNewPms");
      List<OtaRoomOrder> roomOrders = order.getRoomOrderList();
      for (OtaRoomOrder otaRoomOrder : roomOrders) {
          if (StringUtils.isNotBlank(otaRoomOrder.getPmsRoomOrderNo())) {
              logger.info("OTSMessage::createPmsOrder---已经创建过pmsOrder::{}", order.getId());
              return;
          }
      }
      logger.info("OTSMessage::createPmsOrder--roomOrders:"+roomOrders);
      List<OtaRoomPrice> otaRoomPrices = priceService.findOtaRoomPriceByOrder(order);
      // 转换为PMS 接受对象
      if ("T".equals(isNewPms)) {// 新PMS
          JSONObject addOrderObject = convertToNewPms(order, roomOrders, otaRoomPrices);
          logger.info("OTSMessage::接口调用:pms2.0:订单号：{}", order.getId());
          logger.info("OTSMessage::接口调用:pms2.0:订单详细：{}", addOrderObject.toJSONString());
          JSONObject returnObject = JSONObject.parseObject(doPostJson(UrlUtils.getUrl("newpms.url") + "/addorder", addOrderObject.toJSONString()));
          logger.info("OTSMessage::返回:{}", returnObject.toJSONString());
          if (!returnObject.getBooleanValue("success")) {
              logger.info("OTSMessage::接口调用:submitAddOrder::创建psmOrder失败,errcode:{},errmsg:{}", returnObject.getString("errcode"),
                      returnObject.getString("errmsg"));
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ADDPMSORDERERR.getId(), "", "", returnObject.getString("errmsg"));
              if("OTS_HOTEL_OFFLINE".equals(returnObject.getString("errcode"))){
                  if(order.getOrderType()==OrderTypeEnum.PT.getId()){
                      Cat.logEvent("PmsOffLine", "toPayPMS2.0", Event.SUCCESS, "");
                  }
                  if(order.getOrderType()==OrderTypeEnum.YF.getId()){
                      Cat.logEvent("PmsOffLine", "prepayPMS2.0", Event.SUCCESS, "");
                  }
              }
              
              if ("527".equals(returnObject.getString("errcode"))) {
                  throw MyErrorEnum.customError.getMyException("房间没有了");
              } else {
                  throw MyErrorEnum.saveOrderPms.getMyException(returnObject.getString("errmsg"));
              }
          } else {
              logger.info("OTSMessage::接口调用:submitAddOrder::创建psmOrder成功" + order.getId());
              JSONArray tempRoomOrders = returnObject.getJSONArray("roomorder");
              for (int i = 0; i < tempRoomOrders.size(); i++) {
                  JSONObject pmsOtaAddOrder = tempRoomOrders.getJSONObject(i);
                  for (OtaRoomOrder tempRoom : order.getRoomOrderList()) {
                      if (pmsOtaAddOrder.getString("otaroomorderid").equals(String.valueOf(tempRoom.get("Id")))) {
                          tempRoom.set("PmsRoomOrderNo", pmsOtaAddOrder.getString("pmsroomorderid"));
                          logger.info("OTSMessage::PmsRoomOrderNo:{}", pmsOtaAddOrder.getString("pmsroomorderid"));
                          tempRoom.update();
                      }
                  }
              }
          }
      } else { // pms1.0
          List<PmsOtaAddOrder> addOrders = this.convertToPms(roomOrders, otaRoomPrices,order);

          logger.info("调用pms1.0submitAddOrder，订单号：{},订单详细：{}",order.getId(),gson.toJson(addOrders));
          // 旧pms交互
          logger.info("OTSMessage::接口调用:submitAddOrder:" + order.getHotelId() + "....");
          ReturnObject<List<PmsOtaAddOrder>> returnObject = HotelPMSManager.getInstance().getService()
                  .submitAddOrder(order.getHotelId(), addOrders);
          // 成功:订单不删除 失败:订单删除
          if (HotelPMSManager.getInstance().returnError(returnObject)) {
              logger.info("OTSMessage::接口调用:submitAddOrder::创建psmOrder失败,errorcode:{},errormsg:{}", returnObject.getErrorCode(),
                      returnObject.getErrorMessage());
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ADDPMSORDERERR.getId(), "", "", returnObject.getErrorMessage());
              
              if(PmsErrorEnum.offLine.getErrorCode().equals(returnObject.getErrorCode())){
                  if(order.getOrderType()==OrderTypeEnum.PT.getId()){
                      Cat.logEvent("PmsOffLine", "toPayPMS1.0", Event.SUCCESS, "");
                  }
                  if(order.getOrderType()==OrderTypeEnum.YF.getId()){
                      Cat.logEvent("PmsOffLine", "prepayPMS1.0", Event.SUCCESS, "");
                  }
              }
              
              if ("527".equals(returnObject.getErrorCode())) {
                  throw MyErrorEnum.customError.getMyException("房间没有了");
              } else {
                  throw MyErrorEnum.saveOrderPms.getMyException(returnObject.getErrorMessage());
              }
          } else {
              logger.info("OTSMessage::接口调用:submitAddOrder::创建psmOrder成功" + order.getId());
              List<OtaRoomOrder> tempRoomOrders = new ArrayList<>();
              for (PmsOtaAddOrder pmsOtaAddOrder : returnObject.getValue()) {
                  for (OtaRoomOrder tempRoom : order.getRoomOrderList()) {
                      if (pmsOtaAddOrder.getCustomNo().equals(tempRoom.get("Id"))) {
                          tempRoom.set("PmsRoomOrderNo", pmsOtaAddOrder.getPmsCustomNo());
                          tempRoomOrders.add(tempRoom);
                          tempRoom.update();
                      }
                  }
              }
          }
      }
      logger.info("OTSMessage::createPmsOrder---end");
  }

  @Override
  public void createPmsOrderAndLockRoomBeforerPay(OtaOrder order) {
      logger.info("OTSMessage::createPmsOrderAndLockRoomBeforerPay::start::" + order.getId());
      // pms 创建客单
      this.createPmsOrder(order);
      logger.info("OTSMessage::createPmsOrderAndLockRoomBeforerPay::createPmsOrder:: pms 创建客单::ok::" + order.getId());
  }

  @Override
  public void modifyPmsOrderStatusAfterPay(OtaOrder order) {
      logger.info("OTSMessage::modifyPmsOrderStatusAfterPay::start::" + order.getId());
      if (order.getOrderStatus() < OtaOrderStatusEnum.Confirm.getId()) {
          order.setOrderStatus(OtaOrderStatusEnum.Confirm.getId());
      }
      order.setPayStatus(PayStatusEnum.alreadyPay.getId());
      order.update();
      orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.MODIFYPMSORDERAFTERPAY.getId(), "", "支付完成", "");
      logger.info("OTSMessage::modifyPmsOrderStatusAfterPay::start::" + order.getId());
  }

  /**
   * 订单到付，并创建pms客单
   */
  @Override
  public void modifyOrderTypePT(OtaOrder order) {
      // pms 创建客单
      logger.info("OTSMessage::modifyOrderTypePT---start{}", order.getId());
      if (order.getOrderStatus() == OtaOrderStatusEnum.CancelBySystem.getId()) {
          throw MyErrorEnum.customError.getMyException("亲，已经超过15分钟，订单已经被系统取消");
      }
      if (order.getOrderStatus() < OtaOrderStatusEnum.Confirm.getId()) {
          order.setOrderStatus(OtaOrderStatusEnum.Confirm.getId());
      }
      order.setOrderType(OrderTypeEnum.PT.getId());
      order.update();
      orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.AFERPTPAY.getId(), "", "", "");
      logger.info("OTSMessage::modifyOrderTypePT---end");
  }

  @Override
  public boolean lockRoom(OtaOrder order) {
      logger.info("OTSMessage::lockRoom---锁房--begin{}", order.getId());
      List<OtaRoomOrder> roomOrders = order.getRoomOrderList();

      // // added by chuaiqing at 2015-06-14 13:54:20
      boolean lock = true;

      for (OtaRoomOrder otaRoomOrder : roomOrders) {
          // // added by chuaiqing at 2015-06-14 13:55:10
          // //List<String> times = getLockDates(order, otaRoomOrder);
          // //Long roomtypeid = otaRoomOrder.getRoomTypeId();
          // //boolean lock =
          // roomService.bookRoom15Minute(order.getTCity().getStr("code"),
          // order.getHotelId(), roomtypeid, otaRoomOrder.getLong("roomid"),
          // times.toArray(new String[0]));
          try {
              Map<String, Object> resultMap = roomstateService.lockRoomInOTS(otaRoomOrder);
              if (resultMap.get(ServiceOutput.STR_MSG_SUCCESS) == null) {
                  lock = true;
                  return lock;
              }
              lock = Boolean.TRUE == (Boolean) resultMap.get(ServiceOutput.STR_MSG_SUCCESS);
              return lock;
          } catch (Exception e) {
              lock = false;
              logger.info("OrderServiceImpl::lockRoom is error: {}", e.getMessage());
          }
          // //

          if (!lock) {
              throw MyErrorEnum.customError.getMyException("亲，房间没有了");
          }
      }
      logger.info("OTSMessage::lockRoom---锁房完成");
      return true;
  }

  /**
   * @param order
   * @return
   */
  public boolean unLockRoom(OtaOrder order) {
      logger.info("OTSMessage::unLockRoom---解开房间--start{}", order.getId());

      // // added by chuaiqing at 205-06-14 14:02:20
      boolean lock = false;
      try {
          Map<String, Object> resultMap = roomstateService.unlockRoomInOTS(order);
          lock = Boolean.TRUE == (Boolean) resultMap.get(ServiceOutput.STR_MSG_SUCCESS);
          if (!lock) {
              logger.info("OTSMessage::unLockRoom---解房失败{}", order.getId());
          }
      } catch (Exception e) {
          lock = false;
          logger.info("OrderServiceImpl::unLockRoom is error: {}", e.getMessage());
          return lock;
      }

      logger.info("OTSMessage::unLockRoom---解开房间--end");
      return true;
  }

  private List<String> getLockDates(OtaOrder order, OtaRoomOrder otaRoomOrder) {
      logger.info("OTSMessage::getLockDates--{}", otaRoomOrder);
      //
      Date bg = DateUtils.addHours(otaRoomOrder.getDate("BeginTime"), -6);
      Date ed = otaRoomOrder.get("EndTime");
      List<String> times = new ArrayList<>();
      // 计算锁哪几天
      while (bg.before(ed)) {
          // 锁房的天数控制
          if (order.getDaynumber() == times.size()) {
              break;
          }
          times.add(DateUtils.getStringFromDate(bg, DateUtils.FORMATSHORTDATETIME));
          bg = DateUtils.addDays(bg, 1);
      }
      return times;
  }

  @Override
  public OtaRoomOrder findRoomOrderByPmsRoomOrderNoAndHotelId(String pmsRoomOrderNo, Long hotelId) {
      return this.roomOrderDAO.findRoomOrderByPmsRoomOrderNoAndHotelId(pmsRoomOrderNo, hotelId);
  }

  @Override
  public PmsRoomOrder findPmsRoomOrderById(String pmsRoomOrderNo, Long hotelId) {
      return this.roomDAO.findPmsRoomOrder(pmsRoomOrderNo, hotelId);
  }

  /**
   * 支付过程中修改订单状态
   */
  @Override
  public OtaOrder changeOrderStatusByPay(Long otaorderid, OtaOrderStatusEnum orderStatus, PayStatusEnum payStatus, OrderTypeEnum orderType) {
      OtaOrder otaorder = OtaOrder.dao.findById(otaorderid);
      if (otaorder == null) {
          logger.warn("订单不存在otaorderid：" + otaorderid);
          return null;
      }
      if (payStatus != null) {
          otaorder.setPayStatus(payStatus.getId());
      }
      if (orderStatus != null && otaorder.getOrderStatus() < 500) {
          otaorder.setOrderStatus(orderStatus.getId());
      }
      if (orderType != null) {
          otaorder.setOrderType(orderType.getId());
          List<OtaRoomOrder> roomOrderList = otaorder.getRoomOrderList();
          if (roomOrderList != null) {
              for (OtaRoomOrder otaRoomOrder : roomOrderList) {
                  otaRoomOrder.set("ordertype", orderType.getId());
                  otaRoomOrder.saveOrUpdate();
              }
          }
      }
      return otaorder.saveOrUpdate();
  }

  /**
   * 修改订单状态
   */
  @Override
  public void changeOrderStatusByPms(Long otaorderid, PmsRoomOrder pmsRoomOrder, String freqtrv) {
      OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms---otaorderid:{}", otaorderid);
      String status = pmsRoomOrder.getStr("Status");
      OtaOrder otaorder = OtaOrder.dao.findById(otaorderid);
      if (otaorder == null) {
          OrderServiceImpl.logger.warn("订单不存在otaorderid：" + otaorderid);
          return;
      }
      //
      boolean needUnLockroom = false;
      if (otaorder.getOrderStatus() < 500) {
          StringBuffer orderStatusBuf = new StringBuffer();
          int tempOrderStatus = otaorder.getOrderStatus();
          orderStatusBuf.append("OTA订单状态:" + otaorder.getOrderStatus());
          orderStatusBuf.append(",PMS客单状态:" + status);
          // IN【在住】 CheckIn RX【预订取消】 NotIn OK【退房】 CheckOut IX【入住取消】 CheckOut
          // PM【挂账】 Account
          if (status.equals(PmsRoomOrderStatusEnum.IN.toString())) {
              // pms已经入住，需要下发支付
              if (otaorder.getOrderStatus() < OtaOrderStatusEnum.CheckIn.getId().intValue()) {
        			// 校验支付状态
        			payService.checkPayStatusWhenIn(otaorder);
                  OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms调用SendPmsPayEvent:ordertype:{},freqtrv:{}", otaorder.getOrderType(), freqtrv);

                  if (otaorder.getInt("rulecode") == 1001) {
//                  try {
//                          if ((OrderTypeEnum.PT.getId().intValue() == otaorder.getOrderType()) && !"1".equals(freqtrv)) {
//                              OrderServiceImpl.logger.info("到付的订单:调用pay支付,订单号{}", otaorderid);
//                              this.payService.pmsAddpayOk(otaorder, PayStatusEnum.doNotPay, PPayInfoTypeEnum.Y2P);
//                              this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "到付非常住人优惠券入", "");
//                          }
                          // 支付那边要求触发的（抵消流水）
//                          if ((OrderTypeEnum.PT.getId().intValue() == otaorder.getOrderType()) && "1".equals(freqtrv)) {
//                              OrderServiceImpl.logger.info("到付的订单:调用pay支付,订单号{}", otaorderid);
//                              this.payService.pmsAddpayOk(otaorder, PayStatusEnum.doNotPay, PPayInfoTypeEnum.REFUNDLEZHU4LONG);
//                              this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "到付常住人收回乐住币", "");
//                          }
                          // 判断是否回收乐住币
//                          if ((OrderTypeEnum.YF.getId().intValue() == otaorder.getOrderType()) && "1".equals(freqtrv)) {
//                              OrderServiceImpl.logger.info("回收乐住币");
//                              this.payService.cancelPmsPay(otaorder.getId(), PPayInfoTypeEnum.REFUNDLEZHU4LONG);
//                              this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "预付常住人收回乐住币", "");
//                          } else {
//                              OrderServiceImpl.logger.info("不回收乐住币");
//                          }

//                      } catch (Exception e) {
//                          OrderServiceImpl.logger.info("到付的订单:调用pay支付,异常啦,订单号{},exception:{}", otaorderid, e.getLocalizedMessage());
//                      }
                      // 入住时，如果常旅客入住方式，无效切客订单原因Invalidreason”置为1
                      if ("1".equals(freqtrv)) {
                          otaorder.set("Invalidreason", OtaFreqTrvEnum.IN_FREQUSER.getId());
                          this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "常住人Invalidreason为1", "");
                      }
					} else if (otaorder.getInt("rulecode") == 1002) {
						doRuleBWhenPmsIN(otaorder, pmsRoomOrder, freqtrv);
					}

                    // 订单到付的时候 客户有优惠券下发
                  	if ((OrderTypeEnum.PT.getId().intValue() == otaorder.getOrderType())) {
	                    OrderServiceImpl.logger.info("到付的订单:调用pay支付,订单号{}", otaorderid);
	                    this.payService.pmsAddpayOk(otaorder, PayStatusEnum.doNotPay, PPayInfoTypeEnum.Y2P);
	                    this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "到付优惠券入", "");
                  	}
              }
              
              /**************************取消到店提醒和预抵时间***************************/
              logger.info("状态变成入住,取消到店提醒和预抵时间 ,订单号{}", otaorderid);
              //查询出账单对应的还没有发生的关怀推送
              OtaOrderTasts orderTasts=new OtaOrderTasts();
              orderTasts.setOtaorderid(otaorderid);
              orderTasts.setTasktype(OrderTasksTypeEnum.ORDERPUSH.getId());
              orderTasts.setStatus(OrderTasksStatusEnum.INITIALIZE.getId());
              List<OtaOrderTasts> list=orderTastsMapper.findCancelMsg(orderTasts);
              for(int i=0;i<list.size();i++){
                  OtaOrderTasts otaOrderTasts=list.get(i);
                  otaOrderTasts.setStatus(OrderTasksStatusEnum.FAILURE.getId());
                  int result =orderTastsMapper.updateByPrimaryKeySelective(otaOrderTasts);
                  if(result==0){
                      logger.info("状态变成入住,取消到店提醒和预抵时间 失败,消息详细：{}", JsonKit.toJson(orderTasts));
                  }else{
                      logger.info("状态变成入住,取消到店提醒和预抵时间 成功,消息详细：{}", JsonKit.toJson(orderTasts));
                  }
                  
              }
              /**************************取消到店提醒和预抵时间***************************/
              
              /**************************发送入住提醒***************************/
              //判断状态是否改变
              if(otaorder.getOrderStatus()!=OtaOrderStatusEnum.CheckIn.getId()){
                  logger.info("状态变成入住，开始推送消息 ,订单号{}", otaorderid);
                  //开始住店
                  //推迟十分钟执行
                  Calendar nowTime = Calendar.getInstance();
                  nowTime.add(Calendar.MINUTE, 10);
                  
                  orderTasts= this.getMessageToC(otaorder, nowTime.getTime(), false,CopywriterTypeEnum.order_checkin);
                 
                  orderTastsMapper.insertSelective(orderTasts);
                  logger.info("状态变成入住，推送消息结束,订单号{}", otaorderid);
              
              }else{
                  //已经入住，不推送消息 
                  logger.info("已经入住，不推送消息 ,订单号{}", otaorderid);
              }
              /**************************发送入住提醒***************************/
              otaorder.setOrderStatus(OtaOrderStatusEnum.CheckIn.getId());
              OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms调用::CheckIn");
          } else if (status.equals(PmsRoomOrderStatusEnum.RX.toString())) {
              OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms调用::NotIn");
              // 通过预抵时间来判断是否pms 取消
              if ((otaorder.getBeginTime().before(DateUtils.createDate())) && (otaorder.getOrderStatus() < OtaOrderStatusEnum.CheckInOnline.getId())) { // 当pms端进行取消订单操作
                  try {
                      this.pmsCancelOrder(otaorder);
                  } catch (Exception e) {
                      OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms::NotIn出错::otaorderid==" + otaorderid, e);
                  }
                  otaorder.setOrderStatus(OtaOrderStatusEnum.CancelByPMS.getId());
              }
              needUnLockroom = true;
          } else if (status.equals(PmsRoomOrderStatusEnum.OK.toString())) {
              changeOrderStatusForPMAndOK(otaorderid, pmsRoomOrder, otaorder);
              otaorder.setOrderStatus(OtaOrderStatusEnum.CheckOut.getId());
              OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms调用::CheckOut");
              needUnLockroom = true;
          } else if (status.equals(PmsRoomOrderStatusEnum.IX.toString())) {
              otaorder.setOrderStatus(OtaOrderStatusEnum.CheckOut.getId());
              OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms调用::CheckOut");
              needUnLockroom = true;
          } else if (status.equals(PmsRoomOrderStatusEnum.PM.toString())) {
          	changeOrderStatusForPMAndOK(otaorderid, pmsRoomOrder, otaorder);
              OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms调用::Account");
              otaorder.setOrderStatus(OtaOrderStatusEnum.Account.getId());
              
              //挂退也存到历史入住
              ticketService.saveOrUpdateHotelStat(otaorder,pmsRoomOrder);
              needUnLockroom = true;
          }
          otaorder.setUpdateTime(DateUtils.createDate());
          if (tempOrderStatus != otaorder.getOrderStatus()) {
              otaorder.saveOrUpdate();
          }
          orderStatusBuf.append(",更新后OTA订单状态:" + otaorder.getOrderStatus());
          this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.UPDATEORDERSTATUS.getId(), orderStatusBuf.toString(), "", "");
      } else {
          this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.UPDATEORDERSTATUS.getId(), "OTA订单状态" + otaorder.getOrderStatus(), "异常OTA订单已经取消", "");
      }
      OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms::needUnLockroom{}", needUnLockroom);
      if (needUnLockroom) {
          OrderServiceImpl.logger.info("解锁订单的房间, otaorder: {}", JsonKit.toJson(otaorder));
          Map<String, Object> rtnMap = this.roomstateService.unlockRoomInOTS(otaorder);
          OrderServiceImpl.logger.info("OTSMessage::changeOrderStatusByPms调用解锁---end");
      }
  }

	private void changeOrderStatusForPMAndOK(Long otaorderid, PmsRoomOrder pmsRoomOrder, OtaOrder otaorder) {
		// 上海规则
		if (otaorder.getInt("rulecode") == 1001) {
		if (pmsRoomOrder.get("CheckInTime") != null && pmsRoomOrder.get("CheckOutTime") != null) {
		    Date bg = pmsRoomOrder.getDate("CheckInTime");
		    Date ed = pmsRoomOrder.getDate("CheckOutTime");
		    double diffHours = DateUtils.getDiffHoure(DateUtils.getDatetime(bg), DateUtils.getDatetime(ed));
		    logger.info("离店时判断入住时间小于4小时?入住时间:{},离开时间:{},相差:{}", bg, ed, diffHours);
		    // 离店时如果订单是到付切客订单且离店时间减入住时间小于4小时，则订单表需修改标记位字段内容，将“无效切客订单原因Invalidreason”置为2
				if (diffHours < 4) {
					otaorder.set("Invalidreason", OtaFreqTrvEnum.OK_LESS4.getId());
					if (OrderTypeEnum.PT.getId().intValue() == otaorder.getOrderType()) {
						this.payService.pmsAddpayOk(otaorder, PayStatusEnum.getByID(otaorder.getPayStatus()), PPayInfoTypeEnum.REFUNDLEZHU4QIEKE);
					}
				}
		    }
		} else if (otaorder.getInt("rulecode") == 1002) {// 重庆规则
		    if(otaorder.getSpreadUser() != null){// 切客订单
		        if(otaorder.get("Invalidreason") == null){// 非有效切客理由为空
		            OrderServiceImpl.logger.info("重庆非有效切客理由为空" + otaorder.getId() + otaorder.getSpreadUser());
		            Date bgtemp = pmsRoomOrder.getDate("CheckInTime");
		            Date edtemp = pmsRoomOrder.getDate("CheckOutTime");
		            double diffHours = DateUtils.getDiffHoure(DateUtils.getDatetime(bgtemp), DateUtils.getDatetime(edtemp));
		            OrderServiceImpl.logger.info("离店时判断入住时间小于4小时?入住时间:{},离开时间:{},相差:{}", bgtemp, edtemp, diffHours);
		            // 离店时如果订单是到付切客订单且离店时间减入住时间小于4小时，则订单表需修改标记位字段内容，将“无效切客订单原因Invalidreason”置为2
		            if(diffHours < 4){
		                OrderServiceImpl.logger.info("小于4小时非常住人" + otaorder.getId());
		                otaorder.set("Invalidreason", OtaFreqTrvEnum.OK_LESS4.getId());
		                //this.payService.leaveTimeLess(otaorderid);
		            }
		        }
		    }
		}
		// 住三送一活动，调用促销接口
		logger.info("住三送一活动,调用促销接口,orderid = " + otaorderid);
      ticketService.saveOrUpdateHotelStat(otaorder,pmsRoomOrder);
		
		/*********************** 离店推送信息 *********************/
		OrderServiceImpl.logger.info("状态变成离店，开始推送消息 ,订单号{}", otaorderid);
        OtaOrderTasts  orderTasts= this.getMessageToC(otaorder,new Date(), false,CopywriterTypeEnum.order_live);
        this.orderTastsMapper.insertSelective(orderTasts);
		OrderServiceImpl.logger.info("状态变成离店，推送消息结束 ,订单号{}", otaorderid);
		/*********************** 离店推送信息 *********************/
	}

  /**
   * 判断移动硬件信息是否为空
   * @param otaOrderMac
   * @return
   */
  private boolean otaOrderMacIsNull(OtaOrderMac otaOrderMac){
      if(null==otaOrderMac.getUuid()&&
                 null==otaOrderMac.getSysno()&&
                 null==otaOrderMac.getDeviceimei()&&
                 null==otaOrderMac.getSysno()&&
                 null==otaOrderMac.getWifimacaddr()&&
                 null==otaOrderMac.getBlmacaddr()){
                  return true;
              }else{
                  return false;
              }
  }

  /**
   * 保存移动硬件信息
   * 
   * @param otaOrderMac
   */
  private void saveMac(OtaOrderMac otaOrderMac) {
      Gson g = new Gson();
      try {
          logger.info("保存移动设备信息,{}", g.toJson(otaOrderMac));
          // 判断是否需要保持
          if (this.otaOrderMacIsNull(otaOrderMac)) {
              logger.info("移动设备信息为空,所以不保存{}", g.toJson(otaOrderMac));
              return;
          }
          int returnInt = otaOrderMacMapper.insertSelective(otaOrderMac);
          if (returnInt == 0) {
              // 保存失败
              logger.info("保存移动设备信息保存失败");
          } else {
              // 保存成功
              logger.info("保存移动设备信息保存成功  ID:" + otaOrderMac.getId());
          }
      } catch (Exception e) {
          logger.info("保存移动设备信息保存失败,{}", g.toJson(otaOrderMac));
      }
  }

  /**
   * 开始创建订单
   * 
   * @param order
   * @param jsonObj
   */
  public void doCreateOrder(OtaOrder order, JSONObject jsonObj) {
  	  checkMidIsBlack(order.getToken(),"您的账号存在异常，如有疑问请拨打客服电话4001-888-733");

      // 提交订单
        OtaOrder returnOrder = null;
        /*******************订单返现*************/
        Long roomTypeId=order.getRoomOrderList().get(0).getRoomTypeId();
       //判断房间类型
        Long roomId = order.getRoomOrderList().get(0).getRoomId();
        order.setPromoType(getPromoType(roomId));
       //检查订单promo type是否符合支付规则
        checkPayByPromoType(order, order.getPromoType());
		Map<String, Object> cash = cashBackService.getCashBackByRoomtypeId(roomTypeId, DateUtils.formatDate(order.getBeginTime()),
				DateUtils.formatDate(order.getEndTime()));
		this.logger.info("getCashBackByRoomtypeId:返现详细:{}", gson.toJson(cash));
		Long cashBigDecimal = (Long) cash.get("cashbackcost");
		order.setCashBack(new BigDecimal(cashBigDecimal));
		if (cashBigDecimal.longValue() > 0) {
			order.setIsReceiveCashBack(ReceiveCashBackEnum.notReceiveCashBack.getId());
		}
        /*******************订单返现*************/
		/*******************直减订单处理******************/
		//如果是直减订单
		//1，打标记    2，记录结算价格    3，冻结
		/*******************直减订单处理******************/
      // 判断是否调用切客的创建
      if (order.get("spreadUser") != null) {
          returnOrder = createQKOrder(order);
      } else {
          returnOrder = createOrder(order);
      }
      if (returnOrder == null || returnOrder.getId() == null) {
          logger.info("OTSMessage::OrderController::createOrder::异常:: {}", returnOrder);
          throw MyErrorEnum.saveOrder.getMyException("保存订单错误");
      }
      // 保存移动设备信息
      OtaOrderMac otaOrderMac = order.getOtaOrderMac();
      otaOrderMac.setOrderid(returnOrder.getId());
      this.saveMac(otaOrderMac);
      boolean showRoom = true;// 显示客单
      boolean showInUser = false;// 显示入住人----暂时没有
      // 获取pay
      PPay ppay = payService.findPayByOrderId(order.getId());
      List<OtaRoomPrice> otaRoomPrices = priceService.findOtaRoomPriceByOrder(order);
      returnOrder.put("otaRoomPrices", otaRoomPrices);
      returnOrder.put("act", "create");
      Transaction t = Cat.newTransaction("Order.doCreateOrder", "getOrderToJson");
      Cat.logMetricForCount("Order.doCreateOrder.Count");
      try {
          orderUtil.getOrderToJson(jsonObj, ppay, returnOrder, showRoom, showInUser);
            t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
            t.setStatus(e);
            throw e;
     } finally {
           t.complete();
     }
      jsonObj.put("success", true);
      logger.info("OTSMessage::OrderController::下单成功  | :) / 订单号：" + returnOrder.getId());
      // 创建订单后将订单号及订单创建时间放到内存中，进行支付计时
      t = Cat.newTransaction("Order.doCreateOrder", "lockRoom");
      try {
          lockRoom(order);
          t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
            t.setStatus(e);
            throw e;
     } finally {
           t.complete();
     }
      logger.info("OTSMessage::OrderController::createOrder::orderService.lockRoom");
      // 加入定时任务，15分钟后未支付则取消订单
      putOrderJobIntoManager(returnOrder);
  }

    /**
     * 根据房间id得到对应的特价类型
     * @param roomId
     * @return
     */
    private String getPromoType(Long roomId) {
        TRoomSale tRoomSale = new TRoomSale();
        tRoomSale.setRoomId(roomId.intValue());
        TRoomSale resultRoomSale = roomSaleService.getOneRoomSale(tRoomSale);
        if(resultRoomSale == null || "T".equals(resultRoomSale.getIsBack())){
            return PromoTypeEnum.OTHER.getCode().toString();
        }else{
            return PromoTypeEnum.TJ.getCode().toString();
        }
    }

    /**
   * 校验此用户是否有订单
   * 
   * @param order
   */
	private void checkOrdersByMid(OtaOrder order) {
		Long mid = MyTokenUtils.getMidByToken("");
		Date startTime = order.getBeginTime();
		Date endTime = order.getEndTime();
		if (DateUtils.getStringFromDate(startTime, "yyyyMMdd").equals(DateUtils.getStringFromDate(new Date(), "yyyyMMdd"))) {
			startTime = new Date();
		}
		logger.info("checkOrdersByMid:{},startTime:{},endTime:{}", mid, DateUtils.getDatetime(startTime), DateUtils.getDatetime(endTime));
		List<Bean> orders = orderDAO.findOrderCountByMid(mid, startTime, endTime);
		logger.info("checkOrdersByMid:orders:{}", orders);
		if (order.getOrderType() == OrderTypeEnum.YF.getId()) {
			for (Bean bean : orders) {
				if (bean.getLong("id").intValue() != order.getId().intValue() && bean.getInt("ordertype") ==  OrderTypeEnum.YF.getId() && bean.getInt("paystatus") < PayStatusEnum.alreadyPay.getId()) {
					logger.info("您有一个未支付的预付订单，不能再预订新的房间");
					throw MyErrorEnum.customError.getMyException("您有一个未支付的预付订单，不能再预订新的房间");
				}
			}
		} else if (order.getOrderType() == OrderTypeEnum.PT.getId()) {
			int c = 0;
			for (Bean bean : orders) {
				if (bean.getLong("id").intValue() != order.getId().intValue() && bean.getInt("ordertype") ==  OrderTypeEnum.PT.getId()) {
					c++;
				}
			}
			if (c >= 2) {
				logger.info("您已经有2个到付订单，不能再预订新的房间");
				throw MyErrorEnum.customError.getMyException("您已经有2个到付订单，不能再预订新的房间");
			}
		}
	}

  /**
   * 创建切客订单
   * 
   * @param order
   * @return
   */
  private OtaOrder createQKOrder(OtaOrder order) {
      HQrCode code=null;
      Transaction t = Cat.newTransaction("Order.doCreateOrder", "createQKOrder:saveOrder:before");
		try {
      logger.info("OTSMessage::前台切客方法createQKOrder---start");
      if (order.getHotelId() == null) {
          throw MyErrorEnum.notfindHotel.getMyException();
      }
      if (order.getSpreadUser() == null) {
          throw MyErrorEnum.spreadUserNofind.getMyException();
      }
      code = orderDAO.findQrcode(order.getHotelId(), order.getSpreadUser());
      logger.info("OTSMessage::前台切客方法createQKOrder:HQrCode:{}", code);
      if (code == null || code.get("userid") == null) {
          throw MyErrorEnum.spreadUserNofind.getMyException();
      }
      order.setSpreadUser(code.getLong("userid"));

      // 获取酒店对象 订单初始化酒店信息
      THotel hotel = hotelService.readonlyTHotel(order.getLong("hotelid"));
      if (hotel.getInt("rulecode") != null && hotel.getInt("rulecode") == 1002) {
          throw MyErrorEnum.customError.getMyException("B规则酒店不能创建切客订单");
      }
            //设置cityCode
            order.setCityCode(hotel.getTCityByDisId().getStr("code"));
      order.set("hotelname", hotel.get("hotelName"));
      order.set("hotelpms", hotel.get("pms"));
      order.put("hotelAddress", hotel.get("detailAddr"));
      order.put("defaultLeaveTime", hotel.get("defaultLeaveTime", "120000"));// 默认离店时间
      order.put("retentionTime", hotel.get("retentionTime", "180000"));// 订单保留时间
      order.put("hotelPic", hotel.get("hotelpic"));

      //设置规则
      int ruleCode=Integer.parseInt(hotel.get("rulecode").toString());
      order.setRuleCode(ruleCode);
      if (order.getMid() == null) {
          throw MyErrorEnum.notfindUser.getMyException();
      }
      // 缓存获取会员对象 存会员等级
      Optional<UMember> opMember = memberService.findMemberById((order.getMid()));
      UMember member = opMember.get();
      if (member != null) {
          order.set("contactsphone", member.getPhone());
      }
      // 目前没有会员功能，会员等级默认为0
      order.set("mlevel", 0);
      // 客单初始化
      for (OtaRoomOrder tempRoom : order.getRoomOrderList()) {
          checkInTimeBefore(order, hotel, tempRoom, true);// 检查客单时间是否正确可用
          tempRoom.set("HotelId", order.getHotelId());
          tempRoom.set("HotelName", order.getHotelName());
          tempRoom.set("HotelPms", order.getHotelPms());
          // 缓存 获取房间类型信息
          TRoomType tempRoomType = roomTypeService.getTRoomType(tempRoom.getLong("roomtypeid"));
          if (tempRoomType == null) {
              throw MyErrorEnum.saveOrder.getMyException("没有当前roomtypeid的数据");
          }
          tempRoom.set("roomtypename", tempRoomType.get("name"));
          tempRoom.set("roomtypepms", tempRoomType.get("pms"));
          // 缓存 获取房间类型详细信息
          TRoomTypeInfo tempRoomTypeInfo = roomTypeInfoService.findTRoomTypeInfoByRoomTypeId(tempRoom.getLong("roomtypeid"));
          if (tempRoomTypeInfo == null) {
              throw MyErrorEnum.saveOrder.getMyException("没有当前roomtypeid的tempRoomTypeInfo数据");
          }
          // ??tempRoom.put("roomtypepic", tempRoomTypeInfo.get("pics"));
          // 数据库 获取房间信息
          TRoom thisTempRoom = roomService.findTRoomByRoomId(tempRoom.getLong("roomid"));
          if (thisTempRoom == null) {
              throw MyErrorEnum.saveOrder.getMyException("没有当前roomid的房间");
          }
          tempRoom.set("RoomNo", thisTempRoom.get("Name"));
          tempRoom.set("RoomPms", thisTempRoom.get("Pms"));
          tempRoom.set("Mid", order.getMid());
          tempRoom.set("Mlevel", order.getMlevel());
          tempRoom.set("OrderStatus", OtaOrderStatusEnum.WaitPay.getId());
          tempRoom.set("PayStatus", PayStatusEnum.waitPay.getId());
          tempRoom.set("UpdateTime", Calendar.getInstance().getTime());
          tempRoom.set("OrderMethod", order.getOrderMethod());
      }
      // 订单信息为获取订单第一个客单信息
      if (order.getRoomOrderList() != null && order.getRoomOrderList().size() > 0) {
          OtaRoomOrder firstRoom = order.getRoomOrderList().get(0);
          order.set("hotelid", firstRoom.get("hotelid"));
          order.set("ordermethod", firstRoom.get("ordermethod"));
          order.set("ordertype", firstRoom.get("ordertype"));
          order.set("pricetype", firstRoom.get("pricetype"));
          order.set("begintime", firstRoom.get("begintime"));
          order.set("endtime", firstRoom.get("endtime"));
          order.set("breakfastnum", firstRoom.get("breakfastnum"));
          order.set("promotion", firstRoom.get("promotion"));
          order.set("coupon", firstRoom.get("coupon"));
          order.set("note", firstRoom.get("note"));
          order.set("orderstatus", OtaOrderStatusEnum.WaitPay.getId());
          order.set("paystatus", PayStatusEnum.waitPay.getId());
          order.set("createtime", DateUtils.createDate());
          order.set("updatetime", Calendar.getInstance().getTime());
          order.setPromotion("F");
          order.setCoupon("F");
          // 订单联系人 客单第一个
          order.setContacts(firstRoom.getStr("contacts"));
          order.setContactsPhone(firstRoom.getStr("contactsphone"));
          order.setContactsEmail(firstRoom.getStr("contactsemail"));
          order.setContactsWeiXin(firstRoom.getStr("contactsweixin"));
      } else {
          throw MyErrorEnum.saveOrder.getMyException();// 客单为空
      }
       t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
           t.setStatus(e);
             throw e;
      }finally {
            t.complete();
     }
      // 先保存获取id,然后通知PMS.成功:保存的不删除 失败:保存的Order删除
      saveOrder(order);
      // 记录orderLog
      OrderLog log = orderLogService.createOrderLog(order);
      // 创建切客券
      t = Cat.newTransaction("Order.doCreateOrder", "createQKOrder:createQikePromotion");
      try{
      // 创建切客券
      createQikePromotion(order, code, log);
      t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
           t.setStatus(e);
             throw e;
      }finally {
            t.complete();
     }

      return order;
  }

  /**
   * 创建切客券
   * 
   * @param order
   * @param code
   * @param log
   */
  private void createQikePromotion(OtaOrder order, HQrCode code, OrderLog log) {
      logger.info("-------------------创建切客券------开始---------------");
      // 校验切客规则，符合则绑定优惠券
      boolean isSpreadUserNotNull = order != null && order.getSpreadUser() != null && order.getSpreadUser() != 0l;
      boolean isCheckOnceToday = uTicketDao.isCheckNumToday(order.getMid());
      boolean isCheckFourTimesMonth = uTicketDao.isCheckNumMonth(order.getMid(), order.getHotelId());
      logger.info("订单是否绑定前台id:{}", isSpreadUserNotNull);
      logger.info("是否符合(一人一天只允许切一次):{}", isCheckOnceToday);
      logger.info("是否符合(一人一个月一个酒店只允许切四次):{}", isCheckFourTimesMonth);

      // 设置切客情况
      log = orderLogService.setQieKe(order, log, isSpreadUserNotNull, isCheckOnceToday, isCheckFourTimesMonth);
      orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CREATEQKORDER.getId(), "", log.getStr("spreadNote"), "");

      if (isSpreadUserNotNull && isCheckOnceToday && isCheckFourTimesMonth) {
          List<BPromotion> promotions = this.iBPromotionDao.findByPromotionType(PromotionTypeEnum.qieke);
          logger.info("查询切客优惠券:{}", promotions);
          if (promotions != null && promotions.size() == 1) {
              ITicketParse parse = promotions.get(0).createParseBean(order);
              logger.info("检测切客券是否可用:{}", parse.checkUsable());
              if (parse.checkUsable()) {
                  BigDecimal onlinePrice = BigDecimal.ZERO;
                  BigDecimal offlinePrice = BigDecimal.ZERO;
                  if (OrderTypeEnum.YF.getId().intValue() == order.getOrderType()) {
                      onlinePrice = parse.getOnlinePrice();
                  } else if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
                      offlinePrice = parse.getOfflinePrice();
                  }
                  BPromotionPrice bp = new BPromotionPrice();
                  bp.setOfflineprice(offlinePrice);
                  bp.setPrice(onlinePrice);
                  bp.setPromotion(promotions.get(0).getId());
                  bp.setOtaorderid(order.getId());
                  logger.info("绑定切客券. orderid:{}, promotion:{}", order.getId(), bp);
                  this.iBPromotionPriceDao.saveOrUpdate(bp);
                  logger.info("绑定切客券成功. orderid:{}, promotion:{}", order.getId(), bp);
                  // 通知HMS议价
                  logger.info("OTSMessage::ServerChannel.bookHotel:orderid:" + order.getId() + " userid:" + code.getLong("userid"));
                  ServerChannel.bookHotel(order.getId(), code.getLong("userid"));
                  logger.info("OTSMessage::前台切客方法createQKOrder---end");
                  // 切客增加保存订单上优惠券使用状态，是否使用了优惠券。
                  ticketService.saveOrderTicketStatus(order.getId(), "promotion");
              }
          }
      }
      logger.info("-------------------创建切客券------结束---------------");
  }

  /**
   * @param order memberService.findMemberById出现问题。
   * @return
   */
  private OtaOrder createOrder(OtaOrder order) {
      Transaction t = Cat.newTransaction("Order.doCreateOrder", "createOrder：saveOrder:before");
      UMember member =null;
      try{
      logger.info("OTSMessage::OrderController::createOrder::begin");
      if (order.get("hotelId") == null) {
          throw MyErrorEnum.notfindHotel.getMyException();
      }
      // 获取酒店对象 订单初始化酒店信息
      THotel hotel = hotelService.readonlyTHotel(order.getLong("hotelId"));
      if (hotel == null) {
          throw MyErrorEnum.saveOrder.getMyException("没有当前id的酒店");
      }
            //设置cityCode
            order.setCityCode(hotel.getTCityByDisId().getStr("code"));
      order.set("hotelname", hotel.get("hotelName"));
      order.set("hotelpms", hotel.get("pms"));
      order.put("hotelAddress", hotel.get("detailAddr"));
      order.put("defaultLeaveTime", hotel.get("defaultLeaveTime", "120000"));// 默认离店时间
      order.put("retentionTime", hotel.get("retentionTime", "180000"));// 订单保留时间
      order.put("hotelPic", hotel.get("hotelpic"));
      //设置规则
      int ruleCode=Integer.parseInt(hotel.get("rulecode").toString());
      order.setRuleCode(ruleCode);
      // session 获取会员id
      member = MyTokenUtils.getMemberByToken(order.getToken());
      if (member == null) {
          // 会员不存在
          throw MyErrorEnum.memberNotExist.getMyException("");
      }
      order.set("mid", MyTokenUtils.getMidByToken(order.getToken()));
      if (order.get("mid") == null) {
          throw MyErrorEnum.notfindUser.getMyException();
      }
      order.set("mlevel", member.getLevel());
      // 目前没有会员功能，会员等级默认为0
      order.set("mlevel", 0);

      // 客单初始化
      List<OtaRoomOrder> tempList = new ArrayList<>();
      for (OtaRoomOrder tempRoom : order.getRoomOrderList()) {
          checkInTimeBefore(order, hotel, tempRoom, false);// 检查客单时间是否正确可用
          tempRoom.set("HotelId", order.getHotelId());
          tempRoom.set("HotelName", order.getHotelName());
          tempRoom.set("HotelPms", order.getHotelPms());
          // 缓存 获取房间类型信息
          TRoomType tempRoomType = roomTypeService.getTRoomType(tempRoom.getLong("roomtypeid"));
          if (tempRoomType == null) {
              throw MyErrorEnum.saveOrder.getMyException("没有当前roomtypeid的数据");
          }
          tempRoom.set("roomtypename", tempRoomType.get("name"));
          tempRoom.set("roomtypepms", tempRoomType.get("pms"));
          // 缓存 获取房间类型详细信息
          TRoomTypeInfo tempRoomTypeInfo = roomTypeInfoService.findTRoomTypeInfoByRoomTypeId(tempRoom.getLong("roomtypeid"));
          if (tempRoomTypeInfo == null) {
              throw MyErrorEnum.saveOrder.getMyException("没有当前roomtypeid的tempRoomTypeInfo数据");
          }
          // ??tempRoom.put("roomtypepic", tempRoomTypeInfo.get("pics"));
          // 数据库 获取房间信息
          TRoom thisTempRoom = roomService.findTRoomByRoomId(tempRoom.getLong("roomid"));
          if (thisTempRoom == null) {
              throw MyErrorEnum.saveOrder.getMyException("没有当前roomid的房间");
          }
          tempRoom.set("RoomNo", thisTempRoom.get("Name"));
          tempRoom.set("RoomPms", thisTempRoom.get("Pms"));
          tempRoom.set("Mid", order.getMid());
          tempRoom.set("Mlevel", order.getMlevel());
          tempRoom.set("OrderStatus", OtaOrderStatusEnum.WaitPay.getId());
          tempRoom.set("PayStatus", PayStatusEnum.waitPay.getId());
          tempRoom.set("UpdateTime", Calendar.getInstance().getTime());
          tempRoom.set("OrderMethod", order.getOrderMethod());
          tempList.add(tempRoom);
      }
      // 订单信息为获取订单第一个客单信息
      if (order.getRoomOrderList() != null && order.getRoomOrderList().size() > 0) {
          OtaRoomOrder firstRoom = order.getRoomOrderList().get(0);
          order.set("hotelid", firstRoom.get("hotelid"));
          order.set("ordermethod", firstRoom.get("ordermethod"));
          order.set("ordertype", firstRoom.get("ordertype"));
          order.set("pricetype", firstRoom.get("pricetype"));
          order.set("begintime", firstRoom.get("begintime"));
          order.set("endtime", firstRoom.get("endtime"));
          order.set("BreakfastNum", firstRoom.get("breakfastnum"));
          order.set("Promotion", firstRoom.get("promotion"));
          order.setCoupon(firstRoom.getStr("coupon"));
          order.setNote(firstRoom.getStr("note"));
          order.setOrderStatus(OtaOrderStatusEnum.WaitPay.getId());
          order.setPayStatus(PayStatusEnum.waitPay.getId());
          order.setCreateTime(DateUtils.createDate());
          order.setUpdateTime(Calendar.getInstance().getTime());
          order.setPromotion("F");
          order.setCoupon("F");
          // 订单联系人 客单第一个
          order.setContacts(firstRoom.getStr("contacts"));
          order.setContactsPhone(firstRoom.getStr("contactsphone"));
          order.setContactsEmail(firstRoom.getStr("contactsemail"));
          order.setContactsWeiXin(firstRoom.getStr("contactsweixin"));
      } else {
          throw MyErrorEnum.saveOrder.getMyException();// 客单为空
      }
      // 先保存获取id,然后通知PMS.成功:保存的不删除 失败:保存的Order删除
      order = saveOrder(order);
      t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
            t.setStatus(e);
            throw e;
     } finally {
           t.complete();
     }
      t = Cat.newTransaction("Order.doCreateOrder", "saveBindPromotion");
      
      try {
      orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CREATEORDER.getId(), "", "", "");
      OrderLog log = orderLogService.createOrderLog(order);
      logger.info("OTSMessage::OrderController::createOrder::db::save");

      //logger.info("非切客场景下且订单类型为预付的情况下进行普通优惠券绑定.........status:{}", order.getOrderType() == OrderTypeEnum.YF.getId()&& order.getSpreadUser() == null);
      //region 去除创建订单默认绑定的逻辑  update by tankai 因ios不支持去掉默认绑定逻辑，恢复。
          //ticketService.saveBindPromotion(order, member, log);
      t.setStatus(Transaction.SUCCESS);
         } catch (Exception e) {
               t.setStatus(e);
               throw e;
        } finally {
              t.complete();
        }
      //endregion
      return order;
  }

  

  /**
   * 新建订单加入job队列
   * 
   * @param returnOrder
   */
  private void putOrderJobIntoManager(OtaOrder returnOrder) {
      logger.info("OTSMessage::OrderController::putOrderJobIntoManager::加入15分钟调度队列" + returnOrder.getId());
      Long orderId = returnOrder.getId();
      OtsCacheManager manager = AppUtils.getBean(OtsCacheManager.class);
      Jedis jedis = manager.getNewJedis();
      try {
          jedis.hset("orderJobList", String.valueOf(orderId), DateUtils.getStringFromDate(returnOrder.getCreateTime(), DateUtils.FORMATDATETIME));
      } catch (Exception e) {
          throw e;
      } finally {
          jedis.close();
      }
      logger.info("OTSMessage::OrderController::putOrderJobIntoManager::OK::orderJobList" + String.valueOf(orderId) + ":"
              + DateUtils.getStringFromDate(returnOrder.getCreateTime(), DateUtils.FORMAT_FULLTIME));
  }

  /**
   * 切客入住时间检查
   * 
   * @param hotel
   * @param tempRoom
   */
  private void checkInTimeBefore(OtaOrder order, THotel hotel, OtaRoomOrder tempRoom, boolean ifQieKe) {
      logger.info("OrderServiceImpl:: checkInTimeBefore:: begin: orderid = " + order.getOrderId());
      String beginTime = DateUtils.getStringFromDate((Date) tempRoom.get("BeginTime"), DateUtils.FORMATSHORTDATETIME);
      String endTime = DateUtils.getStringFromDate((Date) tempRoom.get("EndTime"), DateUtils.FORMATSHORTDATETIME);
      Date createTime = order.getDate("createtime");
      createTime = createTime == null ? new Date() : createTime;
      Calendar calNow = Calendar.getInstance();
      calNow.setTime(createTime);
      String nowtime = DateUtils.getStringFromDate(createTime, DateUtils.FORMATSHORTDATETIME);

      try {
          if (DateUtils.addHours(DateUtils.getDateFromString(beginTime), 24).compareTo(DateUtils.addHours(new Date(), -6)) < 0 
                  || beginTime.compareTo(endTime) > 0) {
              throw MyErrorEnum.saveOrderInTimeByTime.getMyException("请正确选择入住时间---startdateday:" + beginTime + " enddateday: " + endTime);
          }
      } catch (Exception e) {
          throw MyErrorEnum.errorParm.getMyException();
      }
      double diff = DateUtils.getDiffHoure(DateUtils.getDatetime(createTime), DateUtils.getDatetime(order.getDate("begintime")));
      boolean now = diff <= 2 || DateUtils.getStringFromDate(createTime, "yyyyMMdd").equals(beginTime);
      // 直单
      if(!ifQieKe){ //直单
          if(order.getOrderType() == OrderTypeEnum.PT.getId().intValue()){ //到付单
              if(now && calNow.get(calNow.HOUR_OF_DAY) >= 2 && calNow.get(calNow.HOUR_OF_DAY) < 16){//在晚上2点 到 下午 16点 =4 18：00
                  beginTime = beginTime + "180000";
              } else if (now && calNow.get(calNow.HOUR_OF_DAY) >= 16 && calNow.get(calNow.HOUR_OF_DAY) < 22){//在下午 16点=4 到 晚上22点=10 加2小时
                  calNow.add(Calendar.HOUR_OF_DAY, 2);
                  beginTime = DateUtils.getStringFromDate(calNow.getTime(), "yyyyMMddHHmmss").substring(0, 12)+"00";
              } else if (now && DateUtils.getStringFromDate(calNow.getTime(), "HH:mm").compareTo("22:00") >= 0 
                             && DateUtils.getStringFromDate(calNow.getTime(), "HH:mm").compareTo("23:55") <= 0){// 在22点 到 24点23:55 之前 23：59分 说明还是今天的单子
                  beginTime = beginTime + "235900";
              } else if (now && (DateUtils.getStringFromDate(calNow.getTime(), "HH:mm").compareTo("23:56") >= 0
                              || DateUtils.getStringFromDate(calNow.getTime(), "HH:mm").compareTo("02:00") < 0)){
                  calNow.add(Calendar.HOUR_OF_DAY, 2);
                  beginTime = DateUtils.getStringFromDate(calNow.getTime(), "yyyyMMddHHmmss").substring(0, 12)+"00";
              } else {// 到付   未来单     预订日18:00
                  beginTime = beginTime + hotel.get("RetentionTime", "180000");// 保留时间
              }
          } else { //预付单
              if(now) { //当天单
                  beginTime = DateUtils.getStringFromDate(calNow.getTime(), "yyyyMMddHHmmss").substring(0, 12)+"00";
              } else {
                  beginTime = beginTime + "120000";
              }
          }
          logger.info("OrderServiceImpl:: checkInTimeBefore:: 订单创建时间: " + createTime + "订单号: " + order.getOrderId() + "是否切客: " + ifQieKe + "支付类型: " + order.getOrderType());
      } else { // 切客单
          if (now) {// 当天单 / 下单时间后5分钟
              beginTime = DateUtils.getStringFromDate(DateUtils.addMinutes(createTime, 5), "yyyyMMddHHmmss").substring(0, 12)+"00";
          } else {// 未来单 / 预订日18:00
              beginTime = beginTime + hotel.get("RetentionTime", "180000");// 保留时间
          }
          logger.info("OrderServiceImpl:: checkInTimeBefore:: 订单创建时间: " + createTime + "订单号: " + order.getOrderId() + "是否切客: " + ifQieKe + "支付类型: " + order.getOrderType());
      }
      logger.info("beginTime计算的时间：" + beginTime);

      endTime = endTime + hotel.get("DefaultLeaveTime");// 离店时间
      if(DateUtils.getDateFromString(beginTime).after(DateUtils.getDateFromString(endTime))){
            beginTime = DateUtils.getStringFromDate((Date) tempRoom.get("BeginTime"), DateUtils.FORMAT_DATETIME);
      }
      tempRoom.set("begintime", DateUtils.getDateFromString(beginTime));
      tempRoom.set("endtime", DateUtils.getDateFromString(endTime));
      order.set("begintime", DateUtils.getDateFromString(beginTime));
      order.set("endtime", DateUtils.getDateFromString(endTime));
      if (tempRoom.get("EndTime") == null) {
          throw MyErrorEnum.systemBusy.getMyException("结束时间错误!配置时间错误?");
      }
  }

    public void doModifyOrder(HttpServletRequest request, JSONObject jsonObj, boolean modifyByRoomType) {
      String orderId = request.getParameter("orderid").trim();
      OtaOrder order = this.findOtaOrderById(Long.parseLong(orderId));
      if (order == null) {
          throw MyErrorEnum.findOrder.getMyException("订单号不存在");
      }
      
      
        OtaOrder pOrder = this.extractOrderBeanForModify(request, order, modifyByRoomType);
        // 校验此用户是否有订单
        checkOrdersByMid(pOrder);
        /***************直减订单不能到付***************/
        if(order.getClearingType()==ClearingTypeEnum.priceDrop.getId()&&order.getOrderType()==OrderTypeEnum.PT.getId()){
        	this.logger.info("直减订单，只能在线支付,订单号：{}",pOrder.getId());
        	throw MyErrorEnum.customError.getMyException("直减订单，只能在线支付");
        }
        /***************直减订单不能到付***************/
        // 预付订单、等待支付的情况，计算房价
        pOrder = this.saveOrder(pOrder);
        THotel hotel = hotelService.readonlyTHotel(pOrder.getHotelId());
        logger.info("doModifyOrder:orderid:{},orderStatus:{}", orderId, pOrder.getOrderStatus());
        // 价格获取
        //修改入住人信息
        //预抵时间前一小时push消息放入到orderTasks任务表中
        pushCheckInBefore1Msg(pOrder);
        //过保留时间（预抵时间） 未到的 push消息 放入到任务表中
        pushOutCheckInTimeMsg(pOrder);

        /**
         * 拿到pms客单号
         */
        List<OtaRoomOrder> list = pOrder.getRoomOrderList();
        String pmsRoomOrderNo = null;
        if (null != list && list.size() > 0) {
            OtaRoomOrder otaRoomOrder = list.get(0);
            pmsRoomOrderNo = otaRoomOrder.getPmsRoomOrderNo();
        }
        logger.info("pmsroomorderno = {}", pmsRoomOrderNo);

        if (pOrder.getOrderStatus()<OtaOrderStatusEnum.CheckInOnline.getId()) {
            Long otaOrderId = pOrder.getId();
            OrderServiceImpl.logger.info("OrderServiceImpl:: doModifyOrder:: 修改入住人名字 start orderid : " + otaOrderId);
            List<OtaRoomOrder> roomOrderList;
            //若订单为规则B，则做以下处理：
            //1.15分钟之内，有优惠券的订单若C端调用修改订单接口，修改入住人姓名，OTS记录修改后的入住人姓名，但不向PMS发送修改后的cpname
            Date createTime = pOrder.getCreateTime();
            Date nowDateTime = new Date();
            long temp = nowDateTime.getTime() - createTime.getTime(); // 相差毫秒数
            roomOrderList = pOrder.getRoomOrderList();
            for (OtaRoomOrder roomOrder : roomOrderList) {
                // 如果改变了联系人,是否PmsRoomOrderNo
                if (order.getAttrs().containsKey("modify_lxr") && StringUtils.isNotBlank(roomOrder.getPmsRoomOrderNo())) {
                    modifyPmsOrder(hotel, order, roomOrder);
                }
            }

      }else if(//等待支付 且 已经有客单
              OtaOrderStatusEnum.WaitPay.getId() == pOrder.getOrderStatus()&&
              StringUtils.isNotBlank(pmsRoomOrderNo) 
              && OrderTypeEnum.PT.getId().equals(order.getOrderType()) ){
          logger.info("用户修改支付方式，订单号：{}",pOrder.getId());
      }
      logger.info("绑定优惠券逻辑----------------开始");
      // 优惠券(普通券),非切客场景且为预付情况下可以使用我的优惠券
      String couponno = request.getParameter("couponno");
		if (couponno != null && couponno.trim().length() >= 0) {
			// 促销代码(切客券)
			// String promotion = request.getParameter("promotion");
			// //切客券不允许修改，故创建切客订单后就不允许修改此券
			logger.info("(couponno为null则不处理，为空串则解绑，有值则重新绑定)优惠码:{}, 优惠券:{}", couponno, "");
			List<Long> promoNoList = getPromoNoList("", couponno);
			// 创建订单时无法判断此订单是到付还是预付，且只有非切客模式下可以编辑券
			if (pOrder.getSpreadUser() == null) { // 预付且spreaduser为空
				promoService.bindPromotionPrice(promoNoList, MyTokenUtils.getMemberByToken(order.getToken()), pOrder);
			}
		}
      logger.info("绑定优惠券逻辑----------------结束.");

        /*
         * // 缓存获取会员对象 存会员等级 Optional<UMember> opMember =
         * memberService.findMemberById(order.getLong("mid"));
         * 
         * List<OtaRoomPrice> otaRoomPrices =
         * payService.createPayByCreateOrder(order, opMember.get(),
         * priceService.findOtaRoomPriceByOrder(order), order.getCouponNo(),
         * order.getPromotionNo());
         */
        List<OtaRoomPrice> otaRoomPrices = priceService.findOtaRoomPriceByOrder(order);
        boolean showRoom = true;// 显示客单
        boolean showInUser = true;// 显示入住人--暂时没有
        // 订单转换为json
        order.put("otaRoomPrices", otaRoomPrices);
        order.put("act", "modify");
        order.put("isuselewallet", request.getParameter("isuselewallet"));

      orderUtil.getOrderToJson(jsonObj, null, order, showRoom, showInUser);
      jsonObj.put("success", true);
  }
  
  private void doRuleBWhenPmsIN(OtaOrder otaorder, PmsRoomOrder pmsRoomOrder, String freqtrv) {
	// 判断入住时间减创建时间是否小于15分钟
		Date checkintime = pmsRoomOrder.getDate("CheckInTime");
		Date createtime = otaorder.getDate("Createtime");
		long temp = checkintime.getTime() - createtime.getTime(); // 相差毫秒数
		if (temp < OrderServiceImpl.TIME_FOR_FIFTEEN) {
			OrderServiceImpl.logger.info("小于15分钟" + otaorder.getId());
			otaorder.setSpreadUser(otaorder.getHotelId());
			// 判断入住人是否常住人
			if ("1".equals(freqtrv)) {
				OrderServiceImpl.logger.info("小于15分常住人" + otaorder.getId());
				otaorder.set("Invalidreason", OtaFreqTrvEnum.IN_FREQUSER.getId());
				this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "小于15分常住人", "");
			} else {
				// 判断切客是否有效
				boolean isCheckOnceToday = this.otaOrderDAO.isCheckNumToday(otaorder.getMid(),null);
				if (!isCheckOnceToday) {
					otaorder.set("Invalidreason", OtaFreqTrvEnum.ONEDAY_UP1.getId());
					logger.info(" 同一个用户一天只能切一单" + otaorder.getId());
					this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "15分钟同一个用户一天只能切一单", "");
				} else {
					boolean isCheckFourTimesMonth = this.otaOrderDAO.isCheckNumMonth(otaorder.getMid(), otaorder.getHotelId(),null);
					if (!isCheckFourTimesMonth) {
						otaorder.set("Invalidreason", OtaFreqTrvEnum.MONTHE_UP4.getId());
						logger.info(" 同一个酒店一天只能切四单" + otaorder.getId());
						this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "15分钟同一个酒店一天只能切四单", "");
					} 
				}
				
				// 计算切客收益
				addQiekeIncome(otaorder);
			}
		} else {
			// spreaduser保持为空
			otaorder.setSpreadUser(null);
			// 判断入住人是否常住人
			if ("1".equals(freqtrv)) {
				otaorder.set("Invalidreason", OtaFreqTrvEnum.IN_FREQUSER.getId());
				// 若为常住人 调checkinCancelCoupon去取消优惠券
//				this.payService.checkinCancelCoupon(otaorder);
				this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "大于15分钟常住人取消优惠券", "");
			} 
//			else {
//				if (OrderTypeEnum.PT.getId().intValue() == otaorder.getOrderType()) {
//					logger.info("大于15分钟非常住人" + otaorder.getId());
//					this.payService.pmsAddpayOk(otaorder, PayStatusEnum.doNotPay, PPayInfoTypeEnum.Y2P);
//					this.orderBusinessLogService.saveLog(otaorder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "大于15分到付非常住人优惠券入", "");
//				}
//			}
		}
  }
  
  @Override
	public void callChangeOrderStatusByPmsIN(OtaOrder pOrder) {
		if (pOrder.getOrderStatus() == OtaOrderStatusEnum.CheckIn.getId()) {
			OtaRoomOrder roomOrder = pOrder.getRoomOrderList().get(0);
			PmsRoomOrder pmsRoomOrder = pmsRoomOrderDao.getPmsRoomOrder(roomOrder.getPmsRoomOrderNo(), pOrder.getHotelId());
			PmsCheckinUser checkinUser = findPmsUserIncheckSelect(pOrder.getHotelId(), roomOrder.getPmsRoomOrderNo());
			String freqtrv = checkinUser != null ? String.valueOf(checkinUser.getInt("freqtrv")) : "";
			
			doRuleBWhenPmsIN(pOrder, pmsRoomOrder, freqtrv);
			
			// 订单到付的时候 客户有优惠券下发
          	if ((OrderTypeEnum.PT.getId().intValue() == pOrder.getOrderType())) {
                OrderServiceImpl.logger.info("到付的订单:调用pay支付,订单号{}", pOrder.getId());
                this.payService.pmsAddpayOk(pOrder, PayStatusEnum.doNotPay, PPayInfoTypeEnum.Y2P);
                this.orderBusinessLogService.saveLog(pOrder, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "到付非常住人优惠券入", "");
          	}
		}
	}

  private void modifyPmsOrder(THotel hotel, OtaOrder order, OtaRoomOrder otaRoomOrder) {
      String isNewPms = hotel.getStr("isNewPms");
      logger.info("OTSMessage::modifyPmsOrder::修改订单，isNewPms:{}", isNewPms);
      if ("T".equals(isNewPms)) {
          JSONObject addOrder = new JSONObject();
          addOrder.put("hotelid", hotel.get("pms"));
          addOrder.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
          addOrder.put("function", "updateorder");
          addOrder.put("timestamp ", DateUtils.formatDateTime(new Date()));
          addOrder.put("otaorderid", otaRoomOrder.getLong("otaorderid"));
          addOrder.put("contact", otaRoomOrder.getStr("contacts"));
          addOrder.put("phone", otaRoomOrder.getStr("contactsphone"));
          addOrder.put("memo", otaRoomOrder.getStr("note"));
          if(PromoTypeEnum.TJ.getCode().equals(order.getPromoType())){
              addOrder.put("ispromo", "T");
          }else {
              addOrder.put("ispromo", "F");
          }
          List<OtaRoomPrice> otaRoomPrices = priceService.findOtaRoomPriceByOrder(order);
          JSONObject customerno = new JSONObject();
          customerno.put("customerid", otaRoomOrder.get("id"));
          customerno.put("arrivetime", DateUtils.getStringFromDate(otaRoomOrder.getDate("Begintime"), "yyyyMMddHHmmss"));
          customerno.put("leavetime", DateUtils.getStringFromDate(otaRoomOrder.getDate("Endtime"), "yyyyMMddHHmmss"));
          customerno.put("roomid", otaRoomOrder.get("roompms"));
          // 单日房价list
          JSONArray costArray = new JSONArray();
          if (otaRoomPrices != null) {
              for (OtaRoomPrice otaroomprice : otaRoomPrices) {
                  if (otaroomprice.get("otaroomorderid").equals(otaRoomOrder.getLong("id"))) {
                      JSONObject costItem = new JSONObject();
                      costItem.put("time", otaroomprice.getStr("actiondate"));
                      costItem.put("cost", otaroomprice.get("price"));
                      costArray.add(costItem);
                  }
              }
          }
          customerno.put("cost", costArray);
          List<OtaCheckInUser> otaCheckInUsers = checkInUserDAO.findOtaCheckInUsers(order.getLong("id"));
          if (otaCheckInUsers.size() > 0) {
              JSONArray users = new JSONArray();
              for (OtaCheckInUser otaCheckInUser : otaCheckInUsers) {
                  //name:’’ //入住人姓名   idtype:’’ //证件类型    idno:’’ //证件号 phone ：//电话 ispermanent :  //是否常住人 （1 常住人 2 非常住人）
                  JSONObject checkInUser = new JSONObject();
                  checkInUser.put("name", otaCheckInUser.getName());
//                if (!Strings.isNullOrEmpty(otaCheckInUser.getStr("CardType"))) {
//                    checkInUser.put("idtype", CardTypeEnum.getByName(otaCheckInUser.getStr("CardType")).getId());
//                } else {
//                    checkInUser.put("idtype", "");
//                }
//                checkInUser.put("idno", otaCheckInUser.get("Cardid", ""));
                  checkInUser.put("phone", otaCheckInUser.get("Phone", ""));
                  // ispermanent
                  users.add(checkInUser);
              }
              customerno.put("user", users);
          }
          JSONArray customernos = new JSONArray();
          customernos.add(customerno);
          addOrder.put("customerno", customernos);
          // ots里目前没有换房先注释掉 addOrder.put("roomid",
          // otaRoomOrder.getLong("roomid"));
          
          logger.info("OTSMessage::modifyPmsOrder::修改订单，订单号：{}，参数:{}",order.getId(), addOrder.toJSONString());

          JSONObject returnObject = JSONObject.parseObject(doPostJson(UrlUtils.getUrl("newpms.url") + "/updateorder", addOrder.toJSONString()));
          logger.info("OTSMessage::modifyPmsOrder::修改订单，返回:{}", returnObject.toJSONString());
          if (returnObject.getBooleanValue("success")) {
              logger.info("OTSMessage::modifyPmsOrder::修改订单成功。orderid:{}", otaRoomOrder.getLong("otaorderid"));
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.MODIFY_CHECKINUSERBYUSER.getId(), "", "PMS2.0用户修改入住人:"+otaCheckInUsers.get(0).getName(), "");
          } else {
              throw MyErrorEnum.updateOrder.getMyException("errcode:" + returnObject.getString("errcode") + ",errmsg:"
                      + returnObject.getString("errmsg"));
          }
      } else {

          //发送
          logger.info("发生修改,订单号{}",order.getId());
          PmsUpdateOrder addOrders = new PmsUpdateOrder();
          addOrders.setOrderid(otaRoomOrder.getLong("Id"));
          addOrders.setContact(otaRoomOrder.getStr("Contacts"));
          addOrders.setPhone(otaRoomOrder.getStr("ContactsPhone"));

          addOrders.setCheckin(this.getPmsCheckinPerson(otaRoomOrder));
          logger.info("pms接口调用：修改pms订单：updateOrder：orderid = "+order.getId()+" , 请求参数 = {}",gson.toJson(addOrders));
          ReturnObject<Object> returnObject = HotelPMSManager.getInstance().getService().updateOrder(otaRoomOrder.getLong("HotelId"), addOrders);
          orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.MODIFY_CHECKINUSERBYUSER.getId(), "", "PMS1.0用户修改入住人为："+addOrders.getCheckin().getCpname(), "");
          if (HotelPMSManager.getInstance().returnError(returnObject)) {
              throw MyErrorEnum.updateOrder.getMyException("PMS－" + returnObject.getErrorMessage());
          }
      }
  }

  private PmsCheckinPerson convertOtaCheckInPersonToPms(OtaCheckInUser user) {
      PmsCheckinPerson pmsUser = new PmsCheckinPerson();
      if (user != null) {
          if (StringUtils.isNotBlank(user.getStr("name"))) {
              pmsUser.setCpname(user.getStr("name"));
          }
          if (StringUtils.isNotBlank(user.getStr("phone"))) {
              pmsUser.setCpphone(user.getStr("phone"));
          }
          if (StringUtils.isNotBlank(user.getStr("sex"))) {
              pmsUser.setCpsex(user.getStr("sex"));
          }
      }
      return pmsUser;
  }

  private List<Long> getPromoNoList(String promotionNo, String couponNo) {
      List<Long> promotionNoList = new ArrayList<>();
      if (StringUtils.isNotBlank(promotionNo)) {
          promotionNoList = PayUtil.stringsToList(promotionNo.split(","));
      }
      if (StringUtils.isNotBlank(couponNo)) {
          List<Long> couponNoList = PayUtil.stringsToList(couponNo.split(","));
          promotionNoList.addAll(couponNoList);
      }
      return promotionNoList;
  }

    private OtaOrder extractOrderBeanForModify(HttpServletRequest request, OtaOrder order, boolean createByRoomType) {
      String roomTicket = request.getParameter("roomticket");
      String hotelId = request.getParameter("hotelid");
      String roomTypeId = request.getParameter("roomtypeid");
      String priceType = request.getParameter("pricetype");
      String startdateday = request.getParameter("startdateday");
      String enddateday = request.getParameter("enddateday");
      String roomId = request.getParameter("roomid");
      String orderType = request.getParameter("ordertype");// 1:预付, 2:到店支付
      String hideOrder = request.getParameter("hideorder");
      String breakfastNum = request.getParameter("breakfastnum");// 早餐数
      String contacts = request.getParameter("contacts");
      String contactsPhone = request.getParameter("contactsphone");
      String contactsEmail = request.getParameter("contactsemail");
      String contactsWeixin = request.getParameter("contactsweixin");
      String note = request.getParameter("note");
      String orderMethod = request.getParameter("ordermethod");// 订单方式，默认：3
                                                                  // app提交
      String promotionNo = request.getParameter("promotion");// 促销代码
      // String spreadUser = request.getParameter("spreaduser");
      String couponNo = request.getParameter("couponno");// 优惠券代码
      String quickUserId = request.getParameter("quickuserid");// 常住人主键ID，非必填，可多个，多个使用过
                                                                  // 英文逗号分隔
      String checkInUser = request.getParameter("checkinuser");// 非必填，除去常住人之外的入住人信息，格式为json
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      Date checkBegintime = order.getBeginTime();
      Date checkEndtime = order.getEndTime();
      StringBuffer str = getRequestParamStrings(request);
      logger.info("OTSMessage::OrderController::modifyOrder::提取传递的参数::" + str.toString());
      try {
          if (startdateday != null && enddateday != null) {
              checkBegintime = sdf.parse(startdateday);
              checkEndtime = sdf.parse(enddateday);
          }
      } catch (ParseException e) {
          throw MyErrorEnum.errorParm.getMyException("时间格式错误---startdateday:" + startdateday + " enddateday: " + enddateday);
      }
      try {
          order.setRoomTicket(roomTicket);
          if (StringUtils.isNotBlank(hotelId)) {
              order.setHotelId(Long.parseLong(hotelId));
          }
          // if (StringUtils.isNotBlank(spreadUser)) {
          // order.setSpreadUser(Long.parseLong(spreadUser));
          // }
          if (StringUtils.isNotBlank(roomTypeId)) {
              order.put("roomtypeid", Long.parseLong(roomTypeId));
          }
          if (StringUtils.isNotBlank(priceType)) {
              order.setPriceType(Integer.parseInt(priceType));
          }
          if (StringUtils.isNotBlank(orderMethod)) {
              order.set("ordermethod", Integer.parseInt(orderMethod));
          }
          if (StringUtils.isNotBlank(breakfastNum)) {
              order.set("breakfastnum", Integer.parseInt(breakfastNum));
          }
          if (StringUtils.isNotBlank(orderType)) {
              // 支付支付的单子不能修改paystatus
              if(OrderTypeEnum.YF.getId().equals(order.getOrderType()) && order.getPayStatus() >= PayStatusEnum.alreadyPay.getId().intValue()){
                  logger.info("预付订单已经支付，不能再修改订单类型:orderid:{}", order.getId());
              } else {
                  // 记录旧状态
                  order.put("ordertype_old", order.getOrderType());
                  if (OrderTypeEnum.PT.getId().equals(Integer.parseInt(orderType))) {
                      order.set("ordertype", Integer.parseInt(orderType));
//                      order.set("paystatus", PayStatusEnum.doNotPay.getId());
                        //订单返现
                        order.setIsReceiveCashBack(ReceiveCashBackEnum.noNeedCashBack.getId());
                  } else if(OrderTypeEnum.YF.getId().equals(Integer.parseInt(orderType))){
                      order.set("ordertype", Integer.parseInt(orderType));
//                      order.set("paystatus", PayStatusEnum.waitPay.getId());
                      //订单返现
                      if (order.getCashBack().longValue() > 0) {
              			order.setIsReceiveCashBack(ReceiveCashBackEnum.notReceiveCashBack.getId());
              		}

                  }
              }
          }
      } catch (NumberFormatException e1) {
          throw MyErrorEnum.errorParm.getMyException("数字");
      }
      if (StringUtils.isNotBlank(hideOrder)) {
          order.setHiddenOrder(hideOrder.toUpperCase());
      }
      // 促销代码 和是否使用优惠
      if (StringUtils.isNotBlank(promotionNo)) {
          order.put("promotionno", promotionNo);
          order.set("promotion", "T");
      } else {// add by tankai 20150714 用户修改订单时，可能需要修改优惠券的标示
          order.put("promotionno", "");
          order.set("promotion", "F");
      }
      if (StringUtils.isNotBlank(couponNo)) {
          order.put("couponno", couponNo);
          order.set("coupon", "T");
      } else {// add by tankai 20150714 用户修改订单时，可能需要修改优惠券的标示
          order.put("couponno", "");
          order.set("coupon", "F");
      }
      if (StringUtils.isNotBlank(contacts) || StringUtils.isNotBlank(contactsPhone) || StringUtils.isNotBlank(contactsEmail)
              || StringUtils.isNotBlank(contactsWeixin) || StringUtils.isNotBlank(checkInUser)) {
          order.put("modify_lxr", true);
      }
      THotel hotel = hotelService.readonlyTHotel(order.getHotelId());
      List<OtaRoomOrder> roomOrderList = order.getRoomOrderList();
      // 暂时只有一个订单 可能需要循环
      for (OtaRoomOrder roomOrder : roomOrderList) {
          // Begin 客单信息
          roomOrder.set("hotelid", order.getHotelId());
          // 时租 还是日租
          roomOrder.set("pricetype", order.getPriceType());
          roomOrder.set("begintime", checkBegintime);
          roomOrder.set("endtime", checkEndtime);
          // 如果roomtypeID不为空，换房态
          if (StringUtils.isNotBlank(roomTypeId) && StringUtils.isNotBlank(String.valueOf(roomOrder.getRoomTypeId()))) {
              String oldRoomTypeId = String.valueOf(roomOrder.getRoomTypeId());
              if (!oldRoomTypeId.equals(roomTypeId)) {
                  TRoomType roomType = this.roomTypeDAO.findTRoomType(Long.parseLong(roomTypeId));
                  if (roomType != null) {
                      roomOrder.set("roomtypeid", roomType.get("id"));
                      roomOrder.set("roomtypename", roomType.get("name"));
                      roomOrder.set("roomtypepms", roomType.get("pms"));
                        if (createByRoomType && Long.parseLong(roomTypeId) != order.getLong("roomtypeid")) {
                            // 如果房型变化了,根据房型查一个房间
                            Room room = roomstateService.findVCHotelRoom(Long.parseLong(hotelId), Long.parseLong(roomTypeId), startdateday, enddateday);
                            if (room == null) {
                                throw MyErrorEnum.customError.getMyException("很抱歉，没有房间可以预定了");
                            }
                            roomId = room.getRoomid().toString();
                        }
                    }
                }
            }
          // 如果roomId不为空，如果换房
          if (StringUtils.isNotBlank(roomId) && StringUtils.isNotBlank(String.valueOf(roomOrder.getRoomId()))) {
              String oldRoomId = String.valueOf(roomOrder.getRoomId());
              if (!oldRoomId.equals(roomId)) {
                  // 房间id修改 房号 房间pms
                  String newPromoType = getPromoType(Long.parseLong(oldRoomId));
                  order.setPromoType(newPromoType);
                  TRoom tempRoom = this.roomService.findTRoomByRoomId(Long.parseLong(roomId));
                  if (tempRoom != null) {
                      roomOrder.set("roomid", tempRoom.get("id"));
                      roomOrder.set("roomno", tempRoom.get("name"));
                      roomOrder.set("roompms", tempRoom.get("pms"));
                  }
                  checkPayByPromoType(order, newPromoType);
              }else{
                  //如果选择了今夜特价房则只能使用在线支付或房券支付
                  String oldPromoType = getPromoType(Long.parseLong(oldRoomId));
                  checkPayByPromoType(order, oldPromoType);
              }
          }
          // 预付 到付 担保
          roomOrder.set("ordertype", order.getOrderType());
          // 创建订单是统一为 预付
          roomOrder.set("breakfastNum", order.getBreakfastNum());
          // 联系人信息
          roomOrder.set("contacts", StringUtils.isNotBlank(contacts) ? contacts : order.getContacts());
          roomOrder.set("contactsphone", StringUtils.isNotBlank(contactsPhone) ? contactsPhone : order.getContactsPhone());
          roomOrder.set("contactsemail", StringUtils.isNotBlank(contactsEmail) ? contactsEmail : order.getContactsEmail());
          roomOrder.set("contactsweixin", StringUtils.isNotBlank(contactsWeixin) ? contactsWeixin : order.getContactsWeiXin());
          roomOrder.set("note", StringUtils.isNotBlank(note) ? note : order.getNote());
          roomOrder.set("ordermethod", order.getOrderMethod());

          List<OtaCheckInUser> inUsers = getInUsersByJson(checkInUser);
          roomOrder.put("UserList", inUsers);

          checkInTimeBefore(order, hotel, roomOrder, order.get("spreadUser") != null);
          roomOrder.update();
          order.setContacts(roomOrder.getStr("contacts"));
          order.setContactsPhone(roomOrder.getStr("contactsphone"));
          order.setContactsEmail(roomOrder.getStr("contactsemail"));
          order.setContactsWeiXin(roomOrder.getStr("contactsweixin"));
          order.setNote(roomOrder.getStr("note"));

      }// End 客单信息
      order.put("roomorderlist", roomOrderList);
      return order;
  }

    private void checkPayByPromoType(OtaOrder order, String promoType) {
        if(PromoTypeEnum.TJ.getCode().equals(promoType)){
            //如果选择了今夜特价房则只能使用在线支付或房券支付 其他都不能使用
            if("T".equals(order.getPromotion())){
                throw MyErrorEnum.customError.getMyException("很抱歉，今夜特价房不能与其他促销一起使用");
            }
            if("T".equals(order.getCoupon())){
                throw MyErrorEnum.customError.getMyException("很抱歉，今夜特价房不能使用优惠券");
            }
            if(OrderTypeEnum.YF.getId() != order.getOrderType()){
                throw MyErrorEnum.customError.getMyException("很抱歉，今夜特价房只能使用在线支付");
            }
            try {
                Date begin = DateUtils.parseDate(DateUtils.formatDateTime(order.getBeginTime()), DateUtils.FORMATDATETIME);
                Date end = DateUtils.parseDate(DateUtils.formatDateTime(order.getEndTime()), DateUtils.FORMATDATETIME);
                if(DateUtils.diffDay(begin,end) >= 2){
                    throw MyErrorEnum.customError.getMyException("很抱歉，特价房只能入住一天");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    /**
   * Json转换为联系人
   * 
   * @param checkInUser
   * @return
   * @throws Exception
   */
  public List<OtaCheckInUser> getInUsersByJson(String checkInUser) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      if (StringUtils.isNotBlank(checkInUser)) {
          List<OtaCheckInUser> inUsers = new ArrayList<>();
          JSONArray jsonArr = JSONArray.parseArray(checkInUser);
          for (int i = 0; i < jsonArr.size(); i++) {
              JSONObject jsonObj = jsonArr.getJSONObject(i);
              OtaCheckInUser inUser = new OtaCheckInUser();
              if (StringUtils.isBlank(jsonObj.getString("name"))) {
					continue;
              }
              inUser.set("Name", jsonObj.getString("name"));
              if (StringUtils.isNotBlank(jsonObj.getString("sex"))) {
                  inUser.set("Sex", jsonObj.getString("sex"));
              } else {
                  inUser.set("Sex", "男");
              }
              // 民族
              if (StringUtils.isNotBlank(jsonObj.getString("ethnic"))) {
                  inUser.set("Ethnic", jsonObj.getString("ethnic"));
              }
              try {
                  // 生日
                  if (StringUtils.isNotBlank(jsonObj.getString("birthday"))) {
                      inUser.set("Birthday", sdf.parse(jsonObj.getString("birthday")));
                  }
              } catch (ParseException e) {
                  logger.info("getInUsersByJson::没有birthday信息");
              }
              // 卡类型
              if (StringUtils.isNotBlank(jsonObj.getString("cardtype"))) {
                  inUser.set("CardType", jsonObj.getString("cardtype"));
              }
              // 卡号
              inUser.set("CardId", jsonObj.getString("cardid"));
              inUser.set("DisId", jsonObj.getLong("disid"));
              inUser.set("Address", jsonObj.getString("address"));
                if (StringUtils.isNotBlank(jsonObj.getString("phone"))) {
                    inUser.set("Phone", jsonObj.getString("phone"));
                }

              inUser.set("createTime", new Date());
              inUser.set("cardTime", new Date());
              inUser.set("uploadTime", new Date());
              inUser.set("updateTime", new Date());
              // 照片保存到FTP里 用ImgBase64Util
              /*
               * File file = File.createTempFile("card" + UUID.randomUUID(),
               * "jpg"); ImgBase64Util.generateImage(jsonObj.getString("img"),
               * file); FileManager.getInstance().uploadUserCardPic(file);
               */
//                if (StringUtils.isNotBlank(jsonObj.getString("_pk_"))) {
//                	inUser.set("id", jsonObj.getLong("_pk_"));
//                }
              inUsers.add(inUser);
          }
          return inUsers;
      }
      return null;
  }

  /**
   * 收集请求的参数
   * 
   * @param request
   * @return
   */
  private StringBuffer getRequestParamStrings(HttpServletRequest request) {
      Enumeration<String> enu = request.getParameterNames();
      StringBuffer str = new StringBuffer();
      while (enu.hasMoreElements()) {
          String paraName = (String) enu.nextElement();
          str.append(paraName).append(":").append(request.getParameter(paraName)).append(",");
      }
      str.setLength(str.length() - 1);
      return str;
  }

  /**
   * 订单取消
   * 
   * @param orderid
   * @param jsonObj
   */
  public void doCancelOrder(String orderid, String type, JSONObject jsonObj) {
      logger.info("OTSMessage::取消订单cancelOrder:orderid:{}" + orderid);
      Long orderidTemp = null;
      try {
          orderidTemp = Long.parseLong(orderid);
      } catch (NumberFormatException e1) {
          throw MyErrorEnum.errorParm.getMyException("数字");
      }
      cancelOrder(orderidTemp, type);

      OtaOrder order = findOtaOrderById(orderidTemp);
      PPay ppay = payService.findPayByOrderId(orderidTemp);
      boolean showRoom = true;// 显示客单
      boolean showInUser = true;// 显示入住人----显示客单时才能显示入住人

      orderUtil.getOrderToJson(jsonObj, ppay, order, showRoom, showInUser);
      jsonObj.put("success", true);
  }

  /**
   * 议价后的业务
   * 
   * @param changeprice
   * @param otaOrder
   * @return
   */
  public Map<String, Object> doChageOtaYiJiaPrice(String changeprice, OtaOrder otaOrder) {
      BigDecimal subtrahend = otaOrder.getTotalPrice();
      try {
          // 议价后价格
          subtrahend = new BigDecimal(Double.valueOf(changeprice));
          if (subtrahend.compareTo(BigDecimal.valueOf(10)) <= 0) {
              throw MyErrorEnum.customError.getMyException("修改价格必许大于10元.");
          }
      } catch (NumberFormatException e) {
          logger.info("解析changeprice发生错误. changeprice:{}", changeprice);
      }
      // 四舍五入，不要小数
      subtrahend = subtrahend.setScale(0, BigDecimal.ROUND_HALF_UP);
      BigDecimal promotionprice = otaOrder.getTotalPrice().subtract(subtrahend);

      List<BPromotion> proList = this.iPromoService.findByPromotionType(PromotionTypeEnum.yijia);
      if (proList != null && proList.size() == 1) {
          if (promotionprice.compareTo(BigDecimal.ZERO) <= 0) {
              throw MyErrorEnum.customError.getMyException("修改价格不允许大于或等于原价.");
          }
          Long orderid = otaOrder.getId();
          Long promotionid = proList.get(0).getId();

          BPromotionPrice price = this.iBPromotionPriceDao.findPromotionPricesByOrderIdAndPromoId(orderid, promotionid);
          if (price != null) {
              price.setOfflineprice(promotionprice);
              price.setPrice(promotionprice);
          } else {
              price = new BPromotionPrice();
              price.setOfflineprice(promotionprice);
              price.setPrice(promotionprice);
              price.setOtaorderid(otaOrder.getId());
              price.setPromotion(promotionid);
          }
          iPromoService.saveOrUpdate(price);
          logger.info("OrderController::议价后修改otaroomprice里到pmsprice数据");
          List<OtaRoomPrice> roomPrice = priceDao.findOtaRoomPriceByOrder(orderid);
          if (roomPrice != null) {// 目前应只有一条
              for (OtaRoomPrice otaRoomPrice : roomPrice) {
                  logger.info("OrderController::修改::" + otaRoomPrice);
                  otaRoomPrice.set("pmsPrice", subtrahend);
                  otaRoomPrice.update();
              }
          }
      } else {
          throw MyErrorEnum.customError.getMyException("议价券定义不存在或重复定义.");
      }

      Map<String, Object> rtnMap = Maps.newHashMap();
      rtnMap.put("success", true);
      return rtnMap;
  }

  @Override
  public Long countWeixinOrderNum(Long mid) {
      OtaOrder otaOrder = OtaOrder.dao.findFirst("select count(id) as num from b_otaorder where ordermethod = 3 and mid = ?", mid);
      return otaOrder.getLong("num");
  }

  /*
   * @Override public boolean addpay(Long orderid, Boolean isForce){ OtaOrder
   * order = findOtaOrderById(orderid); if (order == null) { throw
   * MyErrorEnum.findOrder.getMyException("订单号不存在"); }
   * 
   * boolean isRtn = false;
   * logger.info("addpay(orderid:{}, isforce:{})>> ordertype:{}, orderstatus:{}"
   * , orderid, isForce, order.getOrderType(), order.getOrderStatus());
   * if(OrderTypeEnum.PT.getId().intValue() == order.getOrderType()){
   * //1.到付,只允许已经入住状态后的订单才可手动下发
   * if(OtaOrderStatusEnum.CheckIn.getId().intValue() ==
   * order.getOrderStatus()){ boolean isNotSendLezhu = !isSendLezhu(orderid);
   * logger.info("addpay(orderid:{}, isforce:{})>> isNotSendLezhu:{}",
   * orderid, isForce, isNotSendLezhu); //1.1 判断是否成功发送过乐住币 if(isNotSendLezhu){
   * //1.1.1 未成功发送乐住币，可直接调用接口下发 this.payService.pmsAddpayOk(order,
   * PayStatusEnum.doNotPay); isRtn = true; } else { //1.1.2
   * 成功下发乐住币但hotel未收到情况,允许客服手动强制下发 if(isForce){
   * this.payService.pmsAddpayOk(order, PayStatusEnum.doNotPay); isRtn = true;
   * } else { throw MyErrorEnum.customError.getMyException("此订单已成功下发过乐住币."); }
   * } }else{ throw
   * MyErrorEnum.customError.getMyException("到付情况下,只允许已入住状态的订单才可手动下发乐住币."); }
   * } else if(OrderTypeEnum.YF.getId().intValue() == order.getOrderType()){
   * //2.预付,必须支付成功后的订单才可下发乐住币 if(PayStatusEnum.alreadyPay.getId().intValue()
   * == order.getPayStatus()){ boolean isNotSendLezhu = !isSendLezhu(orderid);
   * logger.info("addpay(orderid:{}, isforce:{})>> isNotSendLezhu:{}",
   * orderid, isForce, isNotSendLezhu); //2.1 判断是否成功发送乐住币 if(isNotSendLezhu){
   * //2.1.1 未成功发送乐住币，可直接调用接口下发 this.payService.pmsAddpayOk(order,
   * PayStatusEnum.alreadyPay); isRtn = true; } else { //2.1.2
   * 成功下发乐住币但hotel未收到情况,允许客服手动强制下发 if(isForce){
   * this.payService.pmsAddpayOk(order, PayStatusEnum.alreadyPay); isRtn =
   * true; } else { throw
   * MyErrorEnum.customError.getMyException("此订单已下发过乐住币."); } } }else{ throw
   * MyErrorEnum.customError.getMyException("预付情况下,只允许已支付成功的订单才可手动下发乐住币."); }
   * } return isRtn; }
   * 
   * private boolean isSendLezhu(Long orderid){ PPay pay =
   * this.payService.findPayByOrderId(orderid); if(pay!=null){ POrderLog
   * orderLog = this.iPOrderLogDao.findPOrderLogByPay(pay.getId());
   * if(orderLog!=null){ return
   * PmsSendEnum.ResponseSuccess.equals(orderLog.getPmssend()); } } return
   * false; }
   */

    public void saveOrUpdateOtaCheckInUser(OtaRoomOrder roomOrder, List<OtaCheckInUser> checkInUserList) {
        Long mid = MyTokenUtils.getMidByToken("");
        if (checkInUserList != null && checkInUserList.size() > 0) {
        	// 删除所有入住人
            checkInUserDAO.delectOtaCheckInUserByRoomOrderId(roomOrder.getLong("id"));
            for (OtaCheckInUser otaCheckInUser : checkInUserList) {
                otaCheckInUser.set("mid", mid);
                otaCheckInUser.set("OtaRoomOrderId", roomOrder.getLong("id"));
                otaCheckInUser.saveOrUpdate();
            }
        }
    }

  @Override
  public OtaRoomOrder findOtaRoomOrderByOrderId(Long otaOrderId) {
      return roomOrderDAO.findOtadRoomOrderByOtaOrderId(otaOrderId);
  }

  private void pmsCancelOrder(OtaOrder order) {

      logger.info("OTSMessage::取消订单:pmsCancelOrder:start{}", order);
      // 修改已使用券状态
        iPromotionPriceService.updateTicketStatus(order);
      logger.info("修改订单{}的券为可用状态,执行成功.", order.getId());
      logger.info("pmsCancelOrder修改订单{}的券为可用状态,执行成功.", order.getId());
      orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.UPDATEORDERSTATUS.getId(), "PMS回调，状态:RX", "由PMS取消了订单", "");

      // PMS解房
      unLockRoom(order);
      logger.info("OTSMessage::pmsCancelOrder---end{}", order.getId());
  }

  public void cancelOrderError(OtaOrder order,String flag){
  	if("1".equals(flag)){
          cancelOrder(order);
  	} else {
            iPromotionPriceService.updateTicketStatus(order);
        // ots解房
        unLockRoom(order);
  	}
      order.setOrderStatus(OtaOrderStatusEnum.CancelBySystem.getId());
      order.update();
      //修改已使用券状态

      orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELBYSYSTEM.getId(), "", "系统取消订单成功,orderid:"+order.getId(), "");
  }

  /**
   * 101 http异常，酒店不在线
   * @param url
   * @param json
   * @return
   */
  private String doPostJson(String url, String json) {
      JSONObject back = new JSONObject();
      try {
            return PayTools.dopostjson(url, json);
      } catch (Exception e) {
          logger.info("doPostJson参数:{},{},异常:{}", url, json, e.getLocalizedMessage());
          e.printStackTrace();
          back.put("success", false);
          if(e instanceof HTTPException){
              back.put("errorcode", "101");
          }else{
              back.put("errorcode", "-1");
          }
          back.put("errormsg", e.getLocalizedMessage());
      }
      return back.toJSONString();
  }

  /**
   * 订单数量统计
   * 
   * @param sqnum
   * @param orderStatus
   * @param token
   */
  public JSONObject selectCountByOrderStatus(String sqnum, List<String> orderStatus, String token) {
      logger.info("OTSMessage::OrderService::selectCountByOrderStatus:{}::begin", orderStatus);
      Long count = orderDAO.selectCountByOrderStatus(orderStatus, token);
      JSONObject statuscount = new JSONObject();
      statuscount.put("sqnum", sqnum);
      statuscount.put("ordercount", count);

      return statuscount;
  }

  @Override
  public Map<String, Object> findMaxAndMinOrderId(Map<String, Object> paramMap) {
      return otaOrderDAO.findMaxAndMinOrderId(paramMap);
  }

  @Override
  public List<BOtaorder> findOtaOrderList(Map<String, Object> paramMap) {
      return otaOrderDAO.findOtaOrderList(paramMap);
  }

  /**
   * 【B端】修改订单房间
   * 
   * @param request
   * @param jsonObj
   */
  public void doUpdateOrder(HttpServletRequest request, JSONObject jsonObj) {
      logger.info("OTSMessage::OrderService::doUpdateOrder::begin");
      String orderId = request.getParameter("orderid").trim();
//    String hotelId = request.getParameter("hotelid");
//    String roomNo = request.getParameter("roomno");
      String roomTypeId = request.getParameter("roomtypeid");
      String roomId = request.getParameter("roomid");
      String contacts = request.getParameter("contacts");
      String contactsphone = request.getParameter("contactsphone");
      String checkInUser = request.getParameter("checkinuser");
      String operator = request.getParameter("operator");
      String isSyncPrice = request.getParameter("issyncprice");
      
      StringBuffer str = orderUtil.getRequestParamStrings(request);
      logger.info("开始doUpdateOrder::提取传递的参数::" + orderId + "::" + str.toString());
      
      if(!StringUtils.isNotBlank(operator)){
          operator = "" ;
      }
      if(!StringUtils.isNotBlank(isSyncPrice)){
          isSyncPrice = "F" ;
      }
      OtaOrder order = findOtaOrderById(Long.parseLong(orderId));
      if (order == null) {
          throw MyErrorEnum.customError.getMyException("订单号不存在");
      }
      if (OtaOrderStatusEnum.Confirm.getId() < order.getOrderStatus()) {
          throw MyErrorEnum.customError.getMyException("此订单已经入住或离店不能修改");
      }
      THotel hotel = hotelService.readonlyTHotel(order.getHotelId());
      if (hotel == null) {
          throw MyErrorEnum.customError.getMyException("没有当前id的酒店");
      }

      OtaRoomOrder roomOrder = findOtaRoomOrderByOrderId(Long.parseLong(orderId));
      if (roomOrder != null) {
          // 通过房型查找 房型id 名称
          if (StringUtils.isNotBlank(roomId)) {
              TRoom tempRoom = this.roomService.findTRoomByRoomId(Long.parseLong(roomId));
              logger.info("OrderServiceImpl:: doUpdateOrder:: roomId:" + orderId + "::" +tempRoom);
              if (tempRoom != null) {
                  roomOrder.set("roomid", tempRoom.get("id"));
                  roomOrder.set("roomno", tempRoom.get("name"));
                  roomOrder.set("roompms", tempRoom.get("pms"));
              }
          }
          if (StringUtils.isNotBlank(roomTypeId)) {
              TRoomType roomType = this.roomTypeDAO.findTRoomType(Long.parseLong(roomTypeId));
              logger.info("OrderServiceImpl:: doUpdateOrder:: roomTypeId:" + orderId + "::" +roomType);
              if (roomType != null) {
                  roomOrder.set("roomtypeid", roomType.get("id"));
                  roomOrder.set("roomtypename", roomType.get("name"));
                  roomOrder.set("roomtypepms", roomType.get("pms"));
              }
          }
          if (StringUtils.isNotBlank(contacts)) {
              roomOrder.set("contacts", contacts);
              order.set("contacts", contacts);
          }
          if (StringUtils.isNotBlank(contactsphone)) {
              roomOrder.set("contactsphone", contactsphone);
              order.set("contactsphone", contactsphone);
          }
          if("F".equals(isSyncPrice)){
              roomOrder.update();
              order.update();
          } else {
              roomOrder = this.saveRoomOrder(order, roomOrder, order.get("spreadUser") != null);
              // otaOrder 更新
              order.set("totalprice", roomOrder.getTotalPrice());
              order.set("price", roomOrder.get("price"));
              order.update();
              logger.info("OrderServiceImpl:: doUpdateOrder:: isSyncPrice:T:price:" + orderId + "::"+ roomOrder.get("price"));
          }
          List<OtaCheckInUser> inUsers = this.getInUsersByJsonForB(checkInUser);
          this.saveOrUpdateOtaCheckInUserForB(roomOrder, inUsers);
          OrderServiceImpl.logger.info("OTSMessage::doUpdateOrder:更新房号对应的roomorder数据," +orderId + "::" + roomId + "::" + roomTypeId);
          this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDER.getId(), "", operator + "  更新房间信息：" + roomOrder.getRoomNo(), "");
          List<OtaRoomPrice> otaRoomPrices = this.priceService.findOtaRoomPriceByOrder(order);
         
          logger.info("OrderServiceImpl:: doUpdateOrder:: 修改入住人名字 start orderid : " + orderId);
          //修改入住人信息
          //若订单为规则B，则做以下处理：
          //1.15分钟之内，有优惠券的订单若C端调用修改订单接口，修改入住人姓名，OTS记录修改后的入住人姓名，但不向PMS发送修改后的cpname
          Date createTime = order.getCreateTime();
          Date nowDateTime = new Date();
          long temp = nowDateTime.getTime() - createTime.getTime(); // 相差毫秒数
          // 判断ots订单是否已经下发到pms
          if (roomOrder.getPmsRoomOrderNo() != null) {
              // B端修改入住人代码
              modifyCrsPmsOrder(hotel,order,roomOrder,operator,otaRoomPrices);
          }
      }
      logger.info("OTSMessage::OrderService::doUpdateOrder::end");
      jsonObj.put("success", true);
  }
  
  /**
   * 得到pms2.0报文
   * @param order
   * @return
   */
  private JSONObject getPms20Message(OtaOrder order){
      order.getHotelId();
      THotel hotel = this.hotelService.readonlyTHotel(order.getLong("hotelId"));
      String isNewPms = hotel.getStr("isNewPms");
      
      List<OtaRoomOrder> roomOrders = order.getRoomOrderList();
      List<OtaRoomPrice> otaRoomPrices = priceService.findOtaRoomPriceByOrder(order);
      
      //组装pms2.0报文
      JSONObject addOrder = new JSONObject();
      addOrder.put("hotelid", hotel.get("pms"));
      addOrder.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
      addOrder.put("function", "addorder");
      addOrder.put("timestamp ", DateUtils.formatDateTime(new Date()));
      addOrder.put("otaorderid", order.getId());
      if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
          addOrder.put("ordertype", 1);
      } else {
          addOrder.put("ordertype", 2);
      }
      addOrder.put("contact", order.getContacts());
      addOrder.put("phone", order.getContactsPhone());
      addOrder.put("memo", order.getNote());
      // 客单数据
      JSONArray customerno = new JSONArray();
      for (OtaRoomOrder roomOrder : roomOrders) {
          // 订单对应客单
          JSONObject customernoItem = new JSONObject();
          customernoItem.put("customerid", roomOrder.getId());
          customernoItem.put("roomid", roomOrder.getStr("roompms"));
          customernoItem.put("arrivetime", DateUtils.getStringFromDate(roomOrder.getDate("Begintime"), "yyyyMMddHHmmss"));
          customernoItem.put("leavetime", DateUtils.getStringFromDate(roomOrder.getDate("Endtime"), "yyyyMMddHHmmss"));
          // 单日房价list
          JSONArray costArray = new JSONArray();
          if (otaRoomPrices != null) {
              Collections.sort(otaRoomPrices, new Comparator<OtaRoomPrice>() {
                  @Override
                  public int compare(OtaRoomPrice param1, OtaRoomPrice param2) {
                      return param1.getStr("actiondate").compareTo(param2.getStr("actiondate"));
                  }
              });

              for (int i = 0; i < otaRoomPrices.size(); i++) {// 不用多传一天
                  OtaRoomPrice otaroomprice = otaRoomPrices.get(i);
                  if (otaroomprice.get("otaroomorderid").equals(roomOrder.getLong("id"))) {
                      JSONObject costItem = new JSONObject();

                      costItem.put("time", otaroomprice.getStr("actiondate"));
                      costItem.put("cost", otaroomprice.get("price"));
                      costArray.add(costItem);
                  }
                  if (costArray.size() == order.getDaynumber()) {
                      break;
                  }
              }
          }

          customernoItem.put("cost", costArray);
          List<OtaCheckInUser> otaCheckInUsers = checkInUserDAO.findOtaCheckInUsers(order.getId());
          if (otaCheckInUsers.size() > 0) {
              JSONArray users = new JSONArray();
              for (OtaCheckInUser otaCheckInUser : otaCheckInUsers) {
                  //name:’’ //入住人姓名   idtype:’’ //证件类型    idno:’’ //证件号 phone ：//电话 ispermanent :  //是否常住人 （1 常住人 2 非常住人）
                  JSONObject checkInUser = new JSONObject();
                  checkInUser.put("name", otaCheckInUser.getName());
                  checkInUser.put("phone", otaCheckInUser.get("Phone", ""));
                  users.add(checkInUser);
              }
              customernoItem.put("user", users);
          }

          customerno.add(customernoItem);
      }

      addOrder.put("customerno", customerno);
      return addOrder;
  }
  
  private void modifyCrsPmsOrder(THotel hotel, OtaOrder order, OtaRoomOrder otaRoomOrder,String operator,List<OtaRoomPrice> otaRoomPrices) {
      String isNewPms = hotel.getStr("isNewPms");
      logger.info("OTSMessage::modifyPmsOrder::修改订单，isNewPms:{}", isNewPms);
      if ("T".equals(isNewPms)) {
          JSONObject addOrder = new JSONObject();
          addOrder.put("hotelid", hotel.get("pms"));
          addOrder.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
          addOrder.put("function", "updateorder");
          addOrder.put("timestamp ", DateUtils.formatDateTime(new Date()));
          addOrder.put("otaorderid", otaRoomOrder.getLong("otaorderid"));
          addOrder.put("contact", otaRoomOrder.getStr("contacts"));
          addOrder.put("phone", otaRoomOrder.getStr("contactsphone"));
          addOrder.put("memo", otaRoomOrder.getStr("note"));
//          List<OtaRoomPrice> otaRoomPrices = priceService.findOtaRoomPriceByOrder(order);
          JSONObject customerno = new JSONObject();
          customerno.put("customerid", otaRoomOrder.get("id"));
          customerno.put("arrivetime", DateUtils.getStringFromDate(otaRoomOrder.getDate("Begintime"), "yyyyMMddHHmmss"));
          customerno.put("leavetime", DateUtils.getStringFromDate(otaRoomOrder.getDate("Endtime"), "yyyyMMddHHmmss"));
          customerno.put("roomid", otaRoomOrder.get("roompms"));
          // 单日房价list
          JSONArray costArray = new JSONArray();
          if (otaRoomPrices != null) {
              for (OtaRoomPrice otaroomprice : otaRoomPrices) {
                  if (otaroomprice.get("otaroomorderid").equals(otaRoomOrder.getLong("id"))) {
                      JSONObject costItem = new JSONObject();
                      costItem.put("time", otaroomprice.getStr("actiondate"));
                      costItem.put("cost", otaroomprice.get("price"));
                      costArray.add(costItem);
                  }
              }
          }
          customerno.put("cost", costArray);
          List<OtaCheckInUser> otaCheckInUsers = checkInUserDAO.findOtaCheckInUsers(order.getLong("id"));
          if (otaCheckInUsers.size() > 0) {
              JSONArray users = new JSONArray();
              for (OtaCheckInUser otaCheckInUser : otaCheckInUsers) {
                  JSONObject checkInUser = new JSONObject();
                  checkInUser.put("name", otaCheckInUser.getName());
                  checkInUser.put("phone", otaCheckInUser.get("Phone", ""));
                  // ispermanent
                  users.add(checkInUser);
              }
              customerno.put("user", users);
          }
          JSONArray customernos = new JSONArray();
          customernos.add(customerno);
          addOrder.put("customerno", customernos);
              JSONObject returnObject = JSONObject.parseObject(doPostJson(UrlUtils.getUrl("newpms.url") + "/updateorder", addOrder.toJSONString()));
              logger.info("OTSMessage::modifyPmsOrder::修改订单，返回:{}", returnObject.toJSONString());
              if (returnObject.getBooleanValue("success")) {
                  logger.info("OTSMessage::modifyPmsOrder::修改订单成功。orderid:{}", otaRoomOrder.getLong("otaorderid"));
                  //
                  orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.MODIFY_CHECKINUSERBYUSER.getId(), "", "PMS2.0客服修改入住人:"+otaCheckInUsers.get(0).getName(), "");
              } else {
                  throw MyErrorEnum.updateOrder.getMyException("errorcode:" + returnObject.getString("errorcode") + ",errormsg:"
                          + returnObject.getString("errormsg"));
              }
      } else {
          // 订单下发到pms
        List<PmsOtaAddOrder> list = new ArrayList<PmsOtaAddOrder>();
        {
            PmsOtaAddOrder addOrder = new PmsOtaAddOrder();
            addOrder.setOtaOrderId(order.getLong("id"));
            addOrder.setCustomNo(otaRoomOrder.getLong("id"));
            addOrder.setLinkOrderId(otaRoomOrder.getLinkRoomOrder() == null ? 0 : otaRoomOrder.getLinkRoomOrder().getLong("id"));
            addOrder.setContact(otaRoomOrder.getStr("contacts"));
            addOrder.setPhone(otaRoomOrder.getStr("contactsphone"));
            addOrder.setPromotion(otaRoomOrder.getStr("promotionno"));
            addOrder.setOrdertype(OrderTypeEnum.getByID(otaRoomOrder.getInt("ordertype")).getKey());
            addOrder.setPricetype(PriceTypeEnum.getByID(otaRoomOrder.getInt("pricetype")).getKey());
            addOrder.setMemo(otaRoomOrder.getStr("note"));
            addOrder.setRoompms(otaRoomOrder.getStr("roompms"));
            addOrder.setArrivaltime(otaRoomOrder.getDate("begintime"));
            addOrder.setExcheckouttime(otaRoomOrder.getDate("endtime"));
            addOrder.setBreakfastcnt(otaRoomOrder.getInt("breakfastnum"));
            List<OtaRoomPrice> otaRoomPrices_room = new ArrayList<>();
            if (otaRoomPrices != null) {
                for (OtaRoomPrice otaRoomPrice : otaRoomPrices) {
                    if (otaRoomPrice.get("otaroomorderid").equals(otaRoomOrder.get("id"))) {
                        otaRoomPrices_room.add(otaRoomPrice);
                    }
                }
            }
            logger.info("OTSMessage::doUpdateOrder::getPmsRCost::取得getPmsRCost数据start");
            addOrder.setrCostList(this.getPmsRCost(otaRoomOrder, otaRoomPrices_room));// 获取日租时价格表
            logger.info("OTSMessage::doUpdateOrder::getPmsRCost::取得getPmsRCost数据end");

            if (StringUtils.isNotBlank(otaRoomOrder.getStr("contacts"))) {
                PmsCheckinPerson pmsCheckinPerson=this.getPmsCheckinPerson(otaRoomOrder);
                addOrder.setCheckin(pmsCheckinPerson);
            }
            list.add(addOrder);
        }
        logger.info("OTSMessage::modifyPmsOrder::修改订单，参数:{}", order.getId()+"::"+gson.toJson(list));
        
        ReturnObject<List<PmsOtaAddOrder>> returnObject = HotelPMSManager.getInstance().getService()
                .updateImportantOrder(order.getHotelId(), list);
        
        //
        orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.MODIFY_CHECKINUSERBYUSER.getId(), "", "PMS1.0客服修改入住人:"+otaRoomOrder.getStr("contacts"), "");          
          if (HotelPMSManager.getInstance().returnError(returnObject)) {
              if ("527".equals(returnObject.getErrorCode())) {
                  orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDER.getId(), "", " 更新房间失败，房间没有了!", "");
                  throw MyErrorEnum.customError.getMyException("房间没有了");// PMS
                                                                          // 错误
              } else {
                  logger.info("OTSMessage::OrderService::updateImportantOrder::pms返回error");
                  orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDER.getId(), "", " 更新房间失败，pms返回error!", "");
                  throw MyErrorEnum.customError.getMyException(returnObject.getErrorMessage() == null ? "pms交互返回错误!" : returnObject
                          .getErrorMessage());// PMS 错误
              }
          } else {
              orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.KF_MODIFYORDER.getId(), "", operator + "  更新房间成功。", "");
              logger.info("OTSMessage::OrderService::updateImportantOrder::pms返回sucess");
          }
        }
    }
    
    
    /**
     * 向手机端推送消息
     */
    public void pushMessage(){
        logger.info("开始向手机端推送消息");
        OtaOrderTasts otaOrderTasts=new OtaOrderTasts();
   
        otaOrderTasts.setTasktype(OrderTasksTypeEnum.ORDERPUSH.getId());
        otaOrderTasts.setStatus(OrderTasksStatusEnum.INITIALIZE.getId());
        
        long start = System.currentTimeMillis();
        List<OtaOrderTasts> list=orderTastsMapper.findPushMessage(otaOrderTasts);
        logger.info("要发送的信息：{},查询耗时:{}ms",gson.toJson(list),(System.currentTimeMillis() - start));
        int count=0;
        for(OtaOrderTasts orderTasts:list){
            PushMessage pushMessage=gson.fromJson(orderTasts.getContent(),PushMessage.class);
            
            // 防重处理,失效时间一天
         String key = MD5Util.md5Hex(orderTasts.getOtaorderid()+"-"+orderTasts.getContent());
            String lockValue = DistributedLockUtil.tryLock(key, 86400);
                if (lockValue == null) {
                logger.info("此订单已经发过push消息，无需再发,orderid = {}", orderTasts.getOtaorderid());
                // 修改任务状态为成功
                orderTasts.setStatus(OrderTasksStatusEnum.CHANGE.getId());
                orderTasts.setUpdatetime(new Date());
                orderTastsMapper.updateByPrimaryKeySelective(orderTasts);
                continue;
            }
            
            logger.info("用户关怀消息推送,orderid = {} , content = {}",orderTasts.getOtaorderid(),orderTasts.getContent());
            start = System.currentTimeMillis();
            try {
				Message message=pushMessage.getMessage();
				if(pushMessage.getIsSms()){
					careProducer.sendSmsMsg(message);
				}
				careProducer.sendAppMsg(message);
				careProducer.sendWeixinMsg(message);
				  logger.info("用户关怀消息推送成功,发送时间 = {}ms",(System.currentTimeMillis() - start));
	                start = System.currentTimeMillis();
	                //推送失败增加测试
	                orderTasts.setStatus(OrderTasksStatusEnum.CHANGE.getId());
	                orderTasts.setUpdatetime(new Date());
	                orderTastsMapper.updateByPrimaryKeySelective(orderTasts);
	                logger.info("用户关怀消息推送成功,更新ordertask状态耗时{}ms,orderid = {}",(System.currentTimeMillis() - start),orderTasts.getOtaorderid());
			} catch (Exception e) {
				 //推送失败增加次数
                orderTasts.setCount(count);
                orderTasts.setUpdatetime(new Date());
                orderTastsMapper.updateByPrimaryKeySelective(orderTasts);
                // 推送失败释放防重锁
                DistributedLockUtil.releaseLock(key, lockValue);
                logger.info("用户关怀消息推送失败,orderid = {},错误消息：{}",orderTasts.getOtaorderid(),e.getLocalizedMessage());
			}

        }
        logger.info("结束向手机端推送消息");
    }
   /* public void myTest(long otaorderid,String method){
        OtaOrder pOrder= new OtaOrder().dao.findById(otaorderid);
        *//**
         * 拿到pms客单号
         *//*
        List<OtaRoomOrder> list=pOrder.getRoomOrderList();
        String pmsRoomOrderNo=null;
        if(null!=list&&list.size()>0){
            OtaRoomOrder otaRoomOrder=list.get(0);
            pmsRoomOrderNo=otaRoomOrder.getPmsRoomOrderNo();
        }
        
        if(OtaOrderStatusEnum.Confirm.getId() == pOrder.getOrderStatus()||
                //等待支付 且 已经有客单
                (OtaOrderStatusEnum.WaitPay.getId() == pOrder.getOrderStatus()&&
                StringUtils.isNotBlank(pmsRoomOrderNo))){
            System.out.println("ok");
        }else{
            System.out.println("no");
        }
    }*/
    
    // 入住前 1小时提醒 用户
    // 在确定了入住时间的时候把 这条订单记录放到任务表中， domodify
    public void pushCheckInBefore1Msg(OtaOrder pOrder) {
    	if(pOrder.getOrderType()!=OrderTypeEnum.PT.getId()){
    		logger.info("只有到付才有入住提醒，订单号：{}",pOrder.getId());
           	return;
        }
        // 发送短信 入住前1小时 === 判断条件1：是否已经入住, 判断条件2：切客或预付当天单预抵时间为5分钟 不发送
        Long hotelId = pOrder.getHotelId();
        Long otaOrderid = pOrder.getId();
        logger.info("OrderPushMsgService:: pushCheckInBefore1Msg:: 预抵时间前1小时推送消息 start: otaOrderId = "
                + otaOrderid);
        // 取到预抵时间 前一小时
        Date begintimeBefore1 = DateUtils.addHours(pOrder.getBeginTime(), -1);
        
        /**
         * 如果begintimeBefore1<预定时间，那么就不要发送到店时间提醒
         */
        long l=begintimeBefore1.getTime()-pOrder.getCreateTime().getTime();
        if(l<0){
            logger.info("只有订单创建时间与预抵时间之间超过一个小时,才有到店提醒");
            return;
        }
        
        long temp = pOrder.getBeginTime().getTime()
                - pOrder.getCreateTime().getTime();
        if (temp == OrderServiceImpl.TIME_FOR_FIVEMIN) {
            logger.info("OrderPushMsgService:: pushCheckInBefore1Msg:: 预抵时间前1小时推送消息: 此订单为5分钟到，不需发送消息。otaOrderId = "
                    + otaOrderid);
            return;
        }
        // 封装orderTasks 加到任务表中 status= 0初始化, taskType= 101push类型,
        OtaOrderTasts  otaOrderTasts= this.getMessageToC(pOrder, begintimeBefore1, true,CopywriterTypeEnum.order_reach);

        int result = this.otaOrderTastsMapper.insertSelective(otaOrderTasts);
        if (result == 0) {
            logger.info("保存报文失败，报文信息:{}，订单Id:{}",
                    JsonKit.toJson(otaOrderTasts), otaOrderid);
        } else {
            logger.info("保存报文成功，orderTasts id：{}，订单Id:{}",
                    otaOrderTasts.getId(), otaOrderid);
        }
        logger.info("OrderPushMsgService:: pushCheckInBefore1Msg:: 预抵时间前1小时推送消息 end: otaOrderId = "
                + otaOrderid);
    }

    /**
     * 的到C端消息推送信息
     * @param otaorder
     * @param executeTime
     * @param isSms
     * @return
     */
    private OtaOrderTasts getMessageToC(OtaOrder otaorder,Date executeTime,Boolean isSms,CopywriterTypeEnum copywriterTypeEnum){
    	  
    	  Message message=new Message();
          message.setCopywriterTypeEnum(copywriterTypeEnum);
          message.setMid(otaorder.getMid());
          message.setOrderId(otaorder.getId());
          message.setPhone(otaorder.getContactsPhone());
          
          PushMessage pushMessage= new PushMessage();
          pushMessage.setMessage(message);
          pushMessage.setIsSms(isSms);
          
          //发送信息
          OtaOrderTasts orderTasts=new OtaOrderTasts();
          orderTasts.setCount(0);
          orderTasts.setCreatetime(new Date());
          orderTasts.setUpdatetime(new Date());
          orderTasts.setContent(gson.toJson(pushMessage));
          orderTasts.setHotelid(otaorder.getHotelId());
          //推迟十分钟执行
          orderTasts.setExecuteTime(executeTime);
          
          orderTasts.setOtaorderid(otaorder.getId());
          orderTasts.setTasktype(OrderTasksTypeEnum.ORDERPUSH.getId());
          orderTasts.setStatus(OrderTasksStatusEnum.INITIALIZE.getId());
          this.logger.info("生成手机信息推送，订单号：{}，详细信息：{}",otaorder.getId(),gson.toJson(orderTasts));
          return orderTasts;
    }

    
    // 在取消订单的时候 修改任务表中 status为2 不发送 ==用户取消. pms取消. 系统自动取消 （没有）. 客服取消.
    // 支付失败的时候（待定），不用调用该方法？？？
    public void pushMsgNo(OtaOrder pOrder) {
        Long otaOrderid = pOrder.getId();
        logger.info("OrderPushMsgService:: pushCheckInBefore1Msg:: 订单被取消，不需发送消息。start: otaOrderId = "
                + otaOrderid);
        List<OtaOrderTasts> orderTasksList = otaOrderTastsMapper
                .selectOrderTasksList(otaOrderid);
        for (OtaOrderTasts otaOrderTasts : orderTasksList) {
            otaOrderTasts.setStatus(OrderTasksStatusEnum.FAILURE.getId());//设置orderTasks状态为 不发送
            otaOrderTasts.setUpdatetime(new Date());//设置 更新时间
            Integer count = otaOrderTasts.getCount();//设置 失败次数？？？
            otaOrderTasts.setCount(++count);
            int result = otaOrderTastsMapper
                    .updateByPrimaryKeySelective(otaOrderTasts);
            if (result == 0) {
                logger.info("pushMsg修改报文失败，报文信息:{}，订单Id:{}",
                        JsonKit.toJson(otaOrderTasts), otaOrderid);
            } else {
                logger.info("ushMsg修改报文成功，orderTasts id：{}，订单Id:{}",
                        otaOrderTasts.getId(), otaOrderid);
            }

        }
        logger.info("OrderPushMsgService:: pushCheckInBefore1Msg:: 订单被取消，不需发送消息。end: otaOrderId = "
                + otaOrderid);
    }
    
    //在可以判断用户到保留时间 到或者没到 订单是否改为入住状态的地方  向orderTasks表中存放一条记录 发送消息 订单已过保留时间，
    //如果在入住的时候 saveCustomerNo 用户入住了 就取消这条orderTasks 根据订单id content内容中 pushMessage.setTitle("到店时间提醒"); 确定记录 修改status 为不发送
    //发送 保留时间 取消订单的消息 
    public void pushOutCheckInTimeMsg(OtaOrder pOrder){
    	//判断 只有前台到付的单子 需要发送消息 
    	if(pOrder.getOrderType() == OrderTypeEnum.PT.getId()){
            Long hotelId = pOrder.getHotelId();
            Long otaOrderid = pOrder.getId();
            logger.info("OrderPushMsgService:: pushOutCheckInTimeMsg:: 过保留时间未到的推送消息 start: otaOrderId = "
                    + otaOrderid);

            // 封装orderTasks 加到任务表中 status= 0初始化, taskType= 101push类型,
            OtaOrderTasts  otaOrderTasts= this.getMessageToC(pOrder, pOrder.getBeginTime(), false,CopywriterTypeEnum.order_unreach);
            
            int result = this.otaOrderTastsMapper.insertSelective(otaOrderTasts);
            if (result == 0) {
                logger.info("保存报文失败，报文信息:{}，订单Id:{}",
                        JsonKit.toJson(otaOrderTasts), otaOrderid);
            } else {
                logger.info("保存报文成功，orderTasts id：{}，订单Id:{}",
                        otaOrderTasts.getId(), otaOrderid);
            }
            logger.info("OrderPushMsgService:: pushOutCheckInTimeMsg:: 过保留时间未到的推送消息 end: otaOrderId = "
                    + otaOrderid);
        }
    }

    /**
     * 取消订单的使用钱包
     *
     * @param order
     */
    public void cancelAvailableMoney(OtaOrder order) {
		this.logger.info("cancelAvailableMoney:orderid:{},order.getAvailableMoney:{}", order.getId(), order.getAvailableMoney());
		if(order.getAvailableMoney().doubleValue()>0){
			if (order.getPayStatus() == PayStatusEnum.waitPay.getId() || order.getPayStatus() == PayStatusEnum.doNotPay.getId()) {
				this.logger.info("取消冻结钱包，订单号：{},支付状态：{}", order.getId(), order.getPayStatus());
				walletCashflowService.unLockCashFlow(order.getMid(), order.getId());
//				this.unLockCashFlow(order);
			} else {
				this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_BACKCASH.getId(), "", "¥" + order.getAvailableMoney()
						+ "红包已退回您的账户", "");
//				BigDecimal availableMoney = new BigDecimal(0);
//				order.setAvailableMoney(availableMoney);
//				order.update();
				if (this.walletCashflowService.refund(order.getMid(), order.getId())) {
					this.logger.info("钱包回滚回滚成功，订单号：{}", order.getId());
				} else {
					this.logger.info("通知钱包回滚失败，订单号：{}", order.getId());
				}
			}
		}
        this.logger.info("cancelAvailableMoney:ok");
    }

    /**
     * 根据用户得到历史入住人
     *
     * @return
     */
    public JSONObject getCheckInUserByMid() {
        JSONObject jsonObj = new JSONObject();
        Long mid = MyTokenUtils.getMidByToken("").longValue();
        List<OtaCheckInUser> list = this.checkInUserDAO.findOtaCheckInUserListByMid(mid);
        this.logger.info("mid:{},入住人详细：{}", mid, list);
        JSONArray datas = new JSONArray();
        for (OtaCheckInUser checkInUser : list) {
            JSONObject data = new JSONObject();
            data.put("username", checkInUser.getName());
            data.put("phone", checkInUser.getPhone());
            datas.add(data);
        }
        jsonObj.put("datas", datas);
        jsonObj.put("success", true);
        return jsonObj;
    }
    //如果某一时间办理入住后 取消发送未到提醒的消息任务 
    public void pushOutCheckInTimeMsgNo(Long otaOrderid){
        OtaOrder pOrder = new OrderServiceImpl().findOtaOrderById(otaOrderid);
        if(pOrder.getOrderType() == OrderTypeEnum.PT.getId()){//判断 只有前台到付的单子 需要查询出来修改状态为 不发送status = 2
            logger.info("OrderPushMsgService:: pushOutCheckInTimeMsgNo:: 办理入住后，不需发送保留时间未到消息。start: otaOrderId = "
                    + otaOrderid);
            List<OtaOrderTasts> orderTasksList = otaOrderTastsMapper
                    .selectOrderTasksList(otaOrderid);
            for (OtaOrderTasts otaOrderTasts : orderTasksList) {
                String content = otaOrderTasts.getContent();
                PushMessage pushMessage = gson.fromJson(content, PushMessage.class);
                
                if("保留时间".equals(pushMessage.getTitle())){
                    otaOrderTasts.setStatus(OrderTasksStatusEnum.FAILURE.getId());//设置orderTasks状态为 不发送
                    otaOrderTasts.setUpdatetime(new Date());//设置 更新时间
                    Integer count = otaOrderTasts.getCount();//设置 失败次数？？？
                    otaOrderTasts.setCount(++count);
                    int result = otaOrderTastsMapper
                            .updateByPrimaryKeySelective(otaOrderTasts);
                    if (result == 0) {
                        logger.info("pushMsg修改报文失败，报文信息:{}，订单Id:{}",
                                JsonKit.toJson(otaOrderTasts), otaOrderid);
                    } else {
                        logger.info("pushMsg修改报文成功,不发消息，orderTasts id：{}，订单Id:{}",
                                otaOrderTasts.getId(), otaOrderid);
                    }
                }
            }
            logger.info("OrderPushMsgService:: pushOutCheckInTimeMsgNo:: 办理入住后，不需发送保留时间未到消息。end: otaOrderId = "
                    + otaOrderid);
            
        }   

    }
    @Override
    public Map<String, Object> findPromotionMaxAndMinMId(Map<String, Object> paramMap) {
        return otaOrderDAO.findPromotionMaxAndMinMId(paramMap);
    }

    @Override
    public List<BOtaorder> findPromotionMidList(Map<String, Object> paramMap) {
        return otaOrderDAO.findPromotionMidList(paramMap);
    }
    
    /**
     * 查询用户有几笔订单
     */
    @Override
    public Long findMemberOnlyOneOrderCount(BOtaorder BOtaorder) {
        return otaOrderDAO.findMemberOnlyOneOrderCount(BOtaorder);
    }
    
    /**
     * 1.每分钟执行一次
     * 2.取上1分钟内，所有规则B的，有优惠券的，当前时间大于创建时间15分钟的订单的集合
     */
    public void selectOrderCreatetimegt15(){        
        logger.info("OrderServiceImpl::selectOrderCreatetimegt15  start");
//      List<OtaOrderTasts> orderTasksList = otaOrderTastsMapper.selectall();
        OtaOrderTasts otaOrderTasts=new OtaOrderTasts();
        otaOrderTasts.setStatus(OrderTasksStatusEnum.INITIALIZE.getId());
        otaOrderTasts.setTasktype(OrderTasksTypeEnum.ORDERCREATETIMEGT15.getId());
        //查询推送信息 ，根据(tasktype，status); count 小于3次 
        List<OtaOrderTasts> orderTasksList = otaOrderTastsMapper.findPushMessage(otaOrderTasts);
        
        for (int i = 0; i < orderTasksList.size(); i++) {
            OtaOrderTasts orderTasks = orderTasksList.get(i);
            Long orderid = orderTasks.getOtaorderid();
            //redis锁
            String lockValue = DistributedLockUtil.tryLock("orderTasksLock_"+orderid, 40);
            if (lockValue == null) {
                logger.info("订单：" + orderid + "正在修改，无法执行订单任务");
                continue;
            }
            
            Integer count = orderTasks.getCount();
            try {
                String content = orderTasks.getContent();
                PmsUpdateOrder addOrders = gson.fromJson(content, PmsUpdateOrder.class);
                
                logger.info("pms接口调用：修改pms订单：updateOrder：otaRoomOrderId: content : " + content + " ,otaOrderid: " +orderid);
                ReturnObject<Object> returnObject = HotelPMSManager.getInstance().getService().updateOrder(orderTasks.getHotelid(), addOrders);
                
                if (HotelPMSManager.getInstance().returnError(returnObject)) {
                    logger.info("OrderServiceImpl::selectOrderCreatetimegt15:: PMS－"+returnObject.getErrorMessage() + ", otaOrderid" + orderid);
                    throw MyErrorEnum.updateOrder.getMyException("PMS－"+returnObject.getErrorMessage() + ", otaOrderid" + orderid);
                } else {
                    orderTasks.setStatus(OrderTasksEnum.CHANGE.getId());
                    orderTasks.setUpdatetime(new Date());
                    otaOrderTastsMapper.updateByPrimaryKey(orderTasks);
                    logger.info("OrderServiceImpl::selectOrderCreatetimegt15  end, otaOrderid= " + orderid);
                }
            } catch (Exception e) {
                orderTasks.setCount(++count);
                orderTasks.setUpdatetime(new Date());
                otaOrderTastsMapper.updateByPrimaryKeySelective(orderTasks);
                logger.info("OrderServiceImpl::selectOrderCreatetimegt15:: 失败次数+1,失败次数小于等于5, otaOrderid= " + orderid);
            } finally {
                //释放 redis锁
                logger.info("释放分布锁, orderId= " + orderid);
                DistributedLockUtil.releaseLock("orderTasksLock_"+orderid, lockValue);
            }
        
        }
    }
    
    private void sendPms2_MINUTE(JSONObject pmsOrder,OtaOrder order,Integer MINUTE){
        //组装报文
        OtaOrderTasts orderTasts=new OtaOrderTasts();
       //报文内容
       orderTasts.setContent(pmsOrder.toJSONString());
       //15分钟后发送
       Calendar nowTime = Calendar.getInstance();
       nowTime.add(Calendar.MINUTE, MINUTE);
       orderTasts.setExecuteTime(nowTime.getTime());
       //设置就酒店
       orderTasts.setHotelid(order.getHotelId());
       //设置订单
       orderTasts.setOtaorderid(order.getId());
       //创建时间
       orderTasts.setCreatetime(new Date());
       //错误次数
       orderTasts.setCount(0);
       //修改时间
       orderTasts.setUpdatetime(new Date());
       //消息类型
       orderTasts.setTasktype(OrderTasksTypeEnum.CHONG_QING_RULE_PMS2.getId());
       //状态初始化
       orderTasts.setStatus(OrderTasksStatusEnum.INITIALIZE.getId());
       //修改pms2.0订单的入住人姓名
       //保存
       int result=this.otaOrderTastsMapper.insertSelective(orderTasts);
       if(result==0){
           logger.info("保存报文失败，报文信息:{}，订单Id:{}",JsonKit.toJson(orderTasts),order.getId());
       }else{
           logger.info("保存报文成功，orderTasts id：{}，订单Id:{}",orderTasts.getId(),order.getId());
       }
    }
    
    /**
     * pms2.0订单修改推送
     */
    public void pushMessagePms2Order(){
        logger.info("开始pms2.0订单修改推送");
        OtaOrderTasts otaOrderTasts=new OtaOrderTasts();
   
        otaOrderTasts.setTasktype(OrderTasksTypeEnum.CHONG_QING_RULE_PMS2.getId());
        otaOrderTasts.setStatus(OrderTasksStatusEnum.INITIALIZE.getId());
        List<OtaOrderTasts> list=otaOrderTastsMapper.findPushMessage(otaOrderTasts);
        int count=0;
        for(OtaOrderTasts orderTasts:list){
            JSONObject jsonObject=gson.fromJson(orderTasts.getContent(),JSONObject.class);
            logger.info("pms2.0订单修改推送，{}",orderTasts.getContent());
            //推送消息
            JSONObject returnObject = JSONObject.parseObject(doPostJson(UrlUtils.getUrl("newpms.url") + "/updateorder", jsonObject.toJSONString()));
            logger.info("OTSMessage::modifyPmsOrder::修改订单，返回:{}", returnObject.toJSONString());
            
            if (returnObject.getBooleanValue("success")) {
                
                //推送成功修改状态
                orderTasts.setStatus(OrderTasksStatusEnum.CHANGE.getId());
                orderTasts.setUpdatetime(new Date());
                otaOrderTastsMapper.updateByPrimaryKeySelective(orderTasts);
                logger.info("OTSMessage::pushMessagePms2Order::修改订单成功。orderid:{}", orderTasts.getOtaorderid());
            
            } else {
                //推送失败增加次数
                count=orderTasts.getCount()+1;
                orderTasts.setCount(count);
                orderTasts.setUpdatetime(new Date());
                otaOrderTastsMapper.updateByPrimaryKeySelective(orderTasts);
                logger.info("OTSMessage::pushMessagePms2Order::修改订单失败。orderid:{},errorcode:{},errormsg:{}", 
                        orderTasts.getOtaorderid(),
                        returnObject.getString("errorcode"),
                        returnObject.getString("errormsg"));
            }
        }
        logger.info("结束pms2.0订单修改推送");
    }
    public void test(Long orderId,String o){
        boolean b=this.iPromotionPriceService.isBindPromotion(orderId);
        System.out.println(b);
    }
    /**
     * n分钟后发生真实姓名
     * @param returnOrder
     * @param pmsCheckinPerson
     * @param roomOrder
     */
    private void sendPms1_MINUTE(OtaOrder returnOrder,PmsCheckinPerson pmsCheckinPerson,OtaRoomOrder roomOrder,Integer MINUTE){
        // 组装报文
         PmsUpdateOrder addOrders = new PmsUpdateOrder();
        addOrders.setOrderid(roomOrder.getLong("Id"));
        addOrders.setContact(roomOrder.getStr("Contacts"));
        addOrders.setPhone(roomOrder.getStr("ContactsPhone"));
//        List<OtaCheckInUser> inUsers = roomOrder.get("UserList");
        addOrders.setCheckin(pmsCheckinPerson);
        String PmsUpdateOrderJson=JsonKit.toJson(addOrders);
        logger.info("入住人信息：{}，订单Id:{}",PmsUpdateOrderJson,returnOrder.getId());
        
        OtaOrderTasts orderTasts=new OtaOrderTasts();
        orderTasts.setContent(PmsUpdateOrderJson);
        orderTasts.setCount(0);
        orderTasts.setCreatetime(new Date());
        orderTasts.setHotelid(returnOrder.getHotelId());
        orderTasts.setOtaorderid(returnOrder.getId());
        orderTasts.setStatus(OrderTasksStatusEnum.INITIALIZE.getId());
        orderTasts.setTasktype(OrderTasksTypeEnum.ORDERCREATETIMEGT15.getId());
        orderTasts.setUpdatetime(new Date());
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, MINUTE);
        orderTasts.setExecuteTime(nowTime.getTime());
        
        //保存
        int result=this.otaOrderTastsMapper.insertSelective(orderTasts);
        if(result==0){
            logger.info("保存报文失败，报文信息:{}，订单Id:{}",JsonKit.toJson(orderTasts),returnOrder.getId());
        }else{
            logger.info("保存报文成功，orderTasts id：{}，订单Id:{}",orderTasts.getId(),returnOrder.getId());
        }
    }

    /**
     * 8月份一部分酒店b规则修改重新核算切客
     */
    public void modifyHotelRule(HttpServletRequest request, JSONObject jsonObj) {
        OrderServiceImpl.logger.info("OTSMessage::OrderService::modifyHotelRule::begin");
        String hoteldata = request.getParameter("hoteldata");
        String[] hotels = hoteldata.split(";");
        for (int i=0; i<hotels.length; i++){
            String[] changeHotel = hotels[i].split(",");
            // 通过参数传过来的酒店id和变更时间 取得符合条件的订单
            logger.info("开始处理酒店:{}",changeHotel[0]);
            Map<String,Object> param = Maps.newHashMap();
            param.put("changeTime", changeHotel[1]);
            param.put("hotelid", changeHotel[0]);
            List<OtaOrder> orderList = this.orderDAO.findOrderListByHotelIdAndChangeDate(changeHotel[0],changeHotel[1]);
            for (int j=0; j<orderList.size(); j++){
                changeOrderSpuAndInv(orderList.get(j));
                if(j % 10 == 0){
                    logger.info("酒店:{}，处理了{}单",changeHotel[0],j);
                }
            }
        }
        
        OrderServiceImpl.logger.info("OTSMessage::OrderService::doUpdateOrder::end");
        jsonObj.put("success", true);
    }
    
    /**
     * 8月份一部分酒店b规则修改重新核算优惠券切客
     */
    public void modifyHotelPromotion(HttpServletRequest request, JSONObject jsonObj) {
        OrderServiceImpl.logger.info("OTSMessage::modifyHotelPromotion::begin");
       
        List<OtaOrder> orderList = this.orderDAO.findOrderListByHotelIdAndChangeDate2();
        for (int j=0; j<orderList.size(); j++){
            changeOrderSpuAndInv(orderList.get(j));
            if(j % 10 == 0){
                logger.info("酒店:{}，处理了{}单",orderList.get(j).getHotelId(),j);
            }
        }
        
        OrderServiceImpl.logger.info("OTSMessage::OrderService::doUpdateOrder::end");
        jsonObj.put("success", true);
    }
    
    private void changeOrderSpuAndInv(OtaOrder otaorder) {
        PmsRoomOrder pmsRoomOrder = pmsRoomOrderDao.getCheckInTime(otaorder.getId());
        logger.info("转换订单,orderid = "+otaorder.getId());
        if (pmsRoomOrder != null) {
            // 判断入住时间减创建时间是否小于15分钟
            Date checkintime = pmsRoomOrder.getDate("CheckInTime");
            Date createtime = otaorder.getDate("Createtime");
            if(checkintime != null && createtime != null){
                long temp = checkintime.getTime() - createtime.getTime(); // 相差毫秒数
                logger.info("时间差 = "+temp+",orderid = "+otaorder.getId());
                if (temp < OrderServiceImpl.TIME_FOR_FIFTEEN) { // 负值也算切客
                    otaorder.setSpreadUser(otaorder.getHotelId());

                    // 判断入住人是否常住人
                    if ("1".equals(otaorder.get("Invalidreason"))) {

                    } else {
                        // 判断是否常住人
                        PmsCheckinUser checkinUser = this.findPmsUserIncheckSelect(pmsRoomOrder.getLong("Hotelid"), pmsRoomOrder.getStr("PmsRoomOrderNo"));
                        if(checkinUser != null && "1".equals(checkinUser.get("freqtrv"))){
                            otaorder.set("Invalidreason", OtaFreqTrvEnum.IN_FREQUSER.getId());
                        } else {
                            // 判断切客是否有效
                            boolean isCheckOnceToday = this.otaOrderDAO.isCheckNumToday(otaorder.getMid(),checkintime);
                            if (!isCheckOnceToday) {
                                otaorder.set("Invalidreason", OtaFreqTrvEnum.ONEDAY_UP1.getId());
                            } else {
                                boolean isCheckFourTimesMonth = this.otaOrderDAO.isCheckNumMonth(otaorder.getMid(), otaorder.getHotelId(),checkintime);
                                if (!isCheckFourTimesMonth) {
                                    otaorder.set("Invalidreason", OtaFreqTrvEnum.MONTHE_UP4.getId());
                                }
                            }
                        }
                    }
                    if(otaorder.getSpreadUser() != null && otaorder.get("Invalidreason") == null){
                        Date bgtemp = pmsRoomOrder.getDate("CheckInTime");
                        Date edtemp = pmsRoomOrder.getDate("CheckOutTime");
                        if(bgtemp!= null && edtemp != null){
                            double diffHours = DateUtils.getDiffHoure(DateUtils.getDatetime(bgtemp), DateUtils.getDatetime(edtemp));
                            // 离店时如果订单是到付切客订单且离店时间减入住时间小于4小时，则订单表需修改标记位字段内容，将“无效切客订单原因Invalidreason”置为2
                            if(diffHours < 4){
                                otaorder.set("Invalidreason", OtaFreqTrvEnum.OK_LESS4.getId());
                            }
                        }
                    }
                } else {
                    // spreaduser保持为空
                    otaorder.setSpreadUser(null);
                }
            }
            
            otaorder.setRuleCode(1002);
            otaorder.saveOrUpdate();
            if(otaorder.getSpreadUser() != null && otaorder.get("Invalidreason") == null){
                // 更新支付表 到付和预付
                updatePayLogQiekeIncome(otaorder.getId(), otaorder.getOrderType());
            }
            
        } else {
            otaorder.setRuleCode(1002);
            otaorder.setSpreadUser(null);
            otaorder.set("Invalidreason", null);
            otaorder.saveOrUpdate();
            logger.info("查看酒店订单没有对应的客单!" + otaorder.getId());
        }
    }
    
    public boolean updatePayLogQiekeIncome(Long orderid, int status){
        logger.info("更新贴现"+orderid);
        return orderLogDao.updatePayLogQiekeIncome(orderid,status);
    }
    
    public PmsCheckinUser findPmsUserIncheckSelect(Long hotelid,String pmsRoomNo) {
        return PmsCheckinUser.dao.findFirst("select * from b_pms_checkinuser where hotelid=? and pmsroomorderno=? ", hotelid, pmsRoomNo);
    }
    
    @Override
	public Long findMonthlySales(Long hotelId) {
		OtsCacheManager manager = AppUtils.getBean(OtsCacheManager.class);
	    Jedis salesCache = manager.getNewJedis();
	    
	    Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateNowStr = sdf.format(now);
		
		JSONObject salesObject = new JSONObject();
		Long currentSales = -1l;
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd000000");
		Calendar beforeCalendar = Calendar.getInstance();
		beforeCalendar.add(Calendar.DATE, -30);
		String beforetime = sdf1.format(beforeCalendar.getTime());
		Calendar yesterCalendar = Calendar.getInstance();
		yesterCalendar.add(Calendar.DATE, -1);
		String yestertime = sdf1.format(yesterCalendar.getTime());
		
		
		try {
			if(hotelId == null || hotelId <= 0)  {
				List<Bean> allSales = orderDAO.findAllMonthlySales(beforetime, yestertime);
				for (Bean sales : allSales) {
					String hid = sales.getLong("hid").toString();
					String count = sales.get("cnt").toString();
					salesObject.put(hid, count);
				}
			} else {
				String arrSales = salesCache.get(Constant.MONTHLY_SALES_KEY + dateNowStr);
				if(!StringUtils.isBlank(arrSales)) {
					JSONObject jSales = JSONObject.parseObject(arrSales);
					String hotelSales = jSales.getString(hotelId + "");
					if(!StringUtils.isBlank(hotelSales) && Long.parseLong(hotelSales) >= 0) 
						return Long.parseLong(hotelSales);
					else {
					    currentSales = orderDAO.findMonthlySaleByHotelId(hotelId, beforetime, yestertime);
						jSales.put(hotelId + "", currentSales);
						salesCache.set(Constant.MONTHLY_SALES_KEY + dateNowStr, jSales.toJSONString());
						return currentSales;
					}
				} else {
					currentSales = orderDAO.findMonthlySaleByHotelId(hotelId, beforetime, yestertime);
					salesObject.put(hotelId + "", currentSales);
				}
			}
			salesCache.set(Constant.MONTHLY_SALES_KEY + dateNowStr, salesObject.toJSONString());
			salesCache.expire(Constant.MONTHLY_SALES_KEY + dateNowStr, 24 * 60 * 60);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error("findMonthlySales exception {}", e);
		} finally {
			if(salesCache != null && salesCache.isConnected())
				salesCache.close();
		}
		return currentSales;
	}

    /**
     * c端修改房间入住人
     */
    public void modifyCheckinuser(HttpServletRequest request, JSONObject jsonObj) {
        OrderServiceImpl.logger.info("OTSMessage::OrderServiceImpl::modifyCheckinuser::end");
        String orderId = request.getParameter("orderid");
        String checkInUser = request.getParameter("checkinuser");
        String callMethod = request.getParameter("callmethod");
        String callVersion = request.getParameter("callversion");
        String ip = request.getParameter("ip");
        OrderServiceImpl.logger.info("checkInUser:{}",checkInUser);

        // 对当前订单进行修改入住人
        OtaOrder order = this.findOtaOrderById(Long.parseLong(orderId));
        if (order == null) {
            throw MyErrorEnum.findOrder.getMyException("订单号不存在");
        }
        THotel hotel = hotelService.readonlyTHotel(order.getHotelId());
        if (hotel == null) {
            throw MyErrorEnum.saveOrder.getMyException("没有当前id的酒店");
        }
        // 入住人编辑
        List<OtaCheckInUser> inUsers = this.getInUsersByJson(checkInUser);

        List<OtaRoomOrder> roomOrderList = order.getRoomOrderList();
        OrderServiceImpl.logger.info("OrderServiceImpl:: modifyCheckinuser::修改入住人名字! size:{},orderid : {},入住人详细：{}", roomOrderList.size(), orderId, inUsers);
        for (OtaRoomOrder roomOrder : roomOrderList) {
            OrderServiceImpl.logger.info("是否修改入住人：{}", order.getOrderStatus() < OtaOrderStatusEnum.CheckInOnline.getId());
            if (order.getOrderStatus() < OtaOrderStatusEnum.CheckInOnline.getId()) {
                this.saveOrUpdateOtaCheckInUser(roomOrder, inUsers);
                // 如果改变了联系人,是否PmsRoomOrderNo
                if (StringUtils.isNotBlank(roomOrder.getPmsRoomOrderNo())) {
                    modifyPmsCheckinuser(hotel, order, roomOrder);
                    this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.C_MODIFYCHECKINUSER.getId(), "", ip + " c端更新入住人", "");
                }
            }else{
            	  jsonObj.put("success", false);
            	  jsonObj.put("errcode", "-1");
            	  jsonObj.put("errmsg", "入住后不能修改入住人");
            	  return;
            }
        }
        OrderServiceImpl.logger.info("OTSMessage::OrderServiceImpl::modifyCheckinuser::end");
        jsonObj.put("success", true);
    }
    
    public List<OtaCheckInUser> getInUsersByJsonForB(String checkInUser) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if (StringUtils.isNotBlank(checkInUser)) {
			List<OtaCheckInUser> inUsers = new ArrayList<>();
			JSONArray jsonArr = JSONArray.parseArray(checkInUser);
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject jsonObj = jsonArr.getJSONObject(i);
				OtaCheckInUser inUser = new OtaCheckInUser();
				inUser.set("Name", jsonObj.getString("name"));
				if (StringUtils.isNotBlank(jsonObj.getString("sex"))) {
					inUser.set("Sex", jsonObj.getString("sex"));
				} else {
					inUser.set("Sex", "男");
				}
				// 民族
				if (StringUtils.isNotBlank(jsonObj.getString("ethnic"))) {
					inUser.set("Ethnic", jsonObj.getString("ethnic"));
				}
				try {
					// 生日
					if (StringUtils.isNotBlank(jsonObj.getString("birthday"))) {
						inUser.set("Birthday", sdf.parse(jsonObj.getString("birthday")));
					}
				} catch (ParseException e) {
					logger.info("getInUsersByJson::没有birthday信息");
				}
				// 卡类型
				if (StringUtils.isNotBlank(jsonObj.getString("cardtype"))) {
					inUser.set("CardType", jsonObj.getString("cardtype"));
				}

				inUser.set("CardId", jsonObj.getString("cardid"));// 卡号
				inUser.set("DisId", jsonObj.getLong("disid"));
				inUser.set("Address", jsonObj.getString("address"));
				if (StringUtils.isNotBlank(jsonObj.getString("phone"))) {
					inUser.set("Phone", jsonObj.getString("phone"));
				}
				inUser.set("updateTime", new Date());
				if (StringUtils.isNotBlank(jsonObj.getString("_pk_"))) {
					inUser.set("id", jsonObj.getLong("_pk_"));
				}
				inUsers.add(inUser);
			}
			return inUsers;
		}
		return null;
	}
    
    /**
     * B端修改入住人
     * @param roomOrder
     * @param checkInUserList
     */
    public void saveOrUpdateOtaCheckInUserForB(OtaRoomOrder roomOrder, List<OtaCheckInUser> checkInUserList) {
        Long mid = MyTokenUtils.getMidByToken("");
        if (checkInUserList != null && checkInUserList.size() > 0) {
            for (OtaCheckInUser otaCheckInUser : checkInUserList) {
                otaCheckInUser.set("mid", mid);
                otaCheckInUser.set("OtaRoomOrderId", roomOrder.getLong("id"));
                otaCheckInUser.saveOrUpdate();
            }
        }
    }

    @Override
    public boolean isFirstOrder(FirstOrderModel fom) {
        boolean result = false;
        //根据条件获取用户列表
        List<UMember> members = memberService.findUMemberByFirstOrder(fom);
        if (CollectionUtils.isNotEmpty(members)) {
            List<Long> midList = new ArrayList<Long>();
            for (UMember uMember : members) {
                midList.add(uMember.getMid());
            }
            result = otaOrderDAO.isFirstOrder(midList);
        } else {
            logger.info("查询条件为：{},查询用户列表为空", fom);
        }
        return result;
    }

    private void modifyPmsCheckinuser(THotel hotel, OtaOrder order, OtaRoomOrder otaRoomOrder) {
        String isNewPms = hotel.getStr("isNewPms");
        if ("T".equals(isNewPms)) {
            JSONObject addOrder = new JSONObject();
            addOrder.put("hotelid", hotel.get("pms"));
            addOrder.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
            addOrder.put("function", "updateorder");
            addOrder.put("timestamp ", DateUtils.formatDateTime(new Date()));
            addOrder.put("otaorderid", otaRoomOrder.getLong("otaorderid"));
            addOrder.put("contact", otaRoomOrder.getStr("contacts"));
            addOrder.put("phone", otaRoomOrder.getStr("contactsphone"));
            addOrder.put("memo", otaRoomOrder.getStr("note"));
            List<OtaRoomPrice> otaRoomPrices = priceService.findOtaRoomPriceByOrder(order);
            JSONObject customerno = new JSONObject();
            customerno.put("customerid", otaRoomOrder.get("id"));
            customerno.put("arrivetime", DateUtils.getStringFromDate(otaRoomOrder.getDate("Begintime"), "yyyyMMddHHmmss"));
            customerno.put("leavetime", DateUtils.getStringFromDate(otaRoomOrder.getDate("Endtime"), "yyyyMMddHHmmss"));
            customerno.put("roomid", otaRoomOrder.get("roompms"));
            //单日房价list
			JSONArray costArray = new JSONArray();
			if (otaRoomPrices != null) {
				for (OtaRoomPrice otaroomprice : otaRoomPrices) {
					if (otaroomprice.get("otaroomorderid").equals(
							otaRoomOrder.getLong("id"))) {
						JSONObject costItem = new JSONObject();
						costItem.put("time", otaroomprice.getStr("actiondate"));
						costItem.put("cost", otaroomprice.get("price"));
						costArray.add(costItem);
					}
				}
			}
             customerno.put("cost", costArray);
            List<OtaCheckInUser> otaCheckInUsers = checkInUserDAO.findOtaCheckInUsers(order.getLong("id"));
            if (otaCheckInUsers.size() > 0) {
                JSONArray users = new JSONArray();
                for (OtaCheckInUser otaCheckInUser : otaCheckInUsers) {
                    JSONObject checkInUser = new JSONObject();
                    checkInUser.put("name", otaCheckInUser.getName());
                    checkInUser.put("phone", otaCheckInUser.get("Phone", ""));
                    users.add(checkInUser);
                }
                customerno.put("user", users);
            }
            JSONArray customernos = new JSONArray();
            customernos.add(customerno);
            addOrder.put("customerno", customernos);

            logger.info("OTSMessage::modifyPmsCheckinuser::修改订单，订单号：{}，参数:{}", order.getId(), addOrder.toJSONString());
            JSONObject returnObject = JSONObject.parseObject(doPostJson(UrlUtils.getUrl("newpms.url") + "/updateorder", addOrder.toJSONString()));
            logger.info("OTSMessage::modifyPmsCheckinuser::修改订单，返回:{}", returnObject.toJSONString());
            if (returnObject.getBooleanValue("success")) {
                logger.info("OTSMessage::modifyPmsCheckinuser::修改订单成功。orderid:{}", otaRoomOrder.getLong("otaorderid"));
            } else {
                throw MyErrorEnum.updateOrder.getMyException("errcode:" + returnObject.getString("errcode") + ",errmsg:" + returnObject.getString("errmsg"));
            }
        } else {
            //发送
            logger.info("发生修改,订单号{}", order.getId());
            PmsUpdateOrder addOrders = new PmsUpdateOrder();
            addOrders.setOrderid(otaRoomOrder.getLong("Id"));
            addOrders.setContact(otaRoomOrder.getStr("Contacts"));
            addOrders.setPhone(otaRoomOrder.getStr("ContactsPhone"));

            addOrders.setCheckin(this.getPmsCheckinPerson(otaRoomOrder));
            logger.info("pms接口调用：修改pms订单：updateOrder：orderid = " + order.getId() + " , 请求参数 = {}", gson.toJson(addOrders));
            ReturnObject<Object> returnObject = HotelPMSManager.getInstance().getService().updateOrder(otaRoomOrder.getLong("HotelId"), addOrders);
            if (HotelPMSManager.getInstance().returnError(returnObject)) {
                throw MyErrorEnum.updateOrder.getMyException("PMS－" + returnObject.getErrorMessage());
            }
        }
    }

    /**
     * 订单状态跟踪
     * @param request
     * @param jsonObj
     */
    public void selectOrderStatus(HttpServletRequest request, JSONObject jsonObj) {
        String orderId = request.getParameter("orderid");
        if (StringUtils.isBlank(orderId) || !NumUtils.isNumeric(orderId)) {
            throw MyErrorEnum.customError.getMyException("参数orderid错误");
        }
        this.logger.info("selectOrderStatus,orderid:{}",orderId);
        OtaOrder order = this.findOtaOrderById(Long.parseLong(orderId));
        THotel hotel = hotelService.readonlyTHotel(order.getHotelId());
        List<BOrderBusinessLog> logs = orderBusinessLogService.selectByOrderId(Long.parseLong(orderId));

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("120", "下单成功");
        statusMap.put("180", "办理入住");
        statusMap.put("190,200", "办理离店");
        statusMap.put("512,513", "取消订单");
        statusMap.put("514", "未到,订单取消");
        statusMap.put("520", "未到");

        Map<String, String> businessCodeMap = new HashMap<>();
        businessCodeMap.put(OtaOrderFlagEnum.SCORE_OK.getId(), "点评完成");
        businessCodeMap.put(OtaOrderFlagEnum.SCORE_CASHBACK.getId(), "点评返现完成");
        businessCodeMap.put(OtaOrderFlagEnum.ORDER_CASHBACK.getId(), "订单返现完成");
        businessCodeMap.put(OtaOrderFlagEnum.ORDER_BACKCASH.getId(), "取消订单返回钱包");
        Map<String, JSONObject> result = new HashMap<>();

        for (BOrderBusinessLog log : logs) {
            String logOrderStatus = String.valueOf(log.getOrderstatus());
            // 订单状态的跟踪
            for (String key : statusMap.keySet()) {
                if (key.indexOf(logOrderStatus) >= 0 && !result.containsKey(logOrderStatus)) {
                    JSONObject j = new JSONObject();
                    j.put("statusname", statusMap.get(key));
                    j.put("time", log.getCreatetime());
                    if ("120".indexOf(logOrderStatus) >= 0) {
                        if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
                            String retentiontime = DateUtils.getStringFromDate(order.getBeginTime(), "MM-dd HH:mm");
                            j.put("statusname", j.getString("statusname") + "，您的预抵时间为" + retentiontime);
                        }
                        if (OrderTypeEnum.YF.getId().intValue() == order.getOrderType() && order.getPayStatus() < PayStatusEnum.alreadyPay.getId()) {
                            continue;
                        }
                    }
                    if ("180".indexOf(logOrderStatus) >= 0) {
                        //,您的预离时间为2015-09-12 12:00
                        String leavetime = DateUtils.getStringFromDate(order.getEndTime(), "MM-dd HH:mm");
                        j.put("statusname", j.getString("statusname") + "，您的预离时间为" + leavetime);
                    }
                    if ("190,200".indexOf(logOrderStatus) >= 0) {
                        j.put("statusname", j.getString("statusname") + "，感谢您使用眯客");
                    }
                    if ("512".indexOf(logOrderStatus) >= 0) {
                        BigDecimal payMoney = payService.payMoney(order.getId());
                        // 构建退款文案：¥230已退款
                        if (payMoney != null) {
                            j.put("statusname", j.getString("statusname") + "，¥" + payMoney.toString() + "已退款");
                        }
                    }
                    result.put(logOrderStatus, j);
                }
            }
            // 业务点跟踪的收集
            for (String key : businessCodeMap.keySet()) {
                String bussinessCode = log.getBussinesscode();
                String bussinessDesc = log.getBusinessdesc();
                if (key.indexOf(bussinessCode) >= 0 && !result.containsKey(bussinessCode) && StringUtils.isNotBlank(bussinessDesc)) {
                    JSONObject j = new JSONObject();
                    this.logger.info("加入：bussinessCode:{},bussinessDesc:{}",bussinessCode,bussinessCode);
                    j.put("statusname", bussinessDesc);
                    j.put("time", log.getCreatetime());
                    result.put(bussinessCode, j);
                }
            }
        }
        // 同时包含512、514，删除514
        if (result.containsKey("512") && result.containsKey("514")) {
			result.remove("514");
		}
        List<JSONObject> list = new ArrayList<>(result.values());

        Collections.sort(list, new Comparator<JSONObject>() {// 日期由小到大
            @Override
            public int compare(JSONObject param1, JSONObject param2) {
                return param2.getDate("time").compareTo(param1.getDate("time"));
            }
        });
        JSONArray datas = new JSONArray();
        for (JSONObject j : list) {
            j.put("time", DateUtils.getStringFromDate(j.getDate("time"), DateUtils.FORMAT_DATETIME_HM));
            datas.add(j);
        }
        jsonObj.put("datas", datas);
    }

    @Override
    public boolean modifyOtaOrderByPayStatus(OtaOrder order, Integer payStatus, String opertorName) {
        logger.info("OTSMessage::modifyOtaOrderStatus::start::订单号：" + order.getId() + ",payStatus:" + payStatus + ",opertorName:" + opertorName);
        if (payStatus == PayStatusEnum.alreadyPay.getId()) {
            order.setPayStatus(PayStatusEnum.alreadyPay.getId());
        } else {
            order.setPayStatus(PayStatusEnum.waitPay.getId());
        }
        boolean flag = order.update();
        logger.info("OTSMessage::modifyOtaOrderStatus::订单号：" + order.getId() + ",修改结果：" + flag);
        orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.MODIFY_OTA_ORDER_PAYSTATUS.getId(), "", opertorName + "：修改支付状态为：" + payStatus, "");
        logger.info("OTSMessage::modifyOtaOrderStatus::end::订单号：" + order.getId() + ",payStatus:" + payStatus + ",opertorName:" + opertorName);
        return flag;
    }
    
	/**
	 * 检验mid是否在黑名单
	 */
	private void checkMidIsBlack(String token,String errmsg) {
		UMember member = MyTokenUtils.getMemberByToken(token);
		if (member == null) {
			throw MyErrorEnum.memberNotExist.getMyException("");
		}
		this.memberService.checkPhoneIsBlack(member.getPhone(), "Order.doCreateOrder", "doCreateOrder：checkMidIsBlack", errmsg);
	}
	/*
	 * 支付在check回调时把订单状态置为已确认,支付状态置为支付中,条件是当前订单状态小于已确认,支付状态小于支付中
	 * @see com.mk.ots.order.service.OrderService#modifyOrderStsAndPayStsOnCheck(com.mk.ots.order.bean.OtaOrder)
	 * 20150912 by lindi
	 */
	public void modifyOrderStsAndPayStsOnCheck(OtaOrder order) {
		
		Db.update("update b_otaorder set "
				+ "orderstatus = case when orderStatus < 140 then 140 else orderStatus end,"
				+ "paystatus = case when paystatus < 110 then 110 else paystatus end where id=? ", order.getId());
	}
	
	/*
	 * @see com.mk.ots.order.service.OrderService#getPayingConfirmedOrders(int)
	 * 20150912 by lindi
	 */
	public List<OtaOrder> getPayingConfirmedOrders(int interval) {
		
		return orderDAO.findPayingConfirmedOrders(interval);
	}

	@Override
	public void addQiekeIncome(OtaOrder otaorder) {
		// 增加开关，开启的时候需要判断优惠券使用，没有使用优惠券才能计算切客收益，关闭的时候无需判断优惠券使用，直接计算切客收益
		String isAddQiekeIncomeByCoupon = PropertyConfigurer.getProperty("isAddQiekeIncomeByCoupon");
		if ("1".equals(isAddQiekeIncomeByCoupon)) {
			logger.info("优惠券开关打开，需要判断优惠券使用情况,orderid = {}", otaorder.getId());
			boolean coupon = this.iPromotionPriceService.isBindPromotion(otaorder.getId());
			logger.info("调用优惠券接口判断是否使用了优惠券,返回结果 = {}", coupon);
			if (!coupon) {// B规则15分钟内没有使用优惠劵算切客
				// 则刷新orderlog，该订单记录切客收益（预付20元，到付10元，金额是可以配置的）
				logger.info("调用支付刷新orderlog" + otaorder.getId());
				this.payService.addQiekeIncome(otaorder, RuleEnum.CHONG_QIN);
			}
		} else {
			logger.info("优惠券开关关闭，无需判断优惠券使用情况,orderid = {}", otaorder.getId());
			// 则刷新orderlog，该订单记录切客收益（预付20元，到付10元，金额是可以配置的）
			logger.info("调用支付刷新orderlog" + otaorder.getId());
			this.payService.addQiekeIncome(otaorder, RuleEnum.CHONG_QIN);
		}
	}
	
	public Map<String, String> findNeedCancelOrders(){
		Map<String, String> map = new HashMap<>();
		List<Bean> beans = Db.find("SELECT id, createtime FROM b_otaorder " + 
									" WHERE " + 
									" orderStatus = 120" + 
									" AND createtime > DATE_ADD(NOW(), INTERVAL - 30 MINUTE) " + 
									" AND createtime < DATE_ADD(NOW(), INTERVAL - 15 MINUTE)");
		this.logger.info("findNeedCancelOrders:beans{}", beans);
		for (Bean bean : beans) {
			map.put(String.valueOf(bean.getLong("id")), DateUtils.getDatetime(bean.getDate("createtime")));
		}
		
		return map;
	}
}
