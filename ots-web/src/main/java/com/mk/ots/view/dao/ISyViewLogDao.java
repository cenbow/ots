package com.mk.ots.view.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.BPrize;
import com.mk.ots.view.model.SyViewLog;

import java.util.List;

/**
 * Created by jeashi on 2015/12/9.
 */
public interface ISyViewLogDao  extends BaseDao<SyViewLog, Long> {

    public void save(SyViewLog syViewLog);

    public void insertBatch(List<SyViewLog>  syViewLogList);

}
