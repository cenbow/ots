package com.mk.ots.order.service;

import com.google.common.base.Optional;
import com.mk.ots.common.enums.OrderMethodEnum;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaFreqTrvEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.mapper.OtaOrderMacMapper;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.model.OtaOrderMac;
import com.mk.ots.pay.model.PPay;
import com.mk.ots.pay.service.IPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Thinkpad on 2015/11/11.
 */
@Service
public class QiekeRuleService {
    private static Logger logger = LoggerFactory.getLogger(QiekeRuleService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OtaOrderMacMapper otaOrderMacMapper;

    @Autowired
    private IMemberService iMemberService;

    @Autowired
    private IPayService iPayService;
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
                String openId = member.getOpenid();
                List<UMember> uMemberList = iMemberService.findUMemberByOpenId(openId);

                //去除本次账号
                Set<Long> memberIdSet = new HashSet<>();
                for (UMember dbMember : uMemberList) {
                    Long dbMid = dbMember.getId();
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
            List<OtaOrderStatusEnum> statusList = new ArrayList<>();
            statusList.add(OtaOrderStatusEnum.CheckIn);
            statusList.add(OtaOrderStatusEnum.Account);
            statusList.add(OtaOrderStatusEnum.CheckOut);

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
            List<OtaOrderStatusEnum> statusList = new ArrayList<>();
            statusList.add(OtaOrderStatusEnum.CheckIn);
            statusList.add(OtaOrderStatusEnum.Account);
            statusList.add(OtaOrderStatusEnum.CheckOut);
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
        return OtaFreqTrvEnum.IN_FREQUSER;
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
        return OtaFreqTrvEnum.IN_FREQUSER;
    }

    /**
     * 用户来源必须是切客来源（u_manber中comefromtype=BQK）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkUserSource(OtaOrder otaOrder){
        return OtaFreqTrvEnum.IN_FREQUSER;
    }

    /**
     * 每个酒店每天前10个拉新订单有效，超过10个的，即使满足上述7条，酒店也不能获得更多的拉新收益（10个这个参数可以配置）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkOrderNumberThreshold(OtaOrder otaOrder){
        return OtaFreqTrvEnum.IN_FREQUSER;
    }
}
