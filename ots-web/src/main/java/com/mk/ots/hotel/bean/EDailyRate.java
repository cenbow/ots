/**
 * 
 */
package com.mk.ots.hotel.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yub
 *
 */
public class EDailyRate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6826228331720382802L;
	
	private Long id; 
	private Long ehotelid;   //e_hotel id
	private Long roomtypeid;	//t_roomtype id
	private Long day;	//日期 20150101
	private BigDecimal price;	//特殊价
	private String createtime;
	private String createuser;
	private String updatetime;
	private String updateuser;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEhotelid() {
		return ehotelid;
	}
	public void setEhotelid(Long ehotelid) {
		this.ehotelid = ehotelid;
	}
	public Long getRoomtypeid() {
		return roomtypeid;
	}
	public void setRoomtypeid(Long roomtypeid) {
		this.roomtypeid = roomtypeid;
	}
	public Long getDay() {
		return day;
	}
	public void setDay(Long day) {
		this.day = day;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getCreateuser() {
		return createuser;
	}
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getUpdateuser() {
		return updateuser;
	}
	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}
	
	
	
	
	
}
