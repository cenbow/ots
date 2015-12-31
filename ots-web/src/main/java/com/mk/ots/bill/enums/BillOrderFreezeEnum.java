package com.mk.ots.bill.enums;

/**
 * Created by Thinkpad on 2015/12/28.
 */
public enum BillOrderFreezeEnum {
    //是否冻结 1:冻结 0：未冻结
    YES(1,"冻结"),NO(0,"未冻结");

    private Integer code;
    private String text;
    BillOrderFreezeEnum(Integer code, String text) {
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
