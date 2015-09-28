package com.mk.ots.ticket.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.ticket.model.UTicket;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UTicketDao extends BaseDao<UTicket, Long> {

    /**
     * @param mid
     * @param status
     * @return
     */
    List<UTicket> findUTicket(@Param("mid") long mid, @Param("status") int status);

    /**
     * @param mid
     * @param status
     * @return
     */
    List<UTicket> findUTicketByMid(@Param("mid") Long mid, Boolean status);

    /**
     * @param mid
     * @param status
     * @return
     */
    List<UTicket> findUTicketByMidAndActiveid(Long mid, Long activeid);

    /**
     * @param promotionids
     * @param mid
     * @return
     */
    List<UTicket> findUTicketByPromotionAndMid(@Param("promotionids") List<Long> promotionids, @Param("mid") long mid);

    /**
     * @param hotelid
     * @param mid
     * @return
     */
    boolean isCheckMonthHotel(long hotelid, long mid);

    /**
     * @param mid
     * @return
     */
    boolean isCheckTodayUser(long mid);


    /**
     * 一个房间一天切客一次
     *
     * @param hotelid
     * @param roomid
     * @return
     */
    boolean isCheckTodayHotelRoom(long hotelid, long roomid);

    /**
     * @param mid
     * @param promotiontype
     * @return
     */
    List<UTicket> findUTicketByPromotionType(long mid, PromotionTypeEnum promotiontype);

    /**
     * @param uTicket
     */
    void saveOrUpdate(UTicket uTicket);

    /**
     * @param promotionid
     * @return
     */
    UTicket findByPromotionId(long promotionid);

    /**
     * @param orderid
     * @param promotionid
     * @return
     */
    long findByPromotionIdAndOrderId(Long orderid, Long promotionid);

    /**
     * 查询十五天以前完成订单行为的用户，订单数以及当月的常规券领取数据
     *
     * @return
     */
    List<Map> queryCurMonthUTicketNumAndOrderNum();

    /**
     * 查询某个用户参加某一个活动的优惠券领取数量
     *
     * @param mid
     * @param activeid
     * @return
     */
    long countByMidAndActiveId(Long mid, Long activeid);

    /**
     * 一个用户每个月每个酒店只能切客四次(含四次)
     *
     * @param mid
     * @param hotelid
     * @return
     */
    boolean isCheckNumMonth(Long mid, Long hotelid);

    /**
     * 一个用户一天只允许切客一次
     *
     * @param mid
     * @return
     */
    boolean isCheckNumToday(Long mid);

    /**
     * 一个月内打开APP次数超过2次（含2次）且当月未领取过常规优惠券的用户列表
     *
     * @return
     */
    List<Long> queryActiveMemberRuleList();

    /**
     * 15天内打开APP次数不超过1次且未卸载APP的用户，且当月未领取过常规优惠券的用户列表
     *
     * @return
     */
    List<Long> queryUnActiveMemberRuleList();

    /**
     * @param promotionid
     * @param mid
     * @return
     */
    UTicket findUTicketByPromoIdAndMid(Long promotionid, Long mid);

    /**
     * @param otaorderid
     * @param mid
     * @return
     */
    List<UTicket> findUTicketsByOrderIdAndMid(Long otaorderid, Long mid);

    /**
     * 查询某个用户在某一时间段参与某一活动领取的券数量
     *
     * @param mid
     * @param activeid
     * @param starttime
     * @param endtime
     * @return
     */
    long countByMidAndActiveIdAndTime(Long mid, Long activeid, Date starttime, Date endtime);

    /**
     * 修改某一订单所使用的用户优惠券为可用
     *
     * @param orderid
     */
    void updateUTicketAvailable(Long orderid);

    /**
     * 根据mid和优惠券id修改状态和订单信息
     *
     * @param orderid
     */
    void updateByMidAndPromotionId(UTicket uTicket);

    /**
     * 20150702 add by zhangyajun
     * 根据mid和活动ID(activityid)和优惠券状态status获取对象
     *
     * @param mid
     * @param activityid
     * @param status
     */
    List<UTicket> findUTicketByMidAndActiveidReturnUTicket(long mid, long activityid);

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
    Long getHandGetPromotionCount(Long mid);

    /**
     * 查询用户未激活的优惠券列表
     *
     * @param mid 用户id
     * @return
     */
    List<UTicket> getNotActivePromotions(Long mid);

    /**
     * 优惠券激活
     *
     * @param mid         用户id
     * @param promotionid 优惠券id
     * @return
     */
    boolean activatePromotion(Long mid, Long promotionid);

    /**
     *
     * @param mid
     * @param orderId
     * @return
     */
    boolean checkOrderUsedTicket(Long mid, Long orderId);

    /**
     * 根据用户以及活动，获取未激活的优惠券
     *
     * @param mid             用户编号
     * @param liveThreeActive 活动编号
     * @param id              优惠券状态
     * @return 用户优惠券信息
     */
    UTicket findUnactiveUTicket(Long mid, long liveThreeActive, Integer id);


}
