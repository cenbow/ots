package com.mk.ots.hotel.model;

import java.math.BigDecimal;

public class TRoomTypeInfoModel {
    private Long id;

    private Long roomtypeid;

    private BigDecimal minarea;

    private BigDecimal maxarea;

    private Long bedtype;
    
    private String bedtypename;

    private String bedsize;

    private String pics;

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

    public BigDecimal getMinarea() {
        return minarea;
    }

    public void setMinarea(BigDecimal minarea) {
        this.minarea = minarea;
    }

    public BigDecimal getMaxarea() {
        return maxarea;
    }

    public void setMaxarea(BigDecimal maxarea) {
        this.maxarea = maxarea;
    }

    public Long getBedtype() {
        return bedtype;
    }

    public void setBedtype(Long bedtype) {
        this.bedtype = bedtype;
    }

    public String getBedtypename() {
        return bedtypename;
    }

    public void setBedtypename(String bedtypename) {
        this.bedtypename = bedtypename;
    }

    public String getBedsize() {
        return bedsize;
    }

    public void setBedsize(String bedsize) {
        this.bedsize = bedsize == null ? null : bedsize.trim();
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics == null ? null : pics.trim();
    }
}