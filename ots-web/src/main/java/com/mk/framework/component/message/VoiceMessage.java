package com.mk.framework.component.message;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.ots.manager.SysConfigManager;
import com.mk.ots.ticket.controller.TicketController;

/**
 * voice message.
 * 
 * @author chuaiqing.
 * 
 */
public class VoiceMessage extends AbstractMessage {
	final Logger logger = LoggerFactory.getLogger(VoiceMessage.class);
	
	// 接口必备参数
	private String api = "http://117.79.237.3:8060/webservice.asmx";
	private String sn = "";
	private String pwd = "";

	/**
	 * @throws UnsupportedEncodingException
	 */
	public VoiceMessage() throws UnsupportedEncodingException {
		this("DXX-WSS-10X-06110", "mike1@3$5^", "http://sdk3.entinfo.cn:8060/webservice.asmx");
	}

	 
	/**
	 * @param sn
	 * @param pass
	 * @param serverURL
	 * @throws UnsupportedEncodingException
	 */
	public VoiceMessage(String sn, String pass, String serverURL) throws UnsupportedEncodingException {
		this.sn = sn;
		this.pwd = getMD5(sn + pass);
		this.api = serverURL;
	}

	@Override
	public boolean send() throws Exception {
		return sendVoice(super.getTitle(), super.getReceiver(), super.getContent(), "", "", "");
	}

	/**
	 * sendVoice 
	 * @param title 传真标题
	 * @param mobile 手机号
	 * @param txt 文本内容
	 * @param content 传真base64内容
	 * @param srcnumber
	 * @param stime 定时时间，如果不需要置为空
	 * @return 返回一个唯一值rrid
	 */
	private boolean sendVoice(String title, String mobile, String txt, String content, String srcnumber, String stime) {
		String result = "";
		String soapAction = "http://tempuri.org/mdAudioSend";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">";
		xml += "<soap12:Body>";
		xml += "<mdAudioSend xmlns=\"http://tempuri.org/\">";
		xml += "<sn>" + sn + "</sn>";
		xml += "<pwd>" + pwd + "</pwd>";
		xml += "<title>" + title + "</title>";
		xml += "<mobile>" + mobile + "</mobile>";
		xml += "<txt>" + txt + "</txt>";
		xml += "<content>" + content + "</content>";
		xml += "<srcnumber>" + srcnumber + "</srcnumber>";
		xml += "<stime>" + stime + "</stime>";
		xml += "</mdAudioSend>";
		xml += "</soap12:Body>";
		xml += "</soap12:Envelope>";
		
		logger.info("发送语音xml: {}", xml);
		URL url;
		try {
			url = new URL(api);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes());
			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type","text/xml; charset=gb2312");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(httpconn
					.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern.compile("<mdAudioSendResult>(.*)</mdAudioSendResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			logger.info("发送语音result: {}", result);
			return !result.startsWith("-");
		} catch (Exception e) {
			logger.error("发送语音短信出现错误.\nSOAPAction:"+ soapAction +"\n"+xml, e);
			return false;
		}
	}

	/**
	 * 方法名称：getMD5 功 能：字符串MD5加密 参 数：待转换字符串 返 回 值：加密之后字符串
	 */
	private String getMD5(String sourceStr) throws UnsupportedEncodingException {
		String resultStr = "";
		try {
			byte[] temp = sourceStr.getBytes();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(temp);
			// resultStr = new String(md5.digest());
			byte[] b = md5.digest();
			for (int i = 0; i < b.length; i++) {
				char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
						'9', 'A', 'B', 'C', 'D', 'E', 'F' };
				char[] ob = new char[2];
				ob[0] = digit[(b[i] >>> 4) & 0X0F];
				ob[1] = digit[b[i] & 0X0F];
				resultStr += new String(ob);
			}
			return resultStr;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
