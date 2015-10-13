package com.mk.ots.wallet.model;

/**
 * 业务类别
 * <p/>
 * Created by nolan on 15/9/9.
 */
public enum CashflowTypeEnum {
    DEFAULT(0,"默认错误"),
    CASHBACK_ORDER_IN(1,"订单返现"),           // 订单返现
    CASHBACK_HOTEL_IN(2,"酒店点评返现"),           // 酒店点评返现
    CONSUME_ORDER_OUT_LOCK(3,"订单消费冻结"),      //订单消费已冻结
    CONSUME_ORDER_OUT_CONFIRM(4,"订单消费已确认"),   //订单消费已确认
    CONSUME_ORDER_REFUND(5,"订单消费退回"),        //订单消费退回
    MIKE_CHARGE_CARD(6,"眯客充值卡");

    private int id;
    private String desc;

    CashflowTypeEnum(int id,String desc) {
        this.id = id;
        this.desc=desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static CashflowTypeEnum getById(int id) {
        for (CashflowTypeEnum cashflowTypeEnum : CashflowTypeEnum.values()) {
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
