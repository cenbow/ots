package com.mk.ots.order.hessian;

import java.util.Map;

public interface OrderHessianService {

	
	public Map cancel(Map param);
	
	public Map cancelOrderSuper(Map param);
	
	public Map modify(Map param);
	
	public Map modifyOrderStatusAfterCancelPay(Map param);
}
