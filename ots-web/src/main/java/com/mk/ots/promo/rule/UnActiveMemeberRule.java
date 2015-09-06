package com.mk.ots.promo.rule;

import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;

/**
 * @author nolan
 *
 */
@Rule(name = "未激活用户:跟业务相关特指微信订阅号⽤用户", description = "微信关注后，发送新用户礼包")
public class UnActiveMemeberRule extends BasicRule {
	
	private String mid;
	
	public UnActiveMemeberRule(String mid) {
		this.mid = mid;
	}
	
	@Override
	public boolean evaluateConditions() {
		return isContainMember(this.mid);
	}
	
	@Override
	public void performActions() throws Exception {
		//TODO 调用微信接口发送新⽤用户礼包消息
		System.out.println("调用微信接口发送新⽤用户礼包消息");
	}
	
	private boolean isContainMember(String mid){
		//TODO 调用接口,判断用户是否关注微信
		return true;
	}
}
