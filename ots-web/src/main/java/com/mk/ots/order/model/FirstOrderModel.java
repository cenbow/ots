package com.mk.ots.order.model;

public class FirstOrderModel {
	private Long mid;
	private String phone;
	private String deviceid;
	private String appid;
	private String idcard;

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
		this.phone = phone;
	}


	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	@Override
	public String toString() {
		return "FirstOrderModel [mid=" + mid + ", phone=" + phone
				+ ", deviceid=" + deviceid + ", appid=" + appid + ", idcard="
				+ idcard + "]";
	}

}
