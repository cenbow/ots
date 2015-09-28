package com.mk.framework.component.message;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SMS Message
 *
 * @author chuaiqing.
 *
 */
public class SmsMessage extends AbstractMessage {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(SmsMessage.class);

	// -----------------------------------------------
	/** webservice服务器定义 */
	private String api = "";

	/** 序列号 */
	private String sn = "";

	/** 密码密文 */
	private String pwd = "";

	/** 短信内容签名,与sn是一一对应的 */
	private final String content_sign = "【眯客】";

	//private final String content_sign_end = "（眯客弹指间有房间，保证低价、快速入住)";

	public SmsMessage() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		this("DXX-WSS-10X-06110", "mike1@3$5^", "http://sdk.entinfo.cn:8061/webservice.asmx");
	}

	/**
	 *
	 * @param sn
	 * @param password
	 * @param serviceUrl
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public SmsMessage(String sn, String password, String serviceUrl) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		this.sn = sn;
		this.pwd = this.getMD5(sn + password);
		this.api = serviceUrl;
	}

	@Override
	public boolean send() throws Exception {
		String result = this.sendSMS(super.getReceiver(), super.getContent(), "", "", super.getMsgid(), "");
		return !result.startsWith("-");
	}

	/**
	 * 发送短信
	 *
	 * @param mobile
	 *            参数：接收短信的手机号
	 * @param content
	 *            参数：短信内容
	 * @param ext
	 *            参数：扩展码
	 * @param stime
	 *            参数：定时发送时间
	 * @param rrid
	 *            参数：唯一标识
	 * @param msgfmt
	 *            参数：短信内容编码，0：ASCII串,3：短信写卡操作,4：二进制信息,8：UCS2编码,空或15：含GB汉字.
	 * @return String 返回值：唯一标识，如果不填写rrid将返回系统生成的
	 */
	private String sendSMS(String mobile, String content, String ext, String stime, Long rrid, String msgfmt) {
		String result = "";
		if (StringUtils.isBlank(mobile)) {
			throw MessageErrorEnum.mobileNotEmpty.getMyException();
		}
		HttpURLConnection httpconn = null;
		ByteArrayOutputStream bout = null;
		OutputStream out = null;
		BufferedReader in = null;
		try {
			String soapAction = "http://entinfo.cn/mdsmssend";
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
			xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
			xml += "<soap:Body>";
			xml += "<mdsmssend  xmlns=\"http://entinfo.cn/\">";
			xml += "<sn>" + this.sn + "</sn>";
			xml += "<pwd>" + this.pwd + "</pwd>";
			xml += "<mobile>" + mobile + "</mobile>";
			xml += "<content>" + this.content_sign + content + "</content>";
			xml += "<ext>" + ext + "</ext>";
			xml += "<stime>" + stime + "</stime>";
			xml += "<rrid>" + rrid + "</rrid>";
			xml += "<msgfmt>" + msgfmt + "</msgfmt>";
			xml += "</mdsmssend>";
			xml += "</soap:Body>";
			xml += "</soap:Envelope>";

			// //
			this.logger.error("request xml output begin:\n");
			this.logger.error(xml);
			this.logger.error("request xml output end.\n");
			// //

			URL url = new URL(this.api);
			URLConnection connection = url.openConnection();
			httpconn = (HttpURLConnection) connection;
			bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes(Constant.defaultcharset));
			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			out = httpconn.getOutputStream();
			out.write(b);
			out.close();

			in = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
			String inputLine;
			StringBuffer bfResult = new StringBuffer();
			while (null != (inputLine = in.readLine())) {
				bfResult.append(inputLine).append("\n");
				Pattern pattern = Pattern.compile("<mdsmssendResult>(.*)</mdsmssendResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			// //
			this.logger.error("result is : " + result);

			this.logger.error("response output begin:\n");
			this.logger.error(bfResult.toString());
			this.logger.error("response output end.\n");
			// //
		} catch (Exception e) {
			this.logger.error("SmsMessage sendSMS is error: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			if (httpconn != null) {
				try {
					httpconn.disconnect();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (bout != null) {
				try {
					bout.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (Exception e4) {
					e4.printStackTrace();
				}
			}
		}
		return result;
	}

	// /**
	// * 方法名称：gxmt 功 能：发送短信 参
	// * 数：mobile,content,ext,stime,rrid(手机号，内容，扩展码，定时时间，唯一标识) 返 回
	// * msgfmt内容编码,0：ASCII串,3：短信写卡操作,4：二进制信息,8：UCS2编码,空或15：含GB汉字.
	// * 值：唯一标识，如果不填写rrid将返回系统生成的
	// */
	// private String mdgxsend(String mobile, String content, String ext, String
	// stime, String rrid,String msgfmt ) {
	// String result = "";
	// String soapAction = "http://entinfo.cn/mdgxsend";
	// String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	// xml +=
	// "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
	// xml += "<soap:Body>";
	// xml += "<mdgxsend  xmlns=\"http://entinfo.cn/\">";
	// xml += "<sn>" + this.sn + "</sn>";
	// xml += "<pwd>" + this.pwd + "</pwd>";
	// xml += "<mobile>" + mobile + "</mobile>";
	// xml += "<content>" + content + "</content>";
	// xml += "<ext>" + ext + "</ext>";
	// xml += "<stime>" + stime + "</stime>";
	// xml += "<rrid>" + rrid + "</rrid>";
	// xml += "<msgfmt>" + msgfmt + "</msgfmt>";
	// xml += "</mdgxsend>";
	// xml += "</soap:Body>";
	// xml += "</soap:Envelope>";
	//
	// URL url;
	// try {
	// url = new URL(this.api);
	// URLConnection connection = url.openConnection();
	// HttpURLConnection httpconn = (HttpURLConnection) connection;
	// ByteArrayOutputStream bout = new ByteArrayOutputStream();
	// bout.write(xml.getBytes(Constant.defaultcharset));
	// byte[] b = bout.toByteArray();
	// httpconn.setRequestProperty("Content-Length", String.valueOf(b.length));
	// httpconn.setRequestProperty("Content-Type", "text/xml; charset=utf8");
	// httpconn.setRequestProperty("SOAPAction", soapAction);
	// httpconn.setRequestMethod("POST");
	// httpconn.setDoInput(true);
	// httpconn.setDoOutput(true);
	//
	// OutputStream out = httpconn.getOutputStream();
	// out.write(b);
	// out.close();
	//
	// InputStreamReader isr = new InputStreamReader(httpconn.getInputStream());
	// BufferedReader in = new BufferedReader(isr);
	// String inputLine;
	// while (null != (inputLine = in.readLine())) {
	// Pattern pattern =
	// Pattern.compile("<mdgxsendResult>(.*)</mdgxsendResult>");
	// Matcher matcher = pattern.matcher(inputLine);
	// while (matcher.find()) {
	// result = matcher.group(1);
	// }
	// }
	// return result;
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }

	/**
	 * 字符串MD5加密
	 *
	 * @param sourceStr
	 *            参 数：待转换字符串
	 * @return String 返回值：加密之后字符串
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	private String getMD5(String sourceStr) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String resultStr = "";
		byte[] temp = sourceStr.getBytes(Constant.defaultcharset);
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(temp);
		byte[] b = md5.digest();
		for (int i = 0; i < b.length; i++) {
			char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
			char[] ob = new char[2];
			ob[0] = digit[(b[i] >>> 4) & 0X0F];
			ob[1] = digit[b[i] & 0X0F];
			resultStr += new String(ob);
		}
		return resultStr;
	}
}
