package com.mk.ots.pricedrop.service;

import java.util.List;

import com.mk.ots.pricedrop.model.BStrategyPrice;

public interface IBStrategyPriceService {
   /* int countByExample(BStrategyPriceExample example);

    int deleteByExample(BStrategyPriceExample example);

    int deleteByPrimaryKey(Long id);
*/
    public  void save(BStrategyPrice bStrategyPrice);

    /**
     * 根据酒店id查询房型
     * @param hotelid
     * @return
     */
    public List<BStrategyPrice> findBStrategyPricesByHotelId(Long hotelid);
    /**
     * 根据查询所有房型
     * @param hotelid
     * @return
     */
    public List<BStrategyPrice> findAllBStrategyPrices();
    /*int insertSelective(BStrategyPrice record);

    List<BStrategyPrice> selectByExample(BStrategyPriceExample example);

    BStrategyPrice selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BStrategyPrice record, @Param("example") BStrategyPriceExample example);

    int updateByExample(@Param("record") BStrategyPrice record, @Param("example") BStrategyPriceExample example);

    int updateByPrimaryKeySelective(BStrategyPrice record);

    int updateByPrimaryKey(BStrategyPrice record);*/
}