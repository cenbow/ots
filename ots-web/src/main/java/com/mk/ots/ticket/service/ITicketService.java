package com.mk.ots.ticket.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mk.ots.member.model.UMember;
import com.mk.ots.order.bean.OrderLog;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.ticket.model.BPrizeInfo;
import com.mk.ots.ticket.model.TicketInfo;
import com.mk.ots.ticket.model.UTicket;

public interface ITicketService {
	
	/**
	 * @param mid
	 * @param activeid
	 * @return
	 */
	public long countByMidAndActiveId(Long mid, Long activeid);
	
	/**
	 * 查询用户某一时间段参与某一活动领取的券数量
	 * @param mid 用户编码
	 * @param activeid 活动编码
	 * @param starttime 开始时间
	 * @param endtime 结束时间
	 * @return
	 */
	public long countByMidAndActiveIdAndTime(Long mid, Long activeid, Date starttime, Date endtime);

	/**
	 * 是否下载app且登录过
	 * @param mid
	 * @return
	 */
	public abstract boolean isHaveAppAndLogin(Long mid);

	/**
	 * 是否为五月二十日前注册的用户
	 * @param mid
	 * @return
	 */
	public abstract boolean isBeforeMay20Member(Long mid);

	/**
	 * 查询订单绑定的券记录
	 * @param otaOrder
	 * @return
	 */
	public abstract List<TicketInfo> getOrderAlreadyBindTickets(OtaOrder otaOrder);

	/**
	 * 查询我的优惠券
	 * @param mid
	 * @param status
	 * @return
	 */
	public abstract List<TicketInfo> queryMyTicket(long mid, Boolean status);
	
	/**
	 * 查询某个活动的我的优惠券
	 * @param mid
	 * @param activeid
	 * @return
	 */
	public abstract List<TicketInfo> queryMyTicket(long mid, long activeid);

	/**
	 * 查询用户某一订单的切客券列表
	 * @param mid
	 * @param otaorderid
	 * @return
	 */
	public abstract List<TicketInfo> getHistoryOrderQieKeTicketInfosByMid(Long mid, Long otaorderid);

	/**
	 * 查询绑定券及可用券
	 * @param orderid
	 * @param mid
	 * @return
	 */
	public abstract List<TicketInfo> getBindOrderAndAvailableTicketInfos(OtaOrder otaOrder, Long mid);

	/**
	 * 兑换优惠券
	 * @param member 会员
	 * @param code 兑换码
	 * @return
	 */
	public abstract List<TicketInfo> exchange(UMember member, String code);

	/**
	 * @param orderid
	 */
	public abstract void updateUTicketAvailable(Long orderid);

	/**
	 * 根据兑换码查询对应券信息
	 * @param code
	 * @return
	 */
	public abstract List<TicketInfo> getTicketInfosByCode(String code);
	
	/**
	 * 对优惠券排序
	 * @param ticketInfos
	 */
	public abstract void sortTicketInfos(List<TicketInfo> ticketInfos);
	/**
	 * 默认选中第一个
	 * @param ticketInfos
	 */
	public void defaultTicketSelect(List<TicketInfo> ticketInfos) ;

	/**
	 * 查询某个用户指定券的详细信息
	 * @param mid
	 * @param promotionids
	 * @return
	 */
	public abstract List<TicketInfo> queryMyTicketByPromotionids(long mid, List<Long> promotionids);

	public abstract List<UTicket> queryTicketByMidAndActvie(long mid, long activeid);
	
	public abstract void sortTicketByActivety(List<TicketInfo> ticketInfos,OtaOrder order,long mid);

	/**
	 * 查询我的所有可用优惠券
	 * @param mid
	 * @return
	 */
	public abstract List<TicketInfo> queryMyAvailableTicket(long mid);
	
	/**
	 * @param paramMap
	 * @return
	 */
	public abstract Map<String, Object> findMaxAndMinUTicketId(Map<String, Object> paramMap);
	    
    /**
     * @param paramMap
     * @return
     */
    public abstract List<UTicket> findUTicketList(Map<String, Object> paramMap);
    
    /**
     * 查询用户手动领取优惠券次数, 需统计的领取方式：手动领取，手动领取需激活
     * @param mid 用户id
     * @return
     */
    public long getHandGetPromotionCount(Long mid);
    
    /**
     * 查询用户未激活的优惠券列表
     * @param mid 用户id
     * @return
     */
    public List<TicketInfo> getNotActivePromotionCount(Long mid);
    
    /**
     * 优惠券激活
     * @param mid 用户id
     * @param promotionid 优惠券id
     * @return
     */
    public boolean activatePromotion(Long mid, Long promotionid);
    /**
     * 校验B规则
     * @param myticket
     * @param otaOrder
     */
    public void checkBRule(List<TicketInfo> myticket,OtaOrder otaOrder);
    
    
    /**
     * 绑定优惠券
     * @param order
     * @param member
     * @param log
     */
    public void saveBindPromotion(OtaOrder order, UMember member, OrderLog log);
    /**
     * 保存订单的优惠券状态
     * @param orderId
     * @param ticketName
     */
    public void saveOrderTicketStatus(Long orderId, String ticketName);

    /**
     * 插入和更新数据
     * @param otaOrder 订单对象数据
     */
    public void saveOrUpdateHotelStat(OtaOrder otaOrder,PmsRoomOrder pmsRoomOrder);

    /**
     * 获取符合住三送一条件的用户mid
     * @param statisticInvalid 1:有效统计数据，2：无效统计数据
     * @param liveHotelNum 入住不同酒店数量
     * @param batDataNum 从数据库抓取出来的数量
     * @return 返回符合条件的用户mid
     */
    public List<Long> getCountValid(int statisticInvalid, int liveHotelNum, int batDataNum);

    /**
     * 更新用户入住酒店流水表，把统计有效字段置为无效
     * @param mid 用户编号
     */
    public void updateInvalidByMid(Long mid);
    
    /**
     * 判断用户是否有抽奖机会
     * @param mid
     * @param activeid
     * @param ostype
     */
    public boolean checkUserLuckyDraw(long mid, long activeid,String ostype);
    
    /**
     * 获取领取奖品记录
     * @param mid
     * @param activeid
     * @return
     */
    public List<BPrizeInfo> queryMyHistoryPrize(long mid ,long activeid);
}
