package com.mk.ots.roomsale.model;

import java.util.Date;

public class TRoomSaleShowConfig {
    private Integer id;
    private String promotext;
    private Long promoid;
    private String promoicon;
    private String backPics;
    private String backColor;
    private String fontColor;
    private Integer  fontSize;
    private String fontFamily;
    private Date showBeginTime;
    private Date showEndTime;
    private String  showArea;
    private  String  describe;
    private Integer ord;
    private String valid;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromotext() {
        return promotext;
    }

    public void setPromotext(String promotext) {
        this.promotext = promotext;
    }

    public Long getPromoid() {
        return promoid;
    }

    public void setPromoid(Long promoid) {
        this.promoid = promoid;
    }

    public String getPromoicon() {
        return promoicon;
    }

    public void setPromoicon(String promoicon) {
        this.promoicon = promoicon;
    }

    public String getBackPics() {
        return backPics;
    }

    public void setBackPics(String backPics) {
        this.backPics = backPics;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Date getShowBeginTime() {
        return showBeginTime;
    }

    public void setShowBeginTime(Date showBeginTime) {
        this.showBeginTime = showBeginTime;
    }

    public Date getShowEndTime() {
        return showEndTime;
    }

    public void setShowEndTime(Date showEndTime) {
        this.showEndTime = showEndTime;
    }

    public String getShowArea() {
        return showArea;
    }

    public void setShowArea(String showArea) {
        this.showArea = showArea;
    }

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
