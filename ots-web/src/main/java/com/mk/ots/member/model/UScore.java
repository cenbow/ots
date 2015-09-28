package com.mk.ots.member.model;

/**
 * 积分/储值变化
 * 
 * @author nolan
 */
public class UScore {
	
	/**
	 * 自增主键 
	 */
	private String id;
	
	/**
	 * 积分/储值等变化数值
	 */
	private int score;
	
	/**
	 * 积分类型:
	 * 	2.积分
	 * 	3.枕头
	 * 	4.储值
	 */
	private int type;
	
	/**
	 * 支付id
	 */
	private int payid;

	/**
	 * 支付类型：
	 * 	1. 预订房间
	 */
	private int paytype;
	
	/**
	 * 发生时间
	 */
	private String actiontime;
	
	/**
	 * 用户id 
	 */
	private String mid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPayid() {
		return payid;
	}

	public void setPayid(int payid) {
		this.payid = payid;
	}

	public int getPaytype() {
		return paytype;
	}

	public void setPaytype(int paytype) {
		this.paytype = paytype;
	}

	public String getActiontime() {
		return actiontime;
	}

	public void setActiontime(String actiontime) {
		this.actiontime = actiontime;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
}
