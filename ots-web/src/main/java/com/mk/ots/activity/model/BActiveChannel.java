package com.mk.ots.activity.model;

import java.util.Date;

public class BActiveChannel {
    private Long id;

    private Long activeid;

    private Long channelid;

    private String channelname;

    private Long promotionid;

    private Date expiration;

    public BActiveChannel() {
	}
	
	public BActiveChannel(Long id, Long activeid, Long channelid, String channelname, Long promotionid, Date expiration) {
		super();
		this.id = id;
		this.activeid = activeid;
		this.channelid = channelid;
		this.channelname = channelname;
		this.promotionid = promotionid;
		this.expiration = expiration;
	}
	
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname == null ? null : channelname.trim();
    }

    public Long getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(Long promotionid) {
        this.promotionid = promotionid;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

	@Override
	public String toString() {
		return "BActiveChannel [id=" + id + ", activeid=" + activeid
				+ ", channelid=" + channelid + ", channelname=" + channelname
				+ ", promotionid=" + promotionid + ", expiration=" + expiration
				+ "]";
	}
	
}