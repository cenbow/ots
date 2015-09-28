package com.mk.ots.ticket.model;

import java.util.Date;

public class USendUticket {
    private Long id;

    private Long mid;

    private Integer msgtype;

    private Integer statisticinvalid;

    private Date createtime;

    private Date updatetime;

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

    public Integer getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(Integer msgtype) {
        this.msgtype = msgtype;
    }

    public Integer getStatisticinvalid() {
        return statisticinvalid;
    }

    public void setStatisticinvalid(Integer statisticinvalid) {
        this.statisticinvalid = statisticinvalid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}