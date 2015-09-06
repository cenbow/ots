package com.mk.ots.activity.model;

import java.util.Date;

public class UActiveCDKeyLog {
    private Long id;

    private Long mid;

    private String code;

    private Long activeid;

    private Long channelid;

    private Long promotionid;

    private Boolean success;

    private Boolean push;

    private Date createtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
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


    public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Boolean getPush() {
		return push;
	}

	public void setPush(Boolean push) {
		this.push = push;
	}

	public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}