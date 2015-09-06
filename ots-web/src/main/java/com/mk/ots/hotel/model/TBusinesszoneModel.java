package com.mk.ots.hotel.model;

public class TBusinesszoneModel {
    private Long id;

    private String name;

    private Long dis;

    private Long businesszonetype;

    private Long fatherid;

    private Long cityid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getDis() {
        return dis;
    }

    public void setDis(Long dis) {
        this.dis = dis;
    }

    public Long getBusinesszonetype() {
        return businesszonetype;
    }

    public void setBusinesszonetype(Long businesszonetype) {
        this.businesszonetype = businesszonetype;
    }

    public Long getFatherid() {
        return fatherid;
    }

    public void setFatherid(Long fatherid) {
        this.fatherid = fatherid;
    }

    public Long getCityid() {
        return cityid;
    }

    public void setCityid(Long cityid) {
        this.cityid = cityid;
    }
}