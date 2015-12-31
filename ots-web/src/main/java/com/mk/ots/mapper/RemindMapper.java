package com.mk.ots.mapper;

import com.mk.ots.remind.model.Remind;

import java.util.List;

public interface RemindMapper {

    public Integer save(Remind remind);

    public List<Remind> queryByMid(Remind remind);

    public  List<Remind> findPushRemind(Long typeId);

    public Integer update(Remind remind);
}
