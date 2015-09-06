package com.mk.ots.member.model;

import java.util.Date;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * 生成用户优惠券日志
 * @author nolan
 *
 */
public class UPromotionLog {
    private Long id;

    private Long mid;

    private Long activeid;

    private Long promoinstanceid;
    
    private Long promotionid;

	private String noticetype;

    private Boolean success;

    private String errormsg;

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

    public Long getActiveid() {
        return activeid;
    }

    public void setActiveid(Long activeid) {
        this.activeid = activeid;
    }

    public Long getPromoinstanceid() {
        return promoinstanceid;
    }

    public void setPromoinstanceid(Long promoinstanceid) {
        this.promoinstanceid = promoinstanceid;
    }

    public String getNoticetype() {
        return noticetype;
    }

    public void setNoticetype(String noticetype) {
        this.noticetype = noticetype == null ? null : noticetype.trim();
    }

    public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg == null ? null : errormsg.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
    
    public Long getPromotionid() {
		return promotionid;
	}

	public void setPromotionid(Long promotionid) {
		this.promotionid = promotionid;
	}
    
    public String toString() {
		return MoreObjects.toStringHelper(this)
	        .add("id", id)
	        .add("mid", mid)
	        .add("activeid", activeid)
	        .add("promoinstanceid", promoinstanceid)
	        .add("promotionid", promotionid)
	        .add("noticetype", noticetype)
	        .add("success", success)
	        .add("errormsg", errormsg)
	        .add("createtime", createtime)
	        .toString();
	}
}