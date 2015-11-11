package com.mk.ots.order.service;

import com.mk.ots.common.enums.OtaFreqTrvEnum;
import com.mk.ots.order.bean.OtaOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Thinkpad on 2015/11/11.
 */
@Service
public class QiekeRuleService {
    private static Logger logger = LoggerFactory.getLogger(QiekeRuleService.class);
    /**
     * 手机号必须是第一次，入住并离店的订单
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkMobile(OtaOrder otaOrder){
        return OtaFreqTrvEnum.IN_FREQUSER;
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
