/**
 * 2015年7月17日上午10:10:15
 * zhaochuanbin
 */
package com.mk.ots.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.orm.kit.StrKit;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.pay.job.PayJob;
import com.mk.ots.pay.job.RetrySendLezhuJob;
import com.mk.ots.pay.model.ReSendLeZhu;
import com.mk.ots.pay.service.PayService;

/**
 * 业务：自动下发乐住币任务
 * 功能：对于下发乐住币失败的情况下，进行自动重发，每隔5分钟进行重新发送
 *
 */
public class AutoSendLeZhuJob extends QuartzJobBean {
	
	private static Logger logger = LoggerFactory.getLogger(AutoSendLeZhuJob.class);
	
	private PayJob retrySendLezhuJob = AppUtils.getBean(RetrySendLezhuJob.class);
	
//	@Autowired
//	private OtsCacheManager manager;
//	@Autowired
//	private IPayService payService;
//	@Autowired
//    private OrderService orderService;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		logger.info("自动重试下发乐住币Job启动...");
		
		retrySendLezhuJob.doJob(PayTaskTypeEnum.AUTORETRYSENDLEZHU, PayTaskStatusEnum.INIT, PayTaskStatusEnum.FINISH);
		
		logger.info("自动重试下发乐住币Job启动Job完毕.");
	}
	
	
	/*@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.info("OTSMessage::AutoSendLeZhuJob:自动发送乐住币开始");

		if (this.manager == null) {
			this.manager = AppUtils.getBean(OtsCacheManager.class);
		}
		if (this.payService == null) {
			this.payService = AppUtils.getBean(PayService.class);
		}
		if (this.orderService == null) {
			this.orderService = AppUtils.getBean(OrderService.class);
		}
		
		Jedis jedis = this.manager.getNewJedis();
		try {
	        Long relen = jedis.llen("reSendLeZhuList");   
	        logger.info("reSendLeZhuList的长度:"+relen);
	        for (int i = 0; i < relen; i++) {
	           String str =jedis.rpop("reSendLeZhuList");
	           if(StrKit.notBlank(str)){
	        	   ReSendLeZhu reSendLeZhu = JSON.parseObject(str,ReSendLeZhu.class);
	        	   
	        	   Cat.logEvent("OtsJob", "AutoSendLeZhuJob.executeInternal", Event.SUCCESS, "");
	        	   
	        	   OtaOrder order = this.orderService.findOtaOrderById(reSendLeZhu.getOrder().getId());
	        	   
	        	   if(order.getOrderStatus() >= OtaOrderStatusEnum.CheckOut.getId().intValue()) {
	        		   
	        		   logger.info("自动发送乐住币,订单:" + order.getId() + "订单状态为[" + order.getOrderStatus() + "],不能进行乐住币操作,此订单的自动下发乐住币任务终止.");
	        		   
	        		   return;
	        	   }
	        	   
	        	   
	        	   logger.info("自动发送乐住币,订单号:"+reSendLeZhu.getOrder().getId()+",支付订单号:"+reSendLeZhu.getPayid()+"****单个订单开始******");
	        	   Boolean reSend = this.payService.reSendLeZhu(reSendLeZhu.getOrder(),reSendLeZhu.getPayid(), reSendLeZhu.getPmsSendId(), reSendLeZhu.getPrice(), reSendLeZhu.getMemberName());
	        	   if(reSend!=null&&reSend){
	        		   logger.info("自动发送乐住币,订单号:"+reSendLeZhu.getOrder().getId()+",支付订单号:"+reSendLeZhu.getPayid()+"下发成功");
	        	   }else {
	        		   logger.info("自动发送乐住币,订单号:"+reSendLeZhu.getOrder().getId()+",支付订单号:"+reSendLeZhu.getPayid()+"下发失败");
	        	   }
	        	   logger.info("自动发送乐住币,订单号:"+reSendLeZhu.getOrder().getId()+",支付订单号:"+reSendLeZhu.getPayid()+"****单个订单结束******");
	           }else{
	        	   logger.info("redis中没有数据了，reSendLeZhuList");
	        	   return ;
	           }
	        }  
		} catch (Exception e) {
			  logger.error( "自动发送乐住币抛出异常",e);
		} finally {
			jedis.close();
		}
		logger.info("OTSMessage::AutoSendLeZhuJob:自动发送乐住币结束");
	}*/
	
}
