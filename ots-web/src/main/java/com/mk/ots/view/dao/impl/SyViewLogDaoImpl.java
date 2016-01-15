package com.mk.ots.view.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.mapper.SyViewLogMapper;
import com.mk.ots.ticket.dao.BPrizeDao;
import com.mk.ots.ticket.model.BPrize;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.model.SyViewLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jeashi on 2015/12/9.
 */
@Component
public class SyViewLogDaoImpl  extends MyBatisDaoImpl<SyViewLog, Long> implements ISyViewLogDao {

    @Autowired
     private SyViewLogMapper syViewLogMapper;

    @Override
    public void save(SyViewLog syViewLog) {
        insert(syViewLog);
    }

    public void insertBatch(List<SyViewLog> list){
        syViewLogMapper.insertBatch(list);
    }

}
