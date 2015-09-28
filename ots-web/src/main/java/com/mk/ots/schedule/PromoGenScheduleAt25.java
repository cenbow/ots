package com.mk.ots.schedule;

import com.mk.framework.AppUtils;
import com.mk.framework.schedule.IScheduleEvent;
import com.mk.ots.promo.service.IPromoService;

public class PromoGenScheduleAt25  implements IScheduleEvent{

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "发放优惠券给订单用户";
	}

	@Override
	public String description() {
		return "每月二十五号发放，完成订单数超过2次（含2次）的用户, 并以push，短信通知";
	}

	@Override
	public void execute() {
		IPromoService iPromoService = AppUtils.getBean(IPromoService.class);
		//1. 发放优惠券给活跃用户
		iPromoService.genTicketByActiveMember();

		//2. 发放优惠券给符合一定条件的订单用户, 每月二十五号发放，完成订单数超过2次（含2次）的用户
		iPromoService.genTicketByMemberOrderNumAt25();
	}

}
