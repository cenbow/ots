package com.mk.ots.ticket.model;

import java.util.Date;

public class BPrize {
    private Long id;

    private String name;

    private String description;

    private Long num;

    private Long price;

    private Integer type;

    private String url;

    private Long weight;
    private Long newweight;
    private Long otherweight;
    private Long merchantid;

    private Long activeid;

    private String enable;

    private Date begintime;
    private Date endtime;
    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

 

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(Long merchantid) {
        this.merchantid = merchantid;
    }

    public Long getActiveid() {
        return activeid;
    }

    public void setActiveid(Long activeid) {
        this.activeid = activeid;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable == null ? null : enable.trim();
    }

	public Date getBegintime() {
		return begintime;
	}

	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

    public Long getNewweight() {
        return newweight;
    }

    public void setNewweight(Long newweight) {
        this.newweight = newweight;
    }

    public Long getOtherweight() {
        return otherweight;
    }

    public void setOtherweight(Long otherweight) {
        this.otherweight = otherweight;
    }

    @Override
	public String toString() {
		return "BPrize [id=" + id + ", name=" + name + ", description="
				+ description + ", num=" + num + ", price=" + price + ", type="
				+ type + ", url=" + url + ", weight=" + weight + ", newweight="
                + newweight + ", otherweight=" + otherweight  + ", merchantid=" + merchantid + ", activeid=" + activeid
				+ ", enable=" + enable + ", begintime=" + begintime
				+ ", endtime=" + endtime + "]";
	}

    
}