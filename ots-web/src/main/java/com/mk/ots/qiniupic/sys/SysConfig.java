package com.mk.ots.qiniupic.sys;

/**
 * 七牛 上传图片，取图片用到的一些配置信息
 * @author futao
 */
public class SysConfig {

	/**AK*/
	public static final String QiniuAK = "XPAlKTDVH_gvbAOL5Ny3KNyXbw3nXwJnFUAZzQNF";
	/**SK*/
	public static final String QiniuSK = "R-RFAK9xweKLuVpLE-Dl12LDxi3fDKQgZPpUm0sI";
	/**七牛取图片路径*/                  
	public static final String Url = "https://dn-imike.qbox.me/";
    /**七牛取图片失效时间   7200 秒*/
	public static final long  InvalidationTime = 7200;  
	/**七牛取图片失效时间   7200 秒*/
	public static final long  uploadTime = 7200;  
	/** 上传token*/
	//api地址：http://developer.qiniu.com/docs/v6/sdk/java-sdk.html#io-put-flow
	public static final String  UploadSpace ="imike";  
    
	
}
