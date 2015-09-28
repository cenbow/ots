package com.mk.ots.ticket.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.ots.common.enums.PromotionMethodTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.ticket.service.parse.ITicketParse;

/**
 * 用户优惠券
 *
 * @author nolan
 */
public class UTicket {

	private static final Logger logger = LoggerFactory.getLogger(UTicket.class);
	/**
	 * 自增主键
	 */
	@NotNull
	private Long id;

	/**
	 * 所属用户id
	 */
	@NotNull
	private Long mid;

	/**
	 * 使用时间
	 */
	@NotNull
	private Date usetime;

	/**
	 * 状态： 1. 未使用 2. 已使用
	 */
	@NotNull
	private int status;

	/**
	 * 券id
	 */
	@NotNull
	private String promotionid;
	/**
	 * pms订单id
	 */
	private Long otaorderid;
	/**
	 * 活动id
	 */
	private Long activityid;
	/**
	 * 促销时间
	 */
	private Date promotiontime;

	/**
	 * 优惠券自动发放还是需用户领取
	 */
	private PromotionMethodTypeEnum promotionmethod;
	
	private BPromotion promotion;

	public BPromotion getPromotion() {
		return this.promotion;
	}

	public void setPromotion(BPromotion promotion) {
		this.promotion = promotion;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMid() {
		return this.mid;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}

    
	public Date getUsetime() {
		return usetime;
	}

	public void setUsetime(Date usetime) {
		this.usetime = usetime;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPromotionid() {
		return this.promotionid;
	}

	public void setPromotionid(String promotionid) {
		this.promotionid = promotionid;
	}

	public ITicketParse createParseBean(OtaOrder otaOrder) {
		try {
			Object ob = Class.forName(this.getPromotion().getClassname()).newInstance();
			ITicketParse parse = (ITicketParse) ob;
			parse.init(this, this.getPromotion());
			parse.parse(otaOrder);
			return parse;
		} catch (Throwable e) {
			UTicket.logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	public Long getOtaorderid() {
		return otaorderid;
	}

	public void setOtaorderid(Long otaorderid) {
		this.otaorderid = otaorderid;
	}

	public Long getActivityid() {
		return activityid;
	}

	public void setActivityid(Long activityid) {
		this.activityid = activityid;
	}

	public Date getPromotiontime() {
		return promotiontime;
	}

	public void setPromotiontime(Date promotiontime) {
		this.promotiontime = promotiontime;
	}

	public PromotionMethodTypeEnum getPromotionmethodtype() {
		return promotionmethod;
	}

	public void setPromotionmethodtype(PromotionMethodTypeEnum promotionmethodtype) {
		this.promotionmethod = promotionmethodtype;
	}

	@Override
	public String toString() {
		return "UTicket [id=" + id + ", mid=" + mid + ", usetime=" + usetime
				+ ", status=" + status + ", promotionid=" + promotionid
				+ ", otaorderid=" + otaorderid + ", activityid=" + activityid
				+ ", promotiontime=" + promotiontime + ", promotionmethodtype="
				+ promotionmethod + ", promotion=" + promotion + "]";
	}
	
}
