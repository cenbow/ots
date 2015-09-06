package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum PPayInfoTypeEnum {
	Z2P(1,"第三方支付入"),
	C2P(2,"储值入"),
	Y2P(3,"优惠券入"),
	J2P(4,"积分入"),
	X2P(5,"小枕头入"),
	Z2U(6,"第三方返还"),
	C2U(7,"储值退"),
	Y2U(8,"优惠券退"),
	J2U(9,"积分退"),
	X2U(10,"小枕头退"),
	X2C(11,"小枕头转储值"),
	Z2UX(12,"第三方返还（虚拟）"),
	Z2C(13,"第三方退储值"),
	C2Z(14,"储值转第三方"),
	C2X(15,"储值转小枕头"),
	SENDLEZHU(16,"下发乐住币"),
//	RESENDLEZHU(17,"补发乐住币"),
//	REFUNDLEZHU(18,"手动收回乐住币"),
	REFUNDLEZHU4LONG(19,"常住人收回乐住币"),
	REFUNDLEZHU4QIEKE(20,"切客4小时内离店收回乐住币")
	;
	private final Integer id;
	private final String name;
	
	private PPayInfoTypeEnum(Integer id,String name){
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
