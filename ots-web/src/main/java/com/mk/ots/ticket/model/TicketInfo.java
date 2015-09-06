package com.mk.ots.ticket.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mk.framework.util.TFBooleanSerializer;

public class TicketInfo implements Serializable{
	private static final long serialVersionUID = -1470560953317247446L;
	//优惠券唯一id
	private Long id;				
	//优惠券名
	private String name; 			
	//选中,
	@JsonSerialize(using=TFBooleanSerializer.class)
	private boolean select; 		
	//可以使用
	@JsonSerialize(using=TFBooleanSerializer.class)
	private Boolean check; 			
	//线上优惠多少钱
	private BigDecimal subprice; 	
	//线下优惠多少钱
	private BigDecimal offlineprice; 
	//线下优惠多少钱 跟offlineprice一模一样，变态的接口文档要求用这个字段
	private BigDecimal offlinesubprice; 
	//是否已使用（T/F）
	@JsonSerialize(using=TFBooleanSerializer.class)
	private boolean isused; 
	//优惠券类型 1、普通立减 2、切客优惠码 3、议价优惠码
	private int type; 
	//是否是优惠券（T优惠券，F优惠码）
	@JsonSerialize(using=TFBooleanSerializer.class)
	private Boolean isticket; 
	//线上线下限制
	private String uselimit;
	//有效起始时间
	@JsonFormat(pattern="yyyyMMddHHmmss", timezone = "GMT+8")
	private Date begintime; 
	//有效终止时间
	@JsonFormat(pattern="yyyyMMddHHmmss", timezone = "GMT+8")
	private Date endtime; 
	
	
	private Long activityid ;
	private int status;
	public Date getBegintime() {
		return begintime;
	}
	public Boolean getCheck() {
		return check;
	}
	public Date getEndtime() {
		return endtime;
	}
	public Long getId() {
		return id;
	}
	public Boolean getIsticket() {
		return isticket;
	}
	public String getName() {
		return name;
	}
	public BigDecimal getOfflineprice() {
		return offlineprice;
	}
	public BigDecimal getSubprice() {
		return subprice;
	}
	public int getType() {
		return type;
	}
	public String getUselimit() {
		return uselimit;
	}
	public boolean isIsused() {
		return isused;
	}
	
	public boolean isSelect() {
		return select;
	}
	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}
	public void setCheck(Boolean check) {
		this.check = check;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setIsticket(Boolean isticket) {
		this.isticket = isticket;
	}
	public void setIsused(boolean isused) {
		this.isused = isused;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setOfflineprice(BigDecimal offlineprice) {
		this.offlineprice = offlineprice;
	}
	public void setOfflinePrice(BigDecimal offlinePrice) {
		this.offlineprice = offlinePrice;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public void setSubprice(BigDecimal subprice) {
		this.subprice = subprice;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setUselimit(String uselimit) {
		this.uselimit = uselimit;
	}
	
	public Long getActivityid() {
		return activityid;
	}
	public void setActivityid(Long activityid) {
		this.activityid = activityid;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "TicketInfo [id=" + id + ", name=" + name + ", select=" + select
				+ ", check=" + check + ", subprice=" + subprice
				+ ", offlineprice=" + offlineprice 
				+ ", offlinesubprice=" + offlinesubprice +", isused=" + isused
				+ ", type=" + type + ", isticket=" + isticket + ", uselimit="
				+ uselimit + ", begintime=" + begintime + ", endtime="
				+ endtime + "  activityid="+activityid+" , status=" + status + " ]";
	}
	public BigDecimal getOfflinesubprice() {
		return offlinesubprice;
	}
	public void setOfflinesubprice(BigDecimal offlinesubprice) {
		this.offlinesubprice = offlinesubprice;
	}
	
	
}
