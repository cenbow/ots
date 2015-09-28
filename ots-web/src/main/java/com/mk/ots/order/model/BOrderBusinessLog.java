package com.mk.ots.order.model;

import java.io.Serializable;

import java.util.Date;

public class BOrderBusinessLog implements Serializable{
	
	private static final long serialVersionUID = -5676722014361118413L;

	private Long id;

	private Long orderid;

	private Integer orderstatus;

	private Integer oldorderstatus;

	private String operateuser;

	private String bussinesscode;

	private String businessparams;

	private String businessdesc;

	private String businessexception;

	private Date createtime;

	private String operatename;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Integer getOrderstatus() {
		return orderstatus;
	}

	public void setOrderstatus(Integer orderstatus) {
		this.orderstatus = orderstatus;
	}

	public Integer getOldorderstatus() {
		return oldorderstatus;
	}

	public void setOldorderstatus(Integer oldorderstatus) {
		this.oldorderstatus = oldorderstatus;
	}

	public String getOperateuser() {
		return operateuser;
	}

	public void setOperateuser(String operateuser) {
		this.operateuser = operateuser == null ? null : operateuser.trim();
	}

	public String getBussinesscode() {
		return bussinesscode;
	}

	public void setBussinesscode(String bussinesscode) {
		this.bussinesscode = bussinesscode == null ? null : bussinesscode
				.trim();
	}

	public String getBusinessparams() {
		return businessparams;
	}

	public void setBusinessparams(String businessparams) {
		this.businessparams = businessparams == null ? null : businessparams
				.trim();
	}

	public String getBusinessdesc() {
		return businessdesc;
	}

	public void setBusinessdesc(String businessdesc) {
		this.businessdesc = businessdesc == null ? null : businessdesc.trim();
	}

	public String getBusinessexception() {
		return businessexception;
	}

	public void setBusinessexception(String businessexception) {
		this.businessexception = businessexception == null ? null
				: businessexception.trim();
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getOperatename() {
		return operatename;
	}

	public void setOperatename(String operatename) {
		this.operatename = operatename == null ? null : operatename.trim();
	}

	@Override
	public String toString() {
		return "BOrderBusinessLog [id=" + id + ", orderid=" + orderid
				+ ", orderstatus=" + orderstatus + ", oldorderstatus="
				+ oldorderstatus + ", operateuser=" + operateuser
				+ ", bussinesscode=" + bussinesscode + ", businessparams="
				+ businessparams + ", businessdesc=" + businessdesc
				+ ", businessexception=" + businessexception + ", createtime="
				+ createtime + ", operatename=" + operatename + "]";
	}

	
}