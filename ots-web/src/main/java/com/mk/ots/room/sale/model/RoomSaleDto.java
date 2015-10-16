package com.mk.ots.room.sale.model;

public class RoomSaleDto {
    private String isOnPromo; //是否活动中
    private String  promoText ;//活动文字说明
    private String promoTextColor;//活动文字颜色
    private String promoStartTime;//活动开始时间
    private String promoEndTime;//活动结束时间
    private Integer saleType;//促销类型
    private String saleName;//促销名称
    private Double salePrice;//促销价格
    private String roomNo;//房间号
    private Integer roomtypeid;//房型Id
    private String useDescribe;//使用描述

    public String getIsOnPromo() {
        return isOnPromo;
    }

    public void setIsOnPromo(String isOnPromo) {
        this.isOnPromo = isOnPromo;
    }

    public String getPromoText() {
        return promoText;
    }

    public void setPromoText(String promoText) {
        this.promoText = promoText;
    }

    public String getPromoTextColor() {
        return promoTextColor;
    }

    public void setPromoTextColor(String promoTextColor) {
        this.promoTextColor = promoTextColor;
    }

    public String getPromoStartTime() {
        return promoStartTime;
    }

    public void setPromoStartTime(String promoStartTime) {
        this.promoStartTime = promoStartTime;
    }

    public String getPromoEndTime() {
        return promoEndTime;
    }

    public void setPromoEndTime(String promoEndTime) {
        this.promoEndTime = promoEndTime;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getRoomtypeid() {
        return roomtypeid;
    }

    public void setRoomtypeid(Integer roomtypeid) {
        this.roomtypeid = roomtypeid;
    }

    public String getUseDescribe() {
        return useDescribe;
    }

    public void setUseDescribe(String useDescribe) {
        this.useDescribe = useDescribe;
    }
}
