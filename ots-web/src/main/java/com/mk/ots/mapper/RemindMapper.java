package com.mk.ots.mapper;

import com.mk.ots.remind.model.Remind;

import java.util.List;

public interface RemindMapper {

    public void save(Remind remind);

    public List<Remind> queryByMid(Remind remind);
}
