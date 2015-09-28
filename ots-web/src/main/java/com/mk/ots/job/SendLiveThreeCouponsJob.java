package com.mk.ots.job;

import com.mk.framework.AppUtils;
import com.mk.framework.util.Config;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.StatisticInvalidTypeEnum;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.message.model.LPushLog;
import com.mk.ots.message.model.MessageType;
import com.mk.ots.message.service.IMessageService;
import com.mk.ots.order.model.BOtaorder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.system.model.UToken;
import com.mk.ots.system.service.ITokenService;
import com.mk.ots.ticket.service.ITicketService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.base.Strings;
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
 * 住三送一活动---推送活动消息
 * <p>
 * 1.每天10点01分执行
 * 2.获取那些住过三个不同酒店的用户
 * 3.给符合要求的用户发放优惠券
 * </p>
 *
 * @author jjh
 *
 */
public class SendLiveThreeCouponsJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(SendLiveThreeCouponsJob.class);

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
			SendLiveThreeCouponsJob.logger.info("SendLiveThreeCouponsJob:推送住三送一活动优惠券:start");

            // 先创建线程池
            this.executorService = Executors.newFixedThreadPool(SendLiveThreeCouponsJob.THREAD_COUNT);

            // 住三送一活动
            this.liveThreeToTicket();

			SendLiveThreeCouponsJob.logger.info("SendLiveThreeCouponsJob:推送住三送一活动优惠券:end");
		} finally {
            // 关闭线程池
			this.getExecutorService().shutdown();
		}

	}

    /**
     * 住三送一活动
     */
    private void liveThreeToTicket() {
        String activityMsg = "住三送一活动（activityId：17）";

        logger.info(activityMsg + "开始处理");

        // 从配置文件读出入住酒店数量
        String liveHotelNumStr = Config.getValue("liveHotelNum");
        if (StringUtils.isBlank(liveHotelNumStr)) {
            logger.error(activityMsg + "推送优惠券job:liveHotelNum is invalid!");
            // TODO 若是job出现异常，要通知开发人员排查（啥方式通知可以考虑）
            return;
        }

        try {
            int liveHotelNum = Integer.parseInt(liveHotelNumStr);

            // 先查询出符合条件的用户mid
            List<Long> mids = iTicketService.getCountValid(StatisticInvalidTypeEnum.statisticValid.getType(),
                    liveHotelNum, SendLiveThreeCouponsJob.batDataNum);

            while (!CollectionUtils.isEmpty(mids)) {

                // 数据控制计数，计数不为零情况下阻塞任务
                CountDownLatch doneSingal = new CountDownLatch(mids.size());

                // 多线程处理，给每个用户发送活动券
                for (Long mid : mids) {
                    this.executorService.execute(new LiveThreeRunner(mid, doneSingal, threadSleepTime));
                }

                // 等待线程执行完数据
                boolean result = doneSingal.await(workerTimeout, TimeUnit.MILLISECONDS);
                logger.info(result ? activityMsg + "线程正常执行" : activityMsg + "线程执行慢或者等待时间设置不合理");

                // 再次查询符合条件的用户
                mids = iTicketService.getCountValid(StatisticInvalidTypeEnum.statisticValid.getType(),
                        liveHotelNum, SendLiveThreeCouponsJob.batDataNum);

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
        private Long mid = null;
        private CountDownLatch doneSingal;
        private long sleepTime;

        public LiveThreeRunner(Long mid, CountDownLatch doneSingal, long sleepTime) {
            this.mid = mid;
            this.doneSingal = doneSingal;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            String activityMsg = "住三送一活动（activityId：17）";

            logger.info(activityMsg + "开始给用户{" + mid + "}绑定优惠券，并推送消息");

            try {
                // 给用户生成优惠券
                sendTicket(mid, Constant.LIVE_THREE_ACTIVE);

            } catch (Exception e) {
                logger.error(activityMsg + "给用户{" + mid + "}发放券出现异常", e);
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
	private void sendTicket(Long mid, Long activeId) {

		// 获取用户基本信息
		final UMember uMember = this.iMemberService.findUMemberByMId(mid);
        // 不关心用户存在与否，先把用户入住流水更新掉，免得出现无限次领取
        // 先更新用户入住酒店流水表，把统计有效字段置为无效
        this.iTicketService.updateInvalidByMid(mid);

		if (null != uMember) {
            // 为用户发放优惠券
			final List<Long> list = this.iPromoService.genTicketByActive(activeId, mid);
			if (CollectionUtils.isNotEmpty(list)) {
				SendLiveThreeCouponsJob.logger.info("给用户发放优惠券成功：mid=" + mid);

                // 给用户推送消息
				SendLiveThreeCouponsJob.this.sendMessage(list, uMember, activeId);
			} else {

				SendLiveThreeCouponsJob.logger.info("给用户发放优惠券为空,mid=" + mid);
			}

		} else {
			SendLiveThreeCouponsJob.logger.info("用户基本信息不存在，不发放优惠券,mid=" + mid);
		}
	}

	/**
	 * 发送消息
	 */
	private void sendMessage(List<Long> list, UMember uMember, Long activeId) {
		for (Long promotionid : list) {
			String phone = uMember.getPhone();

			String ostype = uMember.getOstype();
			if (StringUtils.isEmpty(ostype)) {
				SendLiveThreeCouponsJob.logger.info("用户来源渠道ostype为空，发放优惠券成功，但是不推送消息,mid=" + uMember.getMid());
				continue;
			}
			// 根据mid和ostype查询token
			TokenTypeEnum tokenType = TokenTypeEnum.PT;
			if (!Strings.isNullOrEmpty(ostype)) {
				if (OSTypeEnum.IOS.getId().equals(ostype) || OSTypeEnum.ANDROID.getId().equals(ostype)) {
					tokenType = TokenTypeEnum.APP; // 手机app
				} else if (OSTypeEnum.WX.getId().equals(ostype)) {
					tokenType = TokenTypeEnum.WX; // 微信客户端
				} else {
					tokenType = TokenTypeEnum.PT; // 网页
				}

			}
			UToken uToken = this.iTokenService.findTokenByMId(uMember.getMid(), tokenType);

			if (uToken == null) {
				SendLiveThreeCouponsJob.logger.info("用户token为空，发放优惠券成功，但是不推送消息,mid=" + uMember.getMid());
				continue;
			}
			/**
			 * 标题：领取30元红包 内容：感谢您入住眯客酒店，送你30元红包，赶快领取！
			 * URL：http://weixin.imike.com/
			 * event/getvalidticket?token=**&phone=**&systype=1 systype： 1 -
			 * android 2 - IOS TODO:systype能否反过来， 参数promotionid要加？？？？
			 */
            String title = "";
            String msgContent = "";
            String url = "";

            title = "睡3晚 免费送1晚";
            msgContent = "你爱住，我爱送！您已获得住3晚免费送一晚的入住资格，快戳我领取吧！";
            // TODO URL要如何定义
            // 用户手机是ios的话，需要在url增加特殊标示
            if (OSTypeEnum.IOS.getId().equals(ostype)) {
                url = "www.imikeshareMessage-url-scheme://inneractivityurl/"
                        + "http://weixin.imike.com/event/appdetail?token=" + uToken.getAccesstoken()
                        + "&phone=" + uMember.getPhone() + "&systype=" + ostype + "&promotionid=" + promotionid
                        + "&eid=" + activeId + "&comfrom=evenindex";

            } else {
                url = "www.imikeshareMessage-url-scheme://inneractivityurl/"
                        + "http://weixin.imike.com/event/getvalidticket?token=" + uToken.getAccesstoken()
                        + "&phone=" + uMember.getPhone() + "&systype=" + ostype + "&promotionid=" + promotionid
                        + "&eid=" + activeId;
            }

			LPushLog lPushLog = new LPushLog();
			lPushLog.setMid(uMember.getMid());
			lPushLog.setActiveid(activeId);

			// push message
			this.iMessageService.pushMsg(phone, title, msgContent, MessageType.USER.getId(), url, activeId);
		}

        logger.info("住三送一活动（activityId：17）给用户{" + uMember.getMid() + "}发放券成功");

	}

	private ExecutorService getExecutorService() {
		return this.executorService;
	}

}
