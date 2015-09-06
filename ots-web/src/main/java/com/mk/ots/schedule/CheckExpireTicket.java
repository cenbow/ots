package com.mk.ots.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mk.framework.schedule.IScheduleEvent;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.ticket.controller.TicketController;

/**
 * 检测优惠券失效并推送消息
 * @author nolan
 *
 */
public class CheckExpireTicket implements IScheduleEvent{
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IMessageService iMessageService;
	
	@Override
	public String getName() {
		return "优惠券到期检测";
	}

	@Override
	public String description() {
		return "优惠券失效（三天前）推送消息";
	}

	@Override
	public void execute() {
		logger.info("CheckExpireTicket-----------开始检测三天后将要失效的券记录-------------");
		logger.info("1. 查询三天后要到期的券记录");
		
		logger.info("2. push通知用户消息");
		
		logger.info("CheckExpireTicket-----------结束检测三天后将要失效的券记录-------------");
	}

}
