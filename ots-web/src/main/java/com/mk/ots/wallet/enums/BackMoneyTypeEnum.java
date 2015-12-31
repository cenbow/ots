package com.mk.ots.wallet.enums;

public enum BackMoneyTypeEnum {

    //1-支付
    type_pay(1,"支付");


    private int id;
    private String desc;

    BackMoneyTypeEnum(int id, String desc) {
        this.id = id;
        this.desc=desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static BackMoneyTypeEnum getById(int id) {
        for (BackMoneyTypeEnum cashflowTypeEnum : BackMoneyTypeEnum.values()) {
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
