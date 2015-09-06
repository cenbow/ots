package com.mk.ots.common.enums;

/**
 * 
 * @author zhouhuangling
 * @date   2015年3月16日下午4:59:42
 */

public enum MessageTypeEnum {
	normal("1","普通短信"),
	audioMessage("2","语音验证码");
	
	private final String id;
	private final String name;
	
	private MessageTypeEnum(String id,String name){
		this.id=id;
		this.name=name;
	}

	public static MessageTypeEnum getEnumById(String id){
		for (MessageTypeEnum enu : MessageTypeEnum.values()) {
			if(enu.getId().equals(id)){
				return enu;
			}
		}
		return null;
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
