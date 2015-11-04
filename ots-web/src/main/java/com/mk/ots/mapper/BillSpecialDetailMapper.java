package com.mk.ots.mapper;


import com.mk.ots.bill.model.BillSpecialDetail;
import com.mk.ots.bill.model.BillSpecialDetailExample;

import java.util.List;
import java.util.Map;

public interface BillSpecialDetailMapper {
    int countByExample(BillSpecialDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BillSpecialDetail record);

    public void insertBatch(Map<String, Object> params);

    int insertSelective(BillSpecialDetail record);

    List<BillSpecialDetail> selectByExample(BillSpecialDetailExample example);

    BillSpecialDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BillSpecialDetail record);

    int updateByPrimaryKey(BillSpecialDetail record);
}