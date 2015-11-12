package com.mk.ots.promoteconfig.model;

import java.math.BigDecimal;
import java.util.Date;

public class TPromoteConfig {
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCityLevel() {
		return cityLevel;
	}

	public void setCityLevel(Integer cityLevel) {
		this.cityLevel = cityLevel;
	}

	public BigDecimal getOnlineGiveHotel() {
		return onlineGiveHotel;
	}

	public void setOnlineGiveHotel(BigDecimal onlineGiveHotel) {
		this.onlineGiveHotel = onlineGiveHotel;
	}

	public BigDecimal getOfflineGiveHotel() {
		return offlineGiveHotel;
	}

	public void setOfflineGiveHotel(BigDecimal offlineGiveHotel) {
		this.offlineGiveHotel = offlineGiveHotel;
	}

	public BigDecimal getGiveNewMemberGeneral() {
		return giveNewMemberGeneral;
	}

	public void setGiveNewMemberGeneral(BigDecimal giveNewMemberGeneral) {
		this.giveNewMemberGeneral = giveNewMemberGeneral;
	}

	public BigDecimal getGiveNewMemberAppOnly() {
		return giveNewMemberAppOnly;
	}

	public void setGiveNewMemberAppOnly(BigDecimal giveNewMemberAppOnly) {
		this.giveNewMemberAppOnly = giveNewMemberAppOnly;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	private Long id;
	private Integer cityLevel;
	private BigDecimal onlineGiveHotel;
	private BigDecimal offlineGiveHotel;
	private BigDecimal giveNewMemberGeneral;
	private BigDecimal giveNewMemberAppOnly;

	private String description;
	private Date createTime;
	private Date updateTime;
	private String createBy;
	private String updateBy;
	
}
