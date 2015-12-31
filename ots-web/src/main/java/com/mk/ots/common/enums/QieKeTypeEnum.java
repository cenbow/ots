package com.mk.ots.common.enums;

/**
 * Created by Thinkpad on 2015/12/27.
 */
public enum QieKeTypeEnum {
    OTHER("0","其他"),B_RULE("1","B规则"),LAXIN_RULE("2","拉新用规则");
    private String code;
    private String text;

    QieKeTypeEnum(String code, String text) {
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
