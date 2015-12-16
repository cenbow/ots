package com.mk.ots.wallet.service;

import com.mk.ots.order.bean.OtaOrder;

import java.math.BigDecimal;

/**
 * Created by jeashi on 2015/12/10.
 */
public interface ITBackMoneyRuleService {
    public BigDecimal getBackMoneyByOrder(OtaOrder order);
}
