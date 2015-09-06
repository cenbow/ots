package com.mk.orm.plugin.hessian;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.server.HessianServlet;
import com.mk.pms.manager.PropertiesUtil;
import com.mk.pms.order.service.PmsOrderService;

/**
 * 
 * @author chuaiqing.
 *
 */
public class HessianHelper {
	private static final Logger logger = LoggerFactory.getLogger(HessianHelper.class);

	// hessian module
	public static final String hessian_ots = "hessian_ots";
	public static final String hessian_soap = "hessian_soap";
	public static final String hessian_newpms = "hessian_newpms";
	public static final String hessian_bms = "hessian_bms";

	public static Map<String, String> urls = new HashMap<String, String>();
	static {
		try {
			PropertiesUtil pro = new PropertiesUtil();
			pro.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("hessian_urls.properties"));
			urls = pro.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T createClient(String system, String service) {
		T client = null;
		try {
			HessianProxyFactory factory = new HessianProxyFactory();
			HessianServlet hessianServlet = HessianDispatcherServlet.getHessianServlet(service);
			if (!urls.containsKey(system)) {
				throw new Exception("没有找到" + system + "的地址配置，请检查hessian_urls.properties");
			}
			String hessianURL = urls.get(system) + service;
			client = (T) factory.create(hessianServlet.getAPIClass(), hessianURL);
		} catch (Exception e) {
			logger.error("创建 Hessian 客户端出错！", e);
		}
		return client;
	}
	
	public static String getUrl(String system, String service){
		String hessianURL = urls.get(system) + service;
		return hessianURL;
	}
	
	public static <T> T createClient(String system, String service,  Class serviceInterface) {
		T client = null;
        try {
        	String hessianURL = urls.get(system) + service;
            HessianProxyFactory factory = new HessianProxyFactory();
            client = (T) factory.create(serviceInterface, hessianURL);
        } catch (Exception e) {
            logger.error("创建 Hessian 客户端出错！", e);
        }
        return client;
	}
	
    @SuppressWarnings("unchecked")
    public static <T> T createClient(String hessianURL, Class serviceInterface) {
        T client = null;
        try {
            HessianProxyFactory factory = new HessianProxyFactory();
            client = (T) factory.create(serviceInterface, hessianURL);
        } catch (Exception e) {
            logger.error("创建 Hessian 客户端出错！", e);
        }
        return client;
    }
    
    public static void main(String[] args) {
    	PmsOrderService serv  = HessianHelper.createClient(hessian_ots, "/pmsorder", PmsOrderService.class);
    	System.out.println(serv);
	}
}
