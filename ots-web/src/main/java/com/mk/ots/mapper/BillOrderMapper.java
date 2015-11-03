package com.mk.ots.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by Thinkpad on 2015/10/29.
 */
@Repository
public interface BillOrderMapper {
    public List<Map> findBillOrder(Map params);

    public Map findFinanceOrder(Map params);

    public List<Long> findBillOrderHotelId(Map params);
}
