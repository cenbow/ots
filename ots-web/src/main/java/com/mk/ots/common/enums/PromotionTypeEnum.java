package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum PromotionTypeEnum {
	immReduce(1, "立减"),
	qieke(2, "切客优惠码"),
	yijia(3, "议价优惠码"),
	XLB(4, "新用户礼包"),
	LLB(5, "老用户礼包"),
	LJIAN(7, "立减优惠券"),
	CGJ(6, "常规优惠券");
	
	private final Integer id;
	private final String name;
	
	private PromotionTypeEnum(Integer id,String name){
		this.id=id;
		this.name=name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return id.toString();
	}
	
	public static PromotionTypeEnum getByID(Integer id){
        for (PromotionTypeEnum temp : PromotionTypeEnum.values()) {
            if(temp.getId().equals(id)){
                return temp;
            }
        }
        throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
    }
}
