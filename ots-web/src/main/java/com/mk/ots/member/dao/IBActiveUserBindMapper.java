package com.mk.ots.member.dao;

import java.util.List;

import com.google.common.base.Optional;
import com.mk.ots.member.model.BActiveUserBind;
import com.mk.ots.member.model.UMember;


public interface IBActiveUserBindMapper {
    int deleteByPrimaryKey(String id);

    String insert(BActiveUserBind record);

    int insertSelective(BActiveUserBind record);

    BActiveUserBind selectByPrimaryKey(String id);
    
    Optional<List<BActiveUserBind>> selectBindUser(String groupid);

    int updateByPrimaryKeySelective(BActiveUserBind record);

    int updateByPrimaryKey(BActiveUserBind record);
}