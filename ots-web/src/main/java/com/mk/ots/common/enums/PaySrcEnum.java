package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum PaySrcEnum {
	order(1,"订房"),
	;
	private final Integer id;
	private final String name;
	
	private PaySrcEnum(Integer id,String name){
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
	
	public static void main(String[] args) {
		System.out.println(PaySrcEnum.order.ordinal());
	}
}
