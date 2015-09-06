package com.mk.ots.room.bean;

import java.util.Date;

public class RoomStateLog {
    private Long id;

    private String callmethod;

    private String callversion;

    private String ip;

    private String methodurl;

    private String methodparams;

    private String optuser;

    private Date createtime;

    private String other1;

    private String other2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallmethod() {
        return callmethod;
    }

    public void setCallmethod(String callmethod) {
        this.callmethod = callmethod == null ? null : callmethod.trim();
    }

    public String getCallversion() {
        return callversion;
    }

    public void setCallversion(String callversion) {
        this.callversion = callversion == null ? null : callversion.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public String getMethodurl() {
        return methodurl;
    }

    public void setMethodurl(String methodurl) {
        this.methodurl = methodurl == null ? null : methodurl.trim();
    }

    public String getMethodparams() {
        return methodparams;
    }

    public void setMethodparams(String methodparams) {
        this.methodparams = methodparams == null ? null : methodparams.trim();
    }

    public String getOptuser() {
        return optuser;
    }

    public void setOptuser(String optuser) {
        this.optuser = optuser == null ? null : optuser.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getOther1() {
        return other1;
    }

    public void setOther1(String other1) {
        this.other1 = other1 == null ? null : other1.trim();
    }

    public String getOther2() {
        return other2;
    }

    public void setOther2(String other2) {
        this.other2 = other2 == null ? null : other2.trim();
    }
}