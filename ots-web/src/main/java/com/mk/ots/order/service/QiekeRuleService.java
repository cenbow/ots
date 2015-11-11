package com.mk.ots.order.service;

import com.mk.ots.common.enums.OtaFreqTrvEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.dao.OrderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Thinkpad on 2015/11/11.
 */
@Service
public class QiekeRuleService {
    private static Logger logger = LoggerFactory.getLogger(QiekeRuleService.class);

    @Autowired
    private OrderDAO orderDAO;

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
        List<OtaOrder> orderList = this.orderDAO.findOtaOrderByMid(mid, statusList);

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

        //
        return OtaFreqTrvEnum.L1;
    }

    /**
     * 手机唯一码必须是第一次，入住并离店的订单（若手机唯一码未取到，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkSysNo(OtaOrder otaOrder){
        return OtaFreqTrvEnum.IN_FREQUSER;
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
        return OtaFreqTrvEnum.IN_FREQUSER;
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
