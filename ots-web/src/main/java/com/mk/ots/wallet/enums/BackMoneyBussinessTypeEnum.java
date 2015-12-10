package com.mk.ots.wallet.enums;

public enum BackMoneyBussinessTypeEnum {

    //1-支付
    type_PT(0,"普通"),
    type_TJ(1,"特价");


    private int id;
    private String desc;

    BackMoneyBussinessTypeEnum(int id, String desc) {
        this.id = id;
        this.desc=desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static BackMoneyBussinessTypeEnum getById(int id) {
        for (BackMoneyBussinessTypeEnum cashflowTypeEnum : BackMoneyBussinessTypeEnum.values()) {
            if (cashflowTypeEnum.getId() == id) {
                return cashflowTypeEnum;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
