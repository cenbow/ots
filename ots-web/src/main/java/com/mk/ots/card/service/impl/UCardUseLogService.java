package com.mk.ots.card.service.impl;

import com.mk.ots.card.dao.IUCardUseLogDAO;
import com.mk.ots.card.model.UCardUseLog;
import com.mk.ots.card.service.IUCardUseLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UCardUseLogService implements IUCardUseLogService {

    @Autowired
    private IUCardUseLogDAO iUCardUseLogDAO;
    @Override
    public void insert(UCardUseLog log) {
        if (null == log) {
            return;
        }
        this.iUCardUseLogDAO.insert(log);
    }
}
