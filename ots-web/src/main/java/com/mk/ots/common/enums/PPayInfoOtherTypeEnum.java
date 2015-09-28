package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum PPayInfoOtherTypeEnum {
	alipay(1,"支付宝"),
	wechatpay(2,"微信公共帐号支付"),
	wxpay(3,"App微信支付")
	;
	private final Integer id;
	private final String name;
	
	private PPayInfoOtherTypeEnum(Integer id,String name){
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
	public static PPayInfoOtherTypeEnum getByID(Integer id){
		for (PPayInfoOtherTypeEnum temp : PPayInfoOtherTypeEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
