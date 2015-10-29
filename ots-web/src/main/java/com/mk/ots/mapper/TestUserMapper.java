package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.user.model.TestUser;
import com.mk.ots.user.model.TestUserExample;

public interface TestUserMapper {
    int countByExample(TestUserExample example);

    int deleteByExample(TestUserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TestUser record);

    int insertSelective(TestUser record);

    List<TestUser> selectByExample(TestUserExample example);

    TestUser selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TestUser record, @Param("example") TestUserExample example);

    int updateByExample(@Param("record") TestUser record, @Param("example") TestUserExample example);

    int updateByPrimaryKeySelective(TestUser record);

    int updateByPrimaryKey(TestUser record);
}