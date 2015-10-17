package com.mk.ots.card.model;

public enum CardTypeEnum {

    //1-生成 2-入库3-激活4-使用5-注销
    DEFAULT(0,"默认错误"),
    TYPE_INIT(1,"生成"),           // 生成
    TYPE_OUT(2,"入库"),            // 入库
    TYPE_ACTIVE(3,"激活"),        //激活
    TYPE_USED(4,"使用"),          //使用
    TYPE_CANCEL(5,"注销");        //注销

    private int id;
    private String desc;

    CardTypeEnum(int id, String desc) {
        this.id = id;
        this.desc=desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static CardTypeEnum getById(int id) {
        for (CardTypeEnum cashflowTypeEnum : CardTypeEnum.values()) {
            if (cashflowTypeEnum.getId() == id) {
                return cashflowTypeEnum;
            }
        }
        return DEFAULT;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
