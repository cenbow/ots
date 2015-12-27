package com.mk.ots.mapper;

import com.mk.ots.bill.domain.BillOrder;
import com.mk.ots.bill.domain.BillOrderPayInfo;
import com.mk.ots.bill.model.BillOrderDetail;
import com.mk.ots.bill.model.BillOrderDetailExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BillOrderDetailMapper {
    int countByExample(BillOrderDetailExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BillOrderDetail record);

    int insertSelective(BillOrderDetail record);

    List<BillOrderDetail> selectByExample(BillOrderDetailExample example);

    BillOrderDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BillOrderDetail record);

    int updateByPrimaryKey(BillOrderDetail record);

    void insertBatch(List<BillOrderDetail> billOrderDetailList);

    public List<BillOrder> getBillOrderList(@Param("beginTime")Date beginTime, @Param("endTime")Date endTime,
                                                  @Param("pageSize")int pageSize, @Param("pageIndex")int pageIndex);

    public BillOrderPayInfo getOrderPayInfo(@Param("orderId")Long orderId);
}