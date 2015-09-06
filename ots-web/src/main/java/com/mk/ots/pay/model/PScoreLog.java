package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PayLogOtherTypeEnum;
import com.mk.ots.common.enums.PayLogTypeEnum;
import com.mk.ots.member.model.UMember;

/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public class PScoreLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5218632829444463898L;
	
	private Long id;

    private Long mid;

    private Date time;

    private Long otherid;

    private PayLogOtherTypeEnum othertype;

    private BigDecimal price;

    private PayLogTypeEnum type;

    private String otherno;

    private PPayInfoOtherTypeEnum otherpaytype;

    private String otheruser;
	 
    
    public PayLogOtherTypeEnum getOthertype() {
		return othertype;
	}

	public void setOthertype(PayLogOtherTypeEnum othertype) {
		this.othertype = othertype;
	}

	public PayLogTypeEnum getType() {
		return type;
	}

	public void setType(PayLogTypeEnum type) {
		this.type = type;
	}

	public PPayInfoOtherTypeEnum getOtherpaytype() {
		return otherpaytype;
	}

	public void setOtherpaytype(PPayInfoOtherTypeEnum otherpaytype) {
		this.otherpaytype = otherpaytype;
	}

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getOtherid() {
        return otherid;
    }

    public void setOtherid(Long otherid) {
        this.otherid = otherid;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
}
