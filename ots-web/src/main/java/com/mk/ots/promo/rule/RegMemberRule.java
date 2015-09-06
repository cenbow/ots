package com.mk.ots.promo.rule;

import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;

@Rule(name="注册用户", description="注册后新礼包发放")
public class RegMemberRule  extends BasicRule {
	
	private String mid;
	
	public RegMemberRule(String mid) {
		this.mid = mid;
	}
	
	@Override
	public boolean evaluateConditions() {
		//判断是否新建用户 当前时间－注册时间<=1min
		return true;
	}
	
	@Override
	public void performActions() throws Exception {
		//1. 添加券并绑定给用户
		//		1) 新⽤用户礼包:整体50元券;
		//		内含2张10元优惠券 (30天内有效) 1张30元优惠券(发放次⽇日可以使⽤用,30天内有效);
		//2. PUSH、短信
	}
}
