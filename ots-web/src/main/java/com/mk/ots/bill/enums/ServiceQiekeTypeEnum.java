package com.mk.ots.bill.enums;

/**
 * Created by Thinkpad on 2015/12/11.
 */
public enum ServiceQiekeTypeEnum {
    YES(1,"有效则返佣正常返佣"),
    NO(0,"如果无效则返佣金为0");


    private Integer type;
    private String text;
    ServiceQiekeTypeEnum(Integer type, String text) {
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
