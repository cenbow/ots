package com.mk.ots.common.enums;

/**
 * @author kirin
 *
 */
public enum SearchBlackTypeEnum {
    ONESECKILL(1,"一元秒杀");
    private Integer code;
    private String text;

    SearchBlackTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
