package com.mk.ots.appstatus.model;

import java.util.Date;

public class AppStatus {
    private Long id;

    private String sysno;

    private Long mid;

    private String phone;

    private String userlongitude;

    private String userlatitude;

    private Integer runningstatus;

    private String runningpage;
    
    private Date createtime;

    public AppStatus() {
	}
    
    public AppStatus(String sysno, Long mid, String phone, String userlongtitude, String userlatitude, Integer runningstatus, String runningpage) {
    	this.sysno = sysno;
    	this.mid = mid;
    	this.phone = phone;
    	this.userlongitude = userlongtitude;
    	this.userlatitude = userlatitude;
    	this.runningpage = runningpage;
    	this.runningstatus = runningstatus;
    }
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSysno() {
        return sysno;
    }

    public void setSysno(String sysno) {
        this.sysno = sysno == null ? null : sysno.trim();
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getUserlongitude() {
        return userlongitude;
    }

    public void setUserlongitude(String userlongitude) {
        this.userlongitude = userlongitude == null ? null : userlongitude.trim();
    }

    public String getUserlatitude() {
        return userlatitude;
    }

    public void setUserlatitude(String userlatitude) {
        this.userlatitude = userlatitude == null ? null : userlatitude.trim();
    }

    public Integer getRunningstatus() {
        return runningstatus;
    }

    public void setRunningstatus(Integer runningstatus) {
        this.runningstatus = runningstatus;
    }

    public String getRunningpage() {
        return runningpage;
    }

    public void setRunningpage(String runningpage) {
        this.runningpage = runningpage == null ? null : runningpage.trim();
    }

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
}