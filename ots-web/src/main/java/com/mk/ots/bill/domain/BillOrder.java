package com.mk.ots.bill.domain;

import com.mk.ots.bill.model.BillOrderDetail;

import java.math.BigDecimal;

/**
 * Created by Thinkpad on 2015/12/25.
 */
public class BillOrder extends BillOrderDetail{
    /**切客金额*/
    private BigDecimal qiekeIncome;
    private String promoType;

    public BigDecimal getQiekeIncome() {
        return qiekeIncome;
    }

    public void setQiekeIncome(BigDecimal qiekeIncome) {
        this.qiekeIncome = qiekeIncome;
    }

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }
}
