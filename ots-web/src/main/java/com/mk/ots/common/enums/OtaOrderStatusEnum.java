package com.mk.ots.common.enums;

import com.mk.framework.exception.MyErrorEnum;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */

public enum OtaOrderStatusEnum {
//	temp(10,"通信pms之前"),
//	create(50,"创建订单"),
//	SubmitOrder(100,"订单提交"),
	

	wait(110,"等待前台确认"),									
	WaitPay(120,"等待支付"),				//创建订单 都是 OrderTypeEnum.YF ,PayStatusEnum.waitPay
											//可以更改为到付 只修改OrderTypeEnum.PT ,PayStatusEnum.doNotPay
	Confirm(140,"已确认"),				//已经付款 OrderTypeEnum.YF PayStatusEnum.alreadyPay
	CheckInOnline(160,"网上入住"),		//（通过ota网上入住之后，实际入住之前的状态）
	CheckIn(180,"入住"),					//（实际入住之后的状态）（实际入住之后的状态）
	Account(190,"挂账"),					 // （已退房，未结账）
	CheckOut(200,"离店"),			     // （用户离店）
	CancelByU_WaitRefund(510,"取消"),    // （订单被用户取消，等待退款）
	CancelBySystem(511,"取消"),		     // （订单被系统取消）
	CancelByU_Refunded(512,"取消"),	     // （订单被用户取消，已退款）
	CancelByU_NoRefund(513,"取消"),	     // （订单被用户取消，无需退款）
	CancelByPMS(514,"PMS取消订单"),	     // （PMS取消订单）
	NotIn(520,"未到"),				     // （用户未到）
	;
	private final Integer id;
	private final String name;
	
	private OtaOrderStatusEnum(Integer id,String name){
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
	
	public static OtaOrderStatusEnum getByID(Integer id){
		for (OtaOrderStatusEnum temp : OtaOrderStatusEnum.values()) {
			if(temp.getId().equals(id)){
				return temp;
			}
		}
		throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
	}
}
