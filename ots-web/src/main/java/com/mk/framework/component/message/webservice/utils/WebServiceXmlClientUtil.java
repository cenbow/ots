package com.mk.framework.component.message.webservice.utils;

import com.mk.framework.component.message.webservice.EncryptionType;
import com.mk.framework.component.message.webservice.ISmsService4XML;
import com.mk.framework.component.message.webservice.SmsService4XMLImplServiceLocator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.xml.rpc.ServiceException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class  WebServiceXmlClientUtil {

	private static final String CONFIG_NAME = "webserviceXMLConf";// 配置文件名称
	private static final String KEY_SERVERURL = "serverUrl";
	private static final String KEY_TIMEOUT = "timeOut";
	private static String serverUrl = null;// webservice服务端地址
	private static int timeOut = 60000;// 超时设置

	private static MessageDigest md = null;
	private static SmsService4XMLImplServiceLocator smsServiceLocator = null;
	private static ISmsService4XML smsService = null;

	static {
		ResourceBundle _rb = ResourceBundle.getBundle(CONFIG_NAME);
		serverUrl = _rb.getString(KEY_SERVERURL);// 服务端url
		String _timeOutStr = _rb.getString(KEY_TIMEOUT);// 超时设置

		if (_timeOutStr != null && !"".equals(_timeOutStr)) {
			timeOut = Integer.parseInt(_timeOutStr.trim());
			if (timeOut <= 0) {
				timeOut = 60000;
			}
		}
		try {
			smsServiceLocator = new SmsService4XMLImplServiceLocator();
			smsServiceLocator.setTimeOut(timeOut);
			smsService = smsServiceLocator.getSmsService4XMLImplPort(new URL(serverUrl));
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
    }

	/**
	 * 
	 * 方法描述：发送短信方法
	 * 
	 * @param account
	 * @param password
	 * @param msgid
	 * @param phones
	 * @param content
	 * @param sign
	 * @param subcode
	 * @param sendtime
	 * @return
	 * @author: 8527
	 * @date: 2014年9月12日 下午2:21:39
	 */
	public static String sendSms(String account, String password, String msgid, String phones, String content, String sign, String subcode, String sendtime) {
		return sendSms(account, password, msgid, phones, content, sign, subcode, sendtime, false);
	}

	/**
	 * 方法描述：发送短信方法
	 * 
	 * @param account
	 * @param password
	 * @param msgid
	 * @param phones
	 * @param content
	 * @param sign
	 * @param subcode
	 * @param sendtime
	 * @param isURLEncoder
	 * @return
	 * @author: 8527
	 * @date: 2014年10月13日 上午9:44:59
	 */
	public static String sendSms(String account, String password, String msgid, String phones, String content, String sign, String subcode, String sendtime,
			boolean isURLEncoder) {
		String _oRes = null;
		try {
			String message = docXml(account, MD5Encode(password), msgid, phones, content, sign, subcode, sendtime, isURLEncoder); // 使用document对象封装XML
			_oRes = smsService.submit(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _oRes;
	}

	/**
	 * 方法描述：获取上行短信
	 * 
	 * @return
	 * @author: 8527
	 * @date: 2014年9月10日 下午4:42:51
	 */
	public static String getSms(String account, String password) {
		String _oRes = null;
		try {
			String _message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><account>" + account + "</account><password>" + MD5Encode(password)
					+ "</password></message>";
			_oRes = smsService.deliver(_message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _oRes;
	}

	/**
	 * 方法描述：获取状态报告方法
	 * 
	 * @param msgid
	 * @param phone
	 * @return
	 * @author: 8527
	 * @date: 2014年9月10日 下午4:43:11
	 */
	public static String getReport(String account, String password, String msgid, String phone) {
		String _oRes = null;
		try {
			String _message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><account>" + account + "</account><password>" + MD5Encode(password)
					+ "</password><msgid>" + (isBlank(msgid) ? "" : msgid) + "</msgid><phone>" + (isBlank(phone) ? "" : phone) + "</phone></message>";
			_oRes = smsService.report(_message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _oRes;
	}

	/**
	 * 
	 * 方法描述：查询余额方法
	 * 
	 * @return
	 * @author: 8527
	 * @date: 2014年9月10日 下午4:43:22
	 */
	public static String getBalance(String account, String password) {
		String _oRes = null;
		try {
			String _message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><account>" + account + "</account><password>" + MD5Encode(password)
					+ "</password></message>";
			_oRes = smsService.balance(_message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _oRes;
	}

	/**
	 * 
	 * 方法描述：检查敏感词
	 * 
	 * @param account
	 * @param password
	 * @param iContent
	 * @return
	 * @author: 8527
	 * @date: 2014年9月22日 下午3:47:15
	 */
	public static String checkKeyWord(String account, String password, String iContent) {
		String _oRes = null;
		try {
			String _message = null;
			if (!containsSepicChar(iContent)) {
				_message = "<?xml version='1.0' encoding='UTF-8'?><message><account>" + account + "</account>" + "<password>" + MD5Encode(password)
						+ "</password>" + "<content>" + iContent + "</content>" + "</message>";
			} else {
				try {
					_message = "<?xml version='1.0' encoding='UTF-8'?><message><account>" + account + "</account>" + "<password>" + MD5Encode(password)
							+ "</password>" + "<content>" + URLEncoder.encode(iContent, "utf-8") + "</content><encryptionType>"
							+ EncryptionType.URL_UTF8.getDesc() + "</encryptionType></message>";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			_oRes = smsService.keywordcheck(_message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _oRes;
	}

	/**
	 * 方法描述：检测黑名单
	 * 
	 * @param account
	 * @param password
	 * @param iPhones
	 * @return
	 * @author: 8527
	 * @date: 2014年9月22日 下午2:29:02
	 */
	public static String checkBlacklist(String account, String password, String iPhones) {
		String _oRes = null;
		try {
			String _message = "<?xml version='1.0' encoding='UTF-8'?><message><account>" + account + "</account>" + "<password>" + MD5Encode(password)
					+ "</password>" + "<phones>" + iPhones + "</phones>" + "</message>";
			_oRes = smsService.blackListCheck(_message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _oRes;
	}

	/**
	 * 方法描述：使用document 对象封装XML
	 * 
	 * @param account
	 * @param iPwd
	 * @param msgid
	 * @param iContents
	 * @param iSign
	 * @param iSubcode
	 * @param iSendtime
	 * @param isURLEncoder
	 * @return
	 * @author: 8527
	 * @date: 2014年9月10日 下午3:49:20
	 */
	private static String docXml(String account, String iPwd, String msgid, String phones, String iContents, String iSign, String iSubcode, String iSendtime,
			boolean isURLEncoder) {
		Document _doc = DocumentHelper.createDocument();
		_doc.setXMLEncoding("UTF-8");
		Element _message = _doc.addElement("response");
		Element _account = _message.addElement("account");
		_account.setText(account);
		Element password = _message.addElement("password");
		password.setText(iPwd);
		Element _msgid = _message.addElement("msgid");
		_msgid.setText(msgid);
		Element _phones = _message.addElement("phones");
		_phones.setText(phones);
		Element _content = _message.addElement("content");
		if (isURLEncoder) {
			try {
				Element _encryptionType = _message.addElement("encryptionType");
				_encryptionType.setText(EncryptionType.URL_UTF8.getDesc());
				_content.setText(URLEncoder.encode(iContents, "utf-8"));
				// _encryptionType.setText(EncryptionType.BASE64_UTF8.getDesc());
				// _content.setText(new BASE64Encoder().encode(iContents
				// .getBytes("utf-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			if (!containsSepicChar(iContents)) {
				_content.setText(iContents);
			} else {
				try {
					Element _encryptionType = _message.addElement("encryptionType");
					_encryptionType.setText(EncryptionType.URL_UTF8.getDesc());
					_content.setText(URLEncoder.encode(iContents, "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		Element _sign = _message.addElement("sign");
		_sign.setText(iSign);
		Element _subcode = _message.addElement("subcode");
		_subcode.setText(iSubcode);
		Element _sendtime = _message.addElement("sendtime");
		_sendtime.setText(iSendtime);
		return _doc.asXML();
	}

	/**
	 * 
	 * 方法描述：MD5加密函数
	 * 
	 * @param iSourceString
	 * @return
	 * @author: 8527
	 * @date: 2014年9月10日 下午4:44:16
	 */
	private static String MD5Encode(String iSourceString) {
		String _oResultString = null;
		try {
			_oResultString = new String(iSourceString);
			_oResultString = byte2hexString(md.digest(_oResultString.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return _oResultString;
	}

	/**
	 * 方法描述：将字节数组转化成字符串
	 * 
	 * @param iBytes
	 * @return
	 * @author: 8527
	 * @date: 2014年9月10日 下午4:43:33
	 */
	private static final String byte2hexString(byte[] iBytes) {
		StringBuffer _oBf = new StringBuffer(iBytes.length * 2);
		for (int i = 0; i < iBytes.length; i++) {
			if ((iBytes[i] & 0xff) < 0x10) {
				_oBf.append("0");
			}
			_oBf.append(Long.toString(iBytes[i] & 0xff, 16));
		}
		return _oBf.toString();
	}

	/**
	 * 
	 * 方法描述：判断是否包含<>&'"等特殊字符
	 * 
	 * @param iContent
	 * @return
	 * @author: 8527
	 * @date: 2015年1月20日 下午4:49:22
	 */
	private static boolean containsSepicChar(String iContent) {
		return iContent != null
				&& (iContent.indexOf("<") > -1 || iContent.indexOf(">") > -1 || iContent.indexOf("&") > -1 || iContent.indexOf("'") > -1 || iContent
						.indexOf("\"") > -1);
	}

	/**
	 * 方法描述：设置客户端参数
	 * 
	 * @param iServerUrl
	 *            webservice服务地址
	 * @param iTimeOut
	 *            超时时间，单位毫秒
	 * @author: 8527
	 * @date: 2014年9月11日 上午9:27:54
	 */
	public static void setClientParams(String iServerUrl, int iTimeOut) {
		if (!serverUrl.equals(iServerUrl)) {
			WebServiceXmlClientUtil.serverUrl = iServerUrl;
			try {
				smsService = smsServiceLocator.getSmsService4XMLImplPort(new URL(serverUrl));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		WebServiceXmlClientUtil.timeOut = iTimeOut;
	}

	public static void setServerUrl(String iServerUrl) {
		if (!serverUrl.equals(iServerUrl)) {
			WebServiceXmlClientUtil.serverUrl = iServerUrl;
			try {
				smsService = smsServiceLocator.getSmsService4XMLImplPort(new URL(serverUrl));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setTimeOut(int timeOut) {
		WebServiceXmlClientUtil.timeOut = timeOut;
	}

	private static boolean isBlank(String iStr) {
		return iStr == null || "".equals(iStr.trim());
	}

}
