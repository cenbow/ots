package com.mk.ots.promo.service;

import java.util.Date;
import java.util.List;

import com.mk.ots.activity.model.BActiveChannel;
import com.mk.ots.common.enums.PromotionMethodTypeEnum;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.ticket.model.BPrizeInfo;

public interface IPromoService {

	/**
	 *发放优惠券给沉默用户. 
	 *发放券类型：常规券
	 *发放条件: 15天内打开APP次数不超过1次且未卸载APP的用户,且当月未领过优惠券的.
	 *通知方式：push
	 */
	public abstract void genTicketByUnActiveMember();

	/**
	 * 发放优惠券给活跃用户
	 * 发放券类型：常规券
	 * 发放条件: 一个月内打开APP次数超过2次（含2次）的用户,且当月未领过优惠券的
	 * 通知方式：push，短信
	 */
	public abstract void genTicketByActiveMember();

	/**
	 * 发放优惠券给订单用户
	 * 发放券类型：常规券
	 * 发放条件：每月二十五号发放，完成订单数超过2次（含2次）的用户
	 * 通知方式：push，短信
	 */
	public abstract void genTicketByMemberOrderNumAt25();

	/**
	 * 发放优惠券给订单用户
	 * 发放券类型：常规券
	 * 发放条件：每天晚上检测发放，完成订单行为的用户/一个月内完成订单数不超过2次的用户
	 * 通知方式：push，短信
	 */
	public abstract void genTicketByMemberOrderNumEveryDay();

	/**
	 * 发放注册新用户礼包
	 * 发放券类型：新用户礼包
	 * 通知方式：push
	 * @param mid
	 */
	public abstract void genTicketByAllRegNewMember(Long mid);

	/**
     * 
     * @param promotionid
     * @param mid
     * @param starttime
     * @param endtime
     * @param promotionMethodType
     * @param sourcecdkey 兑换码
     * @param channelid 渠道id
     * @return
     */
    public abstract List<Long> genCGTicket(long promotionid, long mid, Date starttime, Date endtime, PromotionMethodTypeEnum promotionMethodType,String sourcecdkey,Long channelid);

	/**
	 * @param activeid
	 * @return
	 */
	public abstract List<BPromotion> findByActiveId(Long activeid);
	
	/**
	 * @param bPromotionPrice
	 */
	public abstract void saveOrUpdate(BPromotionPrice bPromotionPrice);
	
	/**
	 * @param promotionTypeEnum
	 * @return
	 */
	public abstract List<BPromotion> findByPromotionType(PromotionTypeEnum promotionTypeEnum);

	/**
	 * 批量生成优惠码
	 * @param activeid 活动编码
	 * @param channelid 渠道编码
	 * @param batchno	生成批次
	 * @param gennum 生成码数量
	 * @param email 接收邮箱
	 */
	public abstract void genCouponCode(BActiveChannel bActiveChannel, String batchno, Long gennum, String email);

	/**
	 * 根据活动生成优惠券
	 * @param activeid
	 * @param mid
	 * @return
	 */
	public abstract List<Long> genTicketByActive(Long activeid, Long mid);
	
	
	/**
	 * 根据活动、设备生成奖品
	 * @param activeid
	 * @param mid
	 * @param ostype
	 * @return
	 */
	public abstract List<BPrizeInfo> genTicketByActive(Long activeid, Long mid, String ostype); 
	}
