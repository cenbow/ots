/**
 * @author jianghe
 */
package com.mk.ots.common.utils;

import com.google.gson.Gson;
import com.mk.framework.AppUtils;
import com.mk.ots.common.bean.PendingTaskBean;
import com.mk.ots.manager.OtsCacheManager;

/**
 * redis队列操作类
 */
public class RedisQueueUtil {
	private static Gson gson = new Gson();
	/**
	 * 入队
	 * @param pendingTaskBean 待处理任务封装bean
	 * @param queueName 队列名称  按业务区分
	 */
	public static void lpushToQueue(PendingTaskBean pendingTaskBean, String queueName) {
		OtsCacheManager manager = AppUtils.getBean(OtsCacheManager.class);
		manager.lpush(queueName, gson.toJson(pendingTaskBean));
	}
	
	/**
	 * 出队
	 * @param queueName 队列名称
	 */
	public static PendingTaskBean rpopFromQueue(String queueName){
		OtsCacheManager manager = AppUtils.getBean(OtsCacheManager.class);
		String str = manager.rpop(queueName);
		if(str!=null){
			return gson.fromJson(str, PendingTaskBean.class);
		}
		return null;
	}
}
