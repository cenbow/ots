package com.mk.ots.roomsale.model;

import java.sql.Date;
import java.sql.Time;

public class RoomPromoDto {
    private Integer roomId;
    private Integer roomTypeId;
    private String saleName;
    private Time startTime;
    private Time endTime;
    private Date startDate;
    private Date endDate;
    private String nameFontColor;
    private String typeDesc;
    private String promoType;
    private String promoValue;
    private String promoLabel;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getNameFontColor() {
        return nameFontColor;
    }

    public void setNameFontColor(String nameFontColor) {
        this.nameFontColor = nameFontColor;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }

    public String getPromoValue() {
        return promoValue;
    }

    public void setPromoValue(String promoValue) {
        this.promoValue = promoValue;
    }

    public String getPromoLabel() {
        return promoLabel;
    }

    public void setPromoLabel(String promoLabel) {
        this.promoLabel = promoLabel;
    }
}
