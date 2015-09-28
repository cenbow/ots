package com.mk.ots.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.base.Strings;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mk.framework.AppUtils;
import com.mk.framework.util.Config;
import com.mk.ots.common.enums.OSTypeEnum;
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

/**
 * 推送活动消息
 * <p>
 * 1.每天10点执行 2.取入住时间为前一天24小时的所有订单，当前状态为IN、OK、PM，并invalidreason字段不为1或2
 * 3.若该订单是用户的唯一一笔订单，则批量实例化优惠券到用户优惠券表中，优惠券状态为待领取
 * 4.push30元优惠券领取通知消息给用户，url中拼入实例优惠券ID
 * </p>
 *
 * @author tankai
 *
 */
public class SendCouponsJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(SendCouponsJob.class);

	private static final int THREAD_COUNT = 20;

	protected int batchSize = 10000;

	// 每次查询记录数
	protected int step = 1000;
	protected long pageIndex = 0;
	protected long max = 0;
	protected Map<String, Object> paramMap = new HashMap<String, Object>();
	protected Long active_b_30yuan = Constant.ACTIVE_B_30YUAN;// 活动写死

	private OrderService orderService = AppUtils.getBean(OrderService.class);
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
			SendCouponsJob.logger.info("SendCouponsJob::start");
			// 判断该用户参加是否是10+1活动
			this.paramMap.put("activeid", this.active_b_30yuan);

			String date = DateUtils.getCertainDate(Integer.valueOf(Config.getValue("SendCouponsJob.datelimit")));
			// 时间判断区间用离店时间判断
			this.paramMap.put("starttime", date + " 00:00:00");
			this.paramMap.put("endtime", date + " 23:59:59");
			SendCouponsJob.logger.info("checkintime:" + date);

			// 获取记录数
			Map<String, Object> maxAndMin = this.orderService.findPromotionMaxAndMinMId(this.paramMap);
			this.max = (Long) maxAndMin.get("max");
			this.pageIndex = (Long) maxAndMin.get("min") - 1;
			SendCouponsJob.logger.info("begin:" + this.pageIndex + ",max:" + this.max);
			this.executorService = Executors.newFixedThreadPool(SendCouponsJob.THREAD_COUNT);
			while (true) {
				if (!this.run()) {
					break;
				}
			}
			SendCouponsJob.logger.info("SendCouponsJob::end");
		} finally {
			this.getExecutorService().shutdown();
		}

	}

	public boolean run() {

		List<BOtaorder> otaOrderList = null;

		try {
			if (this.max == 0) {
				return false;
			}

			if (this.pageIndex > this.max) {// 只有当达到最大值是才会终止
				return false;
			}

			this.paramMap.put("begin", this.pageIndex);
			this.paramMap.put("max", this.max);
			this.paramMap.put("limit", this.step);

			otaOrderList = this.orderService.findPromotionMidList(this.paramMap);

			if (CollectionUtils.isNotEmpty(otaOrderList)) {

				SendCouponsJob.logger.info("查询结果集为：" + otaOrderList.size());
				this.getExecutorService().execute(new SendTask(otaOrderList));

				int listSize = otaOrderList.size();
				this.pageIndex = otaOrderList.get(listSize - 1).getMid();

				if (listSize < this.step) {// 如果少于阀值，则说明为已经是最后一批了
					return false;
				}
			} else {
				SendCouponsJob.logger.info("查询结果集为空");
				return false;
			}

		} catch (Throwable e) {
			SendCouponsJob.logger.error("执行task出错", e);
			return false;
		}
		return true;
	}

	/**
	 * 给用户发放优惠券
	 *
	 * @param otaOrder
	 */
	private void sendTicket(final BOtaorder otaOrder) {

		// 获取用户基本信息
		final UMember uMember = this.iMemberService.findUMemberByMId(otaOrder.getMid());
		if (uMember != null) {
			final List<Long> list = this.iPromoService.genTicketByActive(this.active_b_30yuan, otaOrder.getMid());
			if (CollectionUtils.isNotEmpty(list)) {
				SendCouponsJob.logger.info("给用户发放优惠券成功：mid=" + otaOrder.getMid());
				SendCouponsJob.this.sendMessage(list, uMember, otaOrder);
			} else {

				SendCouponsJob.logger.info("给用户发放优惠券为空,mid=" + otaOrder.getMid());
			}

		} else {
			SendCouponsJob.logger.info("用户基本信息不存在，不发放优惠券,mid=" + otaOrder.getMid());
		}
	}

	/**
	 * 发送消息
	 */
	private void sendMessage(List<Long> list, UMember uMember, BOtaorder otaOrder) {
		for (Long promotionid : list) {
			String phone = uMember.getPhone();

			String ostype = uMember.getOstype();
			if (StringUtils.isEmpty(ostype)) {
				SendCouponsJob.logger.info("用户来源渠道ostype为空，发放优惠券成功，但是不推送消息,mid=" + otaOrder.getMid());
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
				SendCouponsJob.logger.info("用户token为空，发放优惠券成功，但是不推送消息,mid=" + otaOrder.getMid());
				continue;
			}
			/**
			 * 标题：领取30元红包 内容：感谢您入住眯客酒店，送你30元红包，赶快领取！
			 * URL：http://weixin.imike.com/
			 * event/getvalidticket?token=**&phone=**&systype=1 systype： 1 -
			 * android 2 - IOS TODO:systype能否反过来， 参数promotionid要加？？？？
			 */
			String title = "领取30元红包 ";
			String msgContent = "感谢您入住眯客酒店，送你30元红包，赶快领取！";
			String url = "www.imikeshareMessage-url-scheme://inneractivityurl/" + "http://weixin.imike.com/event/getvalidticket" + "?token=" + uToken.getAccesstoken() + "&phone=" + phone
					+ "&promotionid=" + promotionid + "&systype=" + ostype;
			LPushLog lPushLog = new LPushLog();
			lPushLog.setMid(otaOrder.getMid());
			lPushLog.setActiveid(this.active_b_30yuan);

			// push message
			this.iMessageService.pushMsg(phone, title, msgContent, MessageType.USER.getId(), url, this.active_b_30yuan);
		}
	}

	private ExecutorService getExecutorService() {
		return this.executorService;
	}

	private class SendTask implements Runnable {

		private List<BOtaorder> otaOrderList = null;

		public SendTask(List<BOtaorder> otaOrderList) {
			this.otaOrderList = otaOrderList;
		}

		@Override
		public void run() {
			for (final BOtaorder otaOrder : this.getOtaOrderList()) {
				try {
					if (otaOrder.getMid() == null) {
						continue;
					}
					// check用户有且仅有一单，
					Long count = SendCouponsJob.this.orderService.findMemberOnlyOneOrderCount(otaOrder);
					if ((count == null) || (count > 1)) {
						SendCouponsJob.logger.info("用户不是唯一一笔订单，不发放新手优惠券,mid=" + otaOrder.getMid());
						continue;
					}
					// check用户是否接受过该类型的优惠券
					long count2 = SendCouponsJob.this.iTicketService.countByMidAndActiveId(otaOrder.getMid(), SendCouponsJob.this.active_b_30yuan);
					if (count2 == 0) {// 未发送过，则生成优惠券实例，并发送信息
						// /处理业务
						SendCouponsJob.this.sendTicket(otaOrder);
					} else {
						SendCouponsJob.logger.info("用户mid：" + otaOrder.getMid() + "用户已经存在30元优惠券，不再发送提醒");
					}

				} catch (Exception e) {
					SendCouponsJob.logger.error("发送消息出错", e);
				}
			}
		}

		private List<BOtaorder> getOtaOrderList() {
			return this.otaOrderList;
		}

	}
}
