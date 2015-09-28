package com.mk.ots.pay.module.weixin;



public class Main {

	public static void main(String[] args) {
		 String weixinpayid="1009810960201506080222005336";
		 
//		 boolean b=WeChat.refundQuery(weixinpayid);
		String s= WeChat.query( weixinpayid,2000);
		 System.out.println("------------"+s);
		 
		 
		 
		 
//		 String orderid="117983";
//		 String body="测试支付1";
//		 String notifyurl="http://www.imike.com/ots";
//		 int price=6800;
//		 WeChat.queryOrder(orderid);
//		 WeChat.refund(weixinpayid, orderid, price);
//		 String payid = WeChat.query( weixinpayid, price);
//         System.out.println(payid);
		 
//		 String id=WeChat.
		 
	}

}
