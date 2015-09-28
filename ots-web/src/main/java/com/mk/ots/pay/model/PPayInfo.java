package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PPayInfoTypeEnum;

public class PPayInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;

    private PPay pay;

    private Date time;

    private BigDecimal cost;

    private PPayInfoTypeEnum type;

    private String otherno;

    private PPayInfoOtherTypeEnum othertype;

    private String otheruser;

    private Long innersrcid;

    private Long innerdestid;

    private boolean enable;
    
    private Long pmsSendId;
    
    private BigDecimal realCost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }


    public PPay getPay() {
		return pay;
	}

	public void setPay(PPay pay) {
		this.pay = pay;
	}

	public PPayInfoTypeEnum getType() {
		return type;
	}

	public void setType(PPayInfoTypeEnum type) {
		this.type = type;
	}

	public PPayInfoOtherTypeEnum getOthertype() {
		return othertype;
	}

	public void setOthertype(PPayInfoOtherTypeEnum othertype) {
		this.othertype = othertype;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getOtherno() {
        return otherno;
    }

    public void setOtherno(String otherno) {
        this.otherno = otherno == null ? null : otherno.trim();
    }

   

    public String getOtheruser() {
        return otheruser;
    }

    public void setOtheruser(String otheruser) {
        this.otheruser = otheruser == null ? null : otheruser.trim();
    }

    public Long getInnersrcid() {
        return innersrcid;
    }

    public void setInnersrcid(Long innersrcid) {
        this.innersrcid = innersrcid;
    }

    public Long getInnerdestid() {
        return innerdestid;
    }

    public void setInnerdestid(Long innerdestid) {
        this.innerdestid = innerdestid;
    }

	public Long getPmsSendId() {
		return pmsSendId;
	}

	public void setPmsSendId(Long pmsSendId) {
		this.pmsSendId = pmsSendId;
	}

	public BigDecimal getRealCost() {
		return realCost;
	}

	public void setRealCost(BigDecimal realCost) {
		this.realCost = realCost;
	}

	@Override
	public String toString() {
		return "PPayInfo [id=" + id + ", pay=" + pay + ", time=" + time
				+ ", cost=" + cost + ", type=" + type + ", otherno=" + otherno
				+ ", othertype=" + othertype + ", otheruser=" + otheruser
				+ ", innersrcid=" + innersrcid + ", innerdestid=" + innerdestid
				+ ", enable=" + enable + ", pmsSendId=" + pmsSendId
				+ ", realCost=" + realCost + "]";
	}

	
    
}
