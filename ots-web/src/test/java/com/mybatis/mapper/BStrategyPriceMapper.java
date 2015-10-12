package com.mybatis.mapper;

import com.mybatis.model.BStrategyPrice;
import com.mybatis.model.BStrategyPriceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BStrategyPriceMapper {
    int countByExample(BStrategyPriceExample example);

    int deleteByExample(BStrategyPriceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BStrategyPrice record);

    int insertSelective(BStrategyPrice record);

    List<BStrategyPrice> selectByExample(BStrategyPriceExample example);

    BStrategyPrice selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BStrategyPrice record, @Param("example") BStrategyPriceExample example);

    int updateByExample(@Param("record") BStrategyPrice record, @Param("example") BStrategyPriceExample example);

    int updateByPrimaryKeySelective(BStrategyPrice record);

    int updateByPrimaryKey(BStrategyPrice record);
}