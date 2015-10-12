package com.mk.ots.pricedrop.service;

/**
 * 空房库存服务类
 * <p/>
 * Created by nolan on 15/10/12.
 */
public interface IRoomStockService {
    /**
     * 减少库存
     *
     * @param hotelid  酒店id
     * @param roomtype 房型id
     * @return T/F
     */
    boolean lock(Long hotelid, Long roomtype);

    /**
     * 增加库存
     *
     * @param hotelid  酒店id
     * @param roomtype 房型id
     * @return T/F
     */
    boolean unLock(Long hotelid, Long roomtype);

    /**
     * 查询酒店某房型的空房库存数量
     *
     * @param hotelid  酒店id
     * @param roomtype 房型id
     * @return int
     */
    int queryTotalRoomStock(Long hotelid, Long roomtype);

    /**
     * 查询酒店某房型的眯客可用库存
     *
     * @param hotelid  酒店id
     * @param roomtype 房型id
     * @return int
     */
    int queryAvailibleRoomStock(Long hotelid, Long roomtype);
}
