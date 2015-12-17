package com.mk.ots.order.model;

import java.util.Date;

public class OrderPromoPayRule {
    private Long id;

    private Integer promoType;

    private Integer isTicketPay;

    private Integer isWalletPay;

    private Integer isOnlinePay;

    private Integer isRealPay;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }
}