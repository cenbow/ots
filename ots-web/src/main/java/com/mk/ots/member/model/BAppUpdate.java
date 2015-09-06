package com.mk.ots.member.model;

public class BAppUpdate {
    private Long id;

    private String appupdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getAppupdate() {
		return appupdate;
	}

	public void setAppupdate(String appupdate) {
		this.appupdate = appupdate;
	}

	@Override
	public String toString() {
		return "BAppUpdate [id=" + id + ", appupdate=" + appupdate + "]";
	}

   

}