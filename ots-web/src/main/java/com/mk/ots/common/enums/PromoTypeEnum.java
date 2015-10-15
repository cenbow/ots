package com.mk.ots.common.enums;

/**
 * Created by Thinkpad on 2015/10/13.
 */
public enum PromoTypeEnum {
    OTHER("0","其他"),TJ("1","特价房");
    private String code;
    private String text;

    PromoTypeEnum(String code, String text) {
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
