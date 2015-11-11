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

    private IPayService iPayService;
    /**
     * 手机号必须是第一次，入住并离店的订单
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkMobile(OtaOrder otaOrder){
        if (null == otaOrder) {
            return OtaFreqTrvEnum.PHONE_NOT_FIRST;
        }
        Long mid = otaOrder.getMid();

        //订单状态
        List<OtaOrderStatusEnum> statusList = new ArrayList<>();
        statusList.add(OtaOrderStatusEnum.CheckIn);
        statusList.add(OtaOrderStatusEnum.Account);
        statusList.add(OtaOrderStatusEnum.CheckOut);

        //查询该用户下所有 入住、挂单、离店酒店
        List<OtaOrder> orderList = this.orderService.findOtaOrderByMid(mid, statusList);

        //排除这次订单外，其他是否还有订单
        Set<Long> orderSet = new HashSet<>();
        for(OtaOrder order : orderList) {
            orderSet.add(order.getId());
        }
        orderSet.remove(otaOrder.getId());

        //其他是否还有订单，人为非第一次使用
        if (orderSet.size() > 0 ) {
            return OtaFreqTrvEnum.PHONE_NOT_FIRST;
        }

        //通过
        return OtaFreqTrvEnum.L1;
    }

    /**
     * 系统号必须是第一次，入住并离店的订单（若系统号未取到，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkSysNo(OtaOrder otaOrder){
        if (null == otaOrder) {
            return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
        }

        Integer orderMethod = otaOrder.getOrderMethod();
        if (orderMethod ==  OrderMethodEnum.WECHAT.getId()) {
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
            return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;

        } else if (orderMethod == OrderMethodEnum.ANDROID.getId()) {
            //安卓
            Long orderId = otaOrder.getId();

            OtaOrderMac orderMac = otaOrderMacMapper.selectByOrderId(orderId);
            if (null == orderMac) {
                return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
            }
            String deviceimei = orderMac.getDeviceimei();
            if (null == deviceimei) {
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

            //去除本次账号
            Set<Long> orderIdSet = new HashSet<>();
            for(OtaOrderMac dbOrderMac : orderMacList) {
                Long macOrderId = dbOrderMac.getOrderid();
                orderIdSet.add(macOrderId);
            }
            orderIdSet.remove(orderId);

            //
            if (orderIdSet.size() > 0) {
                return OtaFreqTrvEnum.DEVICE_NUM_NOT_FIRST;
            } else {
                return  OtaFreqTrvEnum.L1;
            }
        } else if (orderMethod == OrderMethodEnum.IOS.getId()) {
            //IOS
            Long orderId = otaOrder.getId();
            OtaOrderMac orderMac = otaOrderMacMapper.selectByOrderId(orderId);
            if (null == orderMac) {
                return OtaFreqTrvEnum.DEVICE_NUM_IS_NULL;
            }
            String uuid = orderMac.getUuid();
            if (null == uuid) {
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

            //去除本次账号
            Set<Long> orderIdSet = new HashSet<>();
            for(OtaOrderMac dbOrderMac : orderMacList) {
                Long macOrderId = dbOrderMac.getOrderid();
                orderIdSet.add(macOrderId);
            }
            orderIdSet.remove(orderId);

            //
            if (orderIdSet.size() > 0) {
                return OtaFreqTrvEnum.DEVICE_NUM_NOT_FIRST;
            } else {
                return  OtaFreqTrvEnum.L1;
            }
        } else {
            //其他类型返回系统号为空
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
        if (null == otaOrder) {
            return OtaFreqTrvEnum.NOT_CHECKIN;
        }
        //判断是否到付
        Integer orderType = otaOrder.getOrderType();
        if (orderType == OrderTypeEnum.PT.getId()) {
            return OtaFreqTrvEnum.OFFLINE_PAY;
        }

        //

        Long orderId = otaOrder.getId();
        PPay pay = this.iPayService.findPayByOrderId(orderId);
        String userId = pay.getUserid();

        List<PPay> payList = this.iPayService.findByUserId(userId);
        //去除本次账号
        Set<Long> payIdSet = new HashSet<>();
        for(PPay dbPay : payList) {
            Long dbPayId = dbPay.getId();
            payIdSet.add(dbPayId);
        }
        payIdSet.remove(pay.getId());

        //
        if (payIdSet.size() > 0) {
            return OtaFreqTrvEnum.ZHIFU_NOT_FIRST;
        } else {
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
