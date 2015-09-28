package com.mk.ots.hotel.model;

import java.math.BigDecimal;

public class TPricetimeWithPrices extends TPricetime {
    private Long roomtypeid;
    private BigDecimal price;
    private BigDecimal subprice;
    private BigDecimal subper;
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
}
