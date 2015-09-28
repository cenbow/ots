package com.mk.ots.promo.rule;

import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;

@Rule(name="激活用户－ 即新⽤用户,nu-new user,安装并打开APP的⽤用户", description="")
public class ActiveMemberRule extends BasicRule {
	private String mid ;
	
	public ActiveMemberRule(String mid) {
		this.mid = mid;
	}

	@Override
	public boolean evaluateConditions() {
		return isNewAndInstallApp(mid);
	}
	
	@Override
	public void performActions() throws Exception {
		System.out.println("push 新⽤用户礼包消息");
	}
	
	private boolean isNewAndInstallApp(String mid){
		//TODO 判断新⽤用户,nu-new user,安装并打开APP的⽤用户
		return true;
	}
	
//	1) 新⽤用户礼包:整体50元券;
//	内含2张10元优惠券 (30天内有效) 1张30元优惠券(发放次⽇日可以使⽤用,30天内有效);
//	2) 常规券:30元优惠券 * 1(发放后30天内有效);
}
