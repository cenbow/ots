package com.mk.ots.mapper;

import com.mk.ots.bill.model.BillOrderWeek;
import com.mk.ots.bill.model.BillOrderWeekExample;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BillOrderWeekMapper {
    int countByExample(BillOrderWeekExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BillOrderWeek record);

    int insertSelective(BillOrderWeek record);

    List<BillOrderWeek> selectByExample(BillOrderWeekExample example);

    BillOrderWeek selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BillOrderWeek record);

    int updateByPrimaryKey(BillOrderWeek record);
}