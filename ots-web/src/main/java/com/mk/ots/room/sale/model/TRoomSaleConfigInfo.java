package com.mk.ots.room.sale.model;

public class TRoomSaleConfigInfo {
    private Integer id;
    private String promoName;
    private String fontColor;
    private Integer saleType;
    private String useDescribe;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public String getUseDescribe() {
        return useDescribe;
    }

    public void setUseDescribe(String useDescribe) {
        this.useDescribe = useDescribe;
    }
}
