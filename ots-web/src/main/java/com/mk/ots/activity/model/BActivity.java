package com.mk.ots.activity.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mk.ots.common.enums.PromotionMethodTypeEnum;

public class BActivity {
	
	@JsonProperty("activeid")
    private Long id;
	
	@JsonProperty("activetitle")
    private String title;
	
	@JsonProperty("activedetil")
    private String detail;
	
	@JsonProperty("activedescription")
    private String description;
	
	@JsonProperty("activetype")
    private String type;
	
	@JsonProperty("begintime")
	@JsonFormat(pattern="yyyyMMddHHmmss")
    private Date begintime;

	@JsonProperty("endtime")
	@JsonFormat(pattern="yyyyMMddHHmmss")
    private Date endtime;

	@JsonProperty("banner")
	private String banner=""; 
	
	@JsonProperty("activeurl")
	private String activeurl=""; 
	
	@JsonProperty("activepic")
	private String activepic=""; 
	
	@JsonIgnore
    private Integer limitget;
	
	@JsonIgnore
    private String hotel;
	
    private String activityCarrier;
    
    private String userGroup;
	
	/**
	 * 优惠券自动发放还是需用户领取
	 */
	@JsonIgnore
    private PromotionMethodTypeEnum promotionmethodtype;
    
	/**
	 * 优惠券生成类型 1.生成全部 2.随机生成一个
	 */
	private PromotionGenTypeEnum gentype;   
	
	@JsonProperty("hotel")
	private List hotelList = Lists.newArrayList();
	
    public String getActivityCarrier() {
		return activityCarrier;
	}

	public void setActivityCarrier(String activityCarrier) {
		this.activityCarrier = activityCarrier;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getActiveurl() {
		return activeurl;
	}

	public void setActiveurl(String activeurl) {
		this.activeurl = activeurl;
	}

	public String getActivepic() {
		return activepic;
	}

	public void setActivepic(String activepic) {
		this.activepic = activepic;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
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

	public Integer getLimitget() {
		return limitget;
	}

	public void setLimitget(Integer limitget) {
		this.limitget = limitget;
	}

	public String getHotel() {
		return hotel;
	}

	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public List getHotelList() {
		return hotelList;
	}

	public void setHotelList(List hotelList) {
		this.hotelList = hotelList;
	}

	public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

	public PromotionGenTypeEnum getGentype() {
		return gentype;
	}

	public void setGentype(PromotionGenTypeEnum gentype) {
		this.gentype = gentype;
	}
	
	public PromotionMethodTypeEnum getPromotionmethodtype() {
		return promotionmethodtype;
	}

	public void setPromotionmethodtype(PromotionMethodTypeEnum promotionmethodtype) {
		this.promotionmethodtype = promotionmethodtype;
	}

	@Override
	public String toString() {
		return "BActivity [id=" + id + ", title=" + title + ", detail="
				+ detail + ", description=" + description + ", type=" + type
				+ ", begintime=" + begintime + ", endtime=" + endtime
				+ ", banner=" + banner + ", activeurl=" + activeurl
				+ ", activepic=" + activepic + ", limitget=" + limitget
				+ ", hotel=" + hotel + ", activityCarrier=" + activityCarrier
				+ ", userGroup=" + userGroup + ", promotionmethodtype="
				+ promotionmethodtype + ", gentype=" + gentype + ", hotelList="
				+ hotelList + "]";
	}





}