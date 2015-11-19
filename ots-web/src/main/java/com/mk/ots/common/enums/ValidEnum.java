package com.mk.ots.common.enums;

/**
 * Created by Thinkpad on 2015/11/16.
 */
public enum ValidEnum {
    VALID("T","有效"),
    INVALID("F","无效");
    private final String code;
    private final String text;

    private ValidEnum(String code,String text){
        this.code=code;
        this.text=text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
