package com.mk.ots.hotel.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class SelectGoogleMap implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3820119762352656990L;

	private Integer no;
	private BigDecimal lat;
	private BigDecimal lon;
	private BigDecimal latCenter;
	private BigDecimal lonCenter;
	private Long count;
	private String content;
	private String hotelsum;
	private BigDecimal top;
	private BigDecimal left;
	private BigDecimal bottom;
	private BigDecimal right;
	//测试使用数据
	private BigDecimal lat1;
	private BigDecimal lon1;
	private BigDecimal lat2;
	private BigDecimal lon2;
	
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	
	public BigDecimal getLat() {
		return lat;
	}
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}
	public BigDecimal getLon() {
		return lon;
	}
	public void setLon(BigDecimal lon) {
		this.lon = lon;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public BigDecimal getTop() {
		return top;
	}
	public void setTop(BigDecimal top) {
		this.top = top;
	}
	public BigDecimal getLeft() {
		return left;
	}
	public void setLeft(BigDecimal left) {
		this.left = left;
	}
	public BigDecimal getRight() {
		return right;
	}
	public void setRight(BigDecimal right) {
		this.right = right;
	}
	public BigDecimal getBottom() {
		return bottom;
	}
	public void setBottom(BigDecimal bottom) {
		this.bottom = bottom;
	}
	public BigDecimal getLatCenter() {
		return latCenter;
	}
	public void setLatCenter(BigDecimal latCenter) {
		this.latCenter = latCenter;
	}
	public BigDecimal getLonCenter() {
		return lonCenter;
	}
	public void setLonCenter(BigDecimal lonCenter) {
		this.lonCenter = lonCenter;
	}
	public String getHotelsum() {
		return hotelsum;
	}
	public void setHotelsum(String hotelsum) {
		this.hotelsum = hotelsum;
	}
	
}
