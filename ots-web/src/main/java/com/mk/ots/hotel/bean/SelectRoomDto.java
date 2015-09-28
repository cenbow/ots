package com.mk.ots.hotel.bean;

import java.math.BigDecimal;
import java.util.Date;


/**
 *
 * @author JGroup
 * @date   2015年1月30日下午5:39:01
 */
public class SelectRoomDto {
	private Date begintime;
	private Date endtime;
	private BigDecimal lat;
	private BigDecimal log;
	private Integer distance;
	private Integer cityid;
	private Long bedTypeId;
	private BigDecimal minBedLength;
	private BigDecimal maxBedLength;
	private Integer bedNum;
	private Integer roomNum;
	private BigDecimal minprice;
	private BigDecimal maxprice;
	private int page=1;
	private int limit=10;
	private TDefselect def;//不记入hashcode
	/**
	 *  1、	离我最近
	 *	2、	价格最低
	 *	3、	价格最高
	 *	4、	人气
	 *	5、	默认
	 */
	private int orderby=5;
	private Long roomtypeid;
	
	private String memcache;
	
	private Long hotelid;
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
	public BigDecimal getLat() {
		return lat;
	}
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}
	public BigDecimal getLog() {
		return log;
	}
	public void setLog(BigDecimal log) {
		this.log = log;
	}
	public Integer getDistance() {
		return distance;
	}
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public Integer getCityid() {
		return cityid;
	}
	public void setCityid(Integer cityid) {
		this.cityid = cityid;
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
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
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
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getOrderby() {
		return orderby;
	}
	public void setOrderby(int orderby) {
		this.orderby = orderby;
	}
	
	public TDefselect getDef() {
		return def;
	}
	public void setDef(TDefselect def) {
		this.def = def;
	}
	public Long getHotelid() {
		return hotelid;
	}
	public void setHotelid(Long hotelid) {
		this.hotelid = hotelid;
	}
	public Long getRoomtypeid() {
		return roomtypeid;
	}
	public void setRoomtypeid(Long roomtypeid) {
		this.roomtypeid = roomtypeid;
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
		result = prime * result + ((cityid == null) ? 0 : cityid.hashCode());
		result = prime * result + ((def == null) ? 0 : def.hashCode());
		result = prime * result
				+ ((distance == null) ? 0 : distance.hashCode());
		result = prime * result + ((endtime == null) ? 0 : endtime.hashCode());
		result = prime * result + ((hotelid == null) ? 0 : hotelid.hashCode());
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + limit;
		result = prime * result + ((log == null) ? 0 : log.hashCode());
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
		result = prime * result + orderby;
		result = prime * result + page;
		result = prime * result + ((roomNum == null) ? 0 : roomNum.hashCode());
		result = prime * result
				+ ((roomtypeid == null) ? 0 : roomtypeid.hashCode());
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
		SelectRoomDto other = (SelectRoomDto) obj;
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
		if (cityid == null) {
			if (other.cityid != null)
				return false;
		} else if (!cityid.equals(other.cityid))
			return false;
		if (def == null) {
			if (other.def != null)
				return false;
		} else if (!def.equals(other.def))
			return false;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.equals(other.distance))
			return false;
		if (endtime == null) {
			if (other.endtime != null)
				return false;
		} else if (!endtime.equals(other.endtime))
			return false;
		if (hotelid == null) {
			if (other.hotelid != null)
				return false;
		} else if (!hotelid.equals(other.hotelid))
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (limit != other.limit)
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
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
		if (orderby != other.orderby)
			return false;
		if (page != other.page)
			return false;
		if (roomNum == null) {
			if (other.roomNum != null)
				return false;
		} else if (!roomNum.equals(other.roomNum))
			return false;
		if (roomtypeid == null) {
			if (other.roomtypeid != null)
				return false;
		} else if (!roomtypeid.equals(other.roomtypeid))
			return false;
		return true;
	}
	
}
