package com.mk.ots.schedule;

import com.mk.framework.AppUtils;
import com.mk.framework.schedule.IScheduleEvent;
import com.mk.ots.promo.service.IPromoService;

/**
 * @author nolan
 *
 */
public class PromoGenScheduleEveryDay implements IScheduleEvent{

	private static final long serialVersionUID = 714016559154707864L;

	@Override
	public String getName() {
		return "发放优惠券";
	}

	@Override
	public String description() {
		return "条件: 1)完成订单行为的用户以及一个月内完成订单数不超过2次的用户; 2)沉默用户：15天内打开APP次数不超过1次且未卸载APP的用户";
	}

	@Override
	public void execute() {
		IPromoService iPromoService = AppUtils.getBean(IPromoService.class);
		//1. 发放优惠券给沉默用户.
		iPromoService.genTicketByUnActiveMember();
		
		//2. 发放优惠券给符合一定条件的订单用户(完成订单行为的用户,一个月内完成订单数不超过2次的用户)
		iPromoService.genTicketByMemberOrderNumEveryDay();
	}

}
