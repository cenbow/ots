package com.mk.ots.promo.job;

import org.springframework.beans.factory.annotation.Autowired;

import com.mk.framework.schedule.IScheduleEvent;
import com.mk.ots.promo.service.IPromoService;

public class GenUTicketJobAt25 implements IScheduleEvent {

	@Autowired
	private IPromoService iPromoService;
	
	@Override
	public String getName() {
		return "";
	}

	@Override
	public String description() {
		return "";
	}

	@Override
	public void execute() {
		iPromoService.genTicketByActiveMember();
		iPromoService.genTicketByMemberOrderNumAt25();
	}

}
