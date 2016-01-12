package com.mk.ots.dynamicprice.service;

import java.math.BigDecimal;

/**
 * Created by kirinli on 16/1/12.
 */
public interface CodeTableService {
    public BigDecimal getCriterionPriceCode(Integer oClock);
    public BigDecimal getCriterionPriceCheckInCode(Integer currentOClock, Integer oClock);
}
