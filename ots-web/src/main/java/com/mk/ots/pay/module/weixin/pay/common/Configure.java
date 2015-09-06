package com.mk.ots.pay.module.weixin.pay.common;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:40
 * 这里放置各种配置数据
 */
public class Configure {
    //这个就是自己要保管好的私有Key了（切记只能放在自己的后台代码里，不能放在任何可能被看到源代码的客户端程序中）
	// 每次自己Post数据给API的时候都要用这个key来对所有字段进行签名，生成的签名会放在Sign这个字段，API收到Post数据的时候也会用同样的签名算法对Post过来的数据进行签名和验证
	// 收到API的返回的时候也要用这个key来对返回的数据算下签名，跟API的Sign数据进行比较，如果值不一致，有可能数据被第三方给篡改

	private static String key = "";
	private static String appID = "";
	private static String mchID = "";
	private static String certLocalPath = "";
	private static String localPath ="";
	public static String HttpsRequestClassName = "com.mk.ots.pay.module.weixin.pay.common.HttpsRequest";
	
	public static String app_weixin_key = "lezhu2015imikel1e2z3h4u5lezhu123";
	// 微信分配的公众号ID（开通公众号之后可以获取到）
	public static String app_weixin_appID = "wx83cc02790df41a2b";
	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	public static String app_weixin_mchID = "1234408702";
	private static String app_certLocalPath = "wx_app/apiclient_cert.p12";

	// -----------------眯客-OK--------------------
	private static String wechat_key = "WAdFh6c24MZ0HB4y0zpSC0zey4vfPZk7";
	// 微信分配的公众号ID（开通公众号之后可以获取到）
	private static String wechat_appID = "wx2d9d3daf15496f60";
	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	private static String wechat_mchID = "1232529402";
	private static String wechat_certLocalPath = "wx_wechat/apiclient_cert.p12";
	
	// 测试帐号 (还需配置)
	private static String test_wechat_key = "IAYG8HpT1f4tosODIryb2BqKSqxIod2S";
	// 微信分配的公众号ID（开通公众号之后可以获取到） wx83cc02790df41a2b
	private static String test_wechat_appID = "wxb0f8a61e80048f38";
	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	private static String test_wechat_mchID = "1240541302"; 
	// HTTPS证书的本地路径 （wechat 公共帐号用） 

	private static String test_certLocalPath =    "wx_test_wechat/apiclient_cert.p12";
	
	
	//HTTPS证书密码，默认密码等于商户号MCHID
	private static String certPassword = "";
	//受理模式下给子商户分配的子商户号
	private static String subMchID = "";


	//是否使用异步线程的方式来上报API测速，默认为异步模式
	private static boolean useThreadToDoReport = true;

	//机器IP
	private static String ip = "";

	//以下是几个API的路径：
	//1）被扫支付API
	public static String PAY_API = "https://api.mch.weixin.qq.com/pay/micropay";

	//2）被扫支付查询API
	public static String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

	//3）退款API
	public static String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	//4）退款查询API
	public static String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

	//5）撤销API
	public static String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

	//6）下载对账单API
	public static String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

	//7) 统计上报API
	public static String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";

	public static boolean isUseThreadToDoReport() {
		return useThreadToDoReport;
	}

	public static void setUseThreadToDoReport(boolean useThreadToDoReport) {
		Configure.useThreadToDoReport = useThreadToDoReport;
	}

	public static void setKey(String key) {
		Configure.key = key;
	}

	public static void setAppID(String appID) {
		Configure.appID = appID;
	}

	public static void setMchID(String mchID) {
		Configure.mchID = mchID;
	}

	public static void setSubMchID(String subMchID) {
		Configure.subMchID = subMchID;
	}

	public static void setCertLocalPath(String certLocalPath) {
		Configure.certLocalPath = certLocalPath;
	}

	public static void setCertPassword(String certPassword) {
		Configure.certPassword = certPassword;
	}

	public static void setIp(String ip) {
		Configure.ip = ip;
	}

	public static String getIP(){
		return ip;
	}

	public static void setHttpsRequestClassName(String name){
		HttpsRequestClassName = name;
	}
	
	
	
	
	
	
	
	
	
	public static String getKey(WxType type){
		if(type.getId().intValue()==WxType.app.getId().intValue()){
			return app_weixin_key;
		}else if(type.getId().intValue()==WxType.wechat.getId().intValue()){
			return wechat_key;
		}else{
			return test_wechat_key;
		}
	}
	
	
	public static String getAppid(WxType type){
		if(type.getId().intValue()==WxType.app.getId().intValue()){
			return app_weixin_appID;
		}else if(type.getId().intValue()==WxType.wechat.getId().intValue()){
			return wechat_appID;
		}else{
			return test_wechat_appID;
		}
	}
	
	public static String getMchid(WxType type){
		if(type.getId().intValue()==WxType.app.getId().intValue()){
			return app_weixin_mchID;
		}else if(type.getId().intValue()==WxType.wechat.getId().intValue()){
			return wechat_mchID;
		}else{
			return test_wechat_mchID;
		}
	}
	
	
	
	public static String getCertLocalPath(WxType type){
		if(type.getId().intValue()==WxType.app.getId().intValue()){
			return app_certLocalPath;
		}else if(type.getId().intValue()==WxType.wechat.getId().intValue()){
			return wechat_certLocalPath;
		}else{
			return test_certLocalPath;
		}
	}
	
	
	
	
	public static String getCertPassword(WxType type){
//		return certPassword;
		return getMchid(type);
	}

	public static String getSubMchid(WxType type){
		return subMchID;
	}
	
	
	
	
}
