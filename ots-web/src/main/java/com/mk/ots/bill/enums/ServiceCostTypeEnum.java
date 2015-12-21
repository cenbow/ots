package com.mk.ots.bill.enums;

/**
 * Created by Thinkpad on 2015/12/11.
 */
public enum ServiceCostTypeEnum {
    ORDER_SERVICE_COST(1,"订单佣金规则");

    private Integer type;
    private String text;
    ServiceCostTypeEnum(Integer type, String text) {
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
