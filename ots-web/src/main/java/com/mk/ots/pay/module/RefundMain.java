package com.mk.ots.pay.module;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.caucho.hessian.client.HessianProxyFactory;
import com.mk.ots.order.hessian.OrderHessianService;

public class RefundMain {

	public static void main(String[] args) {
		String orderid="695385";
		
		HessianProxyFactory factory = new HessianProxyFactory();
		String url = "http://ots.imike.com/ots/hessian/order";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		OrderHessianService service=null;
		try {
			service = (OrderHessianService) factory.create(OrderHessianService.class, url);
			Map param = new HashMap<>();
			param.put("orderid", orderid);
			param.put("customerservicename", "客服人员");
			param = service.cancelOrderSuper(param);
			System.out.println(param);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}


	}

}
