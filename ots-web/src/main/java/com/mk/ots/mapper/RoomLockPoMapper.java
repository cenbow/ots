package com.mk.ots.mapper;

import com.mk.pms.room.bean.RoomLockPo;
import com.mk.pms.room.bean.RoomLockPoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoomLockPoMapper {
    int countByExample(RoomLockPoExample example);

    int deleteByExample(RoomLockPoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(RoomLockPo record);

    int insertSelective(RoomLockPo record);

    List<RoomLockPo> selectByExample(RoomLockPoExample example);

    RoomLockPo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RoomLockPo record, @Param("example") RoomLockPoExample example);

    int updateByExample(@Param("record") RoomLockPo record, @Param("example") RoomLockPoExample example);

    int updateByPrimaryKeySelective(RoomLockPo record);

    int updateByPrimaryKey(RoomLockPo record);
    
    List<RoomLockPo> findByHotelId(Long hotelid);
}