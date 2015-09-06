
package com.mk.ots.pay.model;


import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.ots.common.enums.PmsSendEnum;

@Component
@DbTable(name="p_orderlog", pkey="id")
public class POrderLog extends BizModel<POrderLog>{
	private static final long serialVersionUID = 7741008780225739397L;
	public static final POrderLog dao = new POrderLog();
	
	private Long id;

    private Long payid;

    private BigDecimal allcost;

    private BigDecimal hotelgive;

    private BigDecimal otagive;

    private BigDecimal usercost;

    private BigDecimal realcost;

    private BigDecimal realotagive;
    
    private String refund;
    
    private PmsSendEnum pmssend;

    private Date pmssendtime;
    
    private String sendreason;
    
    private PmsSendEnum pmsrefund;
    
    private Date pmsrefundtime;
    
    private String refundreason;
    
    private BigDecimal qiekeIncome;
    
    public PmsSendEnum getPmssend() {
		return pmssend;
	}

	public void setPmssend(PmsSendEnum pmssend) {
		this.pmssend = pmssend;
	}

	public Date getPmssendtime() {
		return pmssendtime;
	}

	public void setPmssendtime(Date pmssendtime) {
		this.pmssendtime = pmssendtime;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPayid() {
        return payid;
    }

    public void setPayid(Long payid) {
        this.payid = payid;
    }

    public BigDecimal getAllcost() {
        return allcost;
    }

    public void setAllcost(BigDecimal allcost) {
        this.allcost = allcost;
    }

    public BigDecimal getHotelgive() {
        return hotelgive;
    }

    public void setHotelgive(BigDecimal hotelgive) {
        this.hotelgive = hotelgive;
    }

    public BigDecimal getOtagive() {
        return otagive;
    }

    public void setOtagive(BigDecimal otagive) {
        this.otagive = otagive;
    }

    public BigDecimal getUsercost() {
        return usercost;
    }

    public void setUsercost(BigDecimal usercost) {
        this.usercost = usercost;
    }

    public BigDecimal getRealcost() {
        return realcost;
    }

    public void setRealcost(BigDecimal realcost) {
        this.realcost = realcost;
    }

    public BigDecimal getRealotagive() {
        return realotagive;
    }

    public void setRealotagive(BigDecimal realotagive) {
        this.realotagive = realotagive;
    }

	public String getRefund() {
		return refund;
	}

	public void setRefund(String refund) {
		this.refund = refund;
	}

	public PmsSendEnum getPmsrefund() {
		return pmsrefund;
	}

	public void setPmsrefund(PmsSendEnum pmsrefund) {
		this.pmsrefund = pmsrefund;
	}

	public Date getPmsrefundtime() {
		return pmsrefundtime;
	}

	public void setPmsrefundtime(Date pmsrefundtime) {
		this.pmsrefundtime = pmsrefundtime;
	}

	public String getSendreason() {
		return sendreason;
	}

	public void setSendreason(String sendreason) {
		this.sendreason = sendreason;
	}

	public String getRefundreason() {
		return refundreason;
	}

	public void setRefundreason(String refundreason) {
		this.refundreason = refundreason;
	}

	public BigDecimal getQiekeIncome() {
		return qiekeIncome;
	}

	public void setQiekeIncome(BigDecimal qiekeIncome) {
		this.qiekeIncome = qiekeIncome;
	}

	@Override
	public String toString() {
		return "POrderLog [id=" + id + ", payid=" + payid + ", allcost="
				+ allcost + ", hotelgive=" + hotelgive + ", otagive=" + otagive
				+ ", usercost=" + usercost + ", realcost=" + realcost
				+ ", realotagive=" + realotagive + ", refund=" + refund
				+ ", pmssend=" + pmssend + ", pmssendtime=" + pmssendtime
				+ ", pmsrefund=" + pmsrefund + ", pmsrefundtime="
				+ pmsrefundtime +",qiekeIncome="+qiekeIncome+ "]";
	}
	
}
