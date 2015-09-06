package com.mk.ots.log.model;

import java.util.Date;

/**
 * @author nolan
 *
 */
public class BLogOrder {
	private Long id;

	private Date logtime;

	private Long orderid;
	
	private String oldstatus;

	private String newstatus;

	private String note;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLogtime() {
		return logtime;
	}

	public void setLogtime(Date logtime) {
		this.logtime = logtime;
	}

	public String getOldstatus() {
		return oldstatus;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public void setOldstatus(String oldstatus) {
		this.oldstatus = oldstatus == null ? null : oldstatus.trim();
	}

	public String getNewstatus() {
		return newstatus;
	}

	public void setNewstatus(String newstatus) {
		this.newstatus = newstatus == null ? null : newstatus.trim();
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note == null ? null : note.trim();
	}
}