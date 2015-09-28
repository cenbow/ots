package com.mk.ots.logininfo.model;
import java.util.Date;

public class BLoginInfo {
	
    private Long id;
    private String phone;
    private Long mid;
    private String uuid;
    private String sysno;
    private String deviceimei;
    private String simsn;
    private String wifimacaddr;
    private String blmacaddr;
    private Date createtime;
    private String security;

    public BLoginInfo() {
	}
    
    public BLoginInfo(Long id, String phone, Long mid,  String uuid,
    		String sysno,String deviceimei,String simsn,String wifimacaddr,String blmacaddr,Date createtime,String security) {
    	this.sysno = sysno;
    	this.phone = phone;
    	this.mid = mid;
    	this.uuid = uuid;
    	this.sysno = sysno;
    	this.sysno = deviceimei;
    	this.sysno = simsn;
    	this.sysno = wifimacaddr;
    	this.sysno = blmacaddr;
    	this.createtime = createtime;
    	this.security=security;
    }



	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getMid() {
		return mid;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSysno() {
		return sysno;
	}

	 public void setSysno(String sysno) {
	        this.sysno = sysno == null ? null : sysno.trim();
   }

	public String getDeviceimei() {
		return deviceimei;
	}

	public void setDeviceimei(String deviceimei) {
		this.deviceimei = deviceimei;
	}

	public String getSimsn() {
		return simsn;
	}

	public void setSimsn(String simsn) {
		this.simsn = simsn;
	}

	public String getWifimacaddr() {
		return wifimacaddr;
	}

	public void setWifimacaddr(String wifimacaddr) {
		this.wifimacaddr = wifimacaddr;
	}

	public String getBlmacaddr() {
		return blmacaddr;
	}

	public void setBlmacaddr(String blmacaddr) {
		this.blmacaddr = blmacaddr;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	@Override
	public String toString() {
		return "BLoginInfo [id=" + id + ", phone=" + phone + ", mid=" + mid
				+ ", uuid=" + uuid + ", sysno=" + sysno + ", deviceimei="
				+ deviceimei + ", simsn=" + simsn + ", wifimacaddr="
				+ wifimacaddr + ", blmacaddr=" + blmacaddr + ", createtime="
				+ createtime + ", security=" + security + "]";
	}
  
}