package com.mk.ots.ticket.model;

import java.util.Date;

import com.mk.ots.promo.model.BPromotion;

public class UPrizeRecord {
    private Long id;

    private Long mid;

    private Long prizeid;

    private String prizeinfo;

    private Long ostype;
    
    private Long prizetype;

    private Long activeid;

    private Date createtime;

    private BPrize bPrize;
    
    private BPromotion bPromotion;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrizetype() {
		return prizetype;
	}

	public void setPrizetype(Long prizetype) {
		this.prizetype = prizetype;
	}

	public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getPrizeid() {
        return prizeid;
    }

    public void setPrizeid(Long prizeid) {
        this.prizeid = prizeid;
    }

    public String getPrizeinfo() {
        return prizeinfo;
    }

    public void setPrizeinfo(String prizeinfo) {
        this.prizeinfo = prizeinfo == null ? null : prizeinfo.trim();
    }

    public Long getOstype() {
        return ostype;
    }

    public void setOstype(Long ostype) {
        this.ostype = ostype;
    }

    public Long getActiveid() {
        return activeid;
    }

    public void setActiveid(Long activeid) {
        this.activeid = activeid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

	public BPrize getbPrize() {
		return bPrize;
	}

	public void setbPrize(BPrize bPrize) {
		this.bPrize = bPrize;
	}

	public BPromotion getbPromotion() {
		return bPromotion;
	}

	public void setbPromotion(BPromotion bPromotion) {
		this.bPromotion = bPromotion;
	}

	@Override
	public String toString() {
		return "UPrizeRecord [id=" + id + ", mid=" + mid + ", prizeid="
				+ prizeid + ", prizeinfo=" + prizeinfo + ", ostype=" + ostype
				+ ", prizetype=" + prizetype + ", activeid=" + activeid
				+ ", createtime=" + createtime + ", bPrize=" + bPrize
				+ ", bPromotion=" + bPromotion + "]";
	}

	
    
}