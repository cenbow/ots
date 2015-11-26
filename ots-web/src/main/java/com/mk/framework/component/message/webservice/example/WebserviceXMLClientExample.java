//package com.mk.framework.component.message.webservice.example;
//
//
//import com.ctc.smscloud.xml.webservice.utils.WebServiceXmlClientUtil;
//
//public class WebserviceXMLClientExample {
//	private static String account = "dh28001";// 用户名
//	private static String password = "!2FW!7oe";// 密码(明文)
//	private static String phone = "18616886963"; // 要发送的手机号码
//	//private static String content = "【第二产品签名】您好！您发往浙江省台州市玉环县经济开发区小錱𨫎家用电器有限公司,收货人为：骆华辉的网上订单订单号W140924512871已受理";// 短信内容
//
//	private static String content = "2134234234&";
//	// 短信内容
//	private static String sign = "【眯客】"; // 短信签名
//	private static String subcode = "28001"; // 子号码，可为空
//	private static String msgid = "1"; // 短信id，查询短信状态报告时需要，可为空
//	private static String sendtime = ""; // 定时发送时间
//
//	public static void main(String[] args) {
//		// 服务端地址，默认可不设置
//		//WebServiceXmlClientUtil.setServerUrl("http://3tong.net/services/sms");
//		WebServiceXmlClientUtil.setServerUrl("http://ws.3tong.net/services/sms");
//		String _respInfo = null;
//		// 发送短信
//		System.out.println("*************发送短信*************");
//		_respInfo = WebServiceXmlClientUtil.sendSms(account, password, msgid,
//                phone, content, sign, subcode, sendtime);
//		System.out.println(_respInfo);
//		// 获取状态报告
//		System.out.println("*************状态报告*************");
//		_respInfo = WebServiceXmlClientUtil.getReport(account, password, null,
//                null);
//		System.out.println(_respInfo);
//		// 获取余额
//		System.out.println("*************获取余额*************");
//		_respInfo = WebServiceXmlClientUtil.getBalance(account, password);
//		System.out.println(_respInfo);
//		// 获取上行
//		System.out.println("*************获取上行*************");
//		_respInfo = WebServiceXmlClientUtil.getSms(account, password);
//		System.out.println(_respInfo);
//
//		// 检测敏感词
//		System.out.println("*************检测敏感词*************");
//		_respInfo = WebServiceXmlClientUtil.checkKeyWord(account, password,
//                content);
//		System.out.println(_respInfo);
//
//		// 检测黑名单
//		System.out.println("*************检测黑名单*************");
//		_respInfo = WebServiceXmlClientUtil.checkBlacklist(account, password,
//                phone);
//		System.out.println(_respInfo);
//	}
//}
