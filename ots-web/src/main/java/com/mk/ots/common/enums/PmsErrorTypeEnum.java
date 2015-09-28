package com.mk.ots.common.enums;
/**
 *
 * @author shellingford
 * @version 2015年1月22日
 */
public enum PmsErrorTypeEnum {
	payToPmsError(1,"推送支付价格失败"),
	cancelPayToPmsError(2,"取消支付失败"),
	changePriceToPmsError(3,"修改pms价格失败")
	;
	
	private final Integer id;
	private final String name;
	
	private PmsErrorTypeEnum(Integer id,String name){
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
