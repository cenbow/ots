package com.mk.ots.bill.enums;

/**
 * Created by Thinkpad on 2015/12/11.
 */
public enum ServiceCostRuleTypeEnum {
    RATIO(1,"佣金比例"),FIX(2,"固定金额");

    private Integer type;
    private String text;
    ServiceCostRuleTypeEnum(Integer type, String text) {
        this.type = type;
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
