package com.mybatis.model;

import java.math.BigDecimal;
import java.util.Date;

public class BStrategyPrice {
    private Long id;

    private String name;

    private Long type;

    private BigDecimal value;

    private BigDecimal stprice;

    private Long rulearea;

    private Long rulehotel;

    private Long ruleroomtype;

    private Date rulebegintime;

    private Date ruleendtime;

    private String ruleroom;

    private String enable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getStprice() {
        return stprice;
    }

    public void setStprice(BigDecimal stprice) {
        this.stprice = stprice;
    }

    public Long getRulearea() {
        return rulearea;
    }

    public void setRulearea(Long rulearea) {
        this.rulearea = rulearea;
    }

    public Long getRulehotel() {
        return rulehotel;
    }

    public void setRulehotel(Long rulehotel) {
        this.rulehotel = rulehotel;
    }

    public Long getRuleroomtype() {
        return ruleroomtype;
    }

    public void setRuleroomtype(Long ruleroomtype) {
        this.ruleroomtype = ruleroomtype;
    }

    public Date getRulebegintime() {
        return rulebegintime;
    }

    public void setRulebegintime(Date rulebegintime) {
        this.rulebegintime = rulebegintime;
    }

    public Date getRuleendtime() {
        return ruleendtime;
    }

    public void setRuleendtime(Date ruleendtime) {
        this.ruleendtime = ruleendtime;
    }

    public String getRuleroom() {
        return ruleroom;
    }

    public void setRuleroom(String ruleroom) {
        this.ruleroom = ruleroom == null ? null : ruleroom.trim();
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable == null ? null : enable.trim();
    }
}