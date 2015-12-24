package com.mk.ots.view.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.BPrizeDao;
import com.mk.ots.ticket.model.BPrize;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.model.SyViewLog;
import org.springframework.stereotype.Component;

/**
 * Created by jeashi on 2015/12/9.
 */
@Component
public class SyViewLogDaoImpl  extends MyBatisDaoImpl<SyViewLog, Long> implements ISyViewLogDao {
    @Override
    public void save(SyViewLog syViewLog) {
        insert(syViewLog);
    }
}
