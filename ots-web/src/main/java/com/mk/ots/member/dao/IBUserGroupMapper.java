package com.mk.ots.member.dao;

import com.mk.ots.member.model.BUserGroup;


public interface IBUserGroupMapper {
    int deleteByPrimaryKey(String id);

    int insert(BUserGroup record);

    int insertSelective(BUserGroup record);

    BUserGroup selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BUserGroup record);

    int updateByPrimaryKey(BUserGroup record);
}