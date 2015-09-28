package com.mk.ots.system.model;

public class SyConfig {
    private Long id;

    private String skey;

    private String stype;

    private String svalue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey == null ? null : skey.trim();
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype == null ? null : stype.trim();
    }

    public String getSvalue() {
        return svalue;
    }

    public void setSvalue(String svalue) {
        this.svalue = svalue == null ? null : svalue.trim();
    }
}