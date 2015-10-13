package com.mk.ots.wallet.model;

/**
 * 业务类别
 * <p/>
 * Created by nolan on 15/9/9.
 */
public enum CashflowTypeEnum {
    CASHBACK_ORDER_IN(1),           // 订单返现
    CASHBACK_HOTEL_IN(2),           // 酒店点评返现
    CONSUME_ORDER_OUT_LOCK(3),      //订单消费已冻结
    CONSUME_ORDER_OUT_CONFIRM(4),   //订单消费已确认
    CONSUME_ORDER_REFUND(5),        //订单消费退回

    CASHBACK_CARD_IN(6);           // 储值卡充值

    private int id;

    CashflowTypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
