package com.mk.ots.pay.module;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.pay.module.ali.AliPay;
import com.mk.ots.pay.module.query.QueryPayPram;
import com.mk.ots.pay.module.weixin.AppPay;
import com.mk.ots.pay.module.weixin.WeChat;

public class QueryPay {

	 public static void main(String[] args) {

		String orderid="1942596";
		System.out.println(fpay(orderid));
	}
	 
	private static String fpay(String orderid){
		 if(orderid==null || orderid.trim().length()<7 ||  orderid.trim().length()>10){
			 return "订单号有误";
		 }else{
			if (getLongOrderId(orderid) < 1151576) { // 之前的订单号被加长，不能通过此方法查询
				return "请查询(2015-07-25)开始的订单 【订单号大于1151575】";
			} else {

				return findPay(orderid,true);
			}
		 }
	}
	
	public static String seviceFindPay(String orderid ) {
		return findPay(orderid, true) ;
	}
	
	public static String HmsFindPay(String orderid ) {
		return findPay(orderid, false) ;
	}

	
	private static String findPay(String orderid,boolean isService) {
		QueryPayPram pram=findPay(orderid);
		if(!pram.isSuccess()){
			if(isService){
				return "订单号"+orderid+"去微信公众帐号 【正式、测试】 APP微信、支付宝都没有找到支付信息.";
			}else{
				return "未支付";
			}
		}else {
			if(isService){
				return "订单号"+orderid+"去"+pram.getBanktype().getName()+"查询到结果，"
						+ "状态是【"+pram.getPaystatus().getName()+"】"
						+ "交易金额是："+pram.getPrice()+"元，"
						+ "能作为"+pram.getPaystatus().getName()+"凭证！";
			}else{
				return pram.getPaystatus().getName();
			}
		}
	}
	
	public static QueryPayPram findPay(String orderid ) {
		orderid=orderid.trim();
		QueryPayPram pram=new QueryPayPram();
		pram.setOrderid(orderid);
		//先去微信公众帐号
		pram=WeChat.onlyQuery(orderid,pram);     //  QueryPayPram pram
		if(!pram.isSuccess()){
			//微信APP
			pram=AppPay.onlyQuery(orderid, pram);
			if(!pram.isSuccess()){
				//支付宝
				pram=AliPay.onlyQuery(orderid,pram);
				if(!pram.isSuccess()){
					//去微信公众帐号 + a
					pram=WeChat.onlyQuery("a"+orderid,pram);
					if(!pram.isSuccess()){
						//去微信公众帐号 + b
						pram=WeChat.onlyQuery("b"+orderid,pram);
						if(!pram.isSuccess()){
							//去微信公众帐号 + c
							pram=WeChat.onlyQuery("c"+orderid,pram);
							if(!pram.isSuccess()){
								//去微信公众帐号 + d
								pram=WeChat.onlyQuery("d"+orderid,pram);
								if(!pram.isSuccess()){
								   //测试 微信公众帐号
									pram=WeChat.onlyQueryTest(orderid,pram);
								}
							}
						}
					}
				}
			}
		}
		return pram;
	}
 
	
	/** 得到Long类型orderId */
	private static Long getLongOrderId(String orderid) {
		try {
			return Long.parseLong(orderid);
		} catch (NumberFormatException e1) {
			throw MyErrorEnum.errorParm.getMyException("订单号只能是数字");
		}
	}

}