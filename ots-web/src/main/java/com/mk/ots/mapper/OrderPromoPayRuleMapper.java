package com.mk.ots.mapper;


import com.mk.ots.order.model.OrderPromoPayRule;
import com.mk.ots.order.model.OrderPromoPayRuleExample;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderPromoPayRuleMapper {
    int countByExample(OrderPromoPayRuleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrderPromoPayRule record);

    int insertSelective(OrderPromoPayRule record);

    List<OrderPromoPayRule> selectByExample(OrderPromoPayRuleExample example);

    OrderPromoPayRule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderPromoPayRule record);

    int updateByPrimaryKey(OrderPromoPayRule record);
}