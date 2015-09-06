package com.mk.ots.common.enums;

/**
 * 优惠券领取方式
 * @author nolan
 *
 */
public enum PromotionMethodTypeEnum {
	
	/**
	 * 系统自动发放
	 */
	AUTO(1),
	/**
	 * 手工领取
	 */
	HAND(2),
	/**
	 * 需手动领取且需激活
	 */
	HAND_NEEDACTIVE(3);

	private int id;
	
	private PromotionMethodTypeEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
