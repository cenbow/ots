package com.mk.ots.mapper;



import com.mk.ots.bill.model.BillSpecial;
import com.mk.ots.bill.model.BillSpecialExample;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BillSpecialMapper {
    int countByExample(BillSpecialExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BillSpecial record);

    int insertHotelId(BillSpecial record);

    int insertSelective(BillSpecial record);

    List<BillSpecial> selectByExample(BillSpecialExample example);

    BillSpecial selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BillSpecial record);

    int updateByPrimaryKey(BillSpecial record);

    int updateBillSpecial(Long hotelId ,Date beginTime, Date endTime, String billTime);
}