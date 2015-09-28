package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

public enum PmsSendEnum {
	None(50, "未下发(收回)"),
	AlreadyNoneResponse(100,"触发下发(收回)未响应"),
	ResponseSuccess(200,"下发(收回)成功");
	
	private Integer id ;
	private String description ;
	
	PmsSendEnum(Integer id, String description){
		this.id = id;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static PmsSendEnum getByID(Integer id){
		for (PmsSendEnum temp : PmsSendEnum.values()) {
			if(temp.getId().intValue()==id.intValue()){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
