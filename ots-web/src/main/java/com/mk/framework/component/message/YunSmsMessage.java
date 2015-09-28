package com.mk.framework.component.message;

import java.util.HashMap;
import java.util.Set;

import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import com.cloopen.rest.sdk.CCPRestSmsSDK;

/**
 * @author Administrator
 *yuntongxun SMS Message
 */
public class YunSmsMessage extends AbstractMessage {
	
	 private Logger logger = org.slf4j.LoggerFactory.getLogger(YunSmsMessage.class);
	 
	 private String serviceUrl="app.cloopen.com";
	 private String port="8883";
	 private String accountSid="8a48b5514f73ea32014f826d31541807";
	 private String authToken="6dca694570844c4aa8870fd67eb4e883";
	 private String appId="8a48b5514f73ea32014f82a14b9c190d";
	 private String mobile="";
	 private String templeteId = "36546";
	 private String[] data;
	 private CCPRestSmsSDK restAPI;
	 private HashMap<String, Object> result = null;
	 public YunSmsMessage(String serviceUrl,String port,String accountSid,String token,String appId,String templeteId) {
		 this.serviceUrl=serviceUrl;
		 this.port=port;
		 this.accountSid=accountSid;
		 this.authToken=token;
		 this.appId=appId;
		 this.templeteId=templeteId;
		 try {
			 restAPI=new CCPRestSmsSDK();
			 restAPI.init(serviceUrl, port);
			 restAPI.setAccount(accountSid, authToken);
			 restAPI.setAppId(appId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	 
	 public String[] getData() {
		return data;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setTempleteId(String templeteId) {
		this.templeteId = templeteId;
	}

	public YunSmsMessage() {
		 try {
			 restAPI=new CCPRestSmsSDK();
			 restAPI.init(serviceUrl, port);
			 restAPI.setAccount(accountSid, authToken);
			 restAPI.setAppId(appId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ITips setToken(String token) {
		this.authToken=token;
		return null;
	}


	
    /**
     * send SMS.
     */
    @Override
    public boolean send() throws Exception {
    	if (Strings.isNullOrEmpty(mobile)) {
			mobile=super.getMobiles();
		}
    	if (data==null ||data.length==0) {
			data=super.getContent().split(",");
		}
    	result = restAPI.sendTemplateSMS(mobile,templeteId ,data);
    	logger.info("YunSmsMessage result=" + result);
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				logger.info("YunSmsMessage return data :"+key +" = "+object);
			}
			return true;
		}else{
			//异常返回输出错误码和错误信息
			logger.error("YunVoiceMessage 错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
			return false;
		}
        
    }
    
	public void setData(String[] data) {
		this.data = data;
	}
    
	@Override
	public ITips setMsgId(Long msgid) {
		//this.rrid = String.valueOf(msgid);
		return this;
	}

}
