package com.mk.ots.common.enums;
/***
 * @author zhouhuangling
 */

public enum TicketReturnTypeEnum {
	ticketsAvaliable("1","订单有效的优惠券"),
	webQiekeTicketUsed("2","切客超过使用次数"),
	sameRoomTicketUsed("3","切客同房间已使用"),
	;
	private final String id;
	private final String name;
	
	private TicketReturnTypeEnum(String id,String name){
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
