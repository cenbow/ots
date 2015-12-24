package com.mk.ots.bill.dao;

import com.mk.ots.bill.model.BillOrderDetail;
import com.mk.ots.bill.model.BillOrderDetailExample;

import java.util.List;

public interface BillOrderDetailMapper {
    int countByExample(BillOrderDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BillOrderDetail record);

    int insertSelective(BillOrderDetail record);

    List<BillOrderDetail> selectByExample(BillOrderDetailExample example);

    BillOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BillOrderDetail record);

    int updateByPrimaryKey(BillOrderDetail record);
}