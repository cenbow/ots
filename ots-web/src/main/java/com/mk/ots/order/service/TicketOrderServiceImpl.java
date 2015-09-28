package com.mk.ots.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.mk.framework.MkJedisConnectionFactory;
import com.mk.ots.common.enums.PayStatusEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.dao.TicketOrderDAO;
import com.mk.ots.order.model.BTicketOrder;

@Service
public class TicketOrderServiceImpl implements TicketOrderService {

	private static Logger logger = LoggerFactory.getLogger(TicketOrderServiceImpl.class);


	@Autowired
	private MkJedisConnectionFactory jedisFactory = null;
	
	@Autowired
	private TicketOrderDAO payTicketDAO;
	
	@Override
	public Long savePayTicketInfo(BTicketOrder bTicketOrder) {
		return payTicketDAO.insert(bTicketOrder);
	}
	
	//处理订单
	public Integer updateBTicketOrderStatus(BTicketOrder bTicketOrder) {
		return payTicketDAO.update(bTicketOrder);
	}
	public boolean payOkByOrderid(Long orderid) {
		boolean b=false;
		BTicketOrder order = this.payTicketDAO.findById(orderid);
		if (order != null) {  //订单确定
			if(order.getPaystatus() >= PayStatusEnum.alreadyPay.getId().intValue() ){
				b=true;
			}
		}  
		return b;
	}

	@Override
	public BTicketOrder findTicketOrder(Long orderid) {
		return this.payTicketDAO.findById(orderid);
	}

	@Override
	public boolean payIsWaitPay(String orderid) {
		Jedis jedis = this.getJedis();
		try {
			if (!this.payOkLockOrder(orderid, jedis)) {
				return false;
			}
		BTicketOrder order = this.payTicketDAO.getWaitPay(orderid);
		if (order == null) {
			return false;
		} else {
			return true;
		}
		
		} finally {
			this.payOkReleaseLock(orderid, jedis);
			jedis.close();
		}
		
	}
	
	private boolean payOkLockOrder(String orderId, Jedis jedis) {
		long curMills = System.currentTimeMillis();
		String key = "ticketorder:" + orderId;;
		long result = jedis.setnx(key, Long.toString(curMills + 2000));
		return result != 0;
	}
	
	private void payOkReleaseLock(String orderId, Jedis jedis) {
		String key = "ticketorder:" + orderId;;
		jedis.del(key);
	}
	private Jedis getJedis() {
		return this.getJedisFactory().getJedis();
	}

	private MkJedisConnectionFactory getJedisFactory() {
		return this.jedisFactory;
	}

}
