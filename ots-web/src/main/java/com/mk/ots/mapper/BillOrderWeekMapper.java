package com.mk.ots.mapper;

import com.mk.ots.bill.model.BillOrderWeek;
import com.mk.ots.bill.model.BillOrderWeekExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    public List<BillOrderWeek> sumBillOrderWeekList(@Param("beginTime")Date beginTime, @Param("endTime")Date endTime);

    void insertBatch(List<BillOrderWeek> billOrderWeekList);
}