package com.mk.framework.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.http.HTTPException;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetUtils {
	
	private static String charset = "UTF-8";
	private static int timeout = 30 * 1000;
	private static int trytime = 1;
	private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);
	
	public static String getIpAddr(HttpServletRequest request) { 
	 	String ip = request.getHeader("x-forwarded-for"); 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("Proxy-Client-IP"); 
	    } 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("WL-Proxy-Client-IP"); 
	    } 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getRemoteAddr(); 
	    } 
	    String ips[]=StringUtils.split(ip, ',');
	    for (String string : ips) {
			if(StringUtils.isBlank(string) || "unknown".equalsIgnoreCase(string)){
				continue;
			}else{
				return string;
			}
		}
	    return ip; 
	}
	
	/**
	 * 
	 * @param url
	 * @param send
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String dopost(String url, String send) throws HttpException, IOException {
		HttpClient theclient = new HttpClient();
		PostMethod method = new PostMethod(url);
		try {
			method.setRequestEntity(new StringRequestEntity(send, "text/xml", "UTF-8"));
			method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, timeout);
			theclient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(trytime, false));
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			int status = theclient.executeMethod(method);
			if (status == HttpStatus.SC_OK) {
				return method.getResponseBodyAsString();
			} else {
				throw new HTTPException(status);
			}
		} finally {
			method.releaseConnection();
		}
	}
	
    /**
     * 
     * @param address
     * @param params
     * @return
     * @throws IOException
     */
    public static JSONObject dopost(String address, Map<String, String> params) throws IOException {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httpPost = new HttpPost(address);
        // 设置超时
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(NetUtils.timeout).setConnectTimeout(NetUtils.timeout).build();
        httpPost.setConfig(requestConfig);
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        // 追加参数
        for (String s : params.keySet()) {
            formparams.add(new BasicNameValuePair(s, params.get(s)));
        }
        UrlEncodedFormEntity uefEntity = null;
        CloseableHttpResponse response = null;
        JSONObject entityObj = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, NetUtils.charset);
            httpPost.setEntity(uefEntity);
            
            response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String entityStr = EntityUtils.toString(entity, NetUtils.charset);
                    entityObj = JSONObject.fromObject(entityStr);
                    NetUtils.logger.info("Response content: " + entityStr);
                }
            }
        } catch (UnsupportedEncodingException e) {
            NetUtils.logger.error("http请求异常(UnsupportedEncodingException)：" + e.getMessage(), e);
        } catch (ClientProtocolException e) {
            NetUtils.logger.error("http请求异常(ClientProtocolException)：" + e.getMessage(), e);
        } catch (ParseException e) {
            NetUtils.logger.error("http请求异常(ParseException)：" + e.getMessage(), e);
        } catch (IOException e) {
            NetUtils.logger.error("http请求异常(IOException)：" + e.getMessage(), e);
        } finally {
            response.close();
            httpclient.close();
        }
        return entityObj;
    }
}
