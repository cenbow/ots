package com.mk.ots.order.service;

import com.mk.ots.order.model.BTicketOrder;


public interface TicketOrderService {
	public abstract Long savePayTicketInfo(BTicketOrder bTicketOrder);

	public abstract Integer updateBTicketOrderStatus(BTicketOrder bTicketOrder) ;
	
	public boolean payOkByOrderid(Long orderid) ;
	
	public BTicketOrder findTicketOrder(Long orderid) ;
	
	public boolean payIsWaitPay(String orderid);
}