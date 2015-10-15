package com.mk.ots.ticket.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.ticket.dao.IUPromoUserLogDao;
import com.mk.ots.ticket.model.UPromoUserLog;

/**
 * Created by jeashi on 2015/10/14.
 */
@Component
public class UPromoUserLogDaoImpl extends MyBatisDaoImpl<UPromoUserLog, Long> implements IUPromoUserLogDao {

    public  void  add(UPromoUserLog upromoUserLog){
            this.insert(upromoUserLog);
    }
}
