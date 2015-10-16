package com.mk.ots.card.service.impl;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.exception.MyException;
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

    @Override
    public UCardUseLog findByCardId(Long cardId) {
        if (null == cardId) {
            throw MyErrorEnum.customError.getMyException("充值卡号无效.");
        }
        return this.iUCardUseLogDAO.findByCardId(cardId);
    }
}
