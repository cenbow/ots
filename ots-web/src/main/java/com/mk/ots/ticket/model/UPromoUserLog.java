package com.mk.ots.ticket.model;

import java.util.Date;

/**
 * Created by ljx on 15/10/13.
 */
public class UPromoUserLog {

    /**
     * 自增主键id
     */
    private Long id;

    /**
     * 批券id
     */
    private Long  promoId;

    /**
     * 用户id
     */
    private  Long  mid;

    /**
     * 抵扣金额
     */
    private  Double  promoPrice;

    /**
     * 订单id
     */
    private  String   orderId;

    /**
     * 使用时间
     */
    private  Date   create_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPromoId() {
        return promoId;
    }

    public void setPromoId(Long promoId) {
        this.promoId = promoId;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Double getPromoPrice() {
        return promoPrice;
    }

    public void setPromoPrice(Double promoPrice) {
        this.promoPrice = promoPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
