package com.mk.ots.common.enums;


import com.mk.framework.exception.MyErrorEnum;

/**
 *
 * @author shellingford
 * @version 2015年2月16日
 */
public enum  BPromoStatuEnum {
	newProduce(1,"生成"),
	inWarehouse(2,"入库"),
	activite(3,"激活"),
	beuse(4,"使用"),
	Logout(5,"注销"),
	;
	private final Integer type;
	private final String name;

	private BPromoStatuEnum(Integer type,String name){
		this.type=type;
		this.name=name;
	}

	public Integer getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public static BPromoStatuEnum getByID(Integer type){
		for (BPromoStatuEnum temp : BPromoStatuEnum.values()) {
			if(temp.getType().equals(type)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("卷状态不对");
	}
}
