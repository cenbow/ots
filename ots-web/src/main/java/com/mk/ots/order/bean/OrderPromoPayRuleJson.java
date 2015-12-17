package com.mk.ots.order.bean;

import com.mk.ots.common.bean.OutModel;

/**
 * Created by Thinkpad on 2015/12/17.
 */
public class OrderPromoPayRuleJson extends OutModel{
    private Long id;

    private Integer promoType;

    private Integer isTicketPay;

    private Integer isWalletPay;

    private Integer isOnlinePay;

    private Integer isRealPay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPromoType() {
        return promoType;
    }

    public void setPromoType(Integer promoType) {
        this.promoType = promoType;
    }

    public Integer getIsTicketPay() {
        return isTicketPay;
    }

    public void setIsTicketPay(Integer isTicketPay) {
        this.isTicketPay = isTicketPay;
    }

    public Integer getIsWalletPay() {
        return isWalletPay;
    }

    public void setIsWalletPay(Integer isWalletPay) {
        this.isWalletPay = isWalletPay;
    }

    public Integer getIsOnlinePay() {
        return isOnlinePay;
    }

    public void setIsOnlinePay(Integer isOnlinePay) {
        this.isOnlinePay = isOnlinePay;
    }

    public Integer getIsRealPay() {
        return isRealPay;
    }

    public void setIsRealPay(Integer isRealPay) {
        this.isRealPay = isRealPay;
    }
}
