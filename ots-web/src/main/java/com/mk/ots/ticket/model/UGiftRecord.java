package com.mk.ots.ticket.model;

import java.util.Date;

public class UGiftRecord {
    private Long id;

    private Long mid;

    private Long activeid;

    private Date createtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getActiveid() {
        return activeid;
    }

    public void setActiveid(Long activeid) {
        this.activeid = activeid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}