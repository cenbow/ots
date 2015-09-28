package com.mk.ots.ticket.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.ticket.model.BHotelStat;

/**
 * Created by jjh on 15/7/29.
 */
public interface BHotelStatDao extends BaseDao<BHotelStat, Long> {

    /**
     * 保存或更新数据
     * @param bHotelStat
     */
    public abstract void saveOrUpdate(BHotelStat bHotelStat);

    /**
     * 根据订单查询用户住酒店信息
     * @param otaOrderId
     * @return
     */
    public BHotelStat getBHotelStatByOtaorderid(Long otaOrderId);

    /**
     * 获取符合住三送一条件的用户mid
     * @param statisticInvalid 1:有效统计数据，2：无效统计数据
     * @param liveHotelNum 入住不同酒店数量
     * @param batDataNum 从数据库抓取数量
     * @return 返回符合条件的用户
     */
    public List<Long> getCountValid(int statisticInvalid, int liveHotelNum, int batDataNum);

    /**
     * 更新用户入住酒店流水表，把统计有效字段置为无效
     * @param mid
     */
    public void updateInvalidByMid(Long mid);
    
    /**
     * @param mid
     * @param code
     * 查询酒店历史住店记录
     */
    public List<THotelModel> queryHistoryHotels(String token, String code,int start,int limit);
    /**
     * @param mid
     * @param code
     * 查询酒店历史住店记录条数
     */
    public long queryHistoryHotelsCount(String token, String code);
    
    /**
     * @param mid
     * @param hotelid
     * 删除某会员某酒店住店历史纪录
     */
    public void deleteHotelStats(String token,Long hotelid);
}
