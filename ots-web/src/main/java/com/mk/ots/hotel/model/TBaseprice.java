package com.mk.ots.hotel.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TBaseprice Model: 房态基本价格维护.
 * @author chuaiqing.
 *
 */
public class TBaseprice {
    private Long id;

    private Long roomtypeid;

    private BigDecimal price;

    private BigDecimal subprice;

    private BigDecimal subper;

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

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}