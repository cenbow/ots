package com.mk.ots.pricedrop.service;

import com.mk.ots.pricedrop.model.BStrategyPrice;

import java.util.List;

/**
 *
 */
public interface IBStrategyPriceService {

    public void insert(BStrategyPrice bStrategyPrice);

    /**
     * 根据酒店id查询房型
     *
     * @param hotelid 酒店id
     * @return List<BStrategyPrice>
     */
    public List<BStrategyPrice> findBStrategyPricesByHotelId(Long hotelid);

    /**
     * 根据酒店id集合查询房型
     *
     * @param hotelid 酒店id
     * @return List<BStrategyPrice>
     */
    public List<BStrategyPrice> findBStrategyPricesByHotelId(List<Long> hotelid);

    /**
     * 根据查询所有房型
     *
     * @return List<BStrategyPrice>
     */
    public List<BStrategyPrice> findAllBStrategyPrices();

    /**
     * 根据酒店及房型查询对应的单条配置信息
     *
     * @param hotelid    酒店id
     * @param roomtype 房型
     * @return BStrategyPrice
     */
    public BStrategyPrice findByHotelidAndRoomtype(Long hotelid, Long roomtype);
}