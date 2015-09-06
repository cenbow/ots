package com.mk.ots.hotel.bean;

import java.math.BigDecimal;
import java.util.Date;

public class SelectGoogleMapDto {
	private Date begintime;
	private Date endtime;
	private Integer cityid;
	private Integer level;
	private BigDecimal lat1;
	private BigDecimal lon1;
	private BigDecimal lat2;
	private BigDecimal lon2;
	private Boolean group;
	private BigDecimal minprice;
	private BigDecimal maxprice;
	private Long bedTypeId;
	private BigDecimal minBedLength;
	private BigDecimal maxBedLength;
	private Integer bedNum;
	private Integer roomNum;
	private Integer blockNumX;
	private Integer blockNumY;

	private String memcache;
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
	public Integer getCityid() {
		return cityid;
	}
	public void setCityid(Integer cityid) {
		this.cityid = cityid;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public BigDecimal getLat1() {
		return lat1;
	}
	public void setLat1(BigDecimal lat1) {
		this.lat1 = lat1;
	}
	public BigDecimal getLon1() {
		return lon1;
	}
	public void setLon1(BigDecimal lon1) {
		this.lon1 = lon1;
	}
	public BigDecimal getLat2() {
		return lat2;
	}
	public void setLat2(BigDecimal lat2) {
		this.lat2 = lat2;
	}
	public BigDecimal getLon2() {
		return lon2;
	}
	public void setLon2(BigDecimal lon2) {
		this.lon2 = lon2;
	}
	public Boolean getGroup() {
		return group;
	}
	public void setGroup(Boolean group) {
		this.group = group;
	}
	public BigDecimal getMinprice() {
		return minprice;
	}
	public void setMinprice(BigDecimal minprice) {
		this.minprice = minprice;
	}
	public BigDecimal getMaxprice() {
		return maxprice;
	}
	public void setMaxprice(BigDecimal maxprice) {
		this.maxprice = maxprice;
	}
	public Long getBedTypeId() {
		return bedTypeId;
	}
	public void setBedTypeId(Long bedTypeId) {
		this.bedTypeId = bedTypeId;
	}
	public BigDecimal getMinBedLength() {
		return minBedLength;
	}
	public void setMinBedLength(BigDecimal minBedLength) {
		this.minBedLength = minBedLength;
	}
	public BigDecimal getMaxBedLength() {
		return maxBedLength;
	}
	public void setMaxBedLength(BigDecimal maxBedLength) {
		this.maxBedLength = maxBedLength;
	}
	public Integer getBedNum() {
		return bedNum;
	}
	public void setBedNum(Integer bedNum) {
		this.bedNum = bedNum;
	}
	public Integer getRoomNum() {
		return roomNum;
	}
	public void setRoomNum(Integer roomNum) {
		this.roomNum = roomNum;
	}
	public Integer getBlockNumX() {
		return blockNumX;
	}
	public void setBlockNumX(Integer blockNumX) {
		this.blockNumX = blockNumX;
	}
	public Integer getBlockNumY() {
		return blockNumY;
	}
	public void setBlockNumY(Integer blockNumY) {
		this.blockNumY = blockNumY;
	}
	public String getMemcache() {
		return memcache;
	}
	public void setMemcache(String memcache) {
		this.memcache = memcache;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bedNum == null) ? 0 : bedNum.hashCode());
		result = prime * result
				+ ((bedTypeId == null) ? 0 : bedTypeId.hashCode());
		result = prime * result
				+ ((begintime == null) ? 0 : begintime.hashCode());
		result = prime * result
				+ ((blockNumX == null) ? 0 : blockNumX.hashCode());
		result = prime * result
				+ ((blockNumY == null) ? 0 : blockNumY.hashCode());
		result = prime * result + ((cityid == null) ? 0 : cityid.hashCode());
		result = prime * result + ((endtime == null) ? 0 : endtime.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((lat1 == null) ? 0 : lat1.hashCode());
		result = prime * result + ((lat2 == null) ? 0 : lat2.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((lon1 == null) ? 0 : lon1.hashCode());
		result = prime * result + ((lon2 == null) ? 0 : lon2.hashCode());
		result = prime * result
				+ ((maxBedLength == null) ? 0 : maxBedLength.hashCode());
		result = prime * result
				+ ((maxprice == null) ? 0 : maxprice.hashCode());
		result = prime * result
				+ ((memcache == null) ? 0 : memcache.hashCode());
		result = prime * result
				+ ((minBedLength == null) ? 0 : minBedLength.hashCode());
		result = prime * result
				+ ((minprice == null) ? 0 : minprice.hashCode());
		result = prime * result + ((roomNum == null) ? 0 : roomNum.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectGoogleMapDto other = (SelectGoogleMapDto) obj;
		if (bedNum == null) {
			if (other.bedNum != null)
				return false;
		} else if (!bedNum.equals(other.bedNum))
			return false;
		if (bedTypeId == null) {
			if (other.bedTypeId != null)
				return false;
		} else if (!bedTypeId.equals(other.bedTypeId))
			return false;
		if (begintime == null) {
			if (other.begintime != null)
				return false;
		} else if (!begintime.equals(other.begintime))
			return false;
		if (blockNumX == null) {
			if (other.blockNumX != null)
				return false;
		} else if (!blockNumX.equals(other.blockNumX))
			return false;
		if (blockNumY == null) {
			if (other.blockNumY != null)
				return false;
		} else if (!blockNumY.equals(other.blockNumY))
			return false;
		if (cityid == null) {
			if (other.cityid != null)
				return false;
		} else if (!cityid.equals(other.cityid))
			return false;
		if (endtime == null) {
			if (other.endtime != null)
				return false;
		} else if (!endtime.equals(other.endtime))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (lat1 == null) {
			if (other.lat1 != null)
				return false;
		} else if (!lat1.equals(other.lat1))
			return false;
		if (lat2 == null) {
			if (other.lat2 != null)
				return false;
		} else if (!lat2.equals(other.lat2))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (lon1 == null) {
			if (other.lon1 != null)
				return false;
		} else if (!lon1.equals(other.lon1))
			return false;
		if (lon2 == null) {
			if (other.lon2 != null)
				return false;
		} else if (!lon2.equals(other.lon2))
			return false;
		if (maxBedLength == null) {
			if (other.maxBedLength != null)
				return false;
		} else if (!maxBedLength.equals(other.maxBedLength))
			return false;
		if (maxprice == null) {
			if (other.maxprice != null)
				return false;
		} else if (!maxprice.equals(other.maxprice))
			return false;
		if (memcache == null) {
			if (other.memcache != null)
				return false;
		} else if (!memcache.equals(other.memcache))
			return false;
		if (minBedLength == null) {
			if (other.minBedLength != null)
				return false;
		} else if (!minBedLength.equals(other.minBedLength))
			return false;
		if (minprice == null) {
			if (other.minprice != null)
				return false;
		} else if (!minprice.equals(other.minprice))
			return false;
		if (roomNum == null) {
			if (other.roomNum != null)
				return false;
		} else if (!roomNum.equals(other.roomNum))
			return false;
		return true;
	}
	
	
}
