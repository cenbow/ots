package com.mk.ots.common.enums;

public enum MessageSendTypeEnum {

	sms(1,"短信"),
	pushmsg(2,"推送消息");
	
	private final Integer id;
	private final String name;
	
	private MessageSendTypeEnum(Integer id,String name){
		this.id=id;
		this.name=name;
	}

	public static MessageTypeEnum getEnumById(Integer id){
		for (MessageTypeEnum enu : MessageTypeEnum.values()) {
			if(enu.getId().equals(id)){
				return enu;
			}
		}
		return null;
	}
	
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
