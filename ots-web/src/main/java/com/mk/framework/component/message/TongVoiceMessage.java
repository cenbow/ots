package com.mk.framework.component.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * santong.
 *
 * @author bingqiu.yuan.
 *
 */
public class TongVoiceMessage extends AbstractMessage {
	final Logger logger = LoggerFactory.getLogger(TongVoiceMessage.class);

	// 接口必备参数
	private String api = "";
	private String sn = "";
	private String pwd = "";

	/**
	 * @throws java.io.UnsupportedEncodingException
	 */
	public TongVoiceMessage() throws UnsupportedEncodingException {
        this("dh28001", "!2FW!7oe", "http://voice.3tong.net/http/voiceSms/authCodeSubmit");
	}


	/**
	 * @param sn
	 * @param pass
	 * @param serverURL
	 * @throws java.io.UnsupportedEncodingException
	 */
	public TongVoiceMessage(String sn, String pass, String serverURL) throws UnsupportedEncodingException {
		this.sn = sn;
		this.pwd = getMD5(pass);
		this.api = serverURL;
	}

	@Override
	public boolean send() throws Exception {
		return sendVoice(super.getMsgid(),super.getReceiver(),super.getContent());
	}

    /**
     *
     * @param msgId
     * @param mobile
     * @param txt
     * @return
     */
	private boolean sendVoice(Long msgId, String mobile, String txt) {
		String result = "";
		String xml = "<?xml version='1.0' encoding='UTF-8'?>";
		xml += "<message>";
		xml += "<account>"+ sn +"</account>";
		xml += "<password>" + pwd + "</password>";
		xml += "<data>";
		xml += "<voiceSms>";
		xml += "<msgId>" + msgId.toString() + "</msgId>";
		xml += "<phone>"+mobile+"</phone>";
		xml += "<code>"+txt+"</code>";
		xml += "</voiceSms>";
		xml += "</data>";
		xml += "</message>";

        StringBuffer params = new StringBuffer();
        params.append("message").append("=").append(URLEncoder.encode(xml));
        xml = params.toString();

		logger.info("发送大汉三通语音xml: {}", xml);
        byte[] xmlData = xml.getBytes();
		URL url;
		try {
            url = new URL(api + "?" + xml);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) connection;
			httpconn.setRequestProperty("Content-Length", String.valueOf(xmlData.length));
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(xmlData);
			out.close();

			InputStreamReader isr = new InputStreamReader(httpconn
					.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern.compile("<status>0</status>");
				Matcher matcher = pattern.matcher(inputLine);
                if (matcher.find()){
                    result="1";
                }
			}
            logger.info("发送大汉三通语音result xml: {}", inputLine);
			return result=="1";
		} catch (Exception e) {
			logger.error("发送大汉三通语音短信出现错误："+xml, e);
			return false;
		}
	}

    /**
     *
     * @param plain  明文
     * @return 32位小写密文
     */
    public static String getMD5(String plain) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plain.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }
}
