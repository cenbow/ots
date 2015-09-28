package com.mk.ots.ticket.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.ticket.model.USendUticket;

public interface USendUTicketDao extends BaseDao<USendUticket, Long>{
    public List<USendUticket> getNeedSendCountValid(int statisticInvalid, int batDataNum);

    public void updateSendTicketInvalidByMid(Long mid) ;
}
