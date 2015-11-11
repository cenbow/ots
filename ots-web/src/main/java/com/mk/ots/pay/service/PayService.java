package com.mk.ots.pay.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.mk.ots.common.enums.*;
import com.mk.ots.roomsale.model.TRoomSale;
import com.mk.ots.roomsale.service.RoomSaleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import cn.com.winhoo.pms.webout.service.bean.PmsAddPay;
import cn.com.winhoo.pms.webout.service.bean.PmsCancelPay;
import cn.com.winhoo.pms.webout.service.bean.ReturnObject;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.orm.kit.JsonKit;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.SysConfig;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.manager.HotelPMSManager;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.mapper.BOrderLogMapper;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.order.bean.OtaCheckInUser;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.UUseTicketRecord;
import com.mk.ots.order.dao.CheckInUserDAO;
import com.mk.ots.order.dao.IUUseTicketRecordDao;
import com.mk.ots.order.dao.OrderDAO;
import com.mk.ots.order.model.BOrderLog;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderLogService;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.dao.HGroupDao;
import com.mk.ots.pay.dao.IPOrderLogDao;
import com.mk.ots.pay.dao.IPPayInfoDao;
import com.mk.ots.pay.dao.IPPmsPayLogDao;
import com.mk.ots.pay.dao.IPScoreLogDao;
import com.mk.ots.pay.dao.IPayCallbackLogDao;
import com.mk.ots.pay.dao.IPayDAO;
import com.mk.ots.pay.dao.IPayStatusErrorDao;
import com.mk.ots.pay.dao.IPayTaskDao;
import com.mk.ots.pay.model.CancelPms;
import com.mk.ots.pay.model.CouponParam;
import com.mk.ots.pay.model.HGroup;
import com.mk.ots.pay.model.PMSCancelParam;
import com.mk.ots.pay.model.PMSRequest;
import com.mk.ots.pay.model.PMSResponse;
import com.mk.ots.pay.model.POrderLog;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.pay.model.PPayInfo;
import com.mk.ots.pay.model.PPayStatusErrorOrder;
import com.mk.ots.pay.model.PPayTask;
import com.mk.ots.pay.model.PRealOrderLog;
import com.mk.ots.pay.model.PScoreLog;
import com.mk.ots.pay.model.PayCallbackLog;
import com.mk.ots.pay.model.PmsError;
import com.mk.ots.pay.model.ReSendLeZhu;
import com.mk.ots.pay.model.SelectPms;
import com.mk.ots.pay.module.QueryPay;
import com.mk.ots.pay.module.ali.AliPay;
import com.mk.ots.pay.module.query.BankPayStatusEnum;
import com.mk.ots.pay.module.query.QueryPayPram;
import com.mk.ots.pay.module.weixin.AppPay;
import com.mk.ots.pay.module.weixin.WeChat;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.promo.dao.IBPromotionPriceDao;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.rule.model.BAreaRuleDetail;
import com.mk.ots.rule.service.AreaRuleDetailService;
import com.mk.ots.system.model.UToken;
import com.mk.ots.ticket.dao.UTicketDao;
import com.mk.ots.ticket.model.UTicket;
import com.mk.ots.ticket.service.ITicketService;
import com.mk.ots.ticket.service.parse.ITicketParse;
import com.mk.ots.wallet.service.impl.WalletCashflowService;
import com.mk.ots.kafka.message.OtsCareProducer;
import com.mk.care.kafka.common.CopywriterTypeEnum;
import com.mk.care.kafka.model.Message;

@Service
public class PayService implements IPayService {

    private Logger logger = LoggerFactory.getLogger(PayService.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private IMemberService iMemberService;
    @Autowired
    private IPayDAO iPayDAO;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private IBPromotionDao iBPromotionDao;
    @Autowired
    private IBPromotionPriceDao iBPromotionPriceDao;
    @Autowired
    private IPPayInfoDao ipPayInfoDao;
    @Autowired
    private IPOrderLogDao ipOrderLogDao;
    @Autowired
    private UTicketDao uTicketDao;
    @Autowired
    private IUUseTicketRecordDao iUUseTicketRecordDao;
    @Autowired
    private IPScoreLogDao pScoreLogDao;
    @Autowired
    private OrderBusinessLogService orderBusinessLogService;
    @Autowired
    private OrderLogService orderLogService;
    @Autowired
    private IPPmsPayLogDao iPPmsPayLogDao;
    @Autowired
    private CheckInUserDAO checkInUserDAO;
    @Autowired
    private HGroupDao hgroupDao;
	@Autowired
	private THotelMapper thotelMapper;
    @Autowired
	private HotelDAO hotelDAO;
    @Autowired
    private IMessageService iMessageService;
    @Autowired
	private AreaRuleDetailService areaRuleDetailService;
    @Autowired
    private IPayCallbackLogDao payCallbackLogDao;
    @Autowired
    private IPayTaskDao payTaskDao;
    @Autowired
    private BOrderLogMapper bOrderLogMapper;
    @Autowired
    private WalletCashflowService walletCashflowService;
    @Autowired
    private ITicketService ticketService;
    @Autowired
	private OtsCareProducer producer;

    @Autowired
    private IPayStatusErrorDao payStatusErrorDao;

    @Autowired
    private RoomSaleService roomSaleService;

    private static String INFORM_SALE_URL = ""; 
    
  	private static final String PAY_CONFIG = "pay_config.properties"; 
  	
  	//PMS2.0支付url
  	private static String PMS_PAY_ADD_URL = "";
  	
  	//PMS2.0退款url
  	private static String PMS_PAY_CANCEL_URL = "";
  	
  	//PMS2.0客单支付情况查询
  	private static String PMS_FIND_PAYINFO=""; 
  	
  	//异常情况下支付取消cancelpaybyerror
  	private static String PMS_CANCEL_PMS_URL=""; 
  	
  	private static final BigDecimal DEFAULT_YF_QIEKEINCOME = new BigDecimal(20).setScale(2,BigDecimal.ROUND_HALF_UP);
  	private static final BigDecimal DEFAULT_PT_QIEKEINCOME = new BigDecimal(10).setScale(2,BigDecimal.ROUND_HALF_UP);
  	
  	@PostConstruct
  	private void initPmsV2Url() throws IOException {

  		logger.info("==============初始化支付系统配置==============");

  		Properties pro = PropertiesLoaderUtils.loadAllProperties(PAY_CONFIG);

  		INFORM_SALE_URL = pro.getProperty("inform_sale_url");

  		PMS_PAY_ADD_URL = pro.getProperty("addpay");
  		PMS_PAY_CANCEL_URL = pro.getProperty("cancelpay");
  		PMS_FIND_PAYINFO = pro.getProperty("selectCustomerpay");
  		PMS_CANCEL_PMS_URL= pro.getProperty("cancelpaybyerror");
  		
  		logger.info("inform_sale_url路径:" + INFORM_SALE_URL);
  		logger.info("addpay路径:" + PMS_PAY_ADD_URL + ",cancelpay路径:" + PMS_PAY_CANCEL_URL + ",selectCustomerpay:" + PMS_FIND_PAYINFO+" ,cancelpaybyerror:"+PMS_CANCEL_PMS_URL);

  		logger.info("==============初始化支付系统配置完毕==============");
  	}

    public PPay cancelPay(OtaOrder order) {
    	PPay ppay = this.findPayByOrderId(order.getId());
        return cancelPay( order,ppay) ;
    }


    public Long payAfterUpdateStatus(Long orderid, PPay pay, BigDecimal price, PPayInfoOtherTypeEnum payinfotype, String payreturnid, Boolean b) {
        this.logger.info("进入payAfterUpdateStatus 参数是：orderid=" + orderid + "  payid=" + pay.getId() + "   price=" + price + "   payinfotype=" + payinfotype + "  payReturnid" + payreturnid
                + " boolean==" + b);
        // 增加第三方支付（有银行支付的情况会返回 payreturnid）
        if ((payreturnid != null) && (this.ipPayInfoDao.getPayOk(payreturnid) == null)) {
            PPayInfo ppi = new PPayInfo();
            // PPay pay=iPayDAO.getPayByOrderId(orderid);
            ppi.setPay(pay);
            ppi.setType(PPayInfoTypeEnum.Z2P);
            ppi.setTime(new Date());
            ppi.setOthertype(payinfotype);
            ppi.setOtherno(payreturnid);
            ppi.setCost(price);
            ppi.setEnable(b);
            this.logger.info(" 增加支付流水：" + ppi.toString());
            ppi = this.ipPayInfoDao.saveOrUpdate(ppi);

            OtaOrder otaOrder = orderDAO.findOtaOrderById(orderid);
            if(PromoTypeEnum.TJ.getCode().equals(otaOrder.getPromoType())){
                pay.setLezhu(pay.getLezhu());
            }
            this.logger.info("更新pay--------");
            this.iPayDAO.saveOrUpdate(pay);
            return ppi.getId();
        }
        return null;
    }


    /**
     * 支付完成后 AddPay
     *
     * @param order
     *            otaOrder
     */
    public boolean pmsAddpayOk(OtaOrder order, PayStatusEnum payStatus, PPayInfoTypeEnum type) {
        Long orderId = order.getId();
        logger.info("订单:" + orderId + "判断ppayinfo类型.");
        if(type != null){
            PPay pay = this.iPayDAO.getPayByOrderId(orderId);
            POrderLog orderLog = ipOrderLogDao.findPOrderLogByPay(pay.getId());
            logger.info("订单:" + orderId + type.getName());
            if(type == PPayInfoTypeEnum.REFUNDLEZHU4LONG) {
            	
            	    if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
						logger.info("到付订单:" + orderId + "常住人入住不下发乐住币,进行平账...");
						addPPayinfo(pay, orderLog.getRealotagive().negate(), null, type, null);
//						orderLog.setOtagive(BigDecimal.ZERO);
//						orderLog.setRealotagive(BigDecimal.ZERO);
//						ipOrderLogDao.saveOrUpdate(orderLog);
						logger.info("订单:" + orderId + "平账完成.");
				}
            } else if(type == PPayInfoTypeEnum.REFUNDLEZHU4QIEKE) {
            	
            		logger.info("订单:" + orderId + "切客离店,退乐住币.");
            		this.cancelPmsPay(orderId, type);
                
            }else if(type == PPayInfoTypeEnum.Y2P ) {
                PPayInfo  payinfo=ipPayInfoDao.getPPayInfo(pay.getId(),PPayInfoTypeEnum.Y2P);
                if(payinfo!=null && isNot0AndNull( payinfo.getCost() ) ){
                    this.ipPayInfoDao.updatePmsSendIdByPayId(pay.getId(),payinfo.getId());  
                    return this.pmsAddpayOk(order, payStatus, true, payinfo.getId());   
                }else{
                	   //更新orderLog的
                    orderLog.setRealallcost(orderLog.getAllcost());
                    orderLog.setRealaccountcost(orderLog.getAccountcost());
                    orderLog.setRealcost(orderLog.getUsercost());
                    orderLog.setRealotagive(orderLog.getOtagive());
                    ipOrderLogDao.saveOrUpdate(orderLog);
                    logger.info("订单:" + orderId +"更新orderLog完毕");
                }
            }
        }
        return  false;
    }


    public boolean pmsAddpayOk(OtaOrder order, PayStatusEnum payStatus, Boolean ispay,Long pmsSendId) {
        Long orderId = order.getId();
        this.logger.info("订单:" + orderId + "pmsAddpayOk 参数 payStatus= "+payStatus+" ,pmsSendId="+pmsSendId);
        PPay pay = this.iPayDAO.getPayByOrderId(orderId);
        Optional<UMember> op = this.iMemberService.findMemberById(order.getMid());
        if (!op.isPresent()) {
            throw MyErrorEnum.memberNotExist.getMyException();
        }
        UMember member = op.get();
        POrderLog orderLog = this.ipOrderLogDao.findPOrderLogByPay(pay.getId());
        logger.info("订单:" + orderId +"查询orderLog"+orderLog.toString());
        
        //更新orderLog的
        orderLog.setRealallcost(orderLog.getAllcost());
        orderLog.setRealaccountcost(orderLog.getAccountcost());
        orderLog.setRealcost(orderLog.getUsercost());
        orderLog.setRealotagive(orderLog.getOtagive());
        ipOrderLogDao.saveOrUpdate(orderLog);
        logger.info("订单:" + orderId +"更新orderLog完毕");
        
        // pms 补贴的
        BigDecimal price = new BigDecimal(0);
        if ((ispay != null) && ispay) {
            // 已经支付的，加上在线支付的钱
            if (payStatus.getId().intValue() == PayStatusEnum.alreadyPay.getId().intValue()) {
                price = orderLog.getRealallcost();
            } else if (payStatus.getId().intValue() == PayStatusEnum.doNotPay.getId().intValue()) {
            	price = orderLog.getRealotagive();
                if(PayTools.isPositive(orderLog.getRealaccountcost())){
                	price = price.add(orderLog.getRealaccountcost());
                }
            }
        } else {
            price = orderLog.getRealallcost();
        }
        this.logger.info("订单:" + orderId +"*****,price:"+price);
        
        if (price.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        if(PromoTypeEnum.TJ.getCode().equals(order.getPromoType())){
            price = pay.getLezhu();
        }
        this.logger.info("订单:" + orderId + "payService类中调用ticket:pay支付--end--price:" + price);
        return this.pmsAddpay(order,pay.getId(),pmsSendId, price, member.getName(),null);
    }

    /*
	 * 根据EHotel中的isNewPms区分是否走新流程
	 */
	private boolean isNewPMS(OtaOrder order) {
		
//		String isNewPMS = eHotelDao.findEHotelByid(order.getHotelId()).getStr(IS_NEWPMS4EHOTEL);
		String isNewPMS =  thotelMapper.selectById(Long.valueOf(order.getHotelId())).getIsnewpms();
		
		if(isNewPMS.equals("T")) {
			
			return true;
		}
		
		return false;
		
		
	}
    
    public boolean cancelPmsPay(Long orderId, PPayInfoTypeEnum type) {
        
        this.logger.info("订单:" + orderId + "进入取消乐住币流程...");

        PPay pay = this.iPayDAO.getPayByOrderId(orderId);
        
        final OtaOrder order = this.orderService.findOtaOrderByIdNoCancel(orderId);
        POrderLog findPOrderLogByPay = this.ipOrderLogDao.findPOrderLogByPay(pay.getId());

        if (findPOrderLogByPay.getPmssend() == PmsSendEnum.ResponseSuccess && findPOrderLogByPay.getPmsrefund() != PmsSendEnum.ResponseSuccess) {

        	if(isNewPMS(order)) {
				cancelPayToPmsV2(order, pay, findPOrderLogByPay, type);
				return true;
			}
            // 查看pms是否在线
            this.checkPmsOnline(order);
            
            BigDecimal refundPrice = calculateRefundLeZhu(order, findPOrderLogByPay, type);
            
            Long pmsSendIdLong = null;
//            if (!isNot0AndNull(findPOrderLogByPay.getRealotagive())) {
//            	//退款流水
//                pmsSendIdLong = addPPayinfo(pay, refundPrice.negate(), null, PPayInfoTypeEnum.Z2U, null);
//            } else {
//            	//退优惠券流水
//                pmsSendIdLong = addPPayinfo(pay, findPOrderLogByPay.getRealotagive().negate(), null, type, null);
//            }
            
			if (isNot0AndNull(findPOrderLogByPay.getRealotagive())) {
				// 退优惠券流水
				pmsSendIdLong = addPPayinfo(pay, findPOrderLogByPay.getRealotagive().negate(), null, type, null);
			}
            //若此订单支付时包含 红包支付，则流水也需要添加退红包的记录
            if(PayTools.isPositive(findPOrderLogByPay.getRealaccountcost())){
            		logger.info("订单："+orderId+"取消，此订单使用红包支付，金额是="+findPOrderLogByPay.getRealaccountcost()+"添加退红包的流水");
            		PPayInfo info = addPPayinfo(pay, findPOrderLogByPay.getRealaccountcost().negate(),PPayInfoTypeEnum.ACCOUNTCOSTREFUND, pmsSendIdLong);
        			if(null == pmsSendIdLong) {
        				pmsSendIdLong = info.getId();
        			}
            }
            
            if(null == pmsSendIdLong) {
        			//退款流水
            		pmsSendIdLong = addPPayinfo(pay, refundPrice.negate(), null, PPayInfoTypeEnum.Z2U, null);
            }
            
            if(cancelPayToPms(order, pay, refundPrice, findPOrderLogByPay.getId(),pmsSendIdLong)) {
                this.logger.info("订单:" + orderId + "取消乐住币成功,平账...");

                findPOrderLogByPay.setRealotagive(BigDecimal.ZERO);
                //若此订单支付时包含 红包支付
                if(PayTools.isPositive(findPOrderLogByPay.getRealaccountcost())){

                		findPOrderLogByPay.setRealaccountcost(BigDecimal.ZERO);
                }
                
        			findPOrderLogByPay.setRealallcost(BigDecimal.ZERO);
        			findPOrderLogByPay.setRealcost(BigDecimal.ZERO);
                findPOrderLogByPay.setPmsrefund(PmsSendEnum.ResponseSuccess);
                ipOrderLogDao.update(findPOrderLogByPay);
                this.logger.info("订单:" + orderId + "更新orderLog成功.");
                
                ipPayInfoDao.updatePmsSendIdById(pmsSendIdLong, pmsSendIdLong);
                this.logger.info("订单:" + orderId + "更新ppayinfo成功.");
                
                this.logger.info("订单:" + orderId + "平账完成.");
                
                return true;
            } else {
                
                ipPayInfoDao.deleteById(pmsSendIdLong);
                this.logger.info("订单:" + orderId + "取消乐住币失败,更新ppayinfo成功.");
                
                return false;
            }
            
        }
        this.logger.info("订单:" + orderId + "无需调用pms取消乐住币.");
        
        return true;
    }
    
    /*
	 * 通过url发送取消支付信息到PMS2.0
	 */
	private void cancelPayToPmsV2(OtaOrder order, PPay pay, POrderLog orderLogByPay, PPayInfoTypeEnum type) {
		
		String mark = "订单[" + order.getId() + "]PayId[" + pay.getId() + "]";
		logger.info(mark + "开始向PMS2.0发送取消支付信息流程...");
		
		BigDecimal refundPrice = calculateRefundLeZhu(order, orderLogByPay, type);
        
        Long pmsSendIdLong = null;
//        if (!isNot0AndNull(orderLogByPay.getRealotagive())) {
//        	//退款流水
//            pmsSendIdLong = addPPayinfo(pay, refundPrice.negate(), null, PPayInfoTypeEnum.Z2U, null);
//        } else {
//        	//退优惠券流水
//            pmsSendIdLong = addPPayinfo(pay, orderLogByPay.getRealotagive().negate(), null, type, null);
//        }
        
        if (isNot0AndNull(orderLogByPay.getRealotagive())) {
			// 退优惠券流水
			pmsSendIdLong = addPPayinfo(pay, orderLogByPay.getRealotagive().negate(), null, type, null);
		}
         //添加退红包支付的部分 流水
        if(PayTools.isPositive(orderLogByPay.getRealaccountcost())){
        		PPayInfo info = addPPayinfo(pay, orderLogByPay.getRealaccountcost().negate(),PPayInfoTypeEnum.ACCOUNTCOSTREFUND, pmsSendIdLong);
			if(null == pmsSendIdLong) {
				pmsSendIdLong = info.getId();
			}
        }
        
        if(null == pmsSendIdLong) {
			//退款流水
    			pmsSendIdLong = addPPayinfo(pay, refundPrice.negate(), null, PPayInfoTypeEnum.Z2U, null);
        }
        
        if(pmsSendIdLong == null) {
        		logger.info(mark + "无需调用pms2.0取消乐住币.");
        		return;
        }
		
		PMSResponse response = null;
		try {
			String request = wrapPMSRequest(order, pmsSendIdLong, refundPrice, "cancelpay");
			logger.info(mark + "调用PMS2.0,URL[" + PMS_PAY_CANCEL_URL + "]参数[" + request + "].");
			String responseString = PayTools.dopostjson(PMS_PAY_CANCEL_URL, request);
			response = JSON.parseObject(responseString, PMSResponse.class);
		} catch (Exception e) {
			logger.error(mark + "向PMS2.0发送取消支付信息异常!", e);
			process4CancelPMSV2Fail(orderLogByPay, order, e.getMessage());
			ipPayInfoDao.deleteById(pmsSendIdLong);
		}
		if (response.isSuccess()) {
			
			logger.info(mark + "向PMS2.0发送取消支付信息成功.");
			
			orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELPMSPAY.getId(), "", "成功", "");
			
			 this.logger.info(mark + "取消乐住币成功,平账...");
//			 orderLogByPay.setOtagive(BigDecimal.ZERO);
			 orderLogByPay.setRealotagive(BigDecimal.ZERO);
			 orderLogByPay.setPmsrefund(PmsSendEnum.ResponseSuccess);
			 //取消的需要把使用红包支付的部分置0
			 if(PayTools.isPositive(orderLogByPay.getRealaccountcost())){
//				 orderLogByPay.setAccountcost(BigDecimal.ZERO);			
				 orderLogByPay.setRealaccountcost(BigDecimal.ZERO);
			 }
			orderLogByPay.setRealallcost(BigDecimal.ZERO);
			orderLogByPay.setRealcost(BigDecimal.ZERO);
             ipOrderLogDao.update(orderLogByPay);
             this.logger.info(mark + "更新orderLog成功.");
             
             ipPayInfoDao.updatePmsSendIdById(pmsSendIdLong, pmsSendIdLong);
             this.logger.info(mark + "更新ppayinfo成功.");
             
             this.logger.info(mark + "平账完成.");
		} else {
			logger.error(mark + "向PMS2.0发送取消支付信息失败, errorCode[" + response.getErrorcode() + "],errorMsg[" + response.getErrormsg() + "].");
			process4CancelPMSV2Fail(orderLogByPay, order, response.getErrormsg());
			ipPayInfoDao.deleteById(pmsSendIdLong);
		}
	}
	
	private void process4SendPMSV2Fail(POrderLog pOrderLog, OtaOrder order, String errorMsg, String operateName) {
		
		ipOrderLogDao.updatePmsSendStatusById(pOrderLog.getId(), PmsSendEnum.AlreadyNoneResponse, errorMsg);
		if(StringUtils.isNotEmpty(operateName)){
			orderBusinessLogService.saveLog1(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币失败", errorMsg,operateName);
		}else{
			orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币失败", errorMsg);
		}
	}
	
	private void process4CancelPMSV2Fail(POrderLog orderLogByPay, OtaOrder order, String errorMsg) {
		
		ipOrderLogDao.updatePmsRefundStatusById(orderLogByPay.getId(), PmsSendEnum.AlreadyNoneResponse, "");
		orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELPMSPAY.getId(), "", "平账PMS异常", errorMsg);
	}
	
	/**
	 * 封装发送给PMS2.0的数据
	 * @param order
	 * @param payid
	 * @param price
	 * 
	 * @return String
	 */
	private String wrapPMSRequest(OtaOrder order, long payid, BigDecimal price, String function){
		
		THotel hotel=hotelDAO.findHotelByHotelid(String.valueOf(order.getHotelId()));
		
		
		PMSRequest res = new PMSRequest();
		
		res.setHotelid(hotel.get("pms") + "");
		res.setUuid(PayTools.getUuid());
		res.setFunction(function);
		res.setTimestamp(PayTools.getTimestamp());
		res.setPayid(String.valueOf(payid));
		res.setCost(price.toString());
		
		OtaRoomOrder roomOrder = order.getRoomOrderList().get(0);
		
		String pmsRoomOrderNo = roomOrder.getPmsRoomOrderNo();
		if(StringUtils.isEmpty(pmsRoomOrderNo)){
			
			logger.info("订单[" + order.getId() + "]关联OTA客单的PMSRoomOrderNo为空!");
			res.setCustomerid("");
		}
		res.setCustomerid(pmsRoomOrderNo);
		
		return JSON.toJSONString(res);
	}
    
    /**
     * 校验pms是否在线
     *
     * @param order
     */
    private void checkPmsOnline(OtaOrder order) {
        
        long orderId = order.getId();
        
        this.logger.info("订单:" + orderId + "检测pms在线,hotelId::{}", order.getHotelId());
        
        ReturnObject<Boolean> ro = HotelPMSManager.getInstance().getService().checkOnline(order.getHotelId());
        Boolean isError = HotelPMSManager.getInstance().returnError(ro);
        if (isError) {
            this.logger.info("订单:" + orderId + "检测pms在线出错:{}", ro.getErrorMessage());
            throw MyErrorEnum.checkPmsOnlineError.getMyException();
        } else if (ro.getValue() == false) {
            this.logger.info("订单:" + orderId + "检测pms在线出错:errorcode:{},errormessage:{}", ro.getErrorCode(), ro.getErrorMessage());
            throw MyErrorEnum.pmsOffline.getMyException();
        }
        
    }

    private boolean cancelPayToPms(OtaOrder order, PPay pay, BigDecimal refundPrice, Long orderlogid, Long pmsSendId) {
        long orderId = order.getId();
        this.logger.info("订单:" + orderId + ",refundPrice="+refundPrice+", pmsSendId="+pmsSendId);
        if(pmsSendId!=null){
            this.logger.info("订单:" + orderId + "调用pms取消乐住币...");
            OtaRoomOrder roomOrder = order.getRoomOrderList().get(0);
            PmsCancelPay pmspay = new PmsCancelPay();
            pmspay.setOrderid(roomOrder.getLong("Id"));
            pmspay.setCustomerno(roomOrder.getPmsRoomOrderNo());
            pmspay.setOpuser(pay.getMember() != null ? pay.getMember().getName() : "");
            pmspay.setPayid(pmsSendId);
            pmspay.setTime(new Date());
            pmspay.setCost(refundPrice);
            this.logger.info("订单:" + orderId +"调用pms取消乐住币参数::cancelpay::hotelid::{},Pmsorderno::{}", order.getHotelId(), pay.getPmsorderno());
            ReturnObject<PmsCancelPay> ro = HotelPMSManager.getInstance().getService().cancelPayToPms(order.getHotelId(), pmspay);
            if (HotelPMSManager.getInstance().returnError(ro)) {
                this.logger.info("订单:" + orderId + "调用pms取消乐住币失败[payid:{}, errorCode:{}, errormsg:{}].", pay.getId(), ro.getErrorCode(), ro.getErrorMessage());
                this.ipOrderLogDao.updatePmsRefundStatusById(orderlogid, PmsSendEnum.AlreadyNoneResponse, ro.getErrorMessage());
                orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELPMSPAY.getId(), "", "平账PMS异常", ro.getErrorMessage());
          
                return false;
            } else {
                logger.info("订单:" + orderId + "调用pms取消乐住币成功.");
                orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.CANCELPMSPAY.getId(), "", "回收乐住币成功 金额："+refundPrice, "");
                
                return true;
            }
        }
        return false;
    }


    /**
     * 计算取消乐住币金额
     * @param order
     * @param orderLogByPay
     * @return
     */
    private BigDecimal calculateRefundLeZhu(OtaOrder order, POrderLog orderLogByPay, PPayInfoTypeEnum type) {
        
        long orderId = order.getId();
        
        BigDecimal price = new BigDecimal(0);
        if (OrderTypeEnum.YF.getId().intValue() == order.getOrderType()) {//预付
            
            if(type == PPayInfoTypeEnum.Y2U) {//正常取消乐住币:用户支付+优惠补贴
                
                logger.info("订单:" + orderId + "为预付订单正常取消乐住币流程.");
                price = orderLogByPay.getRealallcost();
                
            } else if (type == PPayInfoTypeEnum.REFUNDLEZHU4LONG) {//长住人取消乐住币:优惠补贴
                
                logger.info("订单:" + orderId + "为预付订单长住人取消乐住币流程.");
                price = orderLogByPay.getRealotagive();
            }
        } else if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {//到付
            
            logger.info("订单:" + orderId + "为到付订单取消乐住币流程.");
            price = orderLogByPay.getRealotagive();
        }
        
        logger.info("订单:" + orderId + "取消乐住币金额:" + price);
        
        return price;
    }
    
    /*
	 * 通过url发送支付信息到PMS2.0
	 */
	private  boolean  pmsAddpayV2(OtaOrder order, long payid, long pmsSendId, BigDecimal price, String operateName) {

		String mark = "订单[" + order.getId() + "]PayId[" + payid + "]";
		
		logger.info(mark + "开始向PMS2.0发送支付信息流程...");
		
		POrderLog pOrderLog = ipOrderLogDao.findPOrderLogByPay(payid);
		try {
			String request = wrapPMSRequest(order, pmsSendId, price,"addpay");
			
			logger.info(mark + "调用PMS2.0,URL[" + PMS_PAY_ADD_URL + "]参数[" + request + "].");
			
			String responseString = PayTools.dopostjson(PMS_PAY_ADD_URL, request);

			PMSResponse response = JSON.parseObject(responseString, PMSResponse.class);

			if (response.isSuccess()) {
				
				ipOrderLogDao.updatePmsSendStatusById(pOrderLog.getId(), PmsSendEnum.ResponseSuccess, "");
//				orderLogService.findOrderLog(order.getId()).set("sendLeZhu", "T").saveOrUpdate();
				BOrderLog bOrderLog = new BOrderLog();
				bOrderLog.setSendlezhu("T");
				bOrderLog.setOrderid(order.getId());
				bOrderLogMapper.updateByOrderId(bOrderLog);
				
			   if(StringUtils.isNotEmpty(operateName)){
				   	this.orderBusinessLogService.saveLog1(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币成功，金额：" + price.floatValue(), "",operateName);
	            }else{
	            	this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币成功，金额：" + price.floatValue(), "");
	            }
				
				logger.info(mark + "向PMS2.0发送支付信息成功.");
				
				return true;

			} else {
				
				logger.error(mark + "向PMS2.0发送支付信息失败, errorCode[" + response.getErrorcode() + "],errorMsg["
						+ response.getErrormsg() + "].");
				
				process4SendPMSV2Fail(pOrderLog, order, response.getErrormsg(), operateName);
				sendLeZhuFailWithDB(order, payid, pmsSendId,price, null);
			}
		} catch (Exception e) {

			logger.error(mark + "向PMS2.0发送支付信息异常!", e);

			process4SendPMSV2Fail(pOrderLog, order, e.getMessage(),operateName);
			sendLeZhuFailWithDB(order, payid, pmsSendId,price, null);
		}
		return false;
	}

	private boolean pmsAddpay(OtaOrder order, long payid,Long pmsSendId, BigDecimal price, String memberName, String operateName) {
		
		if (isNewPMS(order)) {
			// 新流程
			return pmsAddpayV2(order, payid, pmsSendId.longValue(), price, operateName);
		}
		
		boolean result = false;
        
        Long orderId = order.getId();
        // 这是ota补贴的钱
        OtaRoomOrder roomOrder = order.getRoomOrderList().get(0);
        PmsAddPay pmspay = new PmsAddPay();
        pmspay.setOrderid(orderId);
        pmspay.setCustomerno(roomOrder.getPmsRoomOrderNo() == null ? "" : roomOrder.getPmsRoomOrderNo());
        pmspay.setOpuser(memberName);
        pmspay.setPayid(pmsSendId);
        // 这是用户实际支付的钱
        pmspay.setCost(price);
        pmspay.setTime(new Date());
        this.logger.info("订单:" + orderId + "pms接口调用:pmsAddpay 支付状态:" + order.getHotelId() + "....");
        this.logger.info("订单:" + orderId + "pmsAddpay(payid:{}, price:{}, membername:{}) start.", payid, price, memberName);
        ReturnObject<PmsAddPay> ro = HotelPMSManager.getInstance().getService().payToPms(order.getHotelId(), pmspay);
        this.logger.info("订单:" + orderId + "开始//标记下发乐住币状态[payid:{}].", payid);
        POrderLog pOrderLog = this.ipOrderLogDao.findPOrderLogByPay(payid);
        if (HotelPMSManager.getInstance().returnError(ro)) {
        	this.logger.info("订单:" + orderId + "乐住币状态[payid:{}, pmssend:{}, errormsg:{}].", payid, PmsSendEnum.AlreadyNoneResponse, ro.getErrorMessage());
            this.ipOrderLogDao.updatePmsSendStatusById(pOrderLog.getId(), PmsSendEnum.AlreadyNoneResponse, ro.getErrorMessage());
            this.logger.info("订单:" + orderId + " 失败情况 更新 pOrderLog 完成 ");
            if(StringUtils.isNotEmpty(operateName)){
            	this.orderBusinessLogService.saveLog1(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币失败", ro.getErrorMessage(),operateName);
            }else{
            	this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币失败", ro.getErrorMessage());
            }
            //下发乐住币失败的数据，添加到redis中
//           this.sendLeZhuFail(order, payid, pmsSendId,price, memberName);
           //下发乐住币失败的数据，添加到任务表中
           this.sendLeZhuFailWithDB(order, payid, pmsSendId,price, memberName);
    	   
        } else {
            this.logger.info("订单:" + orderId + "乐住币状态[payid:{}, pmssend:{}].", payid, PmsSendEnum.ResponseSuccess);
            this.ipOrderLogDao.updatePmsSendStatusById(pOrderLog.getId(), PmsSendEnum.ResponseSuccess, "");
            this.logger.info("订单:" + orderId + "更新 pOrderLog 完成 ");
//            this.orderLogService.findOrderLog(orderId).set("sendLeZhu", "T").saveOrUpdate();
            BOrderLog bOrderLog = new BOrderLog();
			bOrderLog.setSendlezhu("T");
			bOrderLog.setOrderid(order.getId());
			bOrderLogMapper.updateByOrderId(bOrderLog);
            this.logger.info("订单:" + orderId + "更新 B_OrderLog 完成 ");
            if(StringUtils.isNotEmpty(operateName)){
            	this.orderBusinessLogService.saveLog1(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币成功，金额：" + price.floatValue(), "",operateName);
            }else{
            	this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.AFTERXIAFALUZHUBI.getId(), "", "下发乐住币成功，金额：" + price.floatValue(), "");
            }
            result = true;
        }
        this.logger.info("订单:" + orderId + "结束//标记下发乐住币状态[payid:{}].", payid);
        this.logger.info("订单:" + orderId + "errorcode:{}, errormsg:{}, iserror:{}....", ro.getErrorCode(), ro.getErrorMessage(), ro.getIsError());
        try {
            this.logger.info("订单:" + orderId + ">> result: {}", new ObjectMapper().writeValueAsString(ro.getValue()));
        } catch (JsonProcessingException e) {
            this.logger.error("订单:" + orderId + ">> 解析pms返回结果发生错误", e);
        }
        this.logger.info("订单:" + orderId + "pmsAddpay(payid:{}, price:{}, membername:{}) end.", payid, price, memberName);
        return result;
    }
	
	private void sendLeZhuFailWithDB(OtaOrder order, long payid,Long pmsSendId, BigDecimal price, String memberName){
		
		Long orderId = order.getId();
		
		if(payTaskDao.exist(orderId, PayTaskTypeEnum.AUTORETRYSENDLEZHU)) {
			
			this.logger.info("订单:" + orderId + "已经在任务表中,不需要再插入. ");
			
			return;
		}
		
		this.logger.info("订单:" + orderId + "正常流程中下发乐住币失败,存入任务表... ");
		
		ReSendLeZhu reSendLeZhu = null;
		try {
			reSendLeZhu = new ReSendLeZhu(order, payid, pmsSendId,price, memberName);
			String json2ReSendLeZhu = JsonKit.toJson(reSendLeZhu);
			logger.info("订单:" + orderId + "存入任务表的类型=>" +PayTaskTypeEnum.AUTORETRYSENDLEZHU.name()+ ",json串=>" + json2ReSendLeZhu);
			
			Long result = payTaskDao.insertPayTask(buildPayTask(order.getId(), json2ReSendLeZhu, PayTaskStatusEnum.INIT, PayTaskTypeEnum.AUTORETRYSENDLEZHU));
	    	
	    	logger.info("订单:" + order.getId() + "保存任务[" + PayTaskTypeEnum.AUTORETRYSENDLEZHU.name() + "]返回值:" + result);
			
		} catch (Exception e) {
			logger.error("订单:" + orderId + "存入任务表异常! ",e);
		} 
		 this.logger.info("订单:" + orderId + " 存入任务表完成. ");
	}

    /**
     * 根据订单id查交易流水
     *
     * @param orderId
     * @return
     */
    @Override
    public PPay findPayByOrderId(Long orderId) {
        return this.iPayDAO.getPayByOrderId(orderId);
    }

    @Override
    public PmsError findPmsErrorByOrderId(Long orderId) {
        // return iPayDAO.findPmsErrorByOrderId(orderId);
        return null;
    }


    @Override
    public Long countOrderByOrderSts(Long orderId, OtaOrderStatusEnum sts) {
        return (long) this.orderDAO.countOrderByOrderSts(orderId, sts);
    }

    @Override
    public PPay findPayById(Long payid) {
        return this.iPayDAO.findPayById(payid);
    }

//    @Override
//    public Long countTicketsByMember(Long mid, Long promoId) {
//        return this.iPayDAO.countTicketsByMember(mid, promoId);
//    }

//    @Override
//    public Long countOrderTicket(Long promoId, Long orderId) {
//        return this.iPayDAO.countOrderTicket(promoId, orderId);
//    }

    /**
     * 保存优惠流水更新支付流水
     *
     * @param pay
     * @param ticketParses
     * @param order
     */
    private List<PPayInfo> savePromoPayInfosByParse(PPay pay, List<ITicketParse> ticketParses, OtaOrder order) {
        List<PPayInfo> promoInfos = new ArrayList<>();
        for (ITicketParse parse : ticketParses) {
            
//          parse.getTicket().getPromotion().getType()
            BigDecimal cost = new BigDecimal(0);
            if (parse.getTicket() == null) { // 获取优惠码对应线上线下的价格
                if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
                    cost = parse.getOfflinePrice();
                } else {
                    cost = parse.getOnlinePrice();
                }
            } else { // 获取优惠劵对应线上线下的价格
                cost = parse.allSubSidy();
            }

            if (cost.compareTo(BigDecimal.ZERO) == 0) {
                // 优惠金额为0未使用优惠券/码
                parse.setNeedUse(false);
                continue;
            } else {
                parse.setNeedUse(true);
                // 更新订单使用优惠状态
                this.updateOrderPromoSts(order, parse);
            }
            PPayInfo promoInfo = this.savePromoInfos(parse.getTicket(), parse.getPromotion(), cost, PPayInfoTypeEnum.Y2P, pay);
            if (null != promoInfo) {
                promoInfos.add(promoInfo);
                this.logger.info("订单号是"+pay.getOrderid()+",promoInfo:"+promoInfo.toString());
            }
        }
        return promoInfos;
    }

    /**
     * 更新订单是否使用优惠券状态
     *
     * @param order
     * @param parse
     */
    private void updateOrderPromoSts(OtaOrder order, ITicketParse parse) {
        Boolean isPromotion = parse.getTicket() == null ? true : order.getPromotion().equals("T");
        Boolean isCoupon = parse.getTicket() == null ? order.getCoupon().equals("T") : true;
        order.setPromotion(isPromotion ? "T" : "F");
        order.setCoupon(isCoupon ? "T" : "F");
        this.iPayDAO.updateOrderPromos(order.getId(), isCoupon, isPromotion);
    }

    /**
     * 支付获取流水
     *
     * @param order
     * @param member
     * @param allcost
     * @return
     */
    /**
     * 支付获取流水
     *
     * @param order
     * @param member
     * @param allcost
     * @return
     */
    private PPay getPayByOrder(OtaOrder order, UMember member, BigDecimal allcost) {
        this.logger.info("  getPayByOrder   orderid=={}", order.getId());
        // 校验流水是否存在
        // Long payCounts = this.countPayByOrderId(order.getId(),
        // PaySrcEnum.order);
        List<PPay> list = this.iPayDAO.findPayByOrderidAndPaySrc(order.getId(), PaySrcEnum.order);
        this.logger.info("  getPayByOrder   orderid=={} list=={}", order.getId(), list);
        if ((list == null) || (list.size() == 0)) {// 原来存在，直接查
            this.logger.info("orderid=={} p_pay 还没有创建", order.getId());
            // 创建流水信息
            return this.createPay(order, allcost, member);
        } else {
            this.logger.info("orderid=={} p_pay 已经创建过", order.getId());
            // 创建流水信息
            return list.get(0);
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
    private List<ITicketParse> getPromotionParses(OtaOrder otaOrder) {
        // 查询切客券与议价券
        List<BPromotion> promotions = Lists.newArrayList();
        List<ITicketParse> parses = new ArrayList<>();
        if (otaOrder != null) {
            promotions = this.iBPromotionDao.queryYiJiaAndQiKePromotionByOrderId(otaOrder.getId());
            // 解析优惠券码信息
            for (BPromotion promotion : promotions) {
                ITicketParse parse = promotion.createParseBean(otaOrder);
                parse.checkUsable();
                parses.add(parse);
            }
        }
        return parses;
    }

    /**
     * 保存优惠券/码 流水及日志 更新优惠券/码信息
     *
     * @param ticket
     * @param promotion
     * @param allCost
     * @param type
     * @param pay
     */
    private PPayInfo savePromoInfos(UTicket ticket, BPromotion promotion, BigDecimal allCost, PPayInfoTypeEnum type, PPay pay) {
        BigDecimal logCost = PPayInfoTypeEnum.Y2P.equals(type) ? allCost.negate() : allCost;
        if (logCost.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        boolean isreturn = type.equals(PPayInfoTypeEnum.Y2P) ? false : true;
        Date now = new Date();

        // 保存流水
        PPayInfo info = new PPayInfo();
        info.setPay(pay);
        info.setCost(allCost);
        info.setEnable(true);
        info.setTime(now);
        info.setType(type);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("payid", pay.getId());
        map.put("mid", pay.getMember().getMid());

        // 保存日志
        UUseTicketRecord record = new UUseTicketRecord();
        record.setCost(logCost);
        record.setIsreturn(isreturn);
        record.setMid(pay.getMember().getId());
        record.setPayid(pay.getId());
        if (null != ticket) {
            record.setTid(ticket.getId());
            map.put("tid", ticket.getId());
        }
        if (null != promotion) {
            record.setPromotionid(promotion.getId());
            map.put("promotionid", promotion.getId());
        }
        record.setUsetime(now);
        this.iUUseTicketRecordDao.deleteByPayidAndMid(map);
        this.iUUseTicketRecordDao.saveOrUpdate(record);
        this.ipPayInfoDao.saveOrUpdate(info);
        // 保存p_score_log
        PScoreLog psl = new PScoreLog();
        psl.setMid(pay.getMember().getMid());
        psl.setTime(new Date());
        psl.setType(PayLogTypeEnum.otherPay);
        psl.setOtherid(info.getId());
        psl.setPrice(pay.getOrderprice());
        psl.setOthertype(PayLogOtherTypeEnum.pay);
        // 先删除
        this.pScoreLogDao.delete(info.getId());
        this.pScoreLogDao.saveOrUpdate(psl);

        info.setInnersrcid(record.getId());
        return info;
    }

    /**
     * * 保存对账信息
     *
     * @param pay
     * @param allcost
     * @param realCost
     * @param ticketParses
     * @param promotionParses
     */
    private BigDecimal saveOrderLog(PPay pay, BigDecimal allcost, BigDecimal realCost, List<ITicketParse> ticketParses, List<ITicketParse> promotionParses,BigDecimal accountcost) {
        POrderLog orderLog = this.ipOrderLogDao.findPOrderLogByPay(pay.getId());
        if (orderLog == null) {
        	return this.createOrderLog(pay, allcost, realCost, ticketParses, promotionParses, accountcost);
        }
        this.updateOrderLog(ticketParses, promotionParses, orderLog, pay, realCost, accountcost);
        return orderLog.getOtagive();
    }
    
    /**
     * 创建对账流水
     * @param pay
     * @param allcost
     * @param realCost
     * @param ticketParses
     * @param ticketParse
     */
    private BigDecimal createOrderLog(PPay pay, BigDecimal allcost, BigDecimal realCost, List<ITicketParse> ticketParses, List<ITicketParse> promoParses,BigDecimal accountcost) {
        POrderLog orderLog = new POrderLog();
        this.calOrderLogSubsidy(ticketParses, promoParses, orderLog, BigDecimal.ZERO, BigDecimal.ZERO, allcost, BigDecimal.ZERO,accountcost);
        orderLog.setRealcost(realCost);
        orderLog.setAllcost(allcost);
        orderLog.setPayid(pay.getId());
        orderLog.setPmssend(PmsSendEnum.None);
        orderLog.setPmsrefund(PmsSendEnum.None);
        orderLog.setRealotagive(BigDecimal.ZERO);
        orderLog.setRealaccountcost(BigDecimal.ZERO);
        orderLog.setRealallcost(BigDecimal.ZERO);
        if(PayTools.isPositive(accountcost)){
        	orderLog.setAccountcost(accountcost);	
        }
        this.ipOrderLogDao.saveOrUpdate(orderLog);
        pay.setpOrderLog(orderLog);
        return orderLog.getOtagive();
    }    

    /**
     * 更新对账流水
     *
     * @param ticketParses
     * @param orderLog
     * @param pay
     * @param realCost
     * @param promotionParses
     */
    private void updateOrderLog(List<ITicketParse> ticketParses, List<ITicketParse> promoParses, POrderLog orderLog, PPay pay, BigDecimal realCost,BigDecimal accountcost) {
        BigDecimal hotelSubsidy = new BigDecimal(0);
        BigDecimal otaSubsidy = new BigDecimal(0);
        BigDecimal userCost = orderLog.getAllcost();
        BigDecimal realOtagive = new BigDecimal(0);
        this.calOrderLogSubsidy(ticketParses, promoParses, orderLog, hotelSubsidy, otaSubsidy, userCost, realOtagive,accountcost);
        orderLog.setPayid(pay.getId());
        orderLog.setRealcost(realCost.add(orderLog.getRealcost()));
        this.ipOrderLogDao.saveOrUpdate(orderLog);
        pay.setpOrderLog(orderLog);
    }
    
    
    /**
     * 计算用户支付金额
     * @param promotionPrice 券定价
     * @return
     */
    public BigDecimal calcPromotionPrice(Long activeid){
        BigDecimal zero = BigDecimal.ZERO;
        if(activeid==null){
            return zero;
        }
        //if 10+1活动
        if(Constant.ACTIVE_10YUAN_1 == activeid){
            return BigDecimal.ONE;
        }
        //if 15元活动
        if(Constant.ACTIVE_15YUAN == activeid){
            return new BigDecimal(15);
        }
        return zero;
    }

    /**
     * 计算优惠券/码 对应补贴
     *
     * @param ticketParses
     * @param promoParses
     * @param orderLog
     * @param hotelSubsidy
     * @param otaSubsidy
     * @param userCost
     * @param realOtagive
     */
    private void calOrderLogSubsidy(List<ITicketParse> ticketParses, List<ITicketParse> promoParses, POrderLog orderLog, BigDecimal hotelSubsidy, BigDecimal otaSubsidy,
            BigDecimal allCost, BigDecimal realOtagive,BigDecimal accountcost) {
        List<ITicketParse> list = new ArrayList<ITicketParse>();
        if(CollectionUtils.isNotEmpty(ticketParses)){
            list.addAll(ticketParses);
        }
        if(CollectionUtils.isNotEmpty(promoParses)){
            list.addAll(promoParses);
        }
        if(CollectionUtils.isNotEmpty(list)){
            setOrderLog(list, orderLog, hotelSubsidy, otaSubsidy, allCost, realOtagive, accountcost);
        }else{
        	if(PayTools.isPositive(accountcost)){
        		allCost=allCost.subtract(accountcost);
        		if(allCost.compareTo(BigDecimal.ZERO)==1){
        			 orderLog.setUsercost(allCost);
        		}else{
        			orderLog.setUsercost(BigDecimal.ZERO);
        		}
        	}else{
        		 orderLog.setUsercost(allCost);
        	}
            orderLog.setOtagive(otaSubsidy);
            orderLog.setHotelgive(hotelSubsidy);
        }
    }
    
    /**
     * 设置orderlog
     * @param ticketParse
     * @param orderLog
     * @param hotelSubsidy
     * @param otaSubsidy
     * @param allCost
     * @param realOtagive
     */
    private  void setOrderLog(List<ITicketParse> list, POrderLog orderLog, BigDecimal hotelSubsidy, BigDecimal otaSubsidy,
            BigDecimal allCost, BigDecimal realOtagive,BigDecimal accountcost){
        BigDecimal userCost = BigDecimal.ZERO;
        for (ITicketParse ticketParse : list) {
            if (allCost.subtract(ticketParse.allSubSidy()).compareTo(BigDecimal.ZERO) > 0) {
                //订单价格-用户优惠券价格-
                userCost = allCost.subtract(ticketParse.allSubSidy()).add(calcPromotionPrice(ticketParse.getPromotion().getActivitiesid()));
                otaSubsidy = ticketParse.otaSubsidy().add(otaSubsidy);
                allCost =   allCost.subtract(ticketParse.allSubSidy());
            }else{
                userCost = calcPromotionPrice(ticketParse.getPromotion().getActivitiesid());
                otaSubsidy = allCost.subtract(userCost).add(otaSubsidy);
                allCost = userCost;
            }
            realOtagive = ticketParse.getPromotion().getOtapre().multiply(otaSubsidy);
            hotelSubsidy = ticketParse.hotelSubsidy().add(hotelSubsidy);
        }
        if(PayTools.isPositive(accountcost)){
        	userCost=userCost.subtract(accountcost);
        	if(userCost.compareTo(BigDecimal.ZERO)==1){
        		orderLog.setUsercost(userCost);
        	}else{
        		orderLog.setUsercost(BigDecimal.ZERO);
        	}
        }else{
        	 orderLog.setUsercost(userCost);
        }
        orderLog.setOtagive(otaSubsidy);
        orderLog.setHotelgive(hotelSubsidy);
    }

    /**
     * 初始创建流水信息
     *
     * @param order
     * @param allcost
     * @param member
     * @return
     */
    private PPay createPay(OtaOrder order, BigDecimal allcost, UMember member) {
        PPay pPay = new PPay();
        THotel hotel = new THotel();
        hotel.setHotelid(String.valueOf(order.getHotelId()));
        pPay.setHotel(hotel);
        pPay.setOrderid(order.getId());
        pPay.setOrderprice(allcost);
        BigDecimal lezhuBi = getLezhuBi(order);
        pPay.setLezhu(lezhuBi);
        pPay.setMember(member);
        pPay.setNeedreturn(NeedReturnEnum.ok);
        pPay.setNeworderid(null);
        pPay.setPaysrc(PaySrcEnum.order);
        // 这里的pms是第一个客单的pms，所以如果有多个客单，对账时是否正确需要确认
        if ((order.getRoomOrderList() == null) || (order.getRoomOrderList().size() == 0)) {
            throw MyErrorEnum.errorParm.getMyException("订单有误，没有选择房间.");
        } else {
            pPay.setPmsorderno(order.getRoomOrderList().get(0).getPmsRoomOrderNo());
        }
        pPay.setStatus(PayTypeEnum.effective);
        pPay.setTime(new Date());
        List<PPayInfo> infoList = new ArrayList<PPayInfo>();
        pPay.setPayInfos(infoList);
        this.saveOrUpdatePay(pPay);
        return pPay;
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
        //判断特价房
        if(PromoTypeEnum.TJ.getCode().equals(order.getPromoType())){
            for (OtaRoomOrder otaRoomOrder : roomOrdrs) {
                TRoomSale tRoomSale = new TRoomSale();
                tRoomSale.setRoomId((int)otaRoomOrder.getRoomId());
                TRoomSale resultRoomSale = roomSaleService.getOneRoomSale(tRoomSale);
                if(resultRoomSale == null ){
                    throw MyErrorEnum.alreadyEndPromo.getMyException("今夜特价活动已结束");
                }
                allcost = allcost.add(new BigDecimal(resultRoomSale.getSalePrice()));
            }
        }else{
            for (OtaRoomOrder otaRoomOrder : roomOrdrs) {
                allcost = allcost.add(otaRoomOrder.getTotalPrice());
            }
        }

        return allcost;
    }

    /**
     * 计算乐住币
     *
     * @param order
     * @param allcost
     * @return
     */
    private BigDecimal getLezhuBi(OtaOrder order) {
        BigDecimal allcost = BigDecimal.ZERO;
        List<OtaRoomOrder> roomOrdrs = order.getRoomOrderList();
        //判断特价房
        if(PromoTypeEnum.TJ.getCode().equals(order.getPromoType())){
            for (OtaRoomOrder otaRoomOrder : roomOrdrs) {
                TRoomSale tRoomSale = new TRoomSale();
                tRoomSale.setRoomId((int)otaRoomOrder.getRoomId());
                TRoomSale resultRoomSale = roomSaleService.getOneRoomSale(tRoomSale);
                if(resultRoomSale == null ){
                    throw MyErrorEnum.alreadyEndPromo.getMyException("今夜特价活动已结束");
                }
                allcost = allcost.add(resultRoomSale.getSettleValue());
            }
        }else{
            for (OtaRoomOrder otaRoomOrder : roomOrdrs) {
                allcost = allcost.add(otaRoomOrder.getTotalPrice());
            }
        }
        allcost = allcost.setScale(0, BigDecimal.ROUND_HALF_UP);
        return allcost;
    }

    
    /**
     * 更新订单流水
     *
     * @param pay
     */
    private void saveOrUpdatePay(PPay pay) {
        if (pay.getId() != null) {
            this.iPayDAO.update(pay);
        } else {
            this.iPayDAO.saveOrUpdate(pay);
        }

    }

    @Override
    public Boolean payIsWaitPay(String orderid) {
        
            this.logger.info("判断是否是等待支付的订单，订单号：" + orderid);
            PPayInfo payinfo = this.ipPayInfoDao.selectByOrderIdAndPayOk(orderid);
            if (payinfo == null) {
                this.logger.info("订单号：" + orderid + " 没有找到银行支付的流水");
                return true;
            }
            this.logger.info("订单号：" + orderid + " 找到银行支付的流水，是支付过的订单，并且回调成功的");
            return false;

    }

    /**
     * 支付取消订单后，去支付宝退款的回调 把退款成功后的数据做一个修改
     *
     * @param 支付宝返回的
     *            result_details数据
     */
    @Override
    public boolean alipayCancelRes(String str) {
        this.logger.info("支付宝退款回调，进入方法.");
        boolean b = false;
        try {
            String[] sp1 = str.split("\\$");
            if ((sp1 != null) && (sp1.length != 0)) {
                // 去数据库更新的 is集合，用,隔开
                String payids = "";
                for (int i = 0; i < sp1.length; i++) {
                    if ((sp1[i] != null) && (sp1[i].length() != 0)) {
                        String[] sts = sp1[i].split("\\^");
                        if (sts.length >= 2) {
                            String res = sts[sts.length - 1];
                            if (res.equals("SUCCESS")) {
                                String payid = sts[0];
                                payids = payids + payid + ",";
                            }
                            // 去掉最后一个,
                            if (payids.length() != 0) {
                                payids = payids.substring(0, payids.length() - 1);
                                // 修改 p_payinfo
                                this.ipPayInfoDao.aliPayRefundSuccess(payids);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            this.logger.info("支付宝退款回调,回调参数出错");
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 解析优惠券
     *
     * @param order
     * @param mid
     * @param promotionNoList
     * @return
     */

    private List<ITicketParse> getTicketParses(OtaOrder order, Long mid) {
        List<ITicketParse> parses = new ArrayList<ITicketParse>();
        // 查询已绑定的普通优惠券
        List<UTicket> tickets = this.uTicketDao.findUTicketsByOrderIdAndMid(order.getId(), mid);
        if ((tickets == null) || tickets.isEmpty()) {
            return parses;
        }
        // 解析优惠券
        for (UTicket ticket : tickets) {
            parses.add(ticket.createParseBean(order));
        }
        return parses;
    }

    @Override
    public OtaOrder createPayPrice0(OtaOrder order) {
        order.setPayStatus(PayStatusEnum.alreadyPay.getId().intValue());
        if (order.getOrderStatus() < OtaOrderStatusEnum.Confirm.getId().intValue()) {
            order.setOrderStatus(OtaOrderStatusEnum.Confirm.getId().intValue());
        }
        order.saveOrUpdate();
        return order;
    }
    

    @Override
    public Map<String, Object> createPay(HttpServletRequest request, long longorderId, String promotionno, String couponno, String paytype, String onlinepaytype) {
        
        OtaOrder order = this.orderService.findOtaOrderById(longorderId);   
        if (order == null) {
        	logger.error("订单:" + longorderId +"未查询到.");
        	return wrapFailResult(MyErrorEnum.errorParm);
        }
        //check pay
        if(PromoTypeEnum.TJ.getCode().equals(order.getPromoType())) {
            //如果选择了今夜特价房则只能使用在线支付或房券支付 其他都不能使用
            if (StringUtils.isNotEmpty(promotionno)) {
                return wrapFailResult(MyErrorEnum.promotionError);
            }
            if (StringUtils.isNotEmpty(couponno)) {
                return wrapFailResult(MyErrorEnum.couponNoError);
            }
            if (OrderTypeEnum.YF.getId() != order.getOrderType()) {
                return wrapFailResult(MyErrorEnum.OrderTypeError);
            }
        }
        //已经取消的订单不能支付
        if (order.getOrderStatus() >= 510 ) {
        	
        	logger.error("订单:" + longorderId +"该订单已取消,无法支付.");
        	return wrapFailResult(MyErrorEnum.orderCanceled);
        }
        if (order.getOrderStatus() >= OtaOrderStatusEnum.Confirm.getId().intValue() ) {
        	
        	logger.error("订单:" + longorderId +"该订单已确认过,不能重复创建支付.");
        	return wrapFailResult(MyErrorEnum.orderConfirm);
        }
        
        if (order.getPayStatus() >= PayStatusEnum.alreadyPay.getId().intValue()) {
        	
        	logger.error("订单:" + longorderId +"已经支付成功,不能重复创建支付.");
        	return wrapFailResult(MyErrorEnum.alreadyPay);
        }
        
        this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_START_PAY.getId(), "", "", "");
        // 设置订单的支付状态， 到店支付还是在线支付
        if (paytype.trim().equals("1")) {
            order = orderService.changeOrderStatusByPay(order.getId(), null, PayStatusEnum.waitPay, OrderTypeEnum.YF);
        } else {
            order = orderService.changeOrderStatusByPay(order.getId(), null, PayStatusEnum.doNotPay, OrderTypeEnum.PT);
        }
        // 判断原来提交过没有
        PPay oldpay = this.findPayByOrderId(longorderId);
        if (oldpay == null) {
            try { // 去PMS锁房，如果抛出异常，说明房子被占用
                this.orderService.createPmsOrderAndLockRoomBeforerPay(order);
            } catch (Exception e) {
                this.logger.error("订单:" + longorderId + "创建支付时, 锁房出现问题.", e);
                throw MyErrorEnum.errorParm.getMyException(e.getMessage());
            }
        }
        // 创建支付
        PPay pay = this.paycreateByOrder(order, couponno, promotionno, paytype, onlinepaytype);
        if (pay != null) {
        	
            // 设置订单的支付状态， 到店支付还是在线支付
//          this.orderService.modifyOrderStatusOnPay(order, paytype);
            // 得到需要支付的价格，到付和 预付都会
            BigDecimal price = pay.getpOrderLog().getUsercost();
            // 预付
            if (paytype.trim().equals("1")) {
                orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_START_PAY.getId(), "", "选择支付类型："+OrderTypeEnum.YF.getName(), "");
                // 价格为0不用支付，直接修改本地状态
                if (price.doubleValue() <= 0) {
                    this.createPayPrice0(order);
                    Map<String, Object> resMap = new HashMap<String, Object>();
                    if(this.topay0(order, pay)){
                        resMap.put("success", true);
                        resMap.put("onlinepay", BigDecimal.ZERO);
                    }else{
                        resMap.put("success", false);
                        resMap.put("onlinepay", BigDecimal.ZERO);
                        resMap.put("errorcode", MyErrorEnum.payPrice0.getErrorCode());
                        resMap.put("errormsg", MyErrorEnum.payPrice0.getErrorMsg());
                    }
                    
                    
                    return  resMap; 
                }else{
                    if (Strings.isNullOrEmpty(onlinepaytype)) {
                        this.logger.error("创建订单支付，预付订单的预付类型为空，orderid=" + longorderId);
                        throw MyErrorEnum.errorParm.getMyException("预付支付类型为空");
                    }
                    String notify = AliPay.getUrl(request) + "pay/";
                    // 支付的内容
                    StringBuilder bodySB = new StringBuilder();
                    bodySB.append(order.getHotelName());// 酒店名
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    bodySB.append("(" + sf.format(order.getBeginTime()) + "-" + sf.format(order.getEndTime()) + ")");// 住店日期
                    // 微信支付
                    if (onlinepaytype.trim().equals("1")) {
                        this.logger.info("订单号：" + longorderId + "创建微信支付【未支付】，支付金额是：" + price);
                        this.logger.info("订单号：" + longorderId + "创建微信支付【未支付】，回调地址是：" + notify + "wxres");
                        return this.weixinpay(request, longorderId, price, bodySB.toString(), notify + "wxres");
                        // 支付宝
                    } else if (onlinepaytype.trim().equals("2")) {
                        this.logger.info("订单号：" + longorderId + "创建支付宝支付【未支付】，支付金额是：" + price);
                        this.logger.info("订单号：" + longorderId + "创建支付宝支付【未支付】，回调地址是：" + notify + "alires");
                        return this.alipay(longorderId, price, bodySB.toString(), notify + "alires");
                        // 网银
                    } else if (onlinepaytype.trim().equals("3")) {
                        throw MyErrorEnum.errorParm.getMyException("在线支付目前只支持微信和支付宝.");
                        // 其它
                    } else {
                        throw MyErrorEnum.errorParm.getMyException("在线支付目前只支持微信和支付宝.");
                    }
                }
                // 到付 //改状态
            } else if (paytype.trim().equals("2")) {
                orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_START_PAY.getId(), "", "选择支付类型："+OrderTypeEnum.PT.getName(), "");
                this.logger.info("订单号：" + longorderId + "创建到店支付，到店支付金额是：" + price);
                
                Map<String, Object> returnMap = toPay(order, pay, price);
                
                
                
                return returnMap;
            } else {
                throw MyErrorEnum.errorParm.getMyException("预付/到付类型有误.");
            }
        } else { // 错误直接返回false
            this.logger.info("paycreateByOrder::pay为null,orderid::{}", order.getId());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            return map;
        }
        
    }
    
	private Map<String, Object> wrapFailResult(MyErrorEnum errorEnum) {

		Map<String, Object> resMap = new HashMap<String, Object>();

		resMap.put("success", false);
		resMap.put("errorcode", errorEnum.getErrorCode());
		resMap.put("errormsg", errorEnum.getErrorMsg());

		return resMap;
	}
    
	public void pushMsg(Long orderId, String paytype) {

		try {
			
			OtaOrder order = this.orderService.findOtaOrderById(orderId);
			Message message = new Message();
			message.setPhone(order.getContactsPhone());
			message.setMid(order.getMid());
			message.setOrderId(orderId);
			
			if (StringUtils.isNotBlank(paytype)) {

				if (paytype.trim().equals("1")) {
					this.logger.info("订单:" + orderId + "预付订单推送消息.");
					message.setCopywriterTypeEnum(CopywriterTypeEnum.order_yf_create);
				} else if (paytype.trim().equals("2")) {
					this.logger.info("订单:" + orderId + "到付订单推送消息.");
					message.setCopywriterTypeEnum(CopywriterTypeEnum.order_pf_create);
				}
			} else {
				this.logger.info("订单:" + orderId + "退款推送消息.");
				message.setCopywriterTypeEnum(CopywriterTypeEnum.pay_yf_refund);
			}
			this.logger.info("订单:" + orderId +",sendSmsMsg:"+message.toString()+"开始");
			producer.sendSmsMsg(message);
			this.logger.info("订单:" + orderId +",sendSmsMsg:"+message.toString()+"完毕");
			this.logger.info("订单:" + orderId +",sendAppMsg:"+message.toString()+"开始");
			producer.sendAppMsg(message);
			this.logger.info("订单:" + orderId +",sendAppMsg:"+message.toString()+"完毕");
			this.logger.info("订单:" + orderId +",sendWeixinMsg:"+message.toString()+"开始");
			producer.sendWeixinMsg(message);
			this.logger.info("订单:" + orderId +",sendWeixinMsg:"+message.toString()+"完毕");

			} catch (Exception e) {
				this.logger.info("订单:" + orderId +",push消息异常。");
			}
			
	}

	public PPay paycreateByOrder(OtaOrder order, String couponNo, String promotionNo, String paytype, String onlinepaytype) {

		long orderId = order.getId();

		this.logger.info("订单:" + orderId + "=======创建支付订单开始======");
		long mid = order.getMid();
		this.logger.info("订单:" + orderId + ",用户:" + mid + ",码号:" + promotionNo + ",券号:" + couponNo);
		Optional<UMember> op = this.iMemberService.findMemberById(mid);
		if (!op.isPresent()) {

			this.logger.error("订单:" + orderId + "获取用户信息失败");
			throw MyErrorEnum.errorParm.getMyException("获取用户信息失败");
		}
//		List<?> alreadyTickets = this.uTicketDao.findUTicketsByOrderIdAndMid(order.getId(), mid);
		// 到店付订单不允许使用优惠券.  去掉到店付使用优惠券限制
//		boolean cond1 = OrderTypeEnum.PT.getId().intValue() == order.getOrderType();
//		boolean cond2 = (alreadyTickets != null) && (alreadyTickets.size() > 0);
//		if (cond1 && cond2) {
//			this.logger.error("订单:" + orderId + "到店付不允许使有优惠券");
//			throw MyErrorEnum.customError.getMyException("到店付不允许使有优惠券");
//		}
		UMember member = op.get();
		// 计算订单总金额
		BigDecimal allcost = this.caculateAllCost(order);
		this.logger.info("订单:" + orderId + "总金额:{}", allcost);
		// 创建流水信息
		PPay pay = this.getPayByOrder(order, member, allcost);
		// pay.setMember(member);
		this.logger.info("订单:" + orderId + "支付帐单明细:{}", pay.toString());
		// 把原来记录先删除掉
		this.ipPayInfoDao.deletePayInfoByPayid(pay.getId());
		// 解析切客券与议价券
		List<ITicketParse> promotionParses = this.getPromotionParses(order);
		List<PPayInfo> promotionPayInfos = this.savePromoPayInfosByParse(pay, promotionParses, order);
		this.logger.info("订单:" + orderId+ "创建系统优惠码流水:{}", promotionPayInfos);
		//添加使用 钱包支付的流水
		PPayInfo acpayinfo=addAccountCostPayinfo(pay,order.getAvailableMoney(),null);

		// 解析已绑定优惠券
		List<ITicketParse> ticketParses = this.getTicketParses(order, member.getMid());
		List<PPayInfo> ticketPayInfos = this.savePromoPayInfosByParse(pay, ticketParses, order);
		this.logger.info("订单:" + orderId + "创建普通优惠券流水:{}", ticketPayInfos);

		// 计算用户实际支付
		BigDecimal realCost = new BigDecimal(0);
		// 保存对账信息
		this.logger.info("订单:" + orderId + "保存porderlog流水:payid}" + pay.getId() + ",allcost:" + allcost + ",realCost:" + realCost + ",promotionPayInfos的长度:" + promotionPayInfos.size()
				+ ",promotionParses的 长度:" + promotionParses.size() + ",ticketParses的长度:" + ticketParses.size() + ",promotionParses的 长度:" + promotionParses.size());
		BigDecimal otaGive = this.saveOrderLog(pay, allcost, realCost, ticketParses, promotionParses,order.getAvailableMoney());
		// 更新info的realcost
		this.logger.info("订单:" + orderId + "otaGive:" + otaGive + ",ticketPayInfos的长度" + ticketPayInfos.size() + ",promotionPayInfos的长度" + promotionPayInfos);

		List<PPayInfo> countPayInfo = new ArrayList<PPayInfo>();
		if ((promotionPayInfos != null) && (promotionPayInfos.size() > 0)) {
			countPayInfo.addAll(promotionPayInfos);
		}
		if ((ticketPayInfos != null) && (ticketPayInfos.size() > 0)) {
			countPayInfo.addAll(ticketPayInfos);
		}
		
//		if ( acpayinfo != null ) {
//			countPayInfo.add(acpayinfo);
//		}
		if ((countPayInfo != null) && (countPayInfo.size() > 0)) {
			this.logger.info("订单:" + orderId + "PPayinfo中的realCost更新为otaGive开始");
			this.updatePPayInfoRealCost(countPayInfo, otaGive);
			this.logger.info("订单:" + orderId + "PPayinfo中的realCost更新为otaGive完毕");
		}
		this.logger.info("订单:" + orderId + "计算第三方支付价格:");

		// 更新流水详细信息
		if (ticketPayInfos != null) {
			pay.getPayInfos().addAll(ticketPayInfos);
		}
		if (promotionPayInfos != null) {
			pay.getPayInfos().addAll(promotionPayInfos);
		}
		// 到付的 把lezhu 置0
		if (paytype.equals("2")) {
			pay.setLezhu(BigDecimal.ZERO);
		}// 微信
			// 更新数据库交易流水信息
		this.saveOrUpdatePay(pay);
		this.logger.info("订单:" + orderId + "=======创建支付订单完成=======");
		return pay;
	}

	

    //增加使用用户钱包的 流水
    private PPayInfo addAccountCostPayinfo(PPay pay,BigDecimal cost,Long pmsSendId){
    	 logger.info("订单号："+pay.getOrderid()+"准备添加  【使用钱包】的流水,参数是 [payid]=" + pay.getId()+" [price]="+cost );
    	if(PayTools.isPositive(cost)){ //判断使用钱包金额不为空，金额大于0
    	  return addPPayinfo(pay,cost, PPayInfoTypeEnum.ACCOUNTCOST , pmsSendId);
    	}
    	return null;
    }
    
    /*
     * 添加支付流水
     */   
    private PPayInfo addPPayinfo(PPay pay, BigDecimal price,  PPayInfoTypeEnum type,Long pmsSendId) {
        logger.info("订单号："+pay.getOrderid()+"准备添加【使用钱包】流水,参数是" + pay+"  [price]="+price+"[pPayInfoTypeEnum]==(type="+type.getId()+") "+type.getName());
        PPayInfo pin = new PPayInfo();
        pin.setPay(pay);
        pin.setCost(price);
        pin.setEnable(true);
        pin.setTime(new Date());
        pin.setType(type);
        pin.setEnable(true);
        pin.setPmsSendId(pmsSendId);
        ipPayInfoDao.saveOrUpdate(pin);
        logger.info("订单号：" + pay.getOrderid() + "添加【使用钱包】流水完毕,id:" + pin.getId());
        if(pin.getId()!=null){
        	 return pin;
        }
        return null;       
    }
    
    
	
	
	
	
	
	
	/**
	 * 2015年7月27日下午5:17:56
	 *
	 * @param ticketPayInfos
	 * @param realotagive
	 */
	private void updatePPayInfoRealCost(List<PPayInfo> ticketPayInfos, BigDecimal otaGive) {

		PPayInfo payInfo = ticketPayInfos.get(0);
		payInfo.setRealCost(otaGive);

		this.ipPayInfoDao.saveOrUpdate(payInfo);
	}

	/**
	 * 到店里支付，算下要支付的金额【优惠券等优惠减去】
	 *
	 * @return BigDecimal 需要支付的价钱
	 */
	private Map<String, Object> toPay(OtaOrder order, PPay pay, BigDecimal price) {
		Map<String, Object> map = new HashMap<String, Object>();
		this.orderService.modifyOrderTypePT(order);
		// 到店住上了才调用，这里先不调用
		// payService.pmsAddpay(order, pay.getId(), pay.getMember().getName() );
		// 更改状态 是否正确
		map.put("success", true);
		map.put("orderid", order.getId());
		map.put("info", "到店支付" + price + "");
		return map;
	}

	/**
	 * 微信支付
	 *
	 * @author xiaofutao
	 * @param orderid
	 *            订单id, 必填
	 * @param amount支付金额
	 *            , 必填
	 * @return jeson data
	 */
	private Map<String, Object> weixinpay(HttpServletRequest request, long orderid, BigDecimal amount, String body, String notifyUrl) {
		this.logger.info("微信支付前，传递需要的信息参数，orderid=" + orderid);
		String token = request.getParameter("token");
		this.logger.info("订单:" + orderid + "微信支付前，传递需要的信息参数，token=" + token);
		// 判断是否是微信公众帐号
		boolean iswechat = false;
		if (token != null) {
			UToken ut = MyTokenUtils.getToken(token);
			if ((ut != null) && (ut.getType().getId().intValue() == TokenTypeEnum.WX.getId().intValue())) {
				this.logger.info("订单:" + orderid + "是微信公共帐号来的请求,不需要ots支付.");
				iswechat = true;
			}
		}
		Map<String, Object> resmap = new HashMap<String, Object>();
		resmap.put("success", true);
		resmap.put("onlinepay", amount);
		// 如果是第三方公众帐号的支付，就不用返回微信的信息
		if (!iswechat) {
			Map<String, String> map = null;
			try {
				// 把价格设置成【分】
				int price = PayTools.get100price(amount);
				// map = AppPay.apppay(orderid, body, notifyUrl, price+"");
				map = AppPay.pay(request, orderid + "", body, notifyUrl, price);
			} catch (Exception e) {
				this.logger.error(e.getMessage(), e);
			}
			resmap.put("weinxinpay", map);
		}
		return resmap;
	}

	private Map<String, Object> alipay(long orderid, BigDecimal amount, String body, String notifyurl) {
		this.logger.info("支付宝支付前，传递需要的信息参数，orderid=" + orderid);
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("success", true);
		resMap.put("onlinepay", amount);
		Map<String, String> map = new HashMap<String, String>();
		map.put("alipayselleremail", SysConfig.aliPayMerchantEmail); // 商家支付宝email
		map.put("alipaypartner", SysConfig.getInstance().getSysValueByKey(Constant.aliPaypartner)); // 支付宝合作者id
		map.put("alipaykey", SysConfig.getInstance().getSysValueByKey(Constant.aliPaykey)); // 支付宝商户私钥
		map.put("alipaynotifyurl", notifyurl); // 支付宝异步调用url
		map.put("body", body); // 支付的内容
		map.put("price", amount + "");
		resMap.put("alipay", map);
		return resMap;
	}

	// 【付款金额小于等于0才会调用（优惠券减完了）】
	private boolean topay0(OtaOrder order, PPay pay) {
		this.logger.info("不用支付的订单【优惠券金额大于支付金额的订单】，orderid=" + order.getId());
		try {
			// 给老板发短信
			this.createJob4SendMsg(order);
		} catch (Exception e) {
			this.logger.error(" 给老板发送短信失败：" + e.getMessage(), e);
		}
		this.logger.info("发送短信结束，继续下面的操作 ");
		try {
			this.logger.info("发送短信完成， " + order.getId() + "  payid==" + pay.getId());
		} catch (Exception e) {
			this.logger.error(" 发送短信已经----》完成,出错" + e.getMessage(), e);
		}
		// this.logger.info("获取数据  " + pay.toString());
		// 修改p_pay 信息,增加流水
		
		logger.info("订单:" + order.getId() + "预付订单无需支付,通知订单系统.");
		
		walletCashflowService.confirmCashFlow(order.getMid(),order.getId());
		this.logger.info("订单号：" + order.getId() + ",调用红包接口，mid："+order.getMid()+",订单:"+order.getId());
		
		this.logger.info("不需要支付的订单【优惠券金额大于支付金额的订单】准备去pms ，:orderid=" + order.getId());
		this.orderService.modifyPmsOrderStatusAfterPay(order);
		// 调用pms 增加给酒店结算的钱
		this.logger.info("不需要支付的订单【优惠券金额大于支付金额的订单】pms处理完成 ，:orderid=" + order.getId());
		PPayInfo payinfo = this.ipPayInfoDao.getPPayInfo(pay.getId(), PPayInfoTypeEnum.Y2P);
		
		if(payinfo == null) {
			payinfo = this.ipPayInfoDao.getPPayInfo(pay.getId(), PPayInfoTypeEnum.ACCOUNTCOST);
		}
		
		if ((payinfo != null) && this.isNot0AndNull(payinfo.getCost())) {
			this.ipPayInfoDao.updatePmsSendIdByPayId(pay.getId(), payinfo.getId());
			this.pmsAddpayOk(order, PayStatusEnum.alreadyPay, false, payinfo.getId());
		}
		this.logger.info("支付完成回调，【优惠券金额大于支付金额的订单】，处理成功，orderid=" + order.getId());
		return true;

	}

	// 【付成功才会调用】
	// paytype 支付类型,必填： 1、微信 2、支付宝 3、网银 4、其他
	// price 支付了多少钱
	@Override
	public Boolean payresponse(Long longorderid, String bankpayid, String price, PPayInfoOtherTypeEnum payinfotype, 
			String userId) {
		this.logger.info("支付完成回调，确认已经支付成功，开始数据库操作，orderid=" + longorderid);
		if(StringUtils.isNotEmpty(userId)) {
			logger.info("订单:" + longorderid + "记录用户第三方支付标识.");
			try {
				iPayDAO.updateUserIdByOrderId(longorderid, userId);
			} catch (Throwable e) {
				logger.error("订单:" + longorderid + "记录用户第三方支付标识异常!", e);
			}
		}
		//尝试从支付状态异常表删除数据
		deletePayStatusError(longorderid);
		OtaOrder order = this.orderService.findOtaOrderById(longorderid);
		if(order.getOrderType() != OrderTypeEnum.YF.getId().intValue()) {
			orderService.changeOrderStatusByPay(longorderid, null, null, OrderTypeEnum.YF);
		}
		PPay pay = this.findPayByOrderId(longorderid);
		this.logger.info("支付完成回调，判断订单是否被取消，orderid=" + longorderid);
		if (order.getOrderStatus() >= OtaOrderStatusEnum.CancelByU_WaitRefund.getId().intValue()) {
			this.logger.info("支付完成回调，判断订单已经被取消，orderid=" + longorderid + "但是客户又付款成功，马上给客户退款");
			order.setPayStatus(PayStatusEnum.alreadyPay.getId());
			this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.BANK_PAY_SUCCESS.getId(), "", OtaOrderFlagEnum.BANK_PAY_SUCCESS.getName() + ",支付金额是：" + price, "");
			List<PPayInfo> payinfolist = this.ipPayInfoDao.findByPayId(pay.getId());
			PPayInfo payinfobypay = this.addPPayinfoByPay(pay, new BigDecimal(price), bankpayid, PPayInfoTypeEnum.Z2P, payinfotype);
			if (payinfobypay != null) {
				payinfolist.add(payinfobypay);
				if (this.bankRefund(longorderid, pay, payinfolist)) { // 退款成功

					this.pushMsg(longorderid, null);

					order.setPayStatus(PayStatusEnum.refundPay.getId());
					PPayInfo promotionPayinfo = this.getPromotionPayInfo(payinfolist);
					if (promotionPayinfo != null) {
						this.logger.info("支付完成回调，判断订单已经被取消，orderid=" + longorderid + "平优惠券的 payinfo");
						this.addPPayinfo(pay, promotionPayinfo.getCost().negate(), null, PPayInfoTypeEnum.Y2U, null);
					}
					this.logger.info("支付完成回调，判断订单已经被取消，orderid=" + longorderid + "添加 BusinessLog ");
					this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.BANK_REFUND_SUCCESS.getId(), "", OtaOrderFlagEnum.BANK_REFUND_SUCCESS.getName() + ",支付金额是：" + price, "");
					order.update();
					return null;
				}
			}
			order.update();
			return null;
		} else {// 订单还有效
			this.logger.info("支付完成回调，判断订单未被取消，orderid=" + longorderid);
			this.orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.BANK_PAY_SUCCESS.getId(), "", OtaOrderFlagEnum.BANK_PAY_SUCCESS.getName() + ",支付金额是：" + price, "");
			try {
				// 给老板发短信
				this.createJob4SendMsg(order);
			} catch (Exception e) {
				this.logger.error(" 给老板发送短信失败：orderid=" + longorderid + "  " + e.getMessage(), e);
			}
			this.logger.info("支付完成回调，确认已经支付成功，order====" + order.toString());
			
			logger.info("订单:" + longorderid + "支付完成,通知订单系统.");
			
			this.pushMsg(longorderid, "1");

			UMember member = this.getMemberByMId(order.getMid());
			pay.setMember(member);
			this.logger.info("获取数据  " + pay.toString());
			BigDecimal p = BigDecimal.ZERO;
			try {
				p = new BigDecimal(price);
			} catch (NumberFormatException e1) {
				throw MyErrorEnum.errorParm.getMyException("支付的钱只能是数字");
			}
			
			this.logger.info("支付完成回调，已更新RealOtagive，orderid=" + longorderid);
			this.orderService.modifyPmsOrderStatusAfterPay(order);
			this.logger.info("支付完成回调，确认已经支付成功，modifyPmsOrderStatusAfterPay::orderid=" + longorderid);
			Long payinfoid = this.payAfterUpdateStatus(longorderid, pay, p, payinfotype, bankpayid, true);
			this.logger.info("订单号：" + longorderid + "支付完成，添加 p_payinfo  payinfoid=" + payinfoid);
			
			walletCashflowService.confirmCashFlow(order.getMid(),longorderid);
			this.logger.info("订单号：" + longorderid + ",调用红包接口，mid："+order.getMid()+",订单:"+longorderid);

			//支付成功调用订单判断是否符合切客收益规则 
			orderService.callChangeOrderStatusByPmsIN(order);
			
			// 修改p_pay 信息,增加流水
			if (payinfoid != null) {
				this.ipPayInfoDao.updatePmsSendIdByPayId(pay.getId(), payinfoid);
				// 调用pms 增加给酒店结算的钱
				this.pmsAddpayOk(order, PayStatusEnum.alreadyPay, true, payinfoid);
				this.logger.info("支付完成回调，确认已经支付成功，处理成功，orderid=" + longorderid);
				return true;
			} else {
				this.logger.info("支付完成回调，确认已经支付成功，处理失败，orderid=" + longorderid);
				return false;
			}
		}
	}

	/** 得到用 */
	private PPayInfo getPromotionPayInfo(List<PPayInfo> payinfolist) {
		if (payinfolist != null) {
			for (PPayInfo pp : payinfolist) {
				if (pp.getType().getId().intValue() == PPayInfoTypeEnum.Y2P.getId().intValue()) {
					return pp;
				}
			}
		}
		return null;
	}

	@Override
	public ManualLuzhuRstEnum pmspay(Long orderid, BigDecimal lezhu, String operateName) {
		this.logger.info("订单:{}, 手动下发乐住币, 开始....", orderid);

		ManualLuzhuRstEnum result = this.addpay(orderid, lezhu, operateName);
		// 日志记录
//		this.iPPmsPayLogDao.logpay(orderid, lezhu, reason, operatorid);
		this.logger.info("订单:{}, 手动下发乐住币, 完成.", orderid);

		return result;
	}
    
    private UMember getMemberByMId(Long mId)  {
        Optional<UMember> op = this.iMemberService.findMemberById(mId);
        if (!op.isPresent()) {
            throw MyErrorEnum.memberNotExist.getMyException();
        }
        UMember member = op.get();
        if(member == null) {
            throw MyErrorEnum.customError.getMyException("未获取到用户信息,mid:" + mId);
        }
        return member;
    }
    
    public ManualLuzhuRstEnum addpay(Long orderid, BigDecimal price, String operateName) {
    	logger.info("手动下发乐住币，订单:" + orderid + "，价格:"+price+",operateName:"+operateName);
    	boolean doRetry = false;
    	boolean isCancel = false;
        OtaOrder order = orderService.findOtaOrderById(orderid);
        if (order == null) {
            logger.error("订单:" + orderid + "不存在!");
            return ManualLuzhuRstEnum.orderNoExist;
        }
        PPay pay = iPayDAO.getPayByOrderId(order.getId());
        if(pay == null) {
            logger.error("订单:" + orderid + "未获取到对应支付单!");
            return ManualLuzhuRstEnum.payNoExist;
        }
        logger.info("订单:" + orderid + "订单状态为:" + order.getOrderStatus());
        UMember member=getMemberByMId(order.getMid());
        POrderLog orderLog = ipOrderLogDao.findPOrderLogByPay(pay.getId());
        logger.info("手动下发乐住币，订单:" + orderid + ",member:"+member.getId()+",orderLog:"+orderLog.getId());
        PPayInfoTypeEnum pPayinfoType;
        Long pPayinfoId = null;
        if(price == null) {
            // 当乐住币数量为空时 取消已下发乐住币
        	if(orderLog.getPmsrefund() == PmsSendEnum.ResponseSuccess) {
        		logger.info("订单:" + orderid + "已经回收过乐住币,不能再操作整单回收.");
        		return ManualLuzhuRstEnum.cantCancel;
        	}
            logger.info("订单:" + orderid + "进行取消乐住币操作.");
            BigDecimal refundPrice = calculateRefundLeZhu(order, orderLog, PPayInfoTypeEnum.Y2U).negate();
            isCancel = true;
            if(!isNot0AndNull(orderLog.getRealotagive())) {
            	logger.info("订单:" + orderid + "没有使用优惠券.");
            	if (order.getOrderType() == OrderTypeEnum.PT.getId()){
            		logger.info("订单:" + orderid + "是到付订单且没有优惠券,不需要退乐住币.");
            		return ManualLuzhuRstEnum.dontReTry;
            	} else if(order.getOrderType() == OrderTypeEnum.YF.getId()) {
            		
            		logger.info("订单:" + orderid + "是预付订单且没有优惠券,只需要退支付金额乐住币:" + refundPrice);
            		price = refundPrice;
            		pPayinfoId = System.currentTimeMillis();
            	}
            } else { //有补贴
            	logger.info("订单:" + orderid + "使用了优惠券.");
            	price = refundPrice;
            	pPayinfoId = addPPayinfo(pay, orderLog.getRealotagive().negate(), null, PPayInfoTypeEnum.Y2U, null);
            }
			if (PayTools.isPositive(orderLog.getRealaccountcost())) {
				addPPayinfo(pay, orderLog.getRealaccountcost().negate(),PPayInfoTypeEnum.ACCOUNTCOSTREFUND, pPayinfoId);
			}
        } else {
            //乐住币为正数 下发乐住币
            if(price.compareTo(new BigDecimal(0)) == 1) {
            	logger.info("手动下发乐住币，订单:" + orderid + "，price为正数");
            	
                List<PPayInfo> infos = ipPayInfoDao.findByPayId(pay.getId());
                if (order.getOrderType() == OrderTypeEnum.PT.getId()) {
                    if (doSended(infos) && orderLog.getPmssend() != PmsSendEnum.ResponseSuccess) {// 之前正常的下发乐住币失败
                    	if(payTaskDao.exist(orderid, PayTaskTypeEnum.AUTORETRYSENDLEZHU)) {
                    		logger.info("订单:" + orderid + "已经存在自动重试下发乐住币任务.");
                    		return ManualLuzhuRstEnum.autoReTry;
                    	}
                        logger.info("订单:" + orderid + "进行下发(重试)乐住币操作.");
                        pPayinfoId = getSendedLeZhu(infos, PPayInfoTypeEnum.Y2P).getPmsSendId();
                        logger.info("手动下发乐住币，订单:" + orderid + "，price为正数，PT，之前正常的下发乐住币失败，pPayinfoId"+pPayinfoId);
                        doRetry = true;
                    } else {// 之前正常的下发乐住币成功
                        logger.info("订单:" + orderid + "进行下发(增发)乐住币操作.");
                        pPayinfoType = PPayInfoTypeEnum.Y2P;
                        //TODO  ？？
                        pPayinfoId = addPPayinfo(pay, price, null, pPayinfoType, null);
                        logger.info("手动下发乐住币，订单:" + orderid + "，price为正数，PT，之前正常的下发乐住币成功，pPayinfoId"+pPayinfoId);
                    }
                } else if (order.getOrderType() == OrderTypeEnum.YF.getId()) {
                    if (orderLog.getPmssend() != PmsSendEnum.ResponseSuccess) {// 之前正常的下发乐住币失败
                    	if(payTaskDao.exist(orderid, PayTaskTypeEnum.AUTORETRYSENDLEZHU)) {
                    		logger.info("订单:" + orderid + "已经存在自动重试下发乐住币任务.");
                    		return ManualLuzhuRstEnum.autoReTry;
                    	}
                        logger.info("订单:" + orderid + "进行下发(重试)乐住币操作.");
                        pPayinfoId = getSendedLeZhu(infos, PPayInfoTypeEnum.Z2P).getPmsSendId();
                        logger.info("手动下发乐住币，订单:" + orderid + "，price为正数，YT，之前正常的下发乐住币失败，pPayinfoId"+pPayinfoId);
                        doRetry = true;
                    } else {// 之前正常的下发乐住币成功
                        logger.info("订单:" + orderid + "进行下发(增发)乐住币操作.");
                        pPayinfoType = PPayInfoTypeEnum.Y2P;
                        //TODO ？？
                        pPayinfoId = addPPayinfo(pay, price, null, pPayinfoType, null);
                        logger.info("手动下发乐住币，订单:" + orderid + "，price为正数，YT，之前正常的下发乐住币成功，pPayinfoId"+pPayinfoId);
                    }
                }
            	if (PayTools.isPositive(orderLog.getRealaccountcost())) {
    				addPPayinfo(pay, orderLog.getRealaccountcost(),PPayInfoTypeEnum.ACCOUNTCOST, pPayinfoId);
    			}
            } else {//乐住币为负数 取消传入乐住币金额
                logger.info("订单:" + orderid + "进行取消乐住币操作.");
                pPayinfoType = PPayInfoTypeEnum.Y2U;
                pPayinfoId = addPPayinfo(pay, price, null, pPayinfoType,null);
            	if (PayTools.isPositive(orderLog.getRealaccountcost())) {
    				addPPayinfo(pay, orderLog.getRealaccountcost().negate(),PPayInfoTypeEnum.ACCOUNTCOSTREFUND, pPayinfoId);
    			}
                logger.info("手动下发乐住币，订单:" + orderid + "，乐住币为负数 取消传入乐住币金额，pPayinfoId"+pPayinfoId);
            }
        }
        logger.info("手动下发乐住币，订单:" + orderid + "，检测pPayinfoId"+pPayinfoId);
        if (pPayinfoId != null) {
        	if((price.compareTo(new BigDecimal(0)) == -1) && isNewPMS(order)) {
        		logger.info("订单:" + orderid + "调用PMS2.0取消乐住币...");
        		PMSResponse response = null;
        		try {
        			String request = wrapPMSRequest(order, pPayinfoId, price.negate(), "cancelpay");
        			logger.info("订单:" + orderid + "调用PMS2.0,URL[" + PMS_PAY_CANCEL_URL + "]参数[" + request + "].");
        			String responseString = PayTools.dopostjson(PMS_PAY_CANCEL_URL, request);
        			response = JSON.parseObject(responseString, PMSResponse.class);
        		} catch (Exception e) {
        			logger.error("订单:" + orderid + "调用PMS2.0取消乐住币异常!", e);
        			process4CancelPMSV2Fail(orderLog, order, e.getMessage());
        			ipPayInfoDao.deleteById(pPayinfoId);
        			return ManualLuzhuRstEnum.canReTry;
        		}
        		if (response.isSuccess()) {
        			logger.info("订单:" + orderid + "调用PMS2.0取消乐住币成功.");
        			orderBusinessLogService.saveLog1(order, OtaOrderFlagEnum.CANCELPMSPAY.getId(), "", "取消乐住币成功,金额:" + price.negate().floatValue(), "", operateName);
       			 	this.logger.info("订单:" + orderid + "取消乐住币成功,平账...");
       			 	if (isCancel) {
//						orderLog.setOtagive(BigDecimal.ZERO);
						orderLog.setRealotagive(BigDecimal.ZERO);
						orderLog.setRealaccountcost(BigDecimal.ZERO);
						orderLog.setRealallcost(BigDecimal.ZERO);
						orderLog.setRealcost(BigDecimal.ZERO);
					} else {
						orderLog.setOtagive(orderLog.getOtagive().add(price));
						orderLog.setRealotagive(orderLog.getRealotagive().add(price));
						
					}
					orderLog.setPmsrefund(PmsSendEnum.ResponseSuccess);
                    ipOrderLogDao.update(orderLog);
                    this.logger.info("订单:" + orderid + "更新orderLog成功.");
                    ipPayInfoDao.updatePmsSendIdById(pPayinfoId, pPayinfoId);
                    this.logger.info("订单:" + orderid + "更新ppayinfo成功.");
                    this.logger.info("订单:" + orderid + "平账完成.");
        			return ManualLuzhuRstEnum.success;
        		} else {
        			logger.error("订单:" + orderid + "调用PMS2.0取消乐住币失败, errorCode[" + response.getErrorcode() + "],errorMsg[" + response.getErrormsg() + "].");
        			if(order.getOrderStatus() >= OtaOrderStatusEnum.Account.getId().intValue()) {
        				if (isCancel) {
//    						orderLog.setOtagive(BigDecimal.ZERO);
        					orderLog.setRealotagive(BigDecimal.ZERO);
    						orderLog.setRealaccountcost(BigDecimal.ZERO);
    						orderLog.setRealallcost(BigDecimal.ZERO);
    						orderLog.setRealcost(BigDecimal.ZERO);
    					} else {
    						orderLog.setOtagive(orderLog.getOtagive().add(price));
    						orderLog.setRealotagive(orderLog.getRealotagive().add(price));
    					}
    					orderLog.setPmsrefund(PmsSendEnum.AlreadyNoneResponse);
                        ipOrderLogDao.update(orderLog);
                        orderBusinessLogService.saveLog1(order, OtaOrderFlagEnum.CANCELPMSPAY.getId(), "", "平账PMS异常,金额:" + price.negate().floatValue(), response.getErrormsg(), operateName);
                        logger.info("订单:" + orderid + "已经是离店/取消状态,无法操作乐住币,保存账目数据.");
                        return ManualLuzhuRstEnum.cantReTry;
        			}
        			process4CancelPMSV2Fail(orderLog, order, response.getErrormsg());
        			ipPayInfoDao.deleteById(pPayinfoId);
        			return ManualLuzhuRstEnum.canReTry;
        		}
        	}
        	boolean result = pmsAddpay(order,pay.getId(), pPayinfoId, price, member.getName(), operateName);
            if (!result) {
            	if(order.getOrderStatus() >= OtaOrderStatusEnum.Account.getId().intValue()) {
            		logger.info("手动下发乐住币，订单:" + orderid + "，order.getOrderStatus:"+order.getOrderStatus());
                	if (!doRetry) {
						orderLog.setOtagive(orderLog.getOtagive().add(price));
						orderLog.setRealotagive(orderLog.getRealotagive().add(price));
					}
					if (price.compareTo(new BigDecimal(0)) == 1) {
                        orderLog.setPmssend(PmsSendEnum.AlreadyNoneResponse);
                    } else {
                    	if (isCancel) {
//    						orderLog.setOtagive(BigDecimal.ZERO);
    						orderLog.setRealotagive(BigDecimal.ZERO);
    						orderLog.setRealaccountcost(BigDecimal.ZERO);
    						orderLog.setRealallcost(BigDecimal.ZERO);
    						orderLog.setRealcost(BigDecimal.ZERO);
    					}
                        orderLog.setPmsrefund(PmsSendEnum.AlreadyNoneResponse);
                    }
					logger.info("手动下发乐住币，订单:" + orderid + ",doRetry:"+doRetry+"，Otagive:"+orderLog.getOtagive()+"，Realotagive："+orderLog.getRealotagive());
                    ipOrderLogDao.update(orderLog);
                    logger.info("订单:" + orderid + "已经是离店/取消状态,无法操作乐住币,保存账目数据.");
                    return ManualLuzhuRstEnum.cantReTry;
                }
            	logger.info("订单:" + orderid + "调用pms操作乐住币失败,回滚清除已添加账目数据.");
                throw MyErrorEnum.customError.getMyException("手动下发/取消乐住币流程调用PMS失败,回滚!");

            } else {//调用pms成功,更新orderLog数据

            	if (!doRetry) {
					orderLog.setOtagive(orderLog.getOtagive().add(price));
					orderLog.setRealotagive(orderLog.getRealotagive().add(price));
				}
                if (price.compareTo(new BigDecimal(0)) == 1) {
                    orderLog.setPmssend(PmsSendEnum.ResponseSuccess);
                } else {
                	if (isCancel) {
//						orderLog.setOtagive(BigDecimal.ZERO);
                		orderLog.setRealotagive(BigDecimal.ZERO);
						orderLog.setRealaccountcost(BigDecimal.ZERO);
						orderLog.setRealallcost(BigDecimal.ZERO);
						orderLog.setRealcost(BigDecimal.ZERO);
					}
                    orderLog.setPmsrefund(PmsSendEnum.ResponseSuccess);
                }
            	logger.info("手动下发乐住币，订单:" + orderid +",doRetry:"+doRetry+ "，Otagive:"+orderLog.getOtagive()+"，Realotagive："+orderLog.getRealotagive());
                ipOrderLogDao.update(orderLog);
                ipPayInfoDao.updatePmsSendIdById(pPayinfoId, pPayinfoId);
                logger.info("订单:" + orderid + "调用pms操作乐住币成功,已添加账目数据.");
                return ManualLuzhuRstEnum.success;
            }
        }
        logger.info("订单:" + orderid + "操作完成");
        return ManualLuzhuRstEnum.success;
    }
    
    
    private void createJob4SendMsg(OtaOrder order) {
    	
    	sendMsg2Sale(order);
    	
    	Long result = payTaskDao.insertPayTask(buildPayTask(order.getId(), PayTaskStatusEnum.INIT, PayTaskTypeEnum.SENDMSG2LANDLORD));
    	
    	logger.info("订单:" + order.getId() + "保存任务[" + PayTaskTypeEnum.SENDMSG2LANDLORD.name() + "]返回值:" + result);
    }

	private PPayTask buildPayTask(Long orderId, String content, PayTaskStatusEnum status, PayTaskTypeEnum type) {
		
		PPayTask task = new PPayTask();
		
		task.setOrderId(orderId);
		task.setContent(content);
		task.setStatus(status);
		task.setTaskType(type);
		
		return task;
	}
	
	private PPayTask buildPayTask(Long orderId, PayTaskStatusEnum status, PayTaskTypeEnum type) {
		
		PPayTask task = new PPayTask();
		
		task.setOrderId(orderId);
		task.setStatus(status);
		task.setTaskType(type);
		
		return task;
	}
    
    //支付成功给酒店老板发短信
	public Boolean sendMsg(OtaOrder order){
        
		Long orderId = order.getId();
		
        logger.info("orderid="+orderId+" 准备给老板发送短信");
        
        OtaRoomOrder roomOrder=orderService.findOtaRoomOrderByOrderId(orderId);
        if(roomOrder!=null){
            HGroup hg=hgroupDao.getByHotelId(roomOrder.getHotelId());
            OtaCheckInUser user=checkInUserDAO.findOtaCheckInUser(roomOrder.getId());
            if(hg!=null && user!=null){
                String phone=hg.getRegphone();
                String  msgContent="客人"+user.getName()+"("+roomOrder.getContactsPhone()+")"+"预定您"+roomOrder.getHotelName()+roomOrder.getRoomNo()+"号房,"+getTime(order.getBeginTime())+"入住,住"+order.getDaynumber()+"天,全部房费已支付,请做好接待,谢谢!";
                logger.info("orderid="+orderId+" 准备给老板发送短信，发送号码："+phone+" 发送内容是："+msgContent);
                //2. 发送短信
                Long msgid = iMessageService.logsms(phone, msgContent, MessageTypeEnum.normal, "", "", null, null);
                Boolean b=iMessageService.sendMsg(msgid, phone,  msgContent, MessageTypeEnum.normal, "");
                logger.info("orderid="+orderId+"发送短信的结果是  Boolean=="+b);
                return  b;
            }else{
                logger.info("orderid="+orderId+" 准备给老板发送短信，发送短信参数不全，参数是：HGroup="+hg+" OtaCheckInUser="+user);

                return false;
            }
        } else {
            this.logger.info("orderid=" + orderId + " 准备给老板发送短信，发送短信参数不全，OtaRoomOrder 未查询到数据");
            return false;
        }
        
    }

    
    /* 
     * 通知BMS
     * 
     * @param order
     */
    private void sendMsg2Sale(OtaOrder order) {

        long orderId = order.getId();

        this.logger.info("订单:" + orderId + "给销售/区总发送通知...");

        try {

            String response = PayTools.dopost(PayService.INFORM_SALE_URL + orderId, "");

            this.logger.info("订单:" + orderId + "给销售/区总发送通知的返回信息=>" + response);

        } catch (Exception e) {
            
            logger.error("订单:" + orderId + "给销售/区总发送通知异常!", e);
        } 
    }
    
    private String getTime(Date d){
        String str="";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            str = sdf.format(d);
        } catch (Exception e) {
            this.logger.error(e + e.getMessage());
        }
        return str;
    }

    @Override
    public boolean superRefund(Long orderid) {
        boolean b = false;
        OtaOrder order = this.orderService.findOtaOrderById(orderid);
        if (order == null) {
            throw MyErrorEnum.customError.getMyException("订单号不存在");
        }
        PPay pay = findPayByOrderId(orderid);
        if (pay == null ) {

            throw MyErrorEnum.customError.getMyException("订单未找到");
        }
        // 看是否已经退过款
        PPayInfo rinfo = this.ipPayInfoDao.getPPayInfoByRefund(pay.getId());
        if (rinfo != null) {
            b = true;
        } else {
            try {
                pay=cancelPay(order,pay);
            	NeedReturnEnum nr=pay.getNeedreturn();
            	if(nr==NeedReturnEnum.no || nr==NeedReturnEnum.ok || nr==NeedReturnEnum.finish ){
            		b = true;
            	}
            } catch (Exception e) {
            	
            	logger.error("订单:" + orderid + "退款流程异常!", e );
            }
        }
        return b;
    }

    
    
    private PPay cancelPay(OtaOrder order,PPay ppay ) {
        long orderId = order.getId();
        logger.info("订单:" + orderId + "创建取消支付.");
        if (ppay == null) {
            logger.info("订单:" + orderId + "未找到支付单.");
            return null;
        }
        this.logger.info("订单:" + orderId + "对应的支付单:{}", ppay.toString());
        List<PPayInfo> payinfolist = this.ipPayInfoDao.findByPayId(ppay.getId());
        if (doRefund(payinfolist)) {
            logger.info("订单:" + orderId + "已经退过款.");
            if (ppay.getNeedreturn() != NeedReturnEnum.no) {
                updatePTAndNR(ppay, NeedReturnEnum.no);
            }
            return ppay;
        }
        POrderLog orderlog = this.ipOrderLogDao.findPOrderLogByPay(ppay.getId());
        // 预付
        if (order.getOrderType() == OrderTypeEnum.YF.getId()) {
            long m = 0;
            try {
            	//这儿因为用Usercost，因为先取消乐住币，realcost0，所以退钱时用了usercost
                m = orderlog.getUsercost().longValue();
            } catch (Exception e) {
            }
            if (m <= 0) {
                this.logger.info("订单:" + orderId + "是不需要用户付款的订单.");
                // 修改 Ppay
                updatePTAndNR(ppay, NeedReturnEnum.ok);
            } else {
                if (doPayed(payinfolist)) {
                    logger.info("订单:" + orderId + "为已经支付的订单，payid:{}", ppay.getId()+"下一步准备去银行退款");
                    // 支付成功后保存了一条 流水，里面有 支付宝或者微信返回的支付id
                    // ppay.setPayInfos(payinfolist);
                    if (bankRefund(orderId, ppay, payinfolist)) { // 退款成功
                    	
                    	pushMsg(orderId, null);
                    	
                        orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.BANK_REFUND_SUCCESS.getId(), "", OtaOrderFlagEnum.BANK_REFUND_SUCCESS.getName()+",退款金额是："+m,"");
                        // 退款成功 ，更改状态
                        orderService.changeOrderStatusByPay(order.getId(), OtaOrderStatusEnum.CancelByU_Refunded, PayStatusEnum.refundPay, null);
                        updatePTAndNR(ppay, NeedReturnEnum.no);
                        // 把OrderLog refund都改为T
                        ipOrderLogDao.updateByPayRefund(ppay.getId());
                    } else { // 退款失败的
                        // 把状态改为需要人工参与退款
                        this.logger.info("订单:" + order.getId() + "退款失败,须人工参与退款.");
                        updatePTAndNR(ppay, NeedReturnEnum.need);
                    }
                } else {
                    logger.info("订单:" + orderId + "为未支付的预付订单,无需退款.");
                    updatePTAndNR(ppay, NeedReturnEnum.ok);
                }
            }
        } else if (order.getOrderType() == OrderTypeEnum.PT.getId()) {
            logger.info("订单号" + order.getId() + "是到付订单,无需退款.");
            updatePTAndNR(ppay, NeedReturnEnum.ok);
        }
        return ppay;
    }
    
    //如果有平银行的账,返回平账流水
    private PPayInfo getBankRefundInfo(List<PPayInfo> payinfolist) {
        if (payinfolist != null) {
            for (PPayInfo pp : payinfolist) {
                if (pp.getType().getId().intValue() == PPayInfoTypeEnum.Z2U.getId().intValue()) {
                    return pp;
                }
            }
        }
        return null;
    }
    
    
    // 银行退款,增加退款流水
    private boolean bankRefund(Long orderid, PPay pay,List<PPayInfo> payinfolist ) {
        PPayInfo payinfo=getPayedPayinfo(payinfolist);
        PPayInfo bankRefundinfo=getBankRefundInfo(payinfolist);
        PPayInfoOtherTypeEnum bankType = payinfo.getOthertype();
        this.logger.info("订单号:" + pay.getOrderid() + "支付流水:" + payinfo);
        String bankPayId = payinfo.getOtherno();
        logger.info("订单:" + orderid + "支付类型:" + bankType + ",交易号:" + bankPayId);
        String bankRefundId = null;
        // 支付宝退款
        BigDecimal refundPrice = payinfo.getCost();
        if (bankType == PPayInfoOtherTypeEnum.alipay) {
            logger.info("订单:" + orderid + "为支付宝支付.");
            bankRefundId = AliPay.refund(orderid + "", bankPayId, refundPrice + "");
            logger.info("订单:" + orderid + "去支付宝退款交易号:" + bankRefundId);
            // 如果退过款,则退款失败,走一个退款查询
            if (bankRefundId == null && AliPay.refundQuery(orderid + "")) {
                logger.info("订单:" + orderid + "之前在支付宝退过款.");
                bankRefundId = bankPayId;
            }
            // 微信公众账号退款
        } else if (bankType == PPayInfoOtherTypeEnum.wechatpay ) {
            logger.info("订单:" + orderid + "为微信公共账号支付.");
            int m = PayTools.get100price(refundPrice);
            if (m > 0) {
                bankRefundId = WeChat.refund(bankPayId, orderid + "", m);
                logger.info("订单:" + orderid + "去微信公共账号退款交易号:" + bankRefundId);
                // 如果退过款,则退款失败,走一个退款查询
                if (bankRefundId == null && WeChat.refundQuery(bankPayId)) {
                    logger.info("订单:" + orderid + "之前在微信公共账号退过款.");
                    bankRefundId = bankPayId;
                }
            }
            //APP微信支付退款
        } else if (bankType == PPayInfoOtherTypeEnum.wxpay ) {
            logger.info("订单:" + orderid + "为App经微信支付.");
            int m = PayTools.get100price(refundPrice);
            if (m > 0) {
                bankRefundId = AppPay.refund(bankPayId, orderid + "", m);
                logger.info("订单:" + orderid + "App去微信退款交易号:" + bankRefundId);
                // 如果退过款,则退款失败,走一个退款查询
                if (bankRefundId == null && AppPay.refundQuery(bankPayId)) {
                    logger.info("订单:" + orderid + "App之前在微信退过款.");
                    bankRefundId = bankPayId;
                }
            }
        }
        // 退款成功,加退款流水
        if (bankRefundId != null) {
            logger.info("订单:" + orderid + "退款成功,金额为:" + refundPrice + "添加退款支付流水...");
            if(bankRefundinfo==null){
                addPPayinfo(pay, refundPrice.negate(), bankRefundId, PPayInfoTypeEnum.Z2U,bankType,getPmsSendId(payinfolist));
            }else{
                bankRefundinfo.setOtherno(bankRefundId);
                bankRefundinfo.setOthertype(bankType);
                this.ipPayInfoDao.saveOrUpdate(bankRefundinfo);
            }
            return true;
        }
        return false;
    }
    /*
     * 得到用优惠券去 pms回收乐住币的 payinfoid
     */
    private Long  getPmsSendId(List<PPayInfo> payinfolist) {
        if (payinfolist != null) {
            for (PPayInfo pp : payinfolist) {
                if (pp.getType().getId().intValue() == PPayInfoTypeEnum.Y2U.getId().intValue()) {
                    return pp.getId();
                }
            }
        }
        return null;
    }
    
    
    /*
     * 是否退过款
     */
    private boolean doRefund(List<PPayInfo> payinfolist) {
        if (payinfolist != null) {
            for (PPayInfo pp : payinfolist) {
                if (pp.getType().getId().intValue() == PPayInfoTypeEnum.Z2U.getId().intValue()) {
                    if(pp.getOtherno()!=null){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /*
     * 是否付过款
     */
    private boolean doPayed(List<PPayInfo> payinfolist) {
        if (payinfolist != null) {
            for (PPayInfo pp : payinfolist) {
                if (pp.getType().getId().intValue() == PPayInfoTypeEnum.Z2P.getId().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 是否使用过优惠券
     * @param payinfolist
     * @return
     */
    private boolean doSended(List<PPayInfo> payinfolist) {
        if (payinfolist != null) {
            for (PPayInfo pp : payinfolist) {
                if (pp.getType().getId().intValue() == PPayInfoTypeEnum.Y2P.getId().intValue()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /*
     * 返回支付成功的那条流水
     */
    private PPayInfo getPayedPayinfo(List<PPayInfo> payinfolist){
        if (payinfolist != null) {
            for (PPayInfo pp : payinfolist) {
                if (pp.getType().getId().intValue() == PPayInfoTypeEnum.Z2P.getId().intValue()) {
                    return pp;
                }
            }
        }
        return null;
    }

    private PPayInfo getSendedLeZhu(List<PPayInfo> infos, PPayInfoTypeEnum type) {
        
        if (infos != null) {
            for (PPayInfo pp : infos) {
                if (pp.getType().getId().intValue() == type.getId().intValue()) {
                    return pp;
                }
            }
        }
        return null;
    }
    
    /*
     * 添加支付流水
     */   
    private Long addPPayinfo(PPay pay, BigDecimal price, String bankRefundId, PPayInfoTypeEnum type,PPayInfoOtherTypeEnum otherType) {
        return addPPayinfo( pay, price, bankRefundId, type, otherType,null);
    }
    /*
     * 添加支付流水
     */   
    private PPayInfo addPPayinfoByPay(PPay pay, BigDecimal price, String bankPayid, PPayInfoTypeEnum type,PPayInfoOtherTypeEnum otherType ) {
		logger.info("订单号：" + pay.getOrderid() + "准备添加流水,参数是[payId]" + pay.getId() + "[price]" + price + "[bankPayid]" + bankPayid
				+ "[pPayInfoTypeEnum]" + type.getName());
        if (isNot0AndNull(price)) {
            PPayInfo pin = new PPayInfo();
            pin.setOtherno(bankPayid);
            pin.setPay(pay);
            pin.setCost(price);
            pin.setEnable(true);
            pin.setTime(new Date());
            pin.setType(type);
            pin.setOthertype(otherType);
            pin.setEnable(true);
            ipPayInfoDao.saveOrUpdate(pin);
            logger.info("订单号："+pay.getOrderid()+"添加支付完成后的流水完毕,id:" + pin.getId());
            return pin;
        }else{
            logger.info("订单号："+pay.getOrderid()+"添加支付完成后的流水失败,  price="+price);
        }
        return null;
    }
    
    /*
     * 添加支付流水
     */   
    private Long addPPayinfo(PPay pay, BigDecimal price, String bankRefundId, PPayInfoTypeEnum type,PPayInfoOtherTypeEnum otherType,Long pmsSendId) {
        logger.info("订单号："+pay.getOrderid()+"准备添加流水,参数是" + pay+"  [price]="+price+"[bankRefundId]"+bankRefundId+"[pPayInfoTypeEnum]==(type="+type.getId()+") "+type.getName());
        if (isNot0AndNull(price)) {
            PPayInfo pin = new PPayInfo();
            pin.setOtherno(bankRefundId);
            pin.setPay(pay);
            pin.setCost(price);
            pin.setEnable(true);
            pin.setTime(new Date());
            pin.setType(type);
            pin.setOthertype(otherType);
            pin.setEnable(true);
            pin.setPmsSendId(pmsSendId);
            ipPayInfoDao.saveOrUpdate(pin);
            logger.info("订单号："+pay.getOrderid()+"添加退款支付流水完毕,id:" + pin.getId());
            return pin.getId();
        }else{
            logger.info("订单号："+pay.getOrderid()+"添加退款支付流水，未进行 price="+price);
        }
        return null;
    }

    private void updatePTAndNR(PPay pay, NeedReturnEnum needReturnEnum) {
        logger.info("订单:" + pay.getOrderid() + "更新支付状态为用户撤销" + "Needreturn为:" + needReturnEnum.getName());
        pay.setStatus(PayTypeEnum.userCancel);
        pay.setNeedreturn(needReturnEnum);
        saveOrUpdatePay(pay);
    }
    
    
    private boolean isNot0AndNull(BigDecimal price){
        if(price==null){
            return false;
        }else if(price.compareTo(new BigDecimal(0)) != 0){
            return true;
        }else{
            return false;
        }
    }
    
  	/**
  	 * 客服查询订单付款情况
  	 */
  	public String  serviceFindPay(String orderid){
  		logger.info("订单号"+orderid+"准备去银行查询支付信息");

  		String rs=QueryPay.seviceFindPay(orderid);
      	logger.info("订单号"+orderid+"去银行查询支付信息结束，结果是："+rs);
  		return rs;
  	}
  	
  	
	/**
  	 * HMS查询订单付款情况
  	 */
  	public String hmsFindPay(Long orderid){
  		logger.info("订单号"+orderid+"准备去银行查询支付信息");
		PPay pay = this.findPayByOrderId(orderid);
		// 已经付款的,全款
		if (doPayed(this.ipPayInfoDao.findByPayId(pay.getId()))) {
  			return "已支付";
  		}else if(doRefund(this.ipPayInfoDao.findByPayId(pay.getId()))){
  			return "已退款";
  		}else{
  			QueryPayPram pram=QueryPay.findPay(orderid+"");
  			//查询到结果，并且支付成功
  			if(pram.isSuccess() && pram.getPaystatus()==BankPayStatusEnum.success){
//                if(!payTaskDao.exist(orderid, PayTaskTypeEnum.AUTORETRYSENDLEZHU)){ //Task
//                	this.payresponse(orderid, pram.getBankno(), pram.getPrice()+"", pram.getBanktype());
//                }
                this.payresponse(orderid, pram.getBankno(), pram.getPrice()+"", pram.getBanktype(), null);
  				return "已支付";
  			} 
  		}
		return "未支付";
     }
      
  	
  	/**
	 * 离店时间（和入住比）小于4小时订单 调用,p_orderlog 补贴取消
	 */
	@Override
	public boolean leaveTimeLess(Long orderid) {
		this.logger.info("订单号：" + orderid + "离店时间和入住时间比小于4小时，进入payservice 处理");
		try {
			POrderLog orderLog = this.ipOrderLogDao.findPOrderLogByPay(this.getPayid(orderid));
			this.logger.info("订单号：" + orderid + "查询到 POrderLog ，准备把切客收益(QiekeIncome)置为0 ");
			orderLog.setQiekeIncome(BigDecimal.ZERO);
			this.ipOrderLogDao.update(orderLog);
			this.logger.info("订单号：" + orderid + "离店时间和入住时间比小于4小时，处理完成");
			return true;
		} catch (Exception e) {
			this.logger.error("订单号：" + orderid + "离店时间和入住时间比小于4小时，处理失败", e);
			return false;
		}
	}
    
    
    public String selectCustomerpay(Long orderid){
		OtaOrder order = this.orderService.findOtaOrderById(orderid);	
		if (order == null) {
			throw MyErrorEnum.errorParm.getMyException("获取订单失败(id):" + orderid);
		}
		OtaRoomOrder roomOrder = order.getRoomOrderList().get(0);
		String pmsRoomOrderNo = roomOrder.getPmsRoomOrderNo();
		THotel hotel=hotelDAO.findHotelByHotelid(order.getHotelId()+"");
		String responseString=null;
		if(hotel!=null){
			SelectPms pms=new SelectPms(hotel.get("pms")+"",pmsRoomOrderNo);
//			String request="{\"hotelid\":\""+hotel.get("pms")+"\",\"customerid\":\""+pmsRoomOrderNo+"\"}";
			String request=JSON.toJSONString(pms);
			logger.info("去 pms系统请求的参数是："+request );
			try {
				responseString = PayTools.dopostjson(PMS_FIND_PAYINFO, request);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return responseString;
	}
    
    
    
    
    /**
	 * @param orderId
	 *  异常情况下支付取消——cancelpaybyerror
	 * @return  请求PMS 返回的数据
	 */
	public String cancelpaybyerror(Long orderid) {
		OtaOrder order = this.orderService.findOtaOrderById(orderid);
		if (order == null) {
			throw MyErrorEnum.customError.getMyException("订单号不存在");
		}
		PPay pay = iPayDAO.getPayByOrderId(orderid);
		if (pay == null) {
			throw MyErrorEnum.customError.getMyException("支付订单号未找到");
		}

		String responseString = null;

		THotel hotel = hotelDAO.findHotelByHotelid(order.getHotelId() + "");
		if (hotel != null) {
			logger.info("异常情况下支付取消:orderid=" + orderid + " PMS 端Hotel id:" + hotel.get("pms"));
			OtaRoomOrder roomOrder = order.getRoomOrderList().get(0);
			String pmsRoomOrderNo = roomOrder.getPmsRoomOrderNo();
			pmsRoomOrderNo = pmsRoomOrderNo == null ? "" : pmsRoomOrderNo;

			BigDecimal cost = getCost(order, pay.getId());
			if (cost != BigDecimal.ZERO) {
				CancelPms cp = new CancelPms(orderid, cost);
				cp.setPayid(pay.getId() + "");
				cp.setHotelid(hotel.get("pms") + "");
				cp.setCustomerid(pmsRoomOrderNo);
				String request = JSON.toJSONString(cp);
				logger.info("异常情况下支付取消:orderid=" + orderid + " 	去 cancelpaybyerror请求的参数是：" + request);
				try {
					responseString = PayTools.dopostjson(PMS_CANCEL_PMS_URL, request);
					logger.info("异常情况下支付取消:orderid=" + orderid + " 返回的数据是：" + responseString);
				} catch (Exception e) {
					logger.error("异常情况下支付取消:orderid=" + orderid + " 异常:" + e);
					e.printStackTrace();
				}
			} else {
				logger.info("异常情况下支付取消:orderid=" + orderid + "  收回乐住币金额为0  ");
			}
		} else {
			logger.info("异常情况下支付取消:orderid=" + orderid + " THotel 为null");
		}
		return responseString;
	}
	
	/**
	 * 给p_orderlog 加切客收益
	 */
	@Override
	public boolean addQiekeIncome(OtaOrder order, RuleEnum rule) {
		this.logger.info("订单号：" + order.getId() + " 记录切客收益，进入payservice 处理");
		try {
			POrderLog orderLog = this.ipOrderLogDao.findPOrderLogByPay(this.getPayid(order.getId()));
			//判断是否支付
			if (order.getOrderType() == OrderTypeEnum.PT.getId().intValue() || 
					order.getPayStatus() == PayStatusEnum.alreadyPay.getId().intValue()) {
				BigDecimal bd = this.getRuleValue(order.getOrderType(), rule, order.getId());
				this.logger.info("订单号：" + order.getId() + "查询到 POrderLog ，准备把切客收益(QiekeIncome)置为" + bd);
				orderLog.setQiekeIncome(bd);
				this.ipOrderLogDao.update(orderLog);
				this.logger.info("订单号：" + order.getId() + "记录切客收益，处理完成");
				return true;
			}
			return false;
		} catch (Exception e) {
			this.logger.error("订单号：" + order.getId() + "记录切客收益，处理失败", e);
			return false;
		}
	}
	/**
	 * 入住后超过15分钟，取消优惠券补贴
	 */
	public boolean checkinCancelCoupon(OtaOrder order){
		logger.info("订单号："+order.getId()+" 入住后超过15分钟，取消优惠券补贴，进入payservice 处理");
		PPay pay = this.iPayDAO.getPayByOrderId(order.getId());
		POrderLog orderLog = ipOrderLogDao.findPOrderLogByPay(pay.getId());
		logger.info("订单号："+order.getId()+" 入住后超过15分钟，payinfo中记录一笔负的优惠券乐住币   金额是:"+orderLog.getRealotagive().negate());
	    if(!isNot0AndNull(orderLog.getRealotagive())) {
			logger.info("订单号："+order.getId()+"没有优惠券,无需收回.");
			return true;
		}
		Long payinfoid=addPPayinfo(pay, orderLog.getRealotagive().negate(), "", PPayInfoTypeEnum.Y2U, null, null);
		logger.info("订单号："+order.getId()+" 入住后超过15分钟， payinfo记录完成，ID是:"+payinfoid);
		//预付订单
		if(order.getOrderType()==OrderTypeEnum.YF.getId().intValue()){
		  //收回乐住币
			logger.info("订单号："+order.getId()+" 入住后超过15分钟，取消优惠券补贴，订单是【预付】订单，准备收回乐住币,金额是:"+orderLog.getRealotagive());
			 boolean cancelCoupon=false;
			   //新PMS
			if(isNewPMS(order)) {
				cancelCoupon=cancelCouponPmsV2(order,payinfoid,orderLog.getRealotagive().negate());
			}else{ //老PMS
				cancelCoupon=cancelPayToPms(order, pay, orderLog.getRealotagive().negate(),orderLog.getId(),payinfoid ) ;
			}
			if(cancelCoupon){
				logger.info("订单号："+order.getId()+" 入住后超过15分钟，取消乐住币失败成功");
				ipPayInfoDao.updatePmsSendIdById(payinfoid, payinfoid);
				orderLog.setPmsrefund(PmsSendEnum.ResponseSuccess);
			}else{
				logger.info("订单号："+order.getId()+" 入住后超过15分钟，回收乐住币失败");
			}
	    //到付订单
		}else{
			logger.info("订单号："+order.getId()+" 入住后超过15分钟，取消优惠券补贴，订单是【到付】订单，不用收回乐住币");
		}
		logger.info("订单号："+order.getId()+" 入住后超过15分钟，取消优惠券补贴，准备把 orderLog中 ota 补贴的部分置0 ");
		orderLog.setRealotagive(BigDecimal.ZERO);
		this.ipOrderLogDao.update(orderLog);
		logger.info("订单号："+order.getId()+" 入住后超过15分钟，取消优惠券补贴，把 orderLog中 ota 补贴的部分置0 完成 ");
		return true;
	}
	
	/*
	 * 回收 PMS2.0 乐住币
	 */
	private boolean  cancelCouponPmsV2(OtaOrder order, Long payinfoid,   BigDecimal price) {
		PMSResponse response = null;
		try {
			String request = wrapPMSRequest(order, payinfoid, price, "cancelpay");
			String responseString = PayTools.dopostjson(PMS_CANCEL_PMS_URL, request);
			response = JSON.parseObject(responseString, PMSResponse.class);
		} catch (Exception e) {
			logger.error("取消补贴的乐住币失败",e);
		}
		return  response.isSuccess();
	}

	
	/**通过orderid得到payid*/
	private long getPayid(Long orderid) {
		PPay pay = this.iPayDAO.getPayByOrderId(orderid);
		if (pay == null) {
			throw MyErrorEnum.errorParm.getMyException("查找支付信息失败");
		}
		return pay.getId();
	}
	
	/** 从数据库查询补贴的 */
	private BigDecimal getRuleValue(int ordertype, RuleEnum rule, Long orderId) throws Exception {
		BAreaRuleDetail ar = null;
		String cityCode = getCityCode(orderId);
		if (ordertype == OrderTypeEnum.YF.getId().intValue()) {
			ar = this.areaRuleDetailService.queryRuleValue(OrderTypeEnum.YF.name(), rule, cityCode);
		} else if (ordertype == OrderTypeEnum.PT.getId().intValue()) {
			ar = this.areaRuleDetailService.queryRuleValue(OrderTypeEnum.PT.name(), rule, cityCode);
		}
		if (ar != null) {
			BigDecimal qiekeIncome = new BigDecimal(ar.getRulevalue());
			logger.info("订单:" + orderId + "获取到规则明细,切客收益:" + qiekeIncome);
			return qiekeIncome;
		} else {
			if(ordertype == OrderTypeEnum.YF.getId().intValue()) {
				logger.info("预付订单:" + orderId + "没有获取到规则明细,使用默认的切客收益" + DEFAULT_YF_QIEKEINCOME);
				return DEFAULT_YF_QIEKEINCOME;
			} else if (ordertype == OrderTypeEnum.PT.getId().intValue()) {
				logger.info("到付订单:" + orderId + "没有获取到规则明细,使用默认的切客收益" + DEFAULT_PT_QIEKEINCOME);
				return DEFAULT_PT_QIEKEINCOME;
			}
		}
		return BigDecimal.ZERO;
	}
	
	private String getCityCode(Long orderId) {
		
		logger.info("订单:" + orderId + "获取城市编码...");
		String cityCode = hotelService.selectCityCodeByOrderId(orderId);
		logger.info("订单:" + orderId + "城市编码:" + cityCode);
		
		return cityCode;
	}
    
	//计算需要收回乐住币金额
	private BigDecimal getCost(OtaOrder order, Long payid) {
		if (order.getOrderType() == OrderTypeEnum.YF.getId()) {
			POrderLog orderlog = this.ipOrderLogDao.findPOrderLogByPay(payid);
			// 已经付款的,全款
			if (doPayed(ipPayInfoDao.findByPayId(payid))) {
				return orderlog.getRealallcost();
			}
//			if (order.getPayStatus() >= PayStatusEnum.alreadyPay.getId()
//					.intValue()) {
//				return orderlog.getAllcost();
//			}
			// 未付款的,光补贴
			return orderlog.getRealotagive();
		}
		return BigDecimal.ZERO;
	}
	
	public PMSCancelParam getPMSCancelParam(OtaOrder order, PPayInfoTypeEnum type) {
		
		Long orderId = order.getId();
		
		logger.info("订单:" + orderId + "进入封装取消乐住币参数流程...");
		
		PMSCancelParam param = new PMSCancelParam();

        PPay pay = this.iPayDAO.getPayByOrderId(orderId);
        
        POrderLog findPOrderLogByPay = this.ipOrderLogDao.findPOrderLogByPay(pay.getId());
        
        BigDecimal price = calculateRefundLeZhu(order, findPOrderLogByPay, type);
        
        Long pmsSendIdLong = null;
//        if (!isNot0AndNull(findPOrderLogByPay.getRealotagive())) {
//        	//插入退款流水
//            pmsSendIdLong = addPPayinfo(pay, price.negate(), null, PPayInfoTypeEnum.Z2U, null);
//        } else {
//        	//插入退优惠券流水
//            pmsSendIdLong = addPPayinfo(pay, findPOrderLogByPay.getRealotagive().negate(), null, type, null);
//        }
        
        if (isNot0AndNull(findPOrderLogByPay.getRealotagive())) {
			// 退优惠券流水
			pmsSendIdLong = addPPayinfo(pay, findPOrderLogByPay.getRealotagive().negate(), null, type, null);
		}
        //添加退红包支付的部分 流水
		if (PayTools.isPositive(findPOrderLogByPay.getRealaccountcost())) {
			PPayInfo info = addPPayinfo(pay, findPOrderLogByPay.getRealaccountcost().negate(),PPayInfoTypeEnum.ACCOUNTCOSTREFUND, pmsSendIdLong);
			if(null == pmsSendIdLong) {
				pmsSendIdLong = info.getId();
			}
		}
		
		if(null == pmsSendIdLong) {
			//退款流水
			pmsSendIdLong = addPPayinfo(pay, price.negate(), null, PPayInfoTypeEnum.Z2U, null);
		}
		
        param.setPrice(price);
        param.setPayId(pmsSendIdLong);
        
        logger.info("订单:" + orderId + "封装取消乐住币参数完毕,参数:" + param.toString());
        
        this.logger.info("订单:" + orderId + "订单系统取消pms订单/乐住币前预平账.");

        findPOrderLogByPay.setRealotagive(BigDecimal.ZERO);
        findPOrderLogByPay.setRealallcost(BigDecimal.ZERO);
		findPOrderLogByPay.setRealcost(BigDecimal.ZERO);
        findPOrderLogByPay.setPmsrefund(PmsSendEnum.ResponseSuccess);
        if (PayTools.isPositive(findPOrderLogByPay.getRealaccountcost())) {

        		findPOrderLogByPay.setRealaccountcost(BigDecimal.ZERO);
        }
        ipOrderLogDao.update(findPOrderLogByPay);
        this.logger.info("订单:" + orderId + "更新orderLog成功.");
        
        ipPayInfoDao.updatePmsSendIdById(pmsSendIdLong, pmsSendIdLong);
        this.logger.info("订单:" + orderId + "更新ppayinfo成功.");
        
        this.logger.info("订单:" + orderId + "预平账完成.");

        return param;
	}
	
	@Override
	public boolean reSendLeZhu(OtaOrder order, long payid, Long pmsSendId,
			BigDecimal price, String memberName) {
		logger.info("payService:reSendLeZhu:进入自动重发乐住币业务开始，订单是:" + order.getId() + ",支付订单是:" + payid + ",pmsSendId:" + pmsSendId + ",乐住币:" + price + ",membername:" + memberName);
		boolean reFlag = pmsAddpay(order,payid, pmsSendId, price, memberName,null);
	        if (reFlag) {
	        	logger.info("payService:reSendLeZhu:自动重发乐住币业务成功，订单是:"+order.getId()+",支付订单是:"+payid+",pmsSendId:"+pmsSendId+",乐住币:"+price+",membername:"+memberName);
	        	return true;
	        } else {
	        	logger.info("payService:reSendLeZhu:自动重发乐住币业务失败，订单是:"+order.getId()+",支付订单是:"+payid+",pmsSendId:"+pmsSendId+",乐住币:"+price+",membername:"+memberName);
	        	return false;
	        }
	}

	
	@Override
	public boolean insertPayCallbackLog(Long orderId, String callbackFrom, String payResult, String payNo, BigDecimal price, String errorCode, String errorMsg) {
		
		
		PayCallbackLog log = new PayCallbackLog();
		
		log.setOrderId(orderId);
		log.setCallbackFrom(callbackFrom);
		log.setPayResult(payResult);
		log.setPayNo(payNo);
		log.setPrice(price);
		log.setErrorCode(errorCode);
		log.setErrorMsg(errorMsg);
		
		logger.info("订单:" + orderId + "支付回调保存数据库...");
		
		Long result = payCallbackLogDao.insertPayCallbackLog(log);
		if( result != null) {
			logger.info("订单:" + orderId + "支付回调保存数据库成功,返回值:" + result);
			
			return true;
		}
		
		logger.error("订单:" + orderId + "支付回调保存数据库失败.");
		return false;
	}

	@Override
	public List<PPayTask> selectInitTask(PayTaskTypeEnum taskType, PayTaskStatusEnum status) {
		
		return payTaskDao.selectInitTask(taskType, status);
	}

	@Override
	public Long insertPayTask(PPayTask task) {
		
		return payTaskDao.insertPayTask(task);
	}

	@Override
	public int updatePayTask(PayTaskStatusEnum status, List<PPayTask> tasks) {
		
		return payTaskDao.updatePayTask(status, tasks);
	}
	
	@Override
	public boolean modifyPayStatus(Long orderid,Integer payStatus,String opertorName) {
		this.logger.info("PayService::modifyPayStatus"+"orderid"+orderid+",payStatus:"+payStatus+",operator:"+opertorName+"，begin");
		OtaOrder order = orderService.findOtaOrderById(orderid);
		
		if(order == null) {
			this.logger.error("订单:" + orderid + "不存在.");
			throw MyErrorEnum.findOrder.getMyException("订单不存在.");
		}
		if(OrderTypeEnum.PT.getId().intValue() == order.getOrderType()){
			this.logger.error("订单:" + orderid + "到付订单不能修改支付状态.");
			throw MyErrorEnum.updateOrder.getMyException("到付订单不能修改支付状态.");
		}
		this.logger.info("PayService::modifyPayStatus：orderid"+orderid+"，begin");
		boolean flag =	orderService.modifyOtaOrderByPayStatus(order, payStatus, opertorName);
		this.logger.info("PayService::modifyPayStatus：orderid"+orderid+"，end");
		
		return flag;
	}

	@Override
	public void checkPayStatusWhenIn(OtaOrder order) {
		
		if(order.getPayStatus() == PayStatusEnum.paying.getId().intValue()
				|| order.getPayStatus() == PayStatusEnum.waitPay.getId().intValue()) {
			
			Long orderId = order.getId();
			logger.info("订单:" +orderId+ "的订单状态是:" +order.getOrderStatus() +",支付状态是:"+order.getPayStatus());
			
			Long result = payTaskDao.insertPayTask(buildPayTask(order.getId(), PayTaskStatusEnum.INIT, PayTaskTypeEnum.PROCESSPAYSTATUSERROR));
	    	
			logger.info("订单:" + order.getId() + "保存任务[" + PayTaskTypeEnum.PROCESSPAYSTATUSERROR.name() + "]返回值:" + result);
		}
	}

	@Override
	public Long insertPayStatusError(PPayStatusErrorOrder order) {
		return payStatusErrorDao.insertErrorOrder(order);
	}

	@Override
	public boolean deletePayStatusError(Long orderId) {
		
		
		try {
			return payStatusErrorDao.deleteErrorOrder(orderId) > 0;
		} catch (Throwable e) {
			logger.error("订单:" + orderId + "尝试删除异常支付状态表数据时异常!" , e);
			
			return false;
		}
	}

	
	public BigDecimal payMoney(Long  orderid){
		PPay pay=iPayDAO.getPayByOrderId(orderid);
		if(pay!=null){
			POrderLog orderlog=ipOrderLogDao.findPOrderLogByPay(pay.getId());
			if(orderlog!=null){
				return orderlog.getUsercost();
			}
		}
		return null;
	}
	
	public PRealOrderLog getRealOrderLogByOrderid(String orderid){
		logger.error("订单:" + orderid + "查询RealOrderLogByOrderid数据.");
		Long orderId = Long.parseLong(orderid);
		PRealOrderLog pRealOrderLog = new PRealOrderLog();
		PPay pay = this.iPayDAO.getPayByOrderId(orderId);
		if(pay==null){
			return null;
		}
		POrderLog orderLog = this.ipOrderLogDao.findPOrderLogByPay(pay.getId());
		if(orderLog==null){
			return null;
		}
		pRealOrderLog.setOrderid(orderId);
		pRealOrderLog.setPayid(orderLog.getPayid());
		pRealOrderLog.setRealcost(orderLog.getRealcost());
		pRealOrderLog.setRealaccountcost(orderLog.getRealaccountcost());
		pRealOrderLog.setRealotagive(orderLog.getRealotagive());
		pRealOrderLog.setRealallcost(orderLog.getRealallcost());
		pRealOrderLog.setHotelgive(orderLog.getHotelgive());
		pRealOrderLog.setQiekeIncome(orderLog.getQiekeIncome());
		logger.error("订单:" + orderid + "查询数据." + pRealOrderLog.toString());
		return pRealOrderLog;
	}
		
	private int clearPOrderLog(POrderLog porderLog){
		POrderLog orderLog = new POrderLog();
		orderLog.setId(porderLog.getId());
		orderLog.setRealotagive(BigDecimal.ZERO);
		orderLog.setRealaccountcost(BigDecimal.ZERO);
		orderLog.setRealallcost(BigDecimal.ZERO);
		orderLog.setRealcost(BigDecimal.ZERO);
		return  ipOrderLogDao.update(orderLog);
	}
	 
	
	
	
	
	
	
	
	
	
	

	private PPay paycreateByOrder1(OtaOrder order, String couponNo, String promotionNo, String paytype, String onlinepaytype) {
		long orderId = order.getId();
		this.logger.info("订单:" + orderId + "=======创建支付订单开始======");
		UMember member = getUMember(orderId,order.getMid());
		PPay pay = this.saveOrGetPPay(order, member, order.getTotalPrice(),paytype);
		CouponParam cp=ticketService.queryCouponParam(order );  //, pay
		this.logger.info("订单:" + orderId + "总金额:{}", cp.getTotalPrice() + "支付帐单明细:{}", pay.toString());
		// 把原来记录先删除掉
		this.ipPayInfoDao.deletePayInfoByPayid(pay.getId());
		//添加使用 钱包支付的流水
		addAccountCostPayinfo(pay, order.getAvailableMoney(),null);
		//添加优惠券的流水
		addCoupponPayinfo(pay, cp.getCoupon());
		POrderLog orderLog=createOrderLog(pay.getId(),cp,order.getAvailableMoney());
		//外层有取orderLog
		pay.setpOrderLog(orderLog);
		// 更新数据库交易流水信息
		this.saveOrUpdatePay(pay);
		this.logger.info("订单:" + orderId + "=======创建支付订单完成=======");
		return pay;
	}
	
	private POrderLog createOrderLog(Long payId,CouponParam cp,BigDecimal accountcost){
		POrderLog orderLog = this.ipOrderLogDao.findPOrderLogByPay(payId);
		if (orderLog == null) {
			orderLog = new POrderLog();
		}
		orderLog.setRealcost(BigDecimal.ZERO);
		orderLog.setAllcost(cp.getTotalPrice());
		orderLog.setOtagive(cp.getCoupon());
		orderLog.setUsercost(cp.getUserCost());
		orderLog.setPayid(payId);
		orderLog.setPmssend(PmsSendEnum.None);
		orderLog.setPmsrefund(PmsSendEnum.None);
		orderLog.setRealotagive(BigDecimal.ZERO);
		orderLog.setRealaccountcost(BigDecimal.ZERO);
		orderLog.setRealallcost(BigDecimal.ZERO);
		if (PayTools.isPositive(accountcost)) {
			orderLog.setAccountcost(accountcost);
		}
		this.ipOrderLogDao.saveOrUpdate(orderLog);
		return  orderLog;
	}
	
	
	private UMember  getUMember(Long orderId,Long mid){
		this.logger.info("订单:" + orderId + ",用户:" + mid + "查找用户");
		Optional<UMember> op = this.iMemberService.findMemberById(mid);
		if (!op.isPresent()) {
			this.logger.error("订单:" + orderId + "获取用户信息失败");
			throw MyErrorEnum.errorParm.getMyException("获取用户信息失败");
		}
		return op.get();
	}

	//添加优惠券用的流水
    private PPayInfo addCoupponPayinfo(PPay pay, BigDecimal price){
    	logger.info("订单号："+pay.getOrderid()+"添加优惠券流水，金额是："+price);
    	if(PayTools.isPositive(price)){
    		return addPPayinfo(pay, price, PPayInfoTypeEnum.Y2P,null);
    	}
    	return  null;
	}
	
    /**
     * 支付获取流水
     *
     * @param order
     * @param member
     * @param allcost
     * @return
     */
    /**
     * 支付获取流水
     *
     * @param order
     * @param member
     * @param allcost
     * @return
     */
    private PPay saveOrGetPPay(OtaOrder order, UMember member, BigDecimal allcost,String paytype) {
        this.logger.info("  getPayByOrder   orderid=={}", order.getId());
        // 校验流水是否存在
        // Long payCounts = this.countPayByOrderId(order.getId(),
        // PaySrcEnum.order);
        List<PPay> list = this.iPayDAO.findPayByOrderidAndPaySrc(order.getId(), PaySrcEnum.order);
        this.logger.info("  getPayByOrder   orderid=={} list=={}", order.getId(), list);
        if ((list == null) || (list.size() == 0)) {// 原来存在，直接查
            this.logger.info("orderid=={} p_pay 还没有创建", order.getId());
            // 创建流水信息
            return this.createPPay(order, allcost, member,paytype);
        } else {
            this.logger.info("orderid=={} p_pay 已经创建过", order.getId());
            // 创建流水信息
            return list.get(0);
        }
    }
	
    

    /**
     * 初始创建流水信息
     *
     * @param order
     * @param allcost
     * @param member
     * @return
     */
    private PPay createPPay(OtaOrder order, BigDecimal allcost, UMember member,String paytype) {
        PPay pay = new PPay();
        THotel hotel = new THotel();
        hotel.setHotelid(String.valueOf(order.getHotelId()));
        pay.setHotel(hotel);
        pay.setOrderid(order.getId());
        pay.setOrderprice(allcost);
        pay.setLezhu(allcost);
        pay.setMember(member);
        pay.setNeedreturn(NeedReturnEnum.ok);
        pay.setNeworderid(null);
        pay.setPaysrc(PaySrcEnum.order);
        // 这里的pms是第一个客单的pms，所以如果有多个客单，对账时是否正确需要确认
        if ((order.getRoomOrderList() == null) || (order.getRoomOrderList().size() == 0)) {
            throw MyErrorEnum.errorParm.getMyException("订单有误，没有选择房间.");
        } else {
        	pay.setPmsorderno(order.getRoomOrderList().get(0).getPmsRoomOrderNo());
        }
        pay.setStatus(PayTypeEnum.effective);
        pay.setTime(new Date());
		// 到付的 把lezhu 置0
		if (paytype.equals("2")) {
			pay.setLezhu(BigDecimal.ZERO);
		} 
        List<PPayInfo> infoList = new ArrayList<PPayInfo>();
        pay.setPayInfos(infoList);
        this.saveOrUpdatePay(pay);
        return pay;
    }

	public String refundBatchPay(String orderids) {
		StringBuffer fail = new StringBuffer("退款失败订单:");
		StringBuffer success = new StringBuffer("退款成功订单:");
		String[] d = orderids.split(",");
		logger.info("总数" + d.length);
		for (int i = 0; i < d.length; i++) {

			String orderId = d[i];
			logger.info("订单:" + orderId + "退款开始...");
			Long lorderId = Long.parseLong(orderId);

			OtaOrder order = this.orderService.findOtaOrderById(lorderId);

			if (order == null) {
				logger.info("订单:" + orderId + "不存在.");
				fail.append(orderId + ",");
				continue;
			}
			if (OrderTypeEnum.PT.getId().intValue() == order.getOrderType()) {
				logger.info("订单:" + orderId + "为到付单.");
				fail.append(orderId + ",");
				continue;
			}
			PPay ppay = findPayByOrderId(lorderId);
			if (ppay == null) {
				logger.info("订单:" + orderId + "支付ppay不存在.");
				fail.append(orderId + ",");
				continue;
			}
			// 看是否已经退过款
			PPayInfo rinfo = this.ipPayInfoDao.getPPayInfoByRefund(ppay.getId());
			if (rinfo != null) {
				logger.info("订单:" + orderId + "数据库中已有退款记录.");
				fail.append(orderId + ",");
				continue;
			} else {
				logger.info("订单:" + orderId + "数据库中已支付未退款.");
				// this.logger.info("订单:" + orderId + "对应的支付单:{}",
				// ppay.toString());
				List<PPayInfo> payinfolist = this.ipPayInfoDao.findByPayId(ppay.getId());

				PPayInfo payinfo = getPayedPayinfo(payinfolist);
				if(payinfo==null){
					logger.info("订单:" + orderId + "未用在线支付.");
					fail.append(orderId + ",");
					continue;
				}
				PPayInfoOtherTypeEnum bankType = payinfo.getOthertype();
				String bankPayId = payinfo.getOtherno();
				String bankRefundId = null;
				BigDecimal refundPrice = payinfo.getCost();
				if (bankType == PPayInfoOtherTypeEnum.alipay) {
					logger.info("订单:" + orderId + "为支付宝支付.");
					QueryPayPram pram = new QueryPayPram();
					pram.setOrderid(orderId);
					// logger.info("订单:" + orderId +
					// "，payinfo"+payinfo.toString());
					pram = AliPay.onlyQuery(orderId, pram);
					// logger.info("订单:" + orderId + "，pram"+pram.toString());
					if (pram.isSuccess() && (pram.getBankno().equals(payinfo.getOtherno())
							&& (pram.getPrice().compareTo(payinfo.getCost()) == 0))) {
						bankRefundId = AliPay.refund(orderId + "", bankPayId, refundPrice + "");
						logger.info("订单:" + orderId + "去支付宝退款交易号:" + bankRefundId);
						success.append(orderId + ",");
						// 如果退过款,则退款失败,走一个退款查询
						if (bankRefundId == null && AliPay.refundQuery(orderId + "")) {
							logger.info("订单:" + orderId + "之前在支付宝退过款.");
							bankRefundId = bankPayId;
						}
					}
					// 微信公众账号退款
				} else if (bankType == PPayInfoOtherTypeEnum.wechatpay) {
					logger.info("订单:" + orderId + "为微信公共账号支付.");
					int m = PayTools.get100price(refundPrice);
					if (m > 0) {
						bankRefundId = WeChat.refund(bankPayId, orderId + "", m);
						logger.info("订单:" + orderId + "去微信公共账号退款交易号:" + bankRefundId);
						success.append(orderId + ",");
						// 如果退过款,则退款失败,走一个退款查询
						if (bankRefundId == null && WeChat.refundQuery(bankPayId)) {
							logger.info("订单:" + orderId + "之前在微信公共账号退过款.");
							bankRefundId = bankPayId;
						}
					}
					// APP微信支付退款
				} else if (bankType == PPayInfoOtherTypeEnum.wxpay) {
					logger.info("订单:" + orderId + "为App经微信支付.");
					int m = PayTools.get100price(refundPrice);
					if (m > 0) {
						QueryPayPram pram = new QueryPayPram();
						pram.setOrderid(orderId);
						// logger.info("订单:" + orderId +
						// "，payinfo"+payinfo.toString());
						pram = AppPay.onlyQuery(orderId, pram);
						// logger.info("订单:" + orderId +
						// "，pram"+pram.toString());
						if (pram.isSuccess() && (pram.getBankno().equals(payinfo.getOtherno())
								&& (pram.getPrice().compareTo(payinfo.getCost()) == 0))) {
							bankRefundId = AppPay.refund(bankPayId, orderId + "", m);
							logger.info("订单:" + orderId + "App去微信退款交易号:" + bankRefundId);
							success.append(orderId + ",");
							// 如果退过款,则退款失败,走一个退款查询
							if (bankRefundId == null && AppPay.refundQuery(bankPayId)) {
								logger.info("订单:" + orderId + "App之前在微信退过款.");
								bankRefundId = bankPayId;
							}
						}
					}
				}
			}

		}

		logger.info("++++++++++++++++++++++++++++++++++++++++++");
		logger.info(success.toString());
		logger.info(fail.toString());
		logger.info("++++++++++++++++++++++++++++++++++++++++++");
		return "处理完毕.";
	}

    public List<PPay> findByUserId (String userId) {
        return this.iPayDAO.findByUserId(userId);
    }
}