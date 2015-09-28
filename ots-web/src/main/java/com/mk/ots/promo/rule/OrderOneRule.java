package com.mk.ots.promo.rule;

import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;

@Rule(name="距离上次订单15天", description="")
public class OrderOneRule extends BasicRule {

	public OrderOneRule() {
	}
	@Override
	public boolean evaluateConditions() {
		//1.订单⽤用户,ou-order user,完成订单⾏行为的⽤用户
		//2.B类⽤用户,BOU,⼀一个⽉月内完成订单数不超过2次的⽤用户
		//3.A类⽤用户,⼀一个⽉月内完成订单数超过2次(含2次)的⽤用户  每⽉月2次
		//4.沉默⽤用户,15天内打开APP次数不超过1次且未卸载APP的⽤用户
		return true;
	}
	
	@Override
	public void performActions() throws Exception {
		//1.常规券:30元优惠券 * 1(发放后30天内有效);
		//2.PUSH / sms
	}
}
