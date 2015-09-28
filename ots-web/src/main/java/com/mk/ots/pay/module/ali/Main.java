package com.mk.ots.pay.module.ali;

import java.math.BigDecimal;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.thrift.Cassandra.AsyncProcessor.system_add_column_family;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.mk.ots.pay.model.PPayInfo;

public class Main {

	public static void main(String[] args) {
		
		String orderid="318019 ";
		String alipayid="2015060900001000240053740056";
		String price="70.00";
		String s=AliPay.refund(orderid, alipayid, price);
//		System.out.println("s=="+s);
		
//		String s=AliPay.query(orderid, null);
//		System.out.println(s);
		
		boolean b=AliPay.refundQuery(orderid);
		
//		System.out.println(b);
//		String url="https://mapi.alipay.com/gateway.do?_input_charset=utf-8&sign=7abb4e114a614f62efd50d3b9f82a9d6&_input_charset=utf-8&sign_type=MD5&detail_data=2015052300001000280056348161^1.00^协商退款&service=refund_fastpay_by_platform_pwd&seller_user_id=2088712153071777&notify_url=http://www.imike.cc&partner=2088712153071777&seller_email=imikeapp@163.com&batch_num=1&batch_no=20150526110819&refund_date=2015-05-26 11:08:19";
//		Util.openURL(url);
//		String orderid="117536";
//		String payid="2015052300001000280056348161";
//		String price="1.00";
//	    AliPay.refundPWD(orderid, payid, price, notifyUrl);
		
//		String notifyUrl="http://www.imike.cc";
//		List<PPayInfo> list=new ArrayList<PPayInfo>();
//		PPayInfo p1=new  PPayInfo();
//		p1.setOtherno("2015052200001000950052605189");
//		p1.setCost(new BigDecimal("15.00"));
//		list.add(p1);
//		PPayInfo p2=new  PPayInfo();
//		p2.setOtherno("2015052600001000490056918415");
//		p2.setCost(new BigDecimal("1.00"));
//		list.add(p2);
//		PPayInfo p3=new  PPayInfo();
//		p3.setOtherno("2015052600001000490056919888");
//		p3.setCost(new BigDecimal("31.00"));
//		list.add(p3);
//		AliPay.refundPWD(list, notifyUrl);
//		 
		
		
		
		
	}
	

}