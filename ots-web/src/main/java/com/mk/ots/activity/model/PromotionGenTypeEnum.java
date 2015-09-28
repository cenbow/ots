package com.mk.ots.activity.model;

/**
 * 活动优惠券生成类型
 * @author nolan
 * 
 */
public enum PromotionGenTypeEnum {
	NONE(0),
	ALL(1),
	RANDOM(2),
	PRIZERANDOM(3),
	HANDPROMOTION(4);//手动指定优惠券;
	private int type;
	
	private PromotionGenTypeEnum(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
}
