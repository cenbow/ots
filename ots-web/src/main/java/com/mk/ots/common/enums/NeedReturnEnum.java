package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public enum NeedReturnEnum {
	need("T","需要人工退款，其余部分已退款"),
	no("F","不需要人工退款（全部系统退款，无第三方支付）"),
	ok("O","不需要退款"),
	finish("F","已人工退款完成")
	;
	private final String id;
	private final String name;
	
	private NeedReturnEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return id;
	}
}
