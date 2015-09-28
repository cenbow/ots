package com.mk.ots.search.model;

import java.math.BigDecimal;

/**
 * SSubwayStation Model.
 * 
 * @author chuaiqing.
 *
 */
public class SSubwayStation {
    private Long id;

    private Long lineid;

    private Long stationid;

    private String stationname;

    private BigDecimal lat;

    private BigDecimal lng;

    private String pinyin;

    private String citycode;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLineid() {
        return lineid;
    }

    public void setLineid(Long lineid) {
        this.lineid = lineid;
    }

    public Long getStationid() {
        return stationid;
    }

    public void setStationid(Long stationid) {
        this.stationid = stationid;
    }

    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname == null ? null : stationname.trim();
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

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode == null ? null : citycode.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}