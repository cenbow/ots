package com.mk.ots.mapper;

import com.mk.pms.room.bean.RoomRepairPo;
import com.mk.pms.room.bean.RoomRepairPoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoomRepairPoMapper {
    int countByExample(RoomRepairPoExample example);

    int deleteByExample(RoomRepairPoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(RoomRepairPo record);

    int insertSelective(RoomRepairPo record);

    List<RoomRepairPo> selectByExample(RoomRepairPoExample example);

    RoomRepairPo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RoomRepairPo record, @Param("example") RoomRepairPoExample example);

    int updateByExample(@Param("record") RoomRepairPo record, @Param("example") RoomRepairPoExample example);

    int updateByPrimaryKeySelective(RoomRepairPo record);

    int updateByPrimaryKey(RoomRepairPo record);
}