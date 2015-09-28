package com.mk.ots.order.model;

import java.util.Date;

public class BOrderLog {
    private Integer id;

    private Long orderid;

    private Long spreaduser;

    private String spreadnote;

    private String sendlezhu;

    private String promotion;

    private Date checkintime;

    private Date checkouttime;

    private Long pid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Long getSpreaduser() {
        return spreaduser;
    }

    public void setSpreaduser(Long spreaduser) {
        this.spreaduser = spreaduser;
    }

    public String getSpreadnote() {
        return spreadnote;
    }

    public void setSpreadnote(String spreadnote) {
        this.spreadnote = spreadnote == null ? null : spreadnote.trim();
    }

    public String getSendlezhu() {
        return sendlezhu;
    }

    public void setSendlezhu(String sendlezhu) {
        this.sendlezhu = sendlezhu == null ? null : sendlezhu.trim();
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion == null ? null : promotion.trim();
    }

    public Date getCheckintime() {
        return checkintime;
    }

    public void setCheckintime(Date checkintime) {
        this.checkintime = checkintime;
    }

    public Date getCheckouttime() {
        return checkouttime;
    }

    public void setCheckouttime(Date checkouttime) {
        this.checkouttime = checkouttime;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}