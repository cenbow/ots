package com.mk.ots.pay.module.weixin.pay.service;

import com.mk.ots.pay.module.weixin.pay.common.Configure;
import com.mk.ots.pay.module.weixin.pay.common.HttpsRequest;
import com.mk.ots.pay.module.weixin.pay.common.WxType;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * User: rizenguo
 * Date: 2014/12/10
 * Time: 15:44
 * 服务的基类
 */
public class BaseService{

    //API的地址
    private String apiURL;

    //发请求的HTTPS请求器
    private IServiceRequest serviceRequest;

    public BaseService(String api,WxType type) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        apiURL = api;
        Class c = Class.forName(Configure.HttpsRequestClassName);
//        serviceRequest = (IServiceRequest) c.newInstance();
        try {
        	serviceRequest=new HttpsRequest(type);
		} catch (Exception e) {
			System.out.println("eeeeeeeeeeeeeeee========"+e);
		}
        
    }
    
    

    protected String sendPost(Object xmlObj,WxType type) throws UnrecoverableKeyException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return serviceRequest.sendPost(apiURL,xmlObj,type);
    }

    /**
     * 供商户想自定义自己的HTTP请求器用
     * @param request 实现了IserviceRequest接口的HttpsRequest
     */
    public void setServiceRequest(IServiceRequest request){
        serviceRequest = request;
    }
}
