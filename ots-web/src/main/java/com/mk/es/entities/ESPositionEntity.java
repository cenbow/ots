package com.mk.es.entities;


/**
 * Position Entity.
 * @author LYN.
 *
 */
public class ESPositionEntity {
	private Long id;

	private String citycode;
	
    private String name;

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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public String getPtxt() {
		return ptxt;
	}

	public void setPtxt(String ptxt) {
		this.ptxt = ptxt;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
    
	
}
