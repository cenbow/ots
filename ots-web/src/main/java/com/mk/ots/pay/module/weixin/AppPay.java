package com.mk.ots.pay.module.weixin;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.pay.module.query.QueryPayPram;
import com.mk.ots.pay.module.weixin.pay.Weixin;
import com.mk.ots.pay.module.weixin.pay.common.Configure;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.pay.module.weixin.pay.common.RandomStringGenerator;
import com.mk.ots.pay.module.weixin.pay.common.WxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * futao.xiao
 * 2015年5月21日 
 * App专用类
 * 优化过的接口
 */
public class AppPay {
 	private static Logger logger = LoggerFactory.getLogger(AppPay.class);
 	
 	
	  /**
   * 退款查询
   */
  public static boolean refundQuery(String weixinpayid){
		return Weixin.refundQuery(weixinpayid, getWxtype());
  }
 	
 	
 	
	/** price单位是【分】*/
    public static Map<String,String> pay(HttpServletRequest request,
    		String orderid,String  body,String notifyUrl,
    		int price) throws IllegalAccessException{
    	logger.info("微信支付前去微信创建订单，还没进入微信系统.");
		Map<String,String> resmap= new TreeMap<String, String>();
		String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String appid =Configure.getAppid(getWxtype());
		String mch_id =Configure.getMchid(getWxtype());
		String key =Configure.getKey(getWxtype());
		logger.info("############appid={},mch_id={},key={}", appid, mch_id, key);
		String nonce_str =RandomStringGenerator.getRandomStringByLength(32);// UUID.randomUUID().toString().replaceAll("-", "");
  		String total_fee = price+"";
		String spbill_create_ip =  PayTools.getIpAddr();  //"183.131.145.108"; 
		String trade_type = "APP";
		String sign = "";
		Map<String, String> map = new TreeMap<String, String>();
		map.put("appid", appid.trim());
		map.put("mch_id", mch_id.trim());
		map.put("nonce_str", nonce_str.trim());
		map.put("body", body.trim());
		map.put("out_trade_no",orderid);
		map.put("total_fee", total_fee.trim());//"1");//total_fee.trim());
		map.put("spbill_create_ip", spbill_create_ip.trim());
		map.put("notify_url", notifyUrl.trim());
		map.put("trade_type", trade_type.trim());
		StringBuilder sb = new StringBuilder();
		Element root = new Element("xml");
//		MD5.getMessageDigest (sb.toString().getBytes()).toUpperCase();
		for (Entry<String, String> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			String value = entry.getValue();
			sb.append(entry.getKey()).append("=").append(value);
			Element temp = new Element(entry.getKey());
			temp.setText(value);
			root.addContent(temp);
		}
		sb.append("&key=").append(key);
		sign =PayTools.caculateCF(sb.toString(), "UTF-8").toUpperCase();
		map.put("sign", sign);
		Element esign = new Element("sign");
		esign.setText(sign);
		root.addContent(esign);
		String sendxml =PayTools.XMLtoString(new Document(root));
		logger.info("去微信支付请求的参数是:"+sendxml);
		Document doc=null;
		try {
			String xml = PayTools.dopost(url, sendxml);
			doc = PayTools.StringtoXML(xml);
			logger.info("请求微信返回的数据是:"+doc);
		} catch (JDOMException | IOException e1) {
			logger.error(e1.getMessage(), e1);
		}
		Element elroot = doc.getRootElement();
		logger.info("微信返回的return_code==："+elroot.getChildText("return_code"));
		logger.info("微信返回的return_msg==："+elroot.getChildText("return_msg"));
		if (!elroot.getChildText("return_code").isEmpty()
				&& elroot.getChildText("return_code").equalsIgnoreCase("SUCCESS")) {
			String err_code_des=elroot.getChildText("err_code_des");
			//err_code_des><![CDATA[商户订单号重复]]
			if(err_code_des!=null && err_code_des.trim().length()!=0 ){
				throw MyErrorEnum.errorParm.getMyException(err_code_des);
			}
			logger.info("eresult_code==="+elroot.getChildText("result_code"));
			if (!elroot.getChildText("result_code").isEmpty()
					&& elroot.getChildText("result_code").equalsIgnoreCase("SUCCESS")) {
				String prepayid = elroot.getChildText("prepay_id");
				logger.info("微信支付前去微信创建预支付订单  prepayid====="+prepayid);
				resmap.put("appid", appid);
				resmap.put("partnerid", mch_id);    //ConstantUtil.getInstance().getPARTNER();
				resmap.put("noncestr", root.getChildText("nonce_str"));
				resmap.put("prepayid", prepayid);
				resmap.put("package", "Sign=WXPay");
				resmap.put("timestamp", (System.currentTimeMillis() / 1000)+"");
				sb = new StringBuilder();
				for (Entry<String, String> entry : resmap.entrySet()) {
					if (sb.length() > 0) {
						sb.append("&");
					}
					String value = entry.getValue(); // URLEncoder.encode(entry.getValue(),Constant.defaulCharset);
					sb.append(entry.getKey()).append("=").append(value);
				}
				sb.append("&key=").append(key);
				sign = PayTools.caculateCF(sb.toString(), "UTF-8").toUpperCase();
				resmap.put("sign", sign);
				resmap.put("packagevalue", "Sign=WXPay");
			}
			logger.info(" 微信支付参数是："+resmap.toString());
		}
    	return resmap;
    }
	
	
	
    /**
     * 退款总入口
     * @param weixinpayid 支付成功后微信返回的ID
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是分】支付的金额， 会全部退回
     * @return  微信退款ID
     */
    public static String refund(String weixinpayid,String orderid,int price ){
		return Weixin.refund(weixinpayid, orderid, price, getWxtype() );
    }

    /**
     * 查询订单总入口
     * @param weixinpayid 支付成功后微信返回的ID
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是fe】
     */
    public static  String  query(String orderid,String payid,int price){
		return Weixin.queryByPrice(orderid, payid, price, getWxtype() );
    }
    
    /**
     * 查询订单总入口
     * @param orderid  本地ID，会原样返回
     * @return  微信退款ID
     */
    public static  String  query(String weixinid,String orderid){
		return Weixin.queryById(orderid, weixinid, getWxtype() );
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
		return Weixin.queryIsPayING(orderid, getWxtype());
    }
    

    /***
     *  仅供 订单是否支付和退款的查询
     */

    public static QueryPayPram  onlyQuery(String orderid,QueryPayPram pram){
    	return Weixin.onlyQuery(orderid, WxType.app, pram);
    }
    
    private static WxType getWxtype() {
//		logger.info("当前App微信支付环境:" + WX_TYPE);
//		if (WX_TYPE.equals(WxType.test_app.name())) {
//			logger.info("当前App微信支付环境是测试环境.");
//			return WxType.test_app;
//		}
//		logger.info("当前App微信支付环境是生产环境.");
    	//没有测试环境着
		return WxType.app;
	} 
    
    
 
    
    
}