package com.mk.ots.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.framework.component.message.Constant;
import com.mk.ots.common.bean.FlushCache;
import com.mk.ots.manager.SysConfigManager;
//import com.mk.ots.pay.module.wx.common.WxType;

/**
 * @author shellingford
 * @version 创建时间：2013-1-3 上午10:02:08
 */
public class SysConfig {
	
	
	//******* \\\\\\\\\\\\\\ 是否 测试     ----请在这里配置 \\\\\\\\\\\\\\*********************
	
//	private static  WxType wxtype=WxType.app;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SysConfig.class);
	private final static SysConfig instance = new SysConfig();

	public static final String charset = "UTF-8";
	public static final int timeout = 10 * 1000;
	public static final int trytime = 1;

	/** 支付宝 商家Email */
	public static final String aliPayMerchantEmail = "imikeapp@163.com";
	/** 支付宝 用户的授权 */
	public static final String aliPayAuthToken = "用户的授权，需要用户授权的接口不能为空";
	/** 支付宝 AOP分配给应用的唯一标识 */
	public static final String aliPayAppId = "imikeapp@163.com";
	/** 支付宝 终端类型：web：PC方式;wap：手机WAP; mobile：手机客户端应用方式 */
	public static final String aliPayTerminalType = "mobile";
	/** 支付宝 私钥 */
	public static final String aliPayKey = "fb69ao3rw13aklw2s0pgi9zbma5luikv";
	/** 支付宝 公钥 */
	public static final String aliPayPubliKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	/** 支付宝 查询订单方法 */
	public static final String aliPayQueryOrderMethod = "alipay.user.trade.search";
	/** 支付宝 去支付（创建冻结订单）方法 */
	public static final String aliPayOrderPayMethod = "alipay.micropay.order.freeze";
	/** 支付宝 退款 */
	public static final String aliPayOrderPayUnfreeze = "alipay.micropay.order.freeze";


	/**
	 * @@@@@@@@@@@@@@@--&&&&&&&&&&---$$$$$$$$$$$-------【微信支付】---&&&&&&&&&---$$$$$$$$$$---&&&&&&&&&&--
	 *
	 **/
	public static String app_weixin_key = "lezhu2015imikel1e2z3h4u5lezhu123";
	// 微信分配的公众号ID（开通公众号之后可以获取到）
	public static String app_weixin_appID = "wx83cc02790df41a2b";
	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	public static String app_weixin_mchID = "1234408702";

	// -----------------眯客-OK--------------------
	private static String wechat_key = "WAdFh6c24MZ0HB4y0zpSC0zey4vfPZk7";
	// 微信分配的公众号ID（开通公众号之后可以获取到）
	private static String wechat_appID = "wx2d9d3daf15496f60";
	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	private static String wechat_mchID = "1232529402";

	// HTTPS证书的本地路径 （wechat 公共帐号用） 
	private static String certLocalPath = "/wx_wechat/apiclient_cert.p12";

	// ------------------眯客测试-------------------"/wx_test_wechat/apiclient_cert.p12"
	private static String test_certLocalPath =    "/wx_test_wechat/apiclient_cert.p12";

	// 测试帐号 (还需配置)
	private static String test_wechat_key = "IAYG8HpT1f4tosODIryb2BqKSqxIod2S";
	// 微信分配的公众号ID（开通公众号之后可以获取到） wx83cc02790df41a2b
	private static String test_wechat_appID = "wxb0f8a61e80048f38";
	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	private static String test_wechat_mchID = "1240541302";

	/**
	 * @@@@@@@@@@@@@@@--&&&&&&&&&&---$$$$$$$$$$$-------【微信支付】---&&&&&&&&&---$$$$$$$$$$---&&&&&&&&&&--
	 * 
	 */

	private String projectPath;
	private String classpath;
	private ExecutorService threadPool;
	// 刷新缓存专用延迟线程池
	private ScheduledExecutorService flushCacheExecutorService;
	private BlockingQueue<FlushCache> needFlushCache = new ArrayBlockingQueue<FlushCache>(
			1024);
	private String contextPath;

	public void init(String dir, String contextPath) {
		Reader reader = null;
		try {
			this.projectPath = dir;
			SysConfig.logger.info("projectPath:" + this.projectPath);
			File classfile = new File(this.getClass().getResource("/").toURI());
			this.classpath = classfile.getAbsolutePath() + File.separatorChar;
			SysConfig.logger.info("classpath:" + this.classpath);
			this.contextPath = contextPath;
			SysConfig.logger.info("contextPath:" + this.contextPath);
			SysConfig.logger.info("启动线程池");
			this.threadPool = Executors.newCachedThreadPool();
			this.flushCacheExecutorService = Executors
					.newScheduledThreadPool(10);
			// log.info("获取私钥");
			// privatekey=RSACoder.getKey(new
			// File(projectPath+File.separator+"WEB-INF"+File.separatorChar+"private_key_file.rsa"));
			// log.info("加载数据库链接");

			// Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			// PropertyConfigurator.configure(classpath+"proxool.xml");
		} catch (Exception e) {
			SysConfig.logger.error("", e);
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					SysConfig.logger.error("", e);
				}
			}
		}

	}

	private SysConfig() {
	}

	public static final SysConfig getInstance() {
		return SysConfig.instance;
	}

	public final String getProjectPath() {
		return this.projectPath;
	}

	public final String getClasspath() {
		return this.classpath;
	}

	public final ExecutorService getThreadPool() {
		return this.threadPool;
	}

	public final String getContextPath() {
		return this.contextPath;
	}

	public boolean isDebug() {
		String s = this.getSysValueByKey("debug");
		if (StringUtils.isNotBlank(s)) {
			return Boolean.parseBoolean(s);
		}
		return false;
	}

	public String getSysValueByKey(String key) {
		try {
			String value = SysConfigManager.getInstance().readOne(
					Constant.mikewebtype, key);
			return value;
		} catch (Exception e) {
			SysConfig.logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	public void readSysConfig() {
		try {
			SysConfigManager.getInstance().loadSysConfig();
		} catch (Exception e) {
			SysConfig.logger.error("", e);
		}
	}

	public ScheduledExecutorService getFlushCacheExecutorService() {
		return this.flushCacheExecutorService;
	}

	public BlockingQueue<FlushCache> getNeedFlushCache() {
		return this.needFlushCache;
	}

	public static String getCharacterString() {
		Integer length = 30;
		String str = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			boolean b = random.nextBoolean();
			if (b) { // 字符串
				// int choice = random.nextBoolean() ? 65 : 97; 取得65大写字母还是97小写字母
				str += (char) (65 + random.nextInt(26));// 取得大写字母
			} else { // 数字
				str += String.valueOf(random.nextInt(10));
			}
		}
		return str;
	}

	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] b = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				int v = b[i];
				v = v < 0 ? 0x100 + v : v;
				String cc = Integer.toHexString(v);
				if (cc.length() == 1) {
					sb.append('0');
				}
				sb.append(cc);
			}

			return sb.toString();
		} catch (Exception e) {
		}
		return "";
	}

	public static String getTest_certLocalPath() {
		return test_certLocalPath;
	}

	public static void setTest_certLocalPath(String test_certLocalPath) {
		SysConfig.test_certLocalPath = test_certLocalPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public static void setWechat_mchID(String wechat_mchID) {
		SysConfig.wechat_mchID = wechat_mchID;
	}

	public static void setCertLocalPath(String certLocalPath) {
		SysConfig.certLocalPath = certLocalPath;
	}

	public static void setWechat_key(String wechat_key) {
		SysConfig.wechat_key = wechat_key;
	}

	public static void setWechat_appID(String wechat_appID) {
		SysConfig.wechat_appID = wechat_appID;
	}

	public static String getWechat_key() {
		if (isTest()) {
			return test_wechat_key;
		} else {
			return wechat_key;
		}
	}

	public static String getWechat_appID() {
		if (isTest()) {
			return test_wechat_appID;
		} else {
			return wechat_appID;
		}
	}

	public static String getCertLocalPath() {
		if (isTest()) {
			return test_certLocalPath;
		} else {
			return certLocalPath;
		}
	}

	public static String getWechat_mchID() {
		if (isTest()) {
			return test_wechat_mchID;
		} else {
			return wechat_mchID;
		}
	}

	
	public static boolean isTest() {
		String readOne = SysConfigManager.getInstance().readOne(Constant.mikewebtype, Constant.WX_TEST_DEBUG);
		boolean isDebug = !Strings.isNullOrEmpty(readOne);
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>微信debug模式: {}, value:{}<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", isDebug, readOne);
		return isDebug;
	}

}
