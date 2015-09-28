package com.mk.ots.common.enums;



public enum PayTitleTypeEnum {
	needwait("待商议",new ButtonTypeEnum[]{ButtonTypeEnum.cancel,ButtonTypeEnum.pay,ButtonTypeEnum.edit}), 
	doNotPayCanPay("未支付",new ButtonTypeEnum[]{ButtonTypeEnum.cancel,ButtonTypeEnum.pay,ButtonTypeEnum.edit}),
	doNotPaynotPay("未支付",new ButtonTypeEnum[]{ButtonTypeEnum.cancel,ButtonTypeEnum.edit}),
//	pay("已支付",new ButtonTypeEnum[]{ButtonTypeEnum.refund,ButtonTypeEnum.checkin,ButtonTypeEnum.edit}),
	pay("已支付",new ButtonTypeEnum[]{ButtonTypeEnum.cancel,ButtonTypeEnum.edit}),
	arrivePay("到店付",new ButtonTypeEnum[]{ButtonTypeEnum.cancel,ButtonTypeEnum.edit}),
	CheckIn("已入住",new ButtonTypeEnum[]{ButtonTypeEnum.continuedToLive,ButtonTypeEnum.meet,ButtonTypeEnum.evaluation }),
	OK("已离店",new ButtonTypeEnum[]{ButtonTypeEnum.evaluation}),
	cancel("已取消",new ButtonTypeEnum[]{ButtonTypeEnum.delete }),
	cancel_waitPay("等待退款",new ButtonTypeEnum[]{ButtonTypeEnum.delete }),
	NotIn("未到",new ButtonTypeEnum[]{ButtonTypeEnum.delete }),
	;
	private String name;
	private ButtonTypeEnum[] buttons;

	private PayTitleTypeEnum(String name, ButtonTypeEnum[] buttons) {
		this.name = name;
		this.buttons = buttons;
		
	}
	
	public String getName() {
		return name;
	}

	public ButtonTypeEnum[] getButtons() {
		return buttons;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
