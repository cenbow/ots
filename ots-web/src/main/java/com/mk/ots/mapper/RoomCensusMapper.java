package com.mk.ots.mapper;

import com.mk.ots.room.bean.RoomCensus;
import com.mk.ots.room.bean.RoomCensusExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoomCensusMapper {
    int countByExample(RoomCensusExample example);

    int deleteByExample(RoomCensusExample example);

    int deleteByPrimaryKey(Long id);

    int insert(RoomCensus record);

    int insertSelective(RoomCensus record);

    List<RoomCensus> selectByExample(RoomCensusExample example);

    RoomCensus selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RoomCensus record, @Param("example") RoomCensusExample example);

    int updateByExample(@Param("record") RoomCensus record, @Param("example") RoomCensusExample example);

    int updateByPrimaryKeySelective(RoomCensus record);

    int updateByPrimaryKey(RoomCensus record);
    
    void backUpRoomCensus();
    
    void deleteRoomCensus2DaysAgo();
}