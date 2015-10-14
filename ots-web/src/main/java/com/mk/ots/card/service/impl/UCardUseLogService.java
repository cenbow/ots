package com.mk.ots.card.service.impl;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.model.UCardUseLog;
import com.mk.ots.card.service.IBCardService;
import com.mk.ots.card.service.IUCardUseLogService;
import com.mk.ots.mapper.CardMapper;
import com.mk.ots.mapper.CardUseLogMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UCardUseLogService implements IUCardUseLogService {

    @Autowired
    private CardUseLogMapper cardUseLogMapper;

    @Override
    public void insert(UCardUseLog log) {
        if (null == log) {
            return;
        }
        this.cardUseLogMapper.insert(log);
    }
}
