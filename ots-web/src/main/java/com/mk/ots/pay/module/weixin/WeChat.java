package com.mk.ots.pay.module.weixin;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.mk.ots.pay.module.weixin.pay.Weixin;
import com.mk.ots.pay.module.weixin.pay.common.WxType;

/**
 * futao.xiao
 * 2015年5月21日 
 * 微信公众帐号专用类
 * 优化过的接口
 */
public class WeChat {
	private static Logger logger = LoggerFactory.getLogger(WeChat.class);
	
	private static final String ENVIRONMENT_CONFIG = "environment_config.properties";
	
	private static String WX_TYPE = "";
	
	static {

		logger.info("==============初始化微信支付环境==============");

		try {
			Properties pro = PropertiesLoaderUtils.loadAllProperties(ENVIRONMENT_CONFIG);

			WX_TYPE = pro.getProperty("wx_type");

			logger.info("当前微信支付环境:" + WX_TYPE);

		} catch (Exception e) {

			logger.error("获取微信支付环境配置异常!", e);
		}

		logger.info("=============初始化微信支付环境完毕===========");
	}


	
	
	  /**
     * 退款查询
     */
    public static boolean refundQuery(String weixinpayid){
		return Weixin.refundQuery(weixinpayid, getWxtype());
    }
	
	
    /**
     * 退款总入口
     * @param weixinpayid 支付成功后微信返回的ID
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是分】支付的金额， 会全部退回
     * @return  微信退款ID
     */
    public static String refund(String weixinpayid,String orderid,int price ){
		return Weixin.refund(weixinpayid, orderid, price, getWxtype());
    }

    /**
     * 查询订单总入口
     * @param weixinpayid 支付成功后微信返回的ID
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是fe】
     */
    public static  String  query(String weixinid,int price){
		return Weixin.queryByPrice(null, weixinid, price,getWxtype());
    }
    
    /**
     * 查询订单总入口
     * @param orderid  本地ID，会原样返回
     * @return  微信退款ID
     */
    public static  String  query(String weixinid,String orderid){
		return Weixin.queryById(getOut_trade_no(orderid), weixinid, getWxtype());
    }
    
    
    /**
     * 验证返回信息
     * @param orderid  本地ID，会原样返回
     * @return  微信退款ID
     */
    public static  boolean  verify(HttpServletRequest request){
		return Weixin.verify(request, getWxtype());
    }
    
    
    /**
     * 查询订单总入口
     * @param orderid  本地ID，会原样返回
     * @return   boolean
     */
    public static  boolean  queryIsPayING(String orderid){
		return Weixin.queryIsPayING(getOut_trade_no(orderid), getWxtype());
    }
    
    
    /***
     * @param 得到本地真正的 orderId 【微信端把orderid加长至32位长】
     * @return orderid
     */
    public static String getOrderId(String out_trade_no){
    	String oid=null;
    	if(out_trade_no!=null && out_trade_no.trim().length()!=0){
    		out_trade_no=out_trade_no.trim();
    		out_trade_no=out_trade_no.substring(1, out_trade_no.length());
    		int le=out_trade_no.length();
    		int m=0;
    		for(int i=0;i<le-1;i++){
    			if(!out_trade_no.substring(i,i+1).equals("0")){
    				m=i; break;
    			}
    		}
    		if(m>0){
    			oid=out_trade_no.substring(m,le);
    		}
    	}
    	return oid;
    }
    
    /***
     * @param  orderid 
     * @return 生成微信需要的Out_trade_no 【32位长】
     */
    public static String getOut_trade_no(String orderid){
    	String out_trade_no="";
    	if(orderid!=null && orderid.trim().length()!=0){
    		orderid=orderid.trim();
    		int le=orderid.length();
    		for (int i = 1; i < 32-le; i++) {
    			out_trade_no=out_trade_no+"0";
			}
    		out_trade_no="1"+out_trade_no+orderid;
    	}
    	return out_trade_no;
    }
    
    private static WxType getWxtype() {

		logger.info("当前微信支付环境:" + WX_TYPE);

		if (WX_TYPE.equals(WxType.test_wechat.name())) {

			logger.info("当前微信支付环境是测试环境.");

			return WxType.test_wechat;
		}

		logger.info("当前微信支付环境是生产环境.");
		return WxType.wechat;
	}
    
    
    /***
     *  仅供 【正式】订单是否支付和退款的查询
     */
    public static String  onlyQuery(String orderid){
    	String s=Weixin.onlyQuery(orderid, WxType.wechat);
    	if(s!=null){
    		s="订单号"+getOId(orderid)+" "+s;
    	}
    	return  s;
    }
    
    /***
     *  仅供【测试帐号】 订单是否支付和退款的查询
     */
    public static String  onlyQueryTest(String orderid){
    	String s=Weixin.onlyQuery(orderid, WxType.test_wechat);
    	if(s!=null){
    		s="订单号"+orderid+" "+s;
    	}
    	return  s;
    }

    private static String getOId(String orderid){
    	orderid=orderid.trim();
    	if(orderid.length()>1 ){
    		String bef=orderid.substring(0, 1);
        	if(bef.equals("a") || bef.equals("b")  || bef.equals("c")  || bef.equals("d") ){
        		orderid=orderid.substring(1);
        	} 	
    	}
    	return orderid;
    }
    
}
