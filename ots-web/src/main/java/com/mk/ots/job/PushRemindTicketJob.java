package com.mk.ots.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.model.LPushLog;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.ticket.model.UTicket;
import com.mk.ots.ticket.service.ITicketService;

/**
 * 推送15元过一个月未使用的消息
 * <p>获得15元体验券后1个月未使用，push消息提醒用户使用优惠券
 * </p>
 * @author tankai
 *
 */
public class PushRemindTicketJob  extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(PushRemindTicketJob.class);
	protected int batchSize = 10000;

    //每次查询记录数
	protected int step = 1000;
    protected long pageIndex=0;
    protected long max=0;
    protected Map<String, Object> paramMap =new HashMap<String, Object>();
    protected Long activeid15=Constant.ACTIVE_15YUAN;//活动写死

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

		logger.info("PushRemindTicketJob::start");
		//判断该用户参加是否是15活动
		paramMap.put("activeid", activeid15);
		
		//获取记录数
		Map<String, Object> maxAndMin =  iTicketService.findMaxAndMinUTicketId(paramMap);
		max = (Long)maxAndMin.get("max");
		pageIndex = (Long)maxAndMin.get("min") - 1;
		logger.info("begin:"+pageIndex+",max:"+max);
		while(true){
			if(!run()){
				break;
			}
		}
		logger.info("PushRemindTicketJob::end");
	}
	
	

    public boolean run() {

	    List<UTicket> list =null;
		
		try {
			if(max==0){
				return false;
			}
			
			if(pageIndex > max){//只有当达到最大值是才会终止
				return false;
			}
			
			paramMap.put("begin", pageIndex);
			paramMap.put("limit", step);
			
			

			list = iTicketService.findUTicketList(paramMap);
			
			if (CollectionUtils.isNotEmpty(list)) {

				logger.info("查询结果集为："+list.size());
				for (UTicket uTicket : list) {
					try {
						
						//获取用户基本信息
						UMember uMember = iMemberService.findUMemberByMId(uTicket.getMid());
						if(uMember!=null){
							String phone = uMember.getPhone();
							String title = "眯客";
							String msgContent = "您的15元入住体验券还未使用，15元住酒店，再不开房还等啥！";
							String url = "";
							
							LPushLog lPushLog =new LPushLog();
							lPushLog.setMid(uTicket.getMid());
							
							lPushLog.setActiveid(activeid15);
							
							
							//校验是否发送过消息
							Long count = iMessageService.findActiveCount(lPushLog);
							if(count==0){
								//push message
								iMessageService.PushMsg(phone, title, msgContent, MessageType.USER.getId(), url,activeid15);
							}else{
								logger.info("用户mid："+uTicket.getMid()+"没有超过30天的15元优惠券提醒");
							}
						}
					} catch (Exception e) {
						logger.error("发送消息出错",e);
					}
				}
				int listSize = list.size();
	    		pageIndex = list.get(listSize-1).getMid();
	    		
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
