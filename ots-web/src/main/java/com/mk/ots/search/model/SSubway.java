package com.mk.ots.search.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * SSubway Model.
 * 
 * @author chuaiqing.
 *
 */
public class SSubway {
    private Long id;

    private Long lineid;

    private String linename;

    private Integer ltype;

    private String citycode;

    private Integer status;
    
    private List<SSubwayStation> stations = Lists.newArrayList();

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

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename == null ? null : linename.trim();
    }

    public Integer getLtype() {
        return ltype;
    }

    public void setLtype(Integer ltype) {
        this.ltype = ltype;
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

    public List<SSubwayStation> getStations() {
        return stations;
    }

    public void setStations(List<SSubwayStation> stations) {
        this.stations = stations;
    }

}