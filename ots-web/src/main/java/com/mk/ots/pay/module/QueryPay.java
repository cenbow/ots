package com.mk.ots.pay.module;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.pay.module.ali.AliPay;
import com.mk.ots.pay.module.weixin.AppPay;
import com.mk.ots.pay.module.weixin.WeChat;

public class QueryPay {

	 public static void main(String[] args) {
		String orderid="1151571";
		System.out.println(fpay(orderid));
	}
	 
	 
	 
	 
	 
	 
	 
	private static String fpay(String orderid){
		 if(orderid==null || orderid.trim().length()<7 ||  orderid.trim().length()>10){
			 return "订单号有误";
		 }else{
			if (getLongOrderId(orderid) < 1151576) { // 之前的订单号被加长，不能通过此方法查询
				return "请查询(2015-07-25)开始的订单 【订单号大于1151575】";
			} else {
				return findPay(orderid);
			}
		 }
	}
	 
	/** 得到Long类型orderId */
	private static Long getLongOrderId(String orderid) {
		try {
			return Long.parseLong(orderid);
		} catch (NumberFormatException e1) {
			throw MyErrorEnum.errorParm.getMyException("订单号只能是数字");
		}
	}
	
	public static String findPay(String orderid) {
		String rs=null;
		orderid=orderid.trim();
		//先去微信公众帐号
		rs=WeChat.onlyQuery(orderid);
		if(rs==null){
			//微信APP
			rs=AppPay.onlyQuery(orderid);
			if(rs==null){
				//支付宝
				rs=AliPay.onlyQuery(orderid);
				if(rs==null){
					//去微信公众帐号 + a
					rs=WeChat.onlyQuery("a"+orderid);
					if(rs==null){
						//去微信公众帐号 + b
						rs=WeChat.onlyQuery("b"+orderid);
						if(rs==null){
							//去微信公众帐号 + c
							rs=WeChat.onlyQuery("c"+orderid);
							if(rs==null){
								//去微信公众帐号 + d
								rs=WeChat.onlyQuery("d"+orderid);
								if(rs==null){
								   //测试 微信公众帐号
								  rs=WeChat.onlyQueryTest(orderid);
								}
							}
						}
					}
				}
			}
		}
		if(rs==null){
			rs="订单号"+orderid+"去微信公众帐号 【正式、测试】、APP微信、支付宝都没有找到支付信息.";
		}
		return rs;
	}
	
	
	
	
}