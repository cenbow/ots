package com.mk.ots.hotel.model;

import java.math.BigDecimal;

/**
 * TRoomTypeModel.
 * @author chuaiqing.
 *
 */
public class TRoomTypeModel {
    private Long id;

    private Long thotelid;

    private Long ehotelid;

    private String name;

    private String pms;

    private Integer bednum;

    private Integer roomnum;

    private BigDecimal cost;
    
    
    //imike2.5 add 
    private BigDecimal area;
    
    private String bedtypename;
    
    private String bedlength;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getThotelid() {
        return thotelid;
    }

    public void setThotelid(Long thotelid) {
        this.thotelid = thotelid;
    }

    public Long getEhotelid() {
        return ehotelid;
    }

    public void setEhotelid(Long ehotelid) {
        this.ehotelid = ehotelid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPms() {
        return pms;
    }

    public void setPms(String pms) {
        this.pms = pms == null ? null : pms.trim();
    }

    public Integer getBednum() {
        return bednum;
    }

    public void setBednum(Integer bednum) {
        this.bednum = bednum;
    }

    public Integer getRoomnum() {
        return roomnum;
    }

    public void setRoomnum(Integer roomnum) {
        this.roomnum = roomnum;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

	public BigDecimal getArea() {
		return area;
	}

	public void setArea(BigDecimal area) {
		this.area = area;
	}

	public String getBedtypename() {
		return bedtypename;
	}

	public void setBedtypename(String bedtypename) {
		this.bedtypename = bedtypename;
	}

	public String getBedlength() {
		return bedlength;
	}

	public void setBedlength(String bedlength) {
		this.bedlength = bedlength;
	}
    
    
}