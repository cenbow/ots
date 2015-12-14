package com.mk.ots.mapper;

import com.mk.ots.remind.model.RemindType;

public interface RemindTypeMapper {

    public RemindType queryByCode(String code);
}
