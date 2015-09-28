package com.mk.framework.schedule;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.schedule.model.SchedulePlan;

public class TestScheduleTask implements IScheduleEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2670565284965688546L;

	@Override
	public String getName() {
		return "测试调度";
	}

	@Override
	public String description() {
		return "测试调度";
	}

	@Override
	public void execute() {
		System.out.println(getName()+"//"+description());
	}
	
	public static void main(String[] args) throws IOException {
		List list = Lists.newArrayList();
		Map map = Maps.newHashMap();
		map.put("id", 1l);
		map.put("name", "测试调度");
		map.put("startdate", "2015-05-03");
		map.put("enddate", "2015-05-30");
		map.put("script","com.mk.framework.schedule.TestScheduleTask");
		map.put("expression","0/3 * * * * ?");
		map.put("type","2");
		SchedulePlan plan = new ObjectMapper().convertValue(map, SchedulePlan.class);
		System.out.println(plan.getExpression());
		System.out.println(plan.getEnddate());
	}

}
