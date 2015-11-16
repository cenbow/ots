package com.mk.ots.order.service;

import com.google.common.base.Optional;
import com.mk.ots.common.enums.*;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.SearchConst;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.mapper.OrderMapper;
import com.mk.ots.mapper.OtaOrderMacMapper;
import com.mk.ots.mapper.PmsCheckinUserMapper;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.model.UUnionidLog;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.member.service.IUnionidLogService;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.bean.TopPmsRoomOrderQuery;
import com.mk.ots.order.dao.PmsRoomOrderDao;
import com.mk.ots.order.model.OtaOrderMac;
import com.mk.ots.pay.dao.impl.POrderLogDAO;
import com.mk.ots.pay.dao.impl.PayDAO;
import com.mk.ots.pay.model.POrderLog;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.pay.service.IPayService;
import com.mk.ots.promoteconfig.service.impl.PromoteConfigService;
import com.mk.ots.utils.DistanceUtil;
import com.mk.pms.myenum.PmsCheckInTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Thinkpad on 2015/11/11.
 */
@Service
public class QiekeRuleService {
    private static Logger logger = LoggerFactory.getLogger(QiekeRuleService.class);
    /**检查用的坐标是否为空开关， true：则检查 false：不检查**/
    private final static boolean IOS_CHECK_USER_ADDRESS_IS_NULL_SWITCH = true;
    /**检查用的坐标距离开关， true：则检查 false：不检查**/
    private final static boolean IOS_CHECK_USER_ADDRESS_DISTANCE_SWITCH = false;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OtaOrderMacMapper otaOrderMacMapper;
    @Autowired
    private IMemberService iMemberService;
    @Autowired
    private IPayService iPayService;
    @Autowired
    private PmsCheckinUserMapper pmsCheckinUserMapper;
    @Autowired
    private THotelMapper tHotelMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PmsRoomOrderDao pmsRoomOrderDao;
    @Autowired
    private POrderLogDAO pOrderLogDAO;
    @Autowired
    private PromoteConfigService promoteConfigService;
    @Autowired
    private PayDAO payDAO;

    @Autowired
    private IUnionidLogService unionidLogService;
    /**
     * 手机号必须是第一次，入住并离店的订单
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkMobile(OtaOrder otaOrder){
        logger.info(String.format("----------QiekeRuleService.checkMobile start"));
        if (null == otaOrder) {
            logger.info(String.format("----------QiekeRuleService.checkMobile order is null end"));
            return OtaFreqTrvEnum.PHONE_NOT_FIRST;
        }
        logger.info(String.format("----------QiekeRuleService.checkMobile order id:[%s]" , otaOrder.getId()));
        Long mid = otaOrder.getMid();
        logger.info(String.format("----------QiekeRuleService.checkMobile mid id:[%s]" , mid));

        //订单状态
        List<OtaOrderStatusEnum> statusList = new ArrayList<>();
        statusList.add(OtaOrderStatusEnum.CheckIn);
        statusList.add(OtaOrderStatusEnum.Account);
        statusList.add(OtaOrderStatusEnum.CheckOut);

        //查询该用户下所有 入住、挂单、离店酒店
        List<OtaOrder> orderList = this.orderService.findOtaOrderByMid(mid, statusList);
        logger.info(String.format("----------QiekeRuleService.checkMobile get orderList:[%s]" , orderList.size()));

        //排除这次订单外，其他是否还有订单
        Set<Long> orderSet = new HashSet<>();
        for(OtaOrder order : orderList) {
            orderSet.add(order.getId());
        }
        orderSet.remove(otaOrder.getId());

        logger.info(String.format("----------QiekeRuleService.checkMobile orderSet size:[%s] ", orderSet.size()));
        //其他是否还有订单，人为非第一次使用
        if (orderSet.size() > 0 ) {
            logger.info(String.format("----------QiekeRuleService.checkMobile order id:[%s] end phone not first" , otaOrder.getId()));
            return OtaFreqTrvEnum.PHONE_NOT_FIRST;
        }

        //通过
        logger.info(String.format("----------QiekeRuleService.checkMobile order id:[%s] end pass" , otaOrder.getId()));
        return OtaFreqTrvEnum.L1;
    }

    /**
     * 系统号必须是第一次，入住并离店的订单（若系统号未取到，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkSysNo(OtaOrder otaOrder){
        logger.info(String.format("----------QiekeRuleService.checkSysNo start"));
        if (null == otaOrder) {
            logger.info(String.format("----------QiekeRuleService.checkSysNo otaOrder is null end"));
            return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
        }
        logger.info(String.format("----------QiekeRuleService.checkMobile order id:[%s]" , otaOrder.getId()));

        Integer orderMethod = otaOrder.getOrderMethod();
        logger.info(String.format("----------QiekeRuleService.checkMobile orderMethod:[%s]" , orderMethod));
        if (orderMethod ==  OrderMethodEnum.WECHAT.getId()) {
            logger.info(String.format("----------QiekeRuleService.checkMobile do wechat order id:[%s]", otaOrder.getId()));
            //微信
            Long mid = otaOrder.getMid();
            Optional<UMember> memberOptional = iMemberService.findMemberById(mid, "T");
            if (memberOptional.isPresent()) {
                UMember member = memberOptional.get();

                String unionid = member.getUnionid();
                if (null == unionid) {
                    logger.info(String.format("----------QiekeRuleService.checkMobile do wechat order id:[%s] openId is null end", otaOrder.getId()));
                    return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
                }
                List<UUnionidLog> unionidLogList = unionidLogService.queryByUnionid(unionid);

                //去除本次账号
                Set<Long> memberIdSet = new HashSet<>();
                for (UUnionidLog log : unionidLogList) {
                    Long dbMid = log.getMid();
                    memberIdSet.add(dbMid);
                }
                memberIdSet.remove(mid);

                //
                if (memberIdSet.size() > 0) {
                    return OtaFreqTrvEnum.DEVICE_NUM_NOT_FIRST;
                } else {
                    return  OtaFreqTrvEnum.L1;
                }
            }
            logger.info(String.format("----------QiekeRuleService.checkMobile do wechat UMember is null order id:[%s] end", otaOrder.getId()));
            return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;

        } else if (orderMethod == OrderMethodEnum.ANDROID.getId()) {
            logger.info(String.format("----------QiekeRuleService.checkMobile do android order id:[%s]", otaOrder.getId()));
            //安卓
            Long orderId = otaOrder.getId();

            OtaOrderMac orderMac = otaOrderMacMapper.selectByOrderId(orderId);
            if (null == orderMac) {
                logger.info(String.format("----------QiekeRuleService.checkMobile do android order id:[%s] mac is null end", otaOrder.getId()));
                return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
            }
            String deviceimei = orderMac.getDeviceimei();
            if (null == deviceimei) {
                logger.info(String.format("----------QiekeRuleService.checkMobile do android order id:[%s] deviceimei is null end", otaOrder.getId()));
                return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
            }

            //订单状态
            List<Integer> statusList = new ArrayList<>();
            statusList.add(OtaOrderStatusEnum.CheckIn.getId());
            statusList.add(OtaOrderStatusEnum.Account.getId());
            statusList.add(OtaOrderStatusEnum.CheckOut.getId());

            //
            Map<String, Object> param = new HashMap<>();
            param.put("deviceimei",deviceimei);
            param.put("statusList",statusList);
            List<OtaOrderMac> orderMacList = otaOrderMacMapper.selectByDeviceimei(param);

            logger.info(String.format(
                    "----------QiekeRuleService.checkMobile do android order id:[%s] orderMacList size[%s]",
                    otaOrder.getId(), orderMacList.size()));
            //去除本次账号
            Set<Long> orderIdSet = new HashSet<>();
            for(OtaOrderMac dbOrderMac : orderMacList) {
                Long macOrderId = dbOrderMac.getOrderid();
                orderIdSet.add(macOrderId);
            }
            orderIdSet.remove(orderId);

            //
            if (orderIdSet.size() > 0) {
                logger.info(String.format("----------QiekeRuleService.checkMobile do android orderIdSet size:[%s]", orderIdSet.size()));
                logger.info(String.format("----------QiekeRuleService.checkMobile do android order id:[%s] not first", otaOrder.getId()));
                return OtaFreqTrvEnum.DEVICE_NUM_NOT_FIRST;
            } else {
                logger.info(String.format("----------QiekeRuleService.checkMobile do android order id:[%s] pass", otaOrder.getId()));
                return  OtaFreqTrvEnum.L1;
            }
        } else if (orderMethod == OrderMethodEnum.IOS.getId()) {
            logger.info(String.format("----------QiekeRuleService.checkMobile do ios order id:[%s]", otaOrder.getId()));
            //IOS
            Long orderId = otaOrder.getId();
            OtaOrderMac orderMac = otaOrderMacMapper.selectByOrderId(orderId);
            if (null == orderMac) {
                logger.info(String.format("----------QiekeRuleService.checkMobile do ios order id:[%s] orderMac is null end", otaOrder.getId()));
                return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
            }
            String uuid = orderMac.getUuid();
            if (null == uuid) {
                logger.info(String.format("----------QiekeRuleService.checkMobile do ios order id:[%s] uuid is null end", otaOrder.getId()));
                return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
            }

            //订单状态
            List<Integer> statusList = new ArrayList<>();
            statusList.add(OtaOrderStatusEnum.CheckIn.getId());
            statusList.add(OtaOrderStatusEnum.Account.getId());
            statusList.add(OtaOrderStatusEnum.CheckOut.getId());
            //
            Map<String, Object> param = new HashMap<>();
            param.put("uuid",uuid);
            param.put("statusList",statusList);
            List<OtaOrderMac> orderMacList = otaOrderMacMapper.selectByUuid(param);
            logger.info(String.format(
                    "----------QiekeRuleService.checkMobile do ios order id:[%s] get orderMacList size:[%s]",
                    otaOrder.getId(), orderMacList.size()));

            //去除本次账号
            Set<Long> orderIdSet = new HashSet<>();
            for(OtaOrderMac dbOrderMac : orderMacList) {
                Long macOrderId = dbOrderMac.getOrderid();
                orderIdSet.add(macOrderId);
            }
            orderIdSet.remove(orderId);

            //
            if (orderIdSet.size() > 0) {
                logger.info(String.format("----------QiekeRuleService.checkMobile do ios orderIdSet size:[%s]", orderIdSet.size()));
                logger.info(String.format("----------QiekeRuleService.checkMobile do ios order id:[%s] not first end", otaOrder.getId()));
                return OtaFreqTrvEnum.DEVICE_NUM_NOT_FIRST;
            } else {
                logger.info(String.format("----------QiekeRuleService.checkMobile do ios order id:[%s] end pass", otaOrder.getId()));
                return  OtaFreqTrvEnum.L1;
            }
        } else {
            //其他类型返回系统号为空
            logger.info(String.format("----------QiekeRuleService.checkSysNo end do other end"));
            return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
        }
    }

    /**
     * 身份证号必须是第一次，入住并离店的订单
     * （若pms_checkinuser对应的入住类型不是身份证或者没有身份证号，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkIdentityCard(OtaOrder otaOrder){
        logger.info(String.format("checkIdentityCard begin"));
        List<Map> cardIdList = pmsCheckinUserMapper.getCheckUserByOrderId(otaOrder.getId());
        if(CollectionUtils.isEmpty(cardIdList)){
            logger.info(String.format("checkIdentityCard getCardId params otaOrder id[%s]", otaOrder.getId()));
            return OtaFreqTrvEnum.CARD_ID_IS_NULL;
        }
        String cardId = "";
        Integer isScan = null;
        for(Map map : cardIdList){
            String cardType = null;
            if(map.get("cardType") != null){
                cardType = (String)map.get("cardType");
            }
            if( !(CardTypeEnum.shenfenzheng.getId().equals(cardType)
                    || CardTypeEnum.shenfenzheng.getName().equals(cardType))){
                return OtaFreqTrvEnum.CARD_ID_IS_NULL;
            }
            if(map.get("cardid") != null){
                cardId = (String)map.get("cardid");
            }
            if (map.get("isscan") != null){
                isScan = (Integer)map.get("isscan");
            }
            if(StringUtils.isNotEmpty(cardId)){
                logger.info(String.format("checkIdentityCard getCardId cardId[%s]", cardId));
                break;
            }
        }
        if(StringUtils.isEmpty(cardId)){
            return OtaFreqTrvEnum.CARD_ID_IS_NULL;
        }
        if(PmsCheckInTypeEnum.PMS.getCode() != isScan){
            return OtaFreqTrvEnum.CARD_ID_IS_NOT_PMS_SCAN;
        }
        Long cardCount = pmsCheckinUserMapper.getCardCountByCardId(otaOrder.getId().toString(), cardId);
        if(cardCount >= 1){
            return OtaFreqTrvEnum.CARD_ID_NOT_FIRST;
        }
        return OtaFreqTrvEnum.L1;
    }


    /**
     * 支付账号（如支付宝账号或者微信支付账号）必须是第一次（若订单为到付订单，则忽略此条）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkPayAccount(OtaOrder otaOrder){
        logger.info(String.format("----------QiekeRuleService.checkPayAccount start"));
        if (null == otaOrder) {
            logger.info(String.format("----------QiekeRuleService.checkPayAccount otaOrder is null end"));
            return OtaFreqTrvEnum.NOT_CHECKIN;
        }
        //判断是否到付
        Integer orderType = otaOrder.getOrderType();
        if (orderType == OrderTypeEnum.PT.getId()) {
            logger.info(String.format("----------QiekeRuleService.checkPayAccount otaOrder is PT end otaOrder id :[%s]",otaOrder.getId()));
            return OtaFreqTrvEnum.L1;
        }

        //
        Long orderId = otaOrder.getId();
        PPay pay = this.iPayService.findPayByOrderId(orderId);
        if (null == pay) {
            logger.info(String.format("----------QiekeRuleService.checkPayAccount pay is null end otaOrder id :[%s]",otaOrder.getId()));
            return  OtaFreqTrvEnum.L1;
        }
        String userId = pay.getUserid();

        List<PPay> payList = this.iPayService.findByUserId(userId);
        //去除本次账号
        Set<Long> payIdSet = new HashSet<>();
        for(PPay dbPay : payList) {
            Long dbPayOrderid = dbPay.getOrderid();
            OtaOrder dbOrder = this.orderService.findOtaOrderById(dbPayOrderid);
            if (null == dbOrder) {
                continue;
            }
            Integer orderStatus = dbOrder.getOrderStatus();
            if (orderStatus.compareTo(OtaOrderStatusEnum.CheckIn.getId()) == 0
                    || orderStatus.compareTo(OtaOrderStatusEnum.Account.getId()) == 0
                    || orderStatus.compareTo(OtaOrderStatusEnum.CheckOut.getId()) ==0 ) {
                Long dbPayId = dbPay.getId();
                payIdSet.add(dbPayId);
            }
        }
        payIdSet.remove(pay.getId());

        //
        if (payIdSet.size() > 0) {
            logger.info(String.format("----------QiekeRuleService.checkPayAccount payIdSet size :[%s]",payIdSet.size()));
            logger.info(String.format("----------QiekeRuleService.checkPayAccount otaOrder id :[%s] end not first",otaOrder.getId()));
            return OtaFreqTrvEnum.ZHIFU_NOT_FIRST;
        } else {
            logger.info(String.format("----------QiekeRuleService.checkPayAccount otaOrder id :[%s] end pass",otaOrder.getId()));
            return  OtaFreqTrvEnum.L1;
        }
    }

    /**
     * 下单用户地址定位，其坐标必须在酒店坐标1公里内（若未获取用户坐标，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkUserAdders(OtaOrder otaOrder){
        THotelModel tHotelModel = tHotelMapper.selectById(otaOrder.getHotelId());
        BigDecimal userlongitude = otaOrder.getBigDecimal("userlongitude");
        BigDecimal userlatitude = otaOrder.getBigDecimal("userlatitude");
        //地址开关
        Integer orderMethod = otaOrder.getOrderMethod();
        if (orderMethod ==  OrderMethodEnum.IOS.getId()) {
            return checkUserAdders(IOS_CHECK_USER_ADDRESS_IS_NULL_SWITCH, IOS_CHECK_USER_ADDRESS_DISTANCE_SWITCH,
                    userlongitude, userlatitude, tHotelModel.getLongitude(), tHotelModel.getLatitude());
        }else{
            return checkUserAdders(true, true,
                    userlongitude, userlatitude, tHotelModel.getLongitude(), tHotelModel.getLatitude());
        }
    }

    public OtaFreqTrvEnum checkUserAdders(boolean checkAddressIsNullSwitchOpen, boolean checkDistanceSwitchOpen,
                                          BigDecimal userLongitude, BigDecimal userLatitude, BigDecimal hotelLongitude, BigDecimal hotelLatitude){
        if(!checkAddressIsNullSwitchOpen){
            if (userLongitude == null || userLatitude == null){
                return OtaFreqTrvEnum.L1;
            }
        }
        if(!checkDistanceSwitchOpen){
            return OtaFreqTrvEnum.L1;
        }
        if(hotelLongitude == null ||  hotelLatitude== null) {
            return OtaFreqTrvEnum.OUT_OF_RANG;
        }
        if (userLongitude == null || userLatitude == null){
            return OtaFreqTrvEnum.OUT_OF_RANG;
        }
        double distance = DistanceUtil.distance(hotelLongitude.doubleValue(), hotelLatitude.doubleValue(),
                userLongitude.doubleValue(), userLatitude.doubleValue());
        if(distance > SearchConst.SEARCH_RANGE_1_KM){
            return OtaFreqTrvEnum.OUT_OF_RANG;
        }
        return OtaFreqTrvEnum.L1;
    }


    public int updateTopInvalidReasonToNull(Long orderId){
        return orderMapper.updateInvalidReason(orderId);
    }

    public void updateTopInvalidReason(Date now){
        String yesterdayStr = DateUtils.formatDateTime(DateUtils.addDays(now, -1), DateUtils.FORMAT_DATE);
        String todayStr =  DateUtils.formatDateTime(now, DateUtils.FORMAT_DATE);
        //将符合入住时间大于四个小时订单打上标记
        Integer updateCheckInStatusInvalidReasonNum = orderMapper.updateCheckInStatusInvalidReason(yesterdayStr, todayStr, OtaFreqTrvEnum.OVER_RANG.getId());
        logger.info(String.format("updateTopInvalidReason updateTopInvalidReason num[%s]", updateCheckInStatusInvalidReasonNum));
        Integer updateAccountAndCheckOutStatusInvalidReasonNum = orderMapper.updateAccountAndCheckOutStatusInvalidReason(yesterdayStr, todayStr, OtaFreqTrvEnum.OVER_RANG.getId());
        logger.info(String.format("updateTopInvalidReason updateAccountAndCheckOutStatusInvalidReasonNum num[%s]", updateAccountAndCheckOutStatusInvalidReasonNum));
        //更新切克收益
        updateQieKeIncome(yesterdayStr, todayStr);

    }

    private void updateQieKeIncome(String yesterdayStr, String todayStr) {
        List<PmsRoomOrder>  hotelIdList = pmsRoomOrderDao.getHotelIdByCheckInTime(yesterdayStr, todayStr);
        if(CollectionUtils.isEmpty(hotelIdList)){
            logger.info(String.format("updateQieKeIncome hotelIdList is empty. params yesterdayStr[%s] todayStr[%s]", yesterdayStr, todayStr));
            return;
        }
        for(PmsRoomOrder pmsRoomOrder : hotelIdList){
            Long hotelId =  pmsRoomOrder.getLong("hotelId");
            if(hotelId == null){
                continue;
            }
            TopPmsRoomOrderQuery topPmsRoomOrderQuery = new TopPmsRoomOrderQuery();
            topPmsRoomOrderQuery.setCount(Constant.QIE_KE_TOP_NUM);
            topPmsRoomOrderQuery.setLimitBegin(0);
            topPmsRoomOrderQuery.setLimitEen(topPmsRoomOrderQuery.getBasePageSize());
            topPmsRoomOrderQuery.setYesterdayStr(yesterdayStr);
            topPmsRoomOrderQuery.setTodayStr(todayStr);
            topPmsRoomOrderQuery.setHotelId(hotelId);
            updateQieKeIncome(topPmsRoomOrderQuery);
        }

    }

    private void updateQieKeIncome(TopPmsRoomOrderQuery topPmsRoomOrderQuery) {
        //查找每日top订单
        List<PmsRoomOrder> pmsRoomOrderList = getTopPmsRoomOrder(topPmsRoomOrderQuery);
        logger.info(String.format("updateQieKeIncome top[%s] top pmsRoomOrderList size[%s]", Constant.QIE_KE_TOP_NUM.toString(), pmsRoomOrderList.size()+""));
        for(PmsRoomOrder pmsRoomOrder : pmsRoomOrderList){
            Long orderId =  pmsRoomOrder.getLong("orderId");
            String cityCode =  pmsRoomOrder.getStr("cityCode");
            Integer orderType =  pmsRoomOrder.getInt("Ordertype");
            Integer updateTopInvalidReasonToNullNum = updateTopInvalidReasonToNull(orderId);
            logger.info(String.format("updateTopInvalidReason updateTopInvalidReasonToNullNum num[%s]", updateTopInvalidReasonToNullNum));
            //切客收益
            PPay pPay = payDAO.getPayByOrderId(orderId);
            if(pPay == null){
                logger.warn(String.format("updateTopInvalidReason pPay is null orderId[%s]", orderId));
            }
            POrderLog pOrderLog = pOrderLogDAO.findPOrderLogByPay(pPay.getId());
            if(pOrderLog == null){
                logger.warn(String.format("updateTopInvalidReason pOrderLog is null params pPay id[%s]", pPay.getId()));
            }
            BigDecimal qiekeIncome = new BigDecimal(0);
            if(OrderTypeEnum.YF.getId().equals(orderType)){
                qiekeIncome = promoteConfigService.queryOnlineGiveHotel(cityCode);
            }else {
                qiekeIncome = promoteConfigService.queryOfflineGiveHotel(cityCode);
            }
            pOrderLog.setQiekeIncome(qiekeIncome);
            pOrderLogDAO.update(pOrderLog);
        }
    }


    public List<PmsRoomOrder> getTopPmsRoomOrder(TopPmsRoomOrderQuery topPmsRoomOrderQuery){
        List<PmsRoomOrder> pmsRoomOrderList = pmsRoomOrderDao.getPmsRoomOrderByCheckInTime(topPmsRoomOrderQuery.getYesterdayStr(),
                topPmsRoomOrderQuery.getTodayStr(), topPmsRoomOrderQuery.getHotelId(), topPmsRoomOrderQuery.getLimitBegin(), topPmsRoomOrderQuery.getLimitEen());
        if(CollectionUtils.isEmpty(pmsRoomOrderList)){
            return topPmsRoomOrderQuery.getPmsRoomOrderList();
        }
        for(PmsRoomOrder pmsRoomOrder : pmsRoomOrderList){
            if(topPmsRoomOrderQuery.getPmsRoomOrderList().size() >= topPmsRoomOrderQuery.getCount()){
                return topPmsRoomOrderQuery.getPmsRoomOrderList();
            }
            Integer invalidReason = pmsRoomOrder.getInt("Invalidreason");
            if(Integer.valueOf(OtaFreqTrvEnum.OVER_RANG.getId()) == invalidReason){
                topPmsRoomOrderQuery.getPmsRoomOrderList().add(pmsRoomOrder);
            }
        }
        if(topPmsRoomOrderQuery.getPmsRoomOrderList().size() >= topPmsRoomOrderQuery.getCount()){
            return topPmsRoomOrderQuery.getPmsRoomOrderList();
        }
        topPmsRoomOrderQuery.setLimitBegin(topPmsRoomOrderQuery.getLimitEen() + topPmsRoomOrderQuery.getBasePageSize() +1);
        return getTopPmsRoomOrder(topPmsRoomOrderQuery);
    }


    public OtaFreqTrvEnum getQiekeRuleReason(OtaOrder otaOrder){
        OtaFreqTrvEnum otaFreqTrvEnum = checkMobile(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkSysNo(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkIdentityCard(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkPayAccount(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkUserAdders(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }
        return OtaFreqTrvEnum.L1;
    }

    public boolean isOrderQiekeRuleCity(OtaOrder otaOrder){
        if(Constant.QIE_KE_CITY_MAP.containsKey(otaOrder.getCityCode())){
            return true;
        }else{
            return false;
        }
    }

    public List<Long> genTicketByCityCode(String cityCode, long mid){
        return new ArrayList<>();
    }
}
