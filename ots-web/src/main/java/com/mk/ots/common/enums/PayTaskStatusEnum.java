package com.mk.ots.common.enums;

public enum PayTaskStatusEnum {
	
	INIT(1,"初始状态"),
	FINISH(2,"完成状态"),
	;
	
	private Integer code;
	
	private String desc;

	private PayTaskStatusEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return code.toString();
	}
}
