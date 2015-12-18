package com.mk.ots.mapper;

import com.mk.ots.remind.model.RemindType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RemindTypeMapper {

    public RemindType queryByCode(@Param(value="code") String code);

    public List<RemindType> queryByValid();
}
