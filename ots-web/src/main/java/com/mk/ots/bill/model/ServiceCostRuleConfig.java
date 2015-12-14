package com.mk.ots.bill.model;

import java.math.BigDecimal;
import java.util.Date;

public class ServiceCostRuleConfig {
    private Long id;

    private Integer bussinessType;

    private Integer ruleType;

    private Integer isDefault;

    private String hotelCityCode;

    private String memberCityCode;

    private BigDecimal serviceCost;

    private BigDecimal ratio;

    private BigDecimal maxServiceCost;

    private String valid;

    private Date beginTime;

    private Date endTime;

    private Date createtime;

    private Integer qiekeFlag;

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

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getHotelCityCode() {
        return hotelCityCode;
    }

    public void setHotelCityCode(String hotelCityCode) {
        this.hotelCityCode = hotelCityCode == null ? null : hotelCityCode.trim();
    }

    public String getMemberCityCode() {
        return memberCityCode;
    }

    public void setMemberCityCode(String memberCityCode) {
        this.memberCityCode = memberCityCode == null ? null : memberCityCode.trim();
    }

    public BigDecimal getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(BigDecimal serviceCost) {
        this.serviceCost = serviceCost;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public BigDecimal getMaxServiceCost() {
        return maxServiceCost;
    }

    public void setMaxServiceCost(BigDecimal maxServiceCost) {
        this.maxServiceCost = maxServiceCost;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid == null ? null : valid.trim();
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getQiekeFlag() {
        return qiekeFlag;
    }

    public void setQiekeFlag(Integer qiekeFlag) {
        this.qiekeFlag = qiekeFlag;
    }
}