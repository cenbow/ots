package com.mk.ots.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.model.LPushLog;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.order.model.BOtaorder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.ticket.service.ITicketService;

/**
 * 推送活动消息
 * <p>每次跑批获取在跑批前12小时发生的订单中，有适用10+1优惠券的订单，并已离店的订单，
 * 取这些订单的用户的集合。通过和用户优惠券表比较，排除掉已经拥有15元体验券的用户，余下的用户，为每人推送一条个人消息
 * </p>
 * @author tankai
 *
 */
public class PushMessageJob  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(PushMessageJob.class);
	protected int batchSize = 10000;

    //每次查询记录数
	protected int step = 1000;
    protected long pageIndex=0;
    protected long max=0;
    protected Map<String, Object> paramMap =new HashMap<String, Object>();
    protected Long activeid10_1=Constant.ACTIVE_10YUAN_1;//活动写死
    protected Long activeid15=Constant.ACTIVE_15YUAN;//活动写死

	private OrderService orderService = AppUtils.getBean(OrderService.class);
	private IMessageService iMessageService = AppUtils.getBean(IMessageService.class);
	private IMemberService iMemberService = AppUtils.getBean(IMemberService.class);
	private ITicketService iTicketService = AppUtils.getBean(ITicketService.class);
	/**
	 * 测试job
	 * @throws JobExecutionException
	 */
	public void testjob() throws JobExecutionException{
		executeInternal(null);
	}
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("PushMessageJob::start");
		//判断该用户参加是否是10+1活动
		paramMap.put("activeid", activeid10_1);
		//预付
		paramMap.put("ordertype", OrderTypeEnum.YF.getId());
		paramMap.put("orderstatus", OtaOrderStatusEnum.CheckOut.getId());
		
		//获取12小时之前的时间
		Date date = DateUtils.addHours(new Date(), -12);
		//时间判断区间用离店时间判断
		paramMap.put("checkouttime", date);
		//获取记录数
		Map<String, Object> maxAndMin =  orderService.findMaxAndMinOrderId(paramMap);
		max = (Long)maxAndMin.get("max");
		pageIndex = (Long)maxAndMin.get("min") - 1;
		logger.info("begin:"+pageIndex+",max:"+max);
		while(true){
			if(!run()){
				break;
			}
		}
		logger.info("PushMessageJob::end");
	}
	
	

    public boolean run() {

	    List<BOtaorder> otaOrderList =null;
		
		try {
			if(max==0){
				return false;
			}
			
			if(pageIndex > max){//只有当达到最大值是才会终止
				return false;
			}
			
			paramMap.put("begin", pageIndex);
			paramMap.put("limit", step);
			
			Cat.logEvent("OtsJob", "PushMessageJob.run", Event.SUCCESS, "");

			otaOrderList = orderService.findOtaOrderList(paramMap);
			
			if (CollectionUtils.isNotEmpty(otaOrderList)) {

				logger.info("查询结果集为："+otaOrderList.size());
				for (BOtaorder otaOrder : otaOrderList) {
					try {
						if(otaOrder.getMid()==null){
							continue;
						}
						
						
						//获取用户基本信息
						UMember uMember = iMemberService.findUMemberByMId(otaOrder.getMid());
						if(uMember!=null){
							String phone = uMember.getPhone();
							String title = "15元入住体验券";
							String msgContent = "你获得一次免费领取15元入住体验券的机会哦！快点领取吧！";
							String url = "www.imikeshareMessage-url-scheme://inneractivityurl/http://wechat.imike.cn/event/appdetail?eid="+activeid15;
							LPushLog lPushLog =new LPushLog();
							lPushLog.setMid(otaOrder.getMid());
							
							lPushLog.setActiveid(activeid10_1);
							
							
							//校验是否发送过消息
							Long count = iMessageService.findActiveCount(lPushLog);
							if(count==0){
								//check 当前用户是否有15元优惠券，如果有则不发送消息
								//activeid15
								Long count2 = iTicketService.countByMidAndActiveId(otaOrder.getMid(), activeid15);
								if(count2==0){
									//push message
									iMessageService.pushMsg(phone, title, msgContent, MessageType.USER.getId(), url,activeid15);
								}else{
									logger.info("用户mid："+otaOrder.getMid()+"用户已经存在15元优惠券，不再发送提醒");
								}
							}else{
								logger.info("用户mid："+otaOrder.getMid()+"已经发送过优惠券提醒");
							}
						}
					} catch (Exception e) {
						logger.error("发送消息出错",e);
					}
				}
				int listSize = otaOrderList.size();
	    		pageIndex = otaOrderList.get(listSize-1).getMid();
	    		
	    		if(listSize < step){//如果少于阀值，则说明为已经是最后一批了
					return false;
				}
			}else{
				logger.info("查询结果集为空");
				return false;
			}
			
		} catch (Throwable e) {
			logger.error("执行task出错",e);
			return false;
		}
		return true;
	}
}
