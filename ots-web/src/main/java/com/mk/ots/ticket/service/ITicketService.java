package com.mk.ots.ticket.service;

import com.mk.ots.member.model.UMember;
import com.mk.ots.order.bean.OrderLog;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.pay.model.CouponParam;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.ticket.model.*;
import com.mk.ots.ticket.service.parse.ITicketParse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITicketService {

    /**
     * @param mid
     * @param activeid
     * @return
     */
    long countByMidAndActiveId(Long mid, Long activeid);

    /**
     * 查询用户某一时间段参与某一活动领取的券数量
     *
     * @param mid       用户编码
     * @param activeid  活动编码
     * @param starttime 开始时间
     * @param endtime   结束时间
     * @return
     */
    long countByMidAndActiveIdAndTime(Long mid, Long activeid, Date starttime, Date endtime);

    /**
     * 是否下载app且登录过
     *
     * @param mid
     * @return
     */
    boolean isHaveAppAndLogin(Long mid);

    /**
     * 是否为五月二十日前注册的用户
     *
     * @param mid
     * @return
     */
    boolean isBeforeMay20Member(Long mid);

    /**
     * 查询订单绑定的券记录
     *
     * @param otaOrder
     * @return
     */
    List<TicketInfo> getOrderAlreadyBindTickets(OtaOrder otaOrder);

    /**
     * 查询我的优惠券
     *
     * @param mid
     * @param status
     * @return
     */
    List<TicketInfo> queryMyTicket(long mid, Boolean status);

    /**
     * 查询某个活动的我的优惠券
     *
     * @param mid
     * @param activeid
     * @return
     */
    List<TicketInfo> queryMyTicket(long mid, long activeid);

    /**
     * 查询用户某一订单的切客券列表
     *
     * @param mid
     * @param otaorderid
     * @return
     */
    List<TicketInfo> getHistoryOrderQieKeTicketInfosByMid(Long mid, Long otaorderid);

    /**
     * 查询绑定券及可用券
     *
     * @param otaOrder 订单实例
     * @param mid      用户编码
     * @return
     */
    List<TicketInfo> getBindOrderAndAvailableTicketInfos(OtaOrder otaOrder, Long mid);

    /**
     * 兑换优惠券
     *
     * @param member       会员
     * @param code         兑换码
     * @param hardwarecode 硬件编码
     * @return
     */
    List<TicketInfo> exchange(UMember member, String code, String hardwarecode);

    /**
     * @param orderid
     */
    void updateUTicketAvailable(Long orderid);

    /**
     * 根据兑换码查询对应券信息
     *
     * @param code
     * @return
     */
    List<TicketInfo> getTicketInfosByCode(String code);

    /**
     * 对优惠券排序
     *
     * @param ticketInfos
     */
    void sortTicketInfos(List<TicketInfo> ticketInfos);

    /**
     * 默认选中第一个
     *
     * @param ticketInfos
     */
    void defaultTicketSelect(List<TicketInfo> ticketInfos);

    /**
     * 查询某个用户指定券的详细信息
     *
     * @param mid
     * @param promotionids
     * @return
     */
    List<TicketInfo> queryMyTicketByPromotionids(long mid, List<Long> promotionids);

    List<UTicket> queryTicketByMidAndActvie(long mid, long activeid);

    void sortTicketByActivety(List<TicketInfo> ticketInfos, OtaOrder order, long mid);

    /**
     * 查询我的所有可用优惠券
     *
     * @param mid
     * @return
     */
    List<TicketInfo> queryMyAvailableTicket(long mid);

    /**
     * @param paramMap
     * @return
     */
    Map<String, Object> findMaxAndMinUTicketId(Map<String, Object> paramMap);

    /**
     * @param paramMap
     * @return
     */
    List<UTicket> findUTicketList(Map<String, Object> paramMap);

    /**
     * 查询用户手动领取优惠券次数, 需统计的领取方式：手动领取，手动领取需激活
     *
     * @param mid 用户id
     * @return
     */
    long getHandGetPromotionCount(Long mid);

    /**
     * 查询用户未激活的优惠券列表
     *
     * @param mid 用户id
     * @return
     */
    List<TicketInfo> getNotActivePromotionCount(Long mid);

    /**
     * 优惠券激活
     *
     * @param mid         用户id
     * @param promotionid 优惠券id
     * @return
     */
    boolean activatePromotion(Long mid, Long promotionid);

    /**
     * 校验B规则
     *
     * @param myticket
     * @param otaOrder
     */
    void checkBRule(List<TicketInfo> myticket, OtaOrder otaOrder);


    /**
     * 绑定优惠券
     *
     * @param order
     * @param member
     * @param log
     */
    void saveBindPromotion(OtaOrder order, UMember member, OrderLog log);

    /**
     * 保存订单的优惠券状态
     *
     * @param orderId
     * @param ticketName
     */
    void saveOrderTicketStatus(Long orderId, String ticketName);

    /**
     * 插入和更新数据
     *
     * @param otaOrder 订单对象数据
     */
    void saveOrUpdateHotelStat(OtaOrder otaOrder, PmsRoomOrder pmsRoomOrder);

    /**
     * 获取符合住三送一条件的用户mid
     *
     * @param statisticInvalid 1:有效统计数据，2：无效统计数据
     * @param liveHotelNum     入住不同酒店数量
     * @param batDataNum       从数据库抓取出来的数量
     * @return 返回符合条件的用户mid
     */
    List<Long> getCountValid(int statisticInvalid, int liveHotelNum, int batDataNum);

    /**
     * 更新用户入住酒店流水表，把统计有效字段置为无效
     *
     * @param mid 用户编号
     */
    void updateInvalidByMid(Long mid);

    /**
     * 判断用户是否有抽奖机会
     *
     * @param mid
     * @param activeid
     * @param ostype
     */
    boolean checkUserLuckyDraw(long mid, long activeid, String ostype);

    /**
     * 获取领取奖品记录
     *
     * @param mid
     * @param activeid
     * @return
     */
    List<BPrizeInfo> queryMyHistoryPrize(long mid, long activeid);

    /**
     * @param statisticInvalid
     * @param batDataNum
     * @return
     */
    List<USendUticket> getNeedSendCountValid(int statisticInvalid, int batDataNum);
    
    /**
     * 根据订单查询对应的优惠券信息
     * @param order
     * @return
     */
    public CouponParam queryCouponParam(OtaOrder order);


    public List<ITicketParse> getPromotionParses(OtaOrder otaOrder);

    public ITicketParse createParseBean(OtaOrder otaOrder,BPromotion bPromotion);

    /**
     * @param mid
     */
    void updateSendTicketInvalidByMid(Long mid);

    /**
     * 根据手机，活动，设备号，检查该手机号是否领取过奖品
     * @param phone
     * @param activeid
     * @param ostype
     * @return
     */
    public boolean checkReceivePrizeByPhone(String phone,Long activeid,String ostype,String date);
    /**
     * 根据手机和奖品记录id将奖品绑定给用户
     * @param phone
     * @param prizerecordid
     */
    public void prizeBindingUser(String phone ,String prizerecordid);
    public List<BPrizeInfo> queryMyNotreceiveyPrize(Long activeid,String usermark);

    /**
     * 用户未登录前提下获取他的眯客优惠券
     * @param prizeInfo
     * @return
     */
    public List<TicketInfo> queryMyTicketOnUserMark(BPrizeInfo prizeInfo,long activeid);
    /**
     * 根据手机号绑定某活动未领取的优惠券
     * @param member
     * @param activeid
     */
    public void loginbindinggit(UMember member,Long activeid);
    /**
     * 获取可以绑定的奖品
     * @param usermark
     * @param phone
     * @param activeid
     * @param date
     * @param unget
     * @param geted
     * @return
     */
    public List<UPrizeRecord>findEffectivePrizeByPhone(String phone,Long activeid,String  ostype,String date,Integer geted);
}
