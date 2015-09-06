package com.mk.ots.pay.module.weixin.pay.common;

/** 
 * 
 * @author futao.xiao
 * @version 2015年05月21日
 */

public enum WxType {
	app(1,"APP端支付"),
	wechat(2,"公众帐号支付"),   
	test_wechat(3,"测试公众帐号支付"),
	;
	
	private final Integer id;
	private final String name;
	
	private WxType(Integer id,String name ){
		this.id=id;
		this.name=name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	
	
}
