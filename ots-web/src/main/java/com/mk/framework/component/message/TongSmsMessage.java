package com.mk.framework.component.message;

import com.mk.framework.component.message.webservice.utils.WebServiceXmlClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


/**
 *     大汉三通
 * @author bingqiu.yuan.
 *
 */
public class TongSmsMessage extends AbstractMessage {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(TongSmsMessage.class);

	// -----------------------------------------------
	/** webservice服务器定义 */
	private String api = "";

	/** 序列号 */
	private String sn = "";

	/** 密码密文 */
	private String pwd = "";

	/** 短信内容签名,与sn是一一对应的 */
	private final String content_sign = "【眯客】";

    private final String subcode="" ;

	//private final String content_sign_end = "（眯客弹指间有房间，保证低价、快速入住)";

	public TongSmsMessage() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		this("dh28001", "!2FW!7oe", "http://ws.3tong.net/services/sms");
	}

	/**
	 *
	 * @param sn
	 * @param password
	 * @param serviceUrl
	 * @throws java.io.UnsupportedEncodingException
	 * @throws java.security.NoSuchAlgorithmException
	 */
	public TongSmsMessage(String sn, String password, String serviceUrl) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		this.sn = sn;
		this.pwd = password;
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

        // 服务端地址，默认可不设置
        WebServiceXmlClientUtil.setServerUrl(this.api);
        String respInfo = null;
        // 发送短信
        this.logger.info("ctc smsmessage send beging:\n");
        respInfo = WebServiceXmlClientUtil.sendSms(sn, pwd, rrid.toString(),
                mobile, content, content_sign, subcode, stime);
        this.logger.info(respInfo);
        this.logger.info("ctc smsmessage send end.\n");
        return respInfo;
//		// 获取状态报告
//		System.out.println("*************状态报告*************");
//		_respInfo = WebServiceXmlClientUtil.getReport(account, password, msgid,
//				phone);
//		System.out.println(_respInfo);
//		// 获取余额
//		System.out.println("*************获取余额*************");
//		_respInfo = WebServiceXmlClientUtil.getBalance(account, password);
//		System.out.println(_respInfo);
//		// 获取上行
//		System.out.println("*************获取上行*************");
//		_respInfo = WebServiceXmlClientUtil.getSms(account, password);
//		System.out.println(_respInfo);

        // 检测敏感词
//		System.out.println("*************检测敏感词*************");
//		_respInfo = WebServiceXmlClientUtil.checkKeyWord(account, password,
//				content);
//		System.out.println(_respInfo);

        // 检测黑名单
//		System.out.println("*************检测黑名单*************");
//		_respInfo = WebServiceXmlClientUtil.checkBlacklist(account, password,
//				phone);
//		System.out.println(_respInfo);
	}

    private String getReport(String mobile, Long rrid) {
        if (StringUtils.isBlank(mobile)) {
            throw MessageErrorEnum.mobileNotEmpty.getMyException();
        }

        // 服务端地址，默认可不设置
        WebServiceXmlClientUtil.setServerUrl(this.api);
        String respInfo = null;
		// 获取状态报告
        this.logger.info("ctc getReport beging:\n");
        respInfo = WebServiceXmlClientUtil.getReport(sn, pwd, rrid.toString(),
                mobile);
        this.logger.info("msgid respInfo:"+respInfo);
        this.logger.info("ctc getReport end.\n");
        return respInfo;
    }

}
