package com.mk.ots.order.model;

public class OtaOrderMac {
    private Long id;

    private Long orderid;

    private String uuid;

    private String sysno;

    private String deviceimei;

    private String simsn;

    private String wifimacaddr;

    private String blmacaddr;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
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
        this.deviceimei = deviceimei == null ? null : deviceimei.trim();
    }

    public String getSimsn() {
        return simsn;
    }

    public void setSimsn(String simsn) {
        this.simsn = simsn == null ? null : simsn.trim();
    }

    public String getWifimacaddr() {
        return wifimacaddr;
    }

    public void setWifimacaddr(String wifimacaddr) {
        this.wifimacaddr = wifimacaddr == null ? null : wifimacaddr.trim();
    }

    public String getBlmacaddr() {
        return blmacaddr;
    }

    public void setBlmacaddr(String blmacaddr) {
        this.blmacaddr = blmacaddr == null ? null : blmacaddr.trim();
    }
}