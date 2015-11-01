package com.mk.ots.bill.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Thinkpad on 2015/10/31.
 */
public class BillSpecialDay {
    private Long id;
    private Long promoType;
    private Long hotelId;
    private Date beinTime;
    private Date endTime;
    private Long orderId;
    private BigDecimal onlinePaied;
    private BigDecimal aliPaied;
    private BigDecimal wechatPaied;
    private BigDecimal billCost;
    private BigDecimal changeCost;
    private BigDecimal finalCost;
    private BigDecimal income;
    private Date createTime;
    private Long financeStatus;
    private BigDecimal availableMoney;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPromoType() {
        return promoType;
    }

    public void setPromoType(Long promoType) {
        this.promoType = promoType;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Date getBeinTime() {
        return beinTime;
    }

    public void setBeinTime(Date beinTime) {
        this.beinTime = beinTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getOnlinePaied() {
        return onlinePaied;
    }

    public void setOnlinePaied(BigDecimal onlinePaied) {
        this.onlinePaied = onlinePaied;
    }

    public BigDecimal getAliPaied() {
        return aliPaied;
    }

    public void setAliPaied(BigDecimal aliPaied) {
        this.aliPaied = aliPaied;
    }

    public BigDecimal getWechatPaied() {
        return wechatPaied;
    }

    public void setWechatPaied(BigDecimal wechatPaied) {
        this.wechatPaied = wechatPaied;
    }

    public BigDecimal getBillCost() {
        return billCost;
    }

    public void setBillCost(BigDecimal billCost) {
        this.billCost = billCost;
    }

    public BigDecimal getChangeCost() {
        return changeCost;
    }

    public void setChangeCost(BigDecimal changeCost) {
        this.changeCost = changeCost;
    }

    public BigDecimal getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(BigDecimal finalCost) {
        this.finalCost = finalCost;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getFinanceStatus() {
        return financeStatus;
    }

    public void setFinanceStatus(Long financeStatus) {
        this.financeStatus = financeStatus;
    }

    public BigDecimal getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(BigDecimal availableMoney) {
        this.availableMoney = availableMoney;
    }
}
