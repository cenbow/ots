package com.hessian.test;

import java.net.MalformedURLException;
import java.util.ArrayList;

import cn.com.winhoo.pms.webout.service.IPmsOutService;
import cn.com.winhoo.pms.webout.service.bean.PmsOtaAddOrder;

import com.caucho.hessian.client.HessianProxyFactory;

public class HessianTest {

	public static void main(String[] args) throws MalformedURLException {
		HessianProxyFactory factory = new HessianProxyFactory();
		// PMS访问地址，改成读取配置hessian_urls.properties的方式
		String url = "http://10.4.10.190:8080/soap/hessian/pmsOutService";
		IPmsOutService service = (IPmsOutService) factory.create(IPmsOutService.class, url);
		service.submitAddOrder(1L, new ArrayList<PmsOtaAddOrder>());
	}
}
