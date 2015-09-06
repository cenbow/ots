package com.mk.ots.ticket.model;

/**
 * 用户优惠券使用记录
 * @author nolan
 *
 */
public class UTicketUseRecord {
	/**
	 * 自增主键
	 */
	private String id;
	
	/**
	 * 使用时间
	 */
	private String usetime;
	
	/**
	 * 用户券id
	 */
	private String tid;
	
	/**
	 * 券id
	 */
	private String promotionid;
	
	/**
	 * 支付id
	 */
	private String payid;
	
	/**
	 * 是否是退回记录	T/F
	 */
	private String rejectStatus;
	
	/**
	 * 会员id
	 */
	private String mid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsetime() {
		return usetime;
	}

	public void setUsetime(String usetime) {
		this.usetime = usetime;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getPromotionid() {
		return promotionid;
	}

	public void setPromotionid(String promotionid) {
		this.promotionid = promotionid;
	}

	public String getPayid() {
		return payid;
	}

	public void setPayid(String payid) {
		this.payid = payid;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
	
}
