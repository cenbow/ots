package com.mk.ots.ticket.model;

import java.util.Date;

public class BPrizeStock {
    private Long id;

    private Long prizeid;

    private String code;

    private String name;

    private Date begintime;

    private Date endtime;

    private String status;

    private Long merchantid;

    private Long activeid;

    private BPrize bPrize;
   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrizeid() {
        return prizeid;
    }

    public void setPrizeid(Long prizeid) {
        this.prizeid = prizeid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Long getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(Long merchantid) {
        this.merchantid = merchantid;
    }

    public Long getActiveid() {
        return activeid;
    }

    public void setActiveid(Long activeid) {
        this.activeid = activeid;
    }

	public BPrize getbPrize() {
		return bPrize;
	}

	public void setbPrize(BPrize bPrize) {
		this.bPrize = bPrize;
	}

	@Override
	public String toString() {
		return "BPrizeStock [id=" + id + ", prizeid=" + prizeid + ", code="
				+ code + ", name=" + name + ", begintime=" + begintime
				+ ", endtime=" + endtime + ", status=" + status
				+ ", merchantid=" + merchantid + ", activeid=" + activeid
				+ ", bPrize=" + bPrize + "]";
	}
    
}