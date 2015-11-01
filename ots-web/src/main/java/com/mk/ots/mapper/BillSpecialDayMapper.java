package com.mk.ots.mapper;

import com.mk.ots.bill.model.BillSpecialDay;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Thinkpad on 2015/10/29.
 */
@Repository
public interface BillSpecialDayMapper {
    public List<BillSpecialDay> findBillSpecialDayByParams(BillSpecialDay billSpecialDay);

    public BillSpecialDay findBillSpecialDayById(Long id);

    public int updateBillSpecialDay(BillSpecialDay billSpecialDay);

    public int insertBillSpecialDay(BillSpecialDay billSpecialDay);

    public int insertBillSpecialDayBatch(List<BillSpecialDay> billSpecialDayList);
}
