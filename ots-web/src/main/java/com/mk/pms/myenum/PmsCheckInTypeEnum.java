package com.mk.pms.myenum;

/**
 * Created by Thinkpad on 2015/11/13.
 */
public enum PmsCheckInTypeEnum {
    PMS(1,"PMS扫描入住"),Ohter(0,"其他方式扫描入住");
    private PmsCheckInTypeEnum(Integer code, String text){
        this.code = code;
        this.text = text;
    }

    public Integer code;
    public String text;

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
