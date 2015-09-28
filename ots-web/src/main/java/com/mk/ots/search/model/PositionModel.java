package com.mk.ots.search.model;

public class PositionModel {
    private Long id;

    private String citycode;
    
    private String pname;

    /*
     * 1商圈 2机场车站 3地铁路线 4行政区 5景点 6医院 7高校
     */
    private String ptype;

    private String coordinates;

    private String ptxt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname == null ? null : pname.trim();
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype == null ? null : ptype.trim();
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates == null ? null : coordinates.trim();
    }

    public String getPtxt() {
        return ptxt;
    }

    public void setPtxt(String ptxt) {
        this.ptxt = ptxt == null ? null : ptxt.trim();
    }

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
    
}