package com.mk.ots.promo.service;

import com.mk.ots.activity.model.BActiveChannel;
import com.mk.ots.common.enums.PromotionMethodTypeEnum;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.ticket.model.BPrizeInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IPromoService {

    /**
     * 发放优惠券给沉默用户.
     * 发放券类型：常规券
     * 发放条件: 15天内打开APP次数不超过1次且未卸载APP的用户,且当月未领过优惠券的.
     * 通知方式：push
     */
    void genTicketByUnActiveMember();

    /**
     * 发放优惠券给活跃用户
     * 发放券类型：常规券
     * 发放条件: 一个月内打开APP次数超过2次（含2次）的用户,且当月未领过优惠券的
     * 通知方式：push，短信
     */
    void genTicketByActiveMember();

    /**
     * 发放优惠券给订单用户
     * 发放券类型：常规券
     * 发放条件：每月二十五号发放，完成订单数超过2次（含2次）的用户
     * 通知方式：push，短信
     */
    void genTicketByMemberOrderNumAt25();

    /**
     * 发放优惠券给订单用户
     * 发放券类型：常规券
     * 发放条件：每天晚上检测发放，完成订单行为的用户/一个月内完成订单数不超过2次的用户
     * 通知方式：push，短信
     */
    void genTicketByMemberOrderNumEveryDay();

    /**
     * 发放注册新用户礼包
     * 发放券类型：新用户礼包
     * 通知方式：push
     *
     * @param mid
     */
    void genTicketByAllRegNewMember(Long mid);

    /**
     * @param promotionid
     * @param mid
     * @param starttime
     * @param endtime
     * @param promotionMethodType
     * @param sourcecdkey
     * @param channelid
     * @param hardwarecode
     * @return
     */
    List<Long> genCGTicket(long promotionid, long mid, Date starttime, Date endtime, PromotionMethodTypeEnum promotionMethodType, String sourcecdkey, Long channelid, String hardwarecode);

    /**
     * @param activeid
     * @return
     */
    List<BPromotion> findByActiveId(Long activeid);

    /**
     * @param bPromotionPrice
     */
    void saveOrUpdate(BPromotionPrice bPromotionPrice);

    /**
     * @param promotionTypeEnum
     * @return
     */
    List<BPromotion> findByPromotionType(PromotionTypeEnum promotionTypeEnum);


    /**
     * 批量生成优惠码
     *
     * @param bActiveChannel 渠道
     * @param batchno        生成批次
     * @param gennum         生成码数量
     * @param email          接收邮箱
     */
    void genCouponCode(BActiveChannel bActiveChannel, String batchno, Long gennum, String email);

    /**
     * 根据活动生成优惠券
     *
     * @param activeid
     * @param mid
     * @return
     */
    List<Long> genTicketByActive(Long activeid, Long mid, String hardwarecode);

    /**
     * @param activeid
     * @param mid
     * @return
     */
    List<Long> genTicketByActive(Long activeid, Long mid);


    /**
     * 根据活动、设备生成奖品
     *
     * @param activeid
     * @param mid
     * @param ostype
     * @return
     */
    List<BPrizeInfo> tryLuckByActive(Long activeid, Long mid, String ostype);

    /**
     * 根据订单id判断该订单是否使用首单优惠券
     *
     * @param orderId
     * @return
     */
    boolean checkFirstOrderByOrderId(Long orderId);

    /**
     * 判断此设备是否领取过首单优惠券
     *
     * @param hardwarecode
     * @return
     */
    boolean isGetFirstOrderPromotion(String hardwarecode);

    /**
     * 发放指定名称指定价格的优惠券
     * @param activeId
     * @param mid
     * @param name
     * @param descr
     * @param price
     * @param platformtype
     *
     * @return
     */
    public List<Long> genCGTicketByPrice(
            long activeId, long mid, String name,String descr, BigDecimal price, Integer platformtype);
}
