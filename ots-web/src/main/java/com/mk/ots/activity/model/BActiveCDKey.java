package com.mk.ots.activity.model;

import java.util.Date;

public class BActiveCDKey {
    private Long id;

    private String batchno;

    private Long activeid;

    private Long channelid;

    private Long promotionid;

    private String code;

    private Date expiration;

    private Date createtime;

    private Boolean used;

    private Date usetime;
    
    public BActiveCDKey() {
	}
    
    public BActiveCDKey(Long id, String batchno, Long activeid, Long channelid, Long promotionid, String code, Date expiration, Date createtime,
			Boolean used, Date usetime) {
		this.id = id;
		this.batchno = batchno;
		this.activeid = activeid;
		this.channelid = channelid;
		this.promotionid = promotionid;
		this.code = code;
		this.expiration = expiration;
		this.createtime = createtime;
		this.used = used;
		this.usetime = usetime;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno == null ? null : batchno.trim();
    }

    public Long getActiveid() {
        return activeid;
    }

    public void setActiveid(Long activeid) {
        this.activeid = activeid;
    }

    public Long getChannelid() {
        return channelid;
    }

    public void setChannelid(Long channelid) {
        this.channelid = channelid;
    }

    public Long getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(Long promotionid) {
        this.promotionid = promotionid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public Date getUsetime() {
        return usetime;
    }

    public void setUsetime(Date usetime) {
        this.usetime = usetime;
    }

	@Override
	public String toString() {
		return "BActiveCDKey [id=" + id + ", batchno=" + batchno
				+ ", activeid=" + activeid + ", channelid=" + channelid
				+ ", promotionid=" + promotionid + ", code=" + code
				+ ", expiration=" + expiration + ", createtime=" + createtime
				+ ", used=" + used + ", usetime=" + usetime + "]";
	}
}