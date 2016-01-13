package com.mk.ots.mapper;

import com.mk.ots.view.model.SyViewLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by jeashi on 2016/1/12.
 */


public interface SyViewLogMapper {

    void insertBatch(List<SyViewLog> syViewLogList);

}
