package com.mk.ots.card.service;

import com.mk.ots.card.model.UCardUseLog;

public interface IUCardUseLogService {
    public void insert(UCardUseLog log);

    public UCardUseLog findByCardId(Long cardId);
}
