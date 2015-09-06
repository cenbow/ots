package com.mk.ots.schedule;

import com.mk.framework.AppUtils;
import com.mk.framework.schedule.IScheduleEvent;
import com.mk.ots.promo.service.IPromoService;

public class GenNewMemberTicketScheduleAfter30Min implements IScheduleEvent{

	private static final long serialVersionUID = -1038809904010708292L;

	private Long mid;
	
	public GenNewMemberTicketScheduleAfter30Min() {
	}
	
	public GenNewMemberTicketScheduleAfter30Min(Long mid) {
		this.mid = mid;
	}
	
	@Override
	public String getName() {
		return "前台切客用户注册两小时后发券及通知";
	}

	@Override
	public String description() {
		return "";
	}

	@Override
	public void execute() {
		IPromoService promoService = AppUtils.getBean(IPromoService.class);
		promoService.genTicketByActive(1l, mid);
	}

}
