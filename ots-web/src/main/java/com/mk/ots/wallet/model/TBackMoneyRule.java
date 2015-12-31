package com.mk.ots.wallet.model;

import java.math.BigDecimal;

/**
 * Created by jeashi on 2015/12/10.
 */
public class TBackMoneyRule {
    private   Long   id;
    private   Integer   bussinessType;
    private   String   hotelCityCode;
    private   String   memberCityCode;
    private   String   type;
    private   Integer  ruleType;
    private   BigDecimal  perMoney;
    private   BigDecimal maxMoney;
    private   BigDecimal  maxCount;
    private   String   status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBussinessType() {
        return bussinessType;
    }

    public void setBussinessType(Integer bussinessType) {
        this.bussinessType = bussinessType;
    }

    public String getHotelCityCode() {
        return hotelCityCode;
    }

    public void setHotelCityCode(String hotelCityCode) {
        this.hotelCityCode = hotelCityCode;
    }

    public String getMemberCityCode() {
        return memberCityCode;
    }

    public void setMemberCityCode(String memberCityCode) {
        this.memberCityCode = memberCityCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public BigDecimal getPerMoney() {
        return perMoney;
    }

    public void setPerMoney(BigDecimal perMoney) {
        this.perMoney = perMoney;
    }

    public BigDecimal getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(BigDecimal maxMoney) {
        this.maxMoney = maxMoney;
    }

    public BigDecimal getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(BigDecimal maxCount) {
        this.maxCount = maxCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
