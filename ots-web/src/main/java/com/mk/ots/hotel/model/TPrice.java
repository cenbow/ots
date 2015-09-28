package com.mk.ots.hotel.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TPrice Model: 房态策略价格维护，基于cron表达式设置房间价格.
 * @author chuaiqing.
 *
 */
public class TPrice {
    private Long id;

    private Long roomtypeid;

    private Long timeid;

    private BigDecimal price;

    private BigDecimal subprice;

    private BigDecimal subper;

    private Integer orderindex;

    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomtypeid() {
        return roomtypeid;
    }

    public void setRoomtypeid(Long roomtypeid) {
        this.roomtypeid = roomtypeid;
    }

    public Long getTimeid() {
        return timeid;
    }

    public void setTimeid(Long timeid) {
        this.timeid = timeid;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSubprice() {
        return subprice;
    }

    public void setSubprice(BigDecimal subprice) {
        this.subprice = subprice;
    }

    public BigDecimal getSubper() {
        return subper;
    }

    public void setSubper(BigDecimal subper) {
        this.subper = subper;
    }

    public Integer getOrderindex() {
        return orderindex;
    }

    public void setOrderindex(Integer orderindex) {
        this.orderindex = orderindex;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}