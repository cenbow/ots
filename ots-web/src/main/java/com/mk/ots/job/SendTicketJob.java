package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.framework.util.Config;
import com.mk.ots.common.enums.MessageSendTypeEnum;
import com.mk.ots.common.enums.MessageTypeEnum;
import com.mk.ots.common.enums.StatisticInvalidTypeEnum;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.system.service.ITokenService;
import com.mk.ots.ticket.model.USendUticket;
import com.mk.ots.ticket.service.ITicketService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 推送活动消息
 * <p>
 * 给固定用户推送优惠券和消息
 * </p>
 *
 * @author tankai
 *
 */
public class SendTicketJob extends QuartzJobBean {

	private static Logger logger = LoggerFactory.getLogger(SendTicketJob.class);

    // 线程池大小
	private static final int THREAD_COUNT = 20;

    // 每次从数据库抓取数量
    private static final int batDataNum = 100;

    // 给用户发放优惠券处理超时
    private static final long workerTimeout = 600000;

    // 线程休眠时间
    private static final long threadSleepTime = 10;

	protected Map<String, Object> paramMap = new HashMap<String, Object>();

	private IMessageService iMessageService = AppUtils.getBean(IMessageService.class);
	private IMemberService iMemberService = AppUtils.getBean(IMemberService.class);
	private ITicketService iTicketService = AppUtils.getBean(ITicketService.class);
	private IPromoService iPromoService = AppUtils.getBean(IPromoService.class);
	private ITokenService iTokenService = AppUtils.getBean(ITokenService.class);

	private ExecutorService executorService = null;

    /**
     * 测试job
     *
     * @throws JobExecutionException
     */
    public void testjob() throws JobExecutionException {
        this.executeInternal(null);
    }

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		try {
			logger.info("SendLiveThreeCouponsJob:推送住三送一活动优惠券:start");

            // 先创建线程池
            this.executorService = Executors.newFixedThreadPool(THREAD_COUNT);

            // 住三送一活动
            this.sendTicket();

			logger.info("SendLiveThreeCouponsJob:推送住三送一活动优惠券:end");
		} finally {
            // 关闭线程池
			this.getExecutorService().shutdown();
		}

	}

    /**
     * 住三送一活动
     */
    private void sendTicket() {
        String activityMsg = "住三送一活动（activityId："+Config.getValue("SendTicketActiveId")+"）";

        logger.info(activityMsg + "开始处理");

        try {

            // 先查询出符合条件的用户mid
            List<USendUticket> mids = iTicketService.getNeedSendCountValid(StatisticInvalidTypeEnum.statisticValid.getType(),batDataNum);

            while (!CollectionUtils.isEmpty(mids)) {

                // 数据控制计数，计数不为零情况下阻塞任务
                CountDownLatch doneSingal = new CountDownLatch(mids.size());

                // 多线程处理，给每个用户发送活动券
                for (USendUticket uSendUticket : mids) {
                    this.executorService.execute(new LiveThreeRunner(uSendUticket, doneSingal, threadSleepTime));
                }

                // 等待线程执行完数据
                boolean result = doneSingal.await(workerTimeout, TimeUnit.MILLISECONDS);
                logger.info(result ? activityMsg + "线程正常执行" : activityMsg + "线程执行慢或者等待时间设置不合理");

                // 再次查询符合条件的用户
                mids = iTicketService.getNeedSendCountValid(StatisticInvalidTypeEnum.statisticValid.getType(), batDataNum);

            }

            logger.info(activityMsg + "发放券处理完成");

        } catch (Exception e) {
            logger.error(activityMsg + "处理时出现异常", e);
        }
    }

    /**
     * 住三送一活动线程处理
     */
    private class LiveThreeRunner implements Runnable {
        private USendUticket uSendUticket = null;
        private CountDownLatch doneSingal;
        private long sleepTime;

        public LiveThreeRunner(USendUticket uSendUticket, CountDownLatch doneSingal, long sleepTime) {
            this.uSendUticket = uSendUticket;
            this.doneSingal = doneSingal;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            String activityMsg = "住三送一活动（activityId："+Config.getValue("SendTicketActiveId")+"）";

            logger.info(activityMsg + "开始给用户{" + uSendUticket.getMid() + "}绑定优惠券，并推送消息");

            try {
                // 给用户生成优惠券
                sendTicket(uSendUticket, Long.valueOf(Config.getValue("SendTicketActiveId")));

            } catch (Exception e) {
                logger.error(activityMsg + "给用户{" + uSendUticket.getMid() + "}发放券出现异常", e);
            } finally {
                this.doneSingal.countDown();
            }

            // 小线程休眠一下
            try {
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (Exception e) {
                logger.error("线程出现异常", e);
            }

        }
    }

	/**
	 * 给用户发放优惠券
	 *
	 * @param mid 用户编号
	 */
	private void sendTicket(USendUticket uSendUticket, Long activeId) {

		// 获取用户基本信息
		final UMember uMember = this.iMemberService.findUMemberByMId(uSendUticket.getMid());
        // 不关心用户存在与否，先把用户入住流水更新掉，免得出现无限次领取
        // 先更新用户入住酒店流水表，把统计有效字段置为无效
        

		if (null != uMember) {
            // 为用户发放优惠券
			final List<Long> list = this.iPromoService.genTicketByActive(activeId, uSendUticket.getMid());
			if (CollectionUtils.isNotEmpty(list)) {
				logger.info("给用户发放优惠券成功：mid=" + uSendUticket.getMid());

                // 给用户推送消息
				this.sendMessage(list, uMember, activeId,uSendUticket);
				
				this.iTicketService.updateSendTicketInvalidByMid(uSendUticket.getMid());
			} else {

				logger.info("给用户发放优惠券为空,mid=" + uSendUticket.getMid());
			}

		} else {
			logger.info("用户基本信息不存在，不发放优惠券,mid=" + uSendUticket.getMid());
		}
	}

	/**
	 * 发送消息
	 */
	private void sendMessage(List<Long> list, UMember uMember, Long activeId,USendUticket uSendUticket) {

		if(MessageSendTypeEnum.sms.getId().equals(uSendUticket.getMsgtype())){
			String source = "";
	        String ip = "";
	        
	        String message =Config.getValue("SendTicketMsg");
            Long msgid = iMessageService.logsms(uMember.getPhone(), message, MessageTypeEnum.normal, source, ip, null, null);
            boolean sendMsg = iMessageService.sendMsg(msgid, uMember.getPhone(), message, MessageTypeEnum.normal, null);
            logger.info("发送短信: phone:{}, message:{}, messagetype:{}, success:{}", uMember.getPhone(), message.trim(), MessageTypeEnum.normal.getName(), sendMsg);
		}
	}

	private ExecutorService getExecutorService() {
		return this.executorService;
	}


}
