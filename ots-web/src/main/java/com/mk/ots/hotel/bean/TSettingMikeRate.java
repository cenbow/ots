package com.mk.ots.hotel.bean;

import java.math.BigDecimal;
import java.util.Date;

public class TSettingMikeRate {
    private Long id;

    private Long ehotelid;

    private Long roomtypeid;

    private Integer settingtype;

    private Integer settingtime;

    private BigDecimal subprice;

    private BigDecimal subper;

    private Date createtime;

    private String createuser;

    private Date updatetime;

    private String updateuser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEhotelid() {
        return ehotelid;
    }

    public void setEhotelid(Long ehotelid) {
        this.ehotelid = ehotelid;
    }

    public Long getRoomtypeid() {
        return roomtypeid;
    }

    public void setRoomtypeid(Long roomtypeid) {
        this.roomtypeid = roomtypeid;
    }

    public Integer getSettingtype() {
        return settingtype;
    }

    public void setSettingtype(Integer settingtype) {
        this.settingtype = settingtype;
    }

    public Integer getSettingtime() {
        return settingtime;
    }

    public void setSettingtime(Integer settingtime) {
        this.settingtime = settingtime;
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

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser == null ? null : createuser.trim();
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser == null ? null : updateuser.trim();
    }
}