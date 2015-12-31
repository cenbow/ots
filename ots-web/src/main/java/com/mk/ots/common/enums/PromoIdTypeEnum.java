package com.mk.ots.common.enums;

/**
 * Created by Thinkpad on 2015/10/13.
 */
public enum PromoIdTypeEnum {
    YYF("1","一元房");
    private String code;
    private String text;

    PromoIdTypeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
