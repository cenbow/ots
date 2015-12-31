package com.mk.ots.bill.enums;

/**
 * Created by Thinkpad on 2015/12/28.
 */
public enum BillOrderCheckStatusEnum {
    //1：初始化 2，待审核 3，审核拒绝 4, 审核通过 5，已确认 6，已结算
    INIT(1,"初始化");

    private Integer code;
    private String text;
    BillOrderCheckStatusEnum(Integer code, String text) {
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
