package com.mk.ots.mapper;

import com.mk.ots.message.model.BMessageCopywriter;

public interface BMessageCopywriterMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BMessageCopywriter record);

    int insertSelective(BMessageCopywriter record);


    public  BMessageCopywriter selectByPrimaryKey(Long id);


    int updateByPrimaryKeySelective(BMessageCopywriter record);

    int updateByPrimaryKey(BMessageCopywriter record);

    public BMessageCopywriter selectByType(BMessageCopywriter record);
}