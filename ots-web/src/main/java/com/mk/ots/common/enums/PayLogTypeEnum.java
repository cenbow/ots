package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum PayLogTypeEnum {
	xiaozhentou(1,"小枕头"),
	jifen(2,"积分"),
	chuzhi(3,"储值"),
	otherPay(4,"第三方支付"),
	;
	private final Integer id;
	private final String name;
	
	private PayLogTypeEnum(Integer id,String name){
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
}
