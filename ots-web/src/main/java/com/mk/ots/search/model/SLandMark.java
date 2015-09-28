package com.mk.ots.search.model;

import java.math.BigDecimal;

/**
 * s_landmark Model.
 * 
 * @author chuaiqing.
 *
 */
public class SLandMark {
    private Long id;

    private Long landmarkid;

    private String landmarkname;

    private String pinyin;

    private Integer ltype;

    private BigDecimal lat;

    private BigDecimal lng;

    private String citycode;

    private String discode;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLandmarkid() {
        return landmarkid;
    }

    public void setLandmarkid(Long landmarkid) {
        this.landmarkid = landmarkid;
    }

    public String getLandmarkname() {
        return landmarkname;
    }

    public void setLandmarkname(String landmarkname) {
        this.landmarkname = landmarkname == null ? null : landmarkname.trim();
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    public Integer getLtype() {
        return ltype;
    }

    public void setLtype(Integer ltype) {
        this.ltype = ltype;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode == null ? null : citycode.trim();
    }

    public String getDiscode() {
        return discode;
    }

    public void setDiscode(String discode) {
        this.discode = discode == null ? null : discode.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}