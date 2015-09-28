package com.mk.ots.pay.module.ali;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mk.ots.pay.model.PPayInfo;
import com.mk.ots.pay.module.query.QueryPayPram;

/**
 * futao.xiao
 * 2015年5月13日 15:44:01
 * 支付宝退款，查询订单总入口
 * 优化过的接口
 */
public class AliPay {
	
	private static Logger logger = LoggerFactory.getLogger(AliPay.class);
	
	
   /**
   * 支付宝退款总入口【无密退款，直接退】
   */
  public static String refund(String orderid,String alipayid,String price){
		String s=AliPayUtil.refund(orderid, alipayid, price);
		
		logger.info("订单:" + orderid + "取支付宝退款结果:" + s);
		
		if(!AliPayUtil.refundXml(s)){
			alipayid=null;
		}
  	return alipayid;
  }
  
	
	
   /**
   * 支付宝退款总入口【有密退款】 暂时用不着了，有无密的了
   * @param List<PPayInfo>  list  需要退款的集合
   * @param notifyUrl   回调地址
   */
   public static String refundPWD(){
	   return AliPayUtil.refundPWD(1);
   }
	
   public static boolean refundQuery(String orderid){
    	Map<String, String> sPara =new HashMap<String, String>();
    	sPara.put("service", AlipayConfig.aliPayQuery);
    	sPara.put("partner",AlipayConfig.partner);
    	sPara.put("_input_charset", AlipayConfig.input_charset);
     	sPara.put("out_trade_no",orderid);  
     	String s="";
    	try {
			s= AlipaySubmit.buildRequest("","",sPara);
			logger.info("订单:" + orderid + "支付宝退款查询结果:" + s);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return AliPayUtil.refundQuery(orderid,s);
   }
	  /**
     * 支付宝查询订单号
     * @param alipayid 支付成功后微信返回的ID，alipayid可以不传
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是分】支付的金额， 会全部退回
     * @return  支付宝退款ID
     */
    public static String query(String orderid,String alipayid){
    	String s="";
    	Map<String, String> sPara =new HashMap<String, String>();
    	sPara.put("service", AlipayConfig.aliPayQuery);
    	sPara.put("partner",AlipayConfig.partner);
    	sPara.put("_input_charset", AlipayConfig.input_charset);
    	if(alipayid!=null &&  alipayid.trim().length()!=0 ){
    		sPara.put("trade_no",alipayid);  
    	}
     	sPara.put("out_trade_no",orderid);   
    	try {
			s= AlipaySubmit.buildRequest("","",sPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	s=AliPayUtil.query(orderid,s);
    	return s;
    }
	
	
	  /**
     * 支付宝退款总入口【有密退款】
     * @param weixinpayid 支付成功后微信返回的ID
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是元】
     * @return  支付宝退款ID
     */
    public static void refundPWD(String orderid,String payid,String price,String notifyUrl){
    	String url=AliPayUtil.refundPWD(orderid, payid, price, notifyUrl);
//    	String url="http://www.baidu.com";
//    	String url="https://mapi.alipay.com/gateway.do?_input_charset=utf-8&sign=1712ff2e5f5561feeb08323d35d0781e&_input_charset=utf-8&sign_type=MD5&detail_data=2015052300001000280056348161^1.00^协商退款&service=refund_fastpay_by_platform_pwd&seller_user_id=2088712153071777&notify_url=http://www.imike.cc&partner=2088712153071777&seller_email=imikeapp@163.com&batch_num=1&batch_no=20150523183948&refund_date=2015-05-23 18:39:48";
    	System.out.println("url===="+url);
    	AliPayUtil.openURL(url);
    }
    
	  /**
     * 支付宝退款总入口【有密退款】
     * @param List<PPayInfo>  list  需要退款的集合
     * @param notifyUrl   回调地址
     */
    public static void refundPWD(List<PPayInfo>  list,String notifyUrl){
    	String url=AliPayUtil.refundPWD(list, notifyUrl);
    	System.out.println("url==="+url);
    	AliPayUtil.openURL(url);
    }

    
   public static String getUrl(HttpServletRequest request){
    	String path = request.getContextPath();
    	return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
   }
    
   public static String getUrl(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
    	String path = request.getContextPath();
    	return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
   }
   
	  /**
 * 支付宝查询订单号
 * @param alipayid 支付成功后微信返回的ID，alipayid可以不传
 * @param orderid  本地ID，会原样返回
 * @param price    【单位是分】支付的金额， 会全部退回
 * @return  支付宝退款ID
 */
public static QueryPayPram onlyQuery(String orderid,QueryPayPram pram){
	String s=null;
	Map<String, String> sPara =new HashMap<String, String>();
	sPara.put("service", AlipayConfig.aliPayQuery);
	sPara.put("partner",AlipayConfig.partner);
	sPara.put("_input_charset", AlipayConfig.input_charset);
    sPara.put("out_trade_no",orderid);   
	try {
			s= AlipaySubmit.buildRequest("","",sPara);
		} catch (Exception e) {
			e.printStackTrace();
		}

	System.out.println(s);
	return AliPayUtil.onlyQuery(orderid,s,pram);
}
    
	
}
