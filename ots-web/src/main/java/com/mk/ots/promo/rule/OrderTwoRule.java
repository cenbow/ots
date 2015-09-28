package com.mk.ots.promo.rule;

import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;

@Rule(name="每⽉25⽇", description="")
public class OrderTwoRule extends BasicRule {

	public OrderTwoRule() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean evaluateConditions() {
		//3.A类⽤用户,⼀一个⽉月内完成订单数超过2次(含2次)的⽤用户  每⽉月2次
		return true;
	}
	
	@Override
	public void performActions() throws Exception {
		//常规券:30元优惠券 * 1(发放后30天内有效);
		//push / sms
	}
	
}
