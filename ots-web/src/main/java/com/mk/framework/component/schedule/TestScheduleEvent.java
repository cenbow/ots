package com.mk.framework.component.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mk.framework.AppUtils;
import com.mk.framework.schedule.IScheduleEvent;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderServiceImpl;

public class TestScheduleEvent implements IScheduleEvent {

    @Resource
    private OtsCacheManager manager;

	@Override
	public String getName() {
		return "取消订单调度";
	}

	@Override
	public String description() {
		return "订单注册15分钟没有确认付款取消订单调度";
	}

	@Override
	public void execute() {
		// 缓存中取得订单的对象，通过订单id和创建时间判断是否超过15分钟
		Date nowDateTime = new Date();

		if (manager == null) {
            manager = AppUtils.getBean(OtsCacheManager.class);
        }
		List<Map> orderTimeList = (List<Map>) manager.get("orderJobList", "1");
		if (orderTimeList == null) {
			return;
		}
		List<Map> cancelOrderTimeList = new ArrayList<Map>();
		for(Map orderItem : orderTimeList){
			Date orderCreateTime = (Date) orderItem.get("createtime");
			
	        long temp = nowDateTime.getTime() - orderCreateTime.getTime();    //相差毫秒数
	        long hours = temp / 1000 / 3600;                //相差小时数
	        long temp2 = temp % (1000 * 3600);
	        long mins = temp2 / 1000 / 60;                    //相差分钟数
	        // 当前和订单创建时间差大于15分钟
	        if ((hours > 0) || (mins >= 15)) {
	        	cancelOrderTimeList.add(orderItem);
	        	OrderServiceImpl orderService = AppUtils.getBean(OrderServiceImpl.class);
	        	OtaOrder order = orderService.findOtaOrderById(Long.parseLong(orderItem.get("orderid").toString()));
	        	if(order != null && order.getOrderStatus() < 140){
	        		orderService.cancelOrder(order);
	        		//Logger.getLogger(getClass()).warn("订单："+orderItem.get("orderid")+"超过15分钟未付款,取消此订单!");
	        	}
	        }
		}
		if(cancelOrderTimeList.size() > 0) {
			for(Map cancelOrderItem : cancelOrderTimeList){
				if(orderTimeList.contains(cancelOrderItem)){
					orderTimeList.remove(cancelOrderItem);
				}
			}
			manager.put("orderJobList", "1", orderTimeList);
		}
	}

}
