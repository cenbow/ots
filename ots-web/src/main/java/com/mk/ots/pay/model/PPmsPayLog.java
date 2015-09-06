package com.mk.ots.pay.model;

import java.util.Date;

public class PPmsPayLog {
    private Long id;

    private Long orderid;

    private Date createtime;

    private Long lezhu;

    private String reason;

    private Long operator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Long getLezhu() {
        return lezhu;
    }

    public void setLezhu(Long lezhu) {
        this.lezhu = lezhu;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }
}