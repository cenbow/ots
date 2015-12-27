package com.mk.ots.bill.domain;

import java.math.BigDecimal;

/**
 * Created by Thinkpad on 2015/12/25.
 */
public class BillOrderPayInfo {
    private Long orderid;
    private Long payid;
    private BigDecimal allcost;
    private BigDecimal hotelgive;
    private BigDecimal otagive;
    private BigDecimal usercost;
    private BigDecimal realotagive;
    private BigDecimal qiekeIncome;
    private BigDecimal lezhu;
    private Integer onlinePayType;

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Long getPayid() {
        return payid;
    }

    public void setPayid(Long payid) {
        this.payid = payid;
    }

    public BigDecimal getAllcost() {
        return allcost;
    }

    public void setAllcost(BigDecimal allcost) {
        this.allcost = allcost;
    }

    public BigDecimal getHotelgive() {
        return hotelgive;
    }

    public void setHotelgive(BigDecimal hotelgive) {
        this.hotelgive = hotelgive;
    }

    public BigDecimal getOtagive() {
        return otagive;
    }

    public void setOtagive(BigDecimal otagive) {
        this.otagive = otagive;
    }

    public BigDecimal getUsercost() {
        return usercost;
    }

    public void setUsercost(BigDecimal usercost) {
        this.usercost = usercost;
    }

    public BigDecimal getRealotagive() {
        return realotagive;
    }

    public void setRealotagive(BigDecimal realotagive) {
        this.realotagive = realotagive;
    }

    public BigDecimal getQiekeIncome() {
        return qiekeIncome;
    }

    public void setQiekeIncome(BigDecimal qiekeIncome) {
        this.qiekeIncome = qiekeIncome;
    }

    public BigDecimal getLezhu() {
        return lezhu;
    }

    public void setLezhu(BigDecimal lezhu) {
        this.lezhu = lezhu;
    }

    public Integer getOnlinePayType() {
        return onlinePayType;
    }

    public void setOnlinePayType(Integer onlinePayType) {
        this.onlinePayType = onlinePayType;
    }
}
