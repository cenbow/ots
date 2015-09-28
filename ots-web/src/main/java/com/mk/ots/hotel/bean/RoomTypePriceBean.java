package com.mk.ots.hotel.bean;

import java.math.BigDecimal;

/**
 * @author he 房型价格bean（供订单组使用）
 */
public class RoomTypePriceBean {

	private String day;// yyyyMMdd
	private Long roomtypeid;// 房型id
	private BigDecimal mikeprice;// 眯客价
	private BigDecimal price;// 门市价

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Long getRoomtypeid() {
		return roomtypeid;
	}

	public void setRoomtypeid(Long roomtypeid) {
		this.roomtypeid = roomtypeid;
	}

	public BigDecimal getMikeprice() {
		return mikeprice;
	}

	public void setMikeprice(BigDecimal mikeprice) {
		this.mikeprice = mikeprice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
