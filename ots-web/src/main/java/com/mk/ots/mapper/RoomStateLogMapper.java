package com.mk.ots.mapper;

import com.mk.ots.room.bean.RoomStateLog;

public interface RoomStateLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoomStateLog record);

    int insertSelective(RoomStateLog record);

    RoomStateLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoomStateLog record);

    int updateByPrimaryKey(RoomStateLog record);
}